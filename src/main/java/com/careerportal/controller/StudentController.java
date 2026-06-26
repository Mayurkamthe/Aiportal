package com.careerportal.controller;

import com.careerportal.entity.*;
import com.careerportal.repository.*;
import com.careerportal.service.*;
import com.careerportal.util.SessionUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.*;

/**
 * StudentController - handles all student pages and actions
 */
@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired private StudentService studentService;
    @Autowired private AssessmentService assessmentService;
    @Autowired private ChatService chatService;
    @Autowired private PDFService pdfService;
    @Autowired private UserService userService;
    @Autowired private CareerRepository careerRepo;
    @Autowired private CourseRepository courseRepo;
    @Autowired private JobRepository jobRepo;
    @Autowired private InternshipRepository internshipRepo;

    // ---- Middleware: check session ----
    private Student getStudent(HttpSession session) {
        if (!SessionUtil.isLoggedIn(session) || !"STUDENT".equals(SessionUtil.getRole(session))) return null;
        Long userId = SessionUtil.getUserId(session);
        return studentService.findByUserId(userId).orElse(null);
    }

    // ======================== DASHBOARD ========================
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Student student = getStudent(session);
        if (student == null) return "redirect:/login";

        model.addAttribute("student", student);
        model.addAttribute("user", student.getUser());

        // Assessment status
        List<Assessment> assessments = assessmentService.getStudentAssessments(student);
        model.addAttribute("assessments", assessments);
        model.addAttribute("allComplete", assessmentService.allAssessmentsComplete(student));

        // AI Report
        assessmentService.getAIReport(student).ifPresent(r -> model.addAttribute("report", r));

        // Recommendations
        model.addAttribute("careers", careerRepo.findByActive(true).stream().limit(4).toList());
        model.addAttribute("courses", courseRepo.findByActive(true).stream().limit(3).toList());
        model.addAttribute("jobs", jobRepo.findByActive(true).stream().limit(3).toList());
        model.addAttribute("internships", internshipRepo.findByActive(true).stream().limit(3).toList());

        return "student/dashboard";
    }

    // ======================== PROFILE ========================
    @GetMapping("/profile")
    public String profilePage(HttpSession session, Model model) {
        Student student = getStudent(session);
        if (student == null) return "redirect:/login";
        model.addAttribute("student", student);
        model.addAttribute("user", student.getUser());
        return "student/profile";
    }

    @PostMapping("/profile/save")
    public String saveProfile(HttpSession session,
                               @RequestParam Map<String, String> params,
                               @RequestParam(required = false) MultipartFile photo,
                               RedirectAttributes redirect) {
        Student student = getStudent(session);
        if (student == null) return "redirect:/login";

        student.setDateOfBirth(params.get("dateOfBirth"));
        student.setGender(params.get("gender"));
        student.setAddress(params.get("address"));
        student.setCity(params.get("city"));
        student.setState(params.get("state"));
        student.setCurrentClass(params.get("currentClass"));
        student.setSchool(params.get("school"));
        student.setBoard(params.get("board"));
        parseDouble(params.get("mathMarks")).ifPresent(student::setMathMarks);
        parseDouble(params.get("scienceMarks")).ifPresent(student::setScienceMarks);
        parseDouble(params.get("englishMarks")).ifPresent(student::setEnglishMarks);
        parseDouble(params.get("overallPercentage")).ifPresent(student::setOverallPercentage);
        student.setSkills(params.get("skills"));
        student.setInterests(params.get("interests"));
        student.setHobbies(params.get("hobbies"));
        student.setCareerGoal(params.get("careerGoal"));

        // Upload photo
        if (photo != null && !photo.isEmpty()) {
            try {
                String filename = studentService.uploadPhoto(photo);
                student.setProfilePhoto(filename);
            } catch (Exception e) {
                redirect.addFlashAttribute("error", "Photo upload failed: " + e.getMessage());
            }
        }

        studentService.save(student);
        redirect.addFlashAttribute("success", "Profile saved successfully!");
        return "redirect:/student/profile";
    }

    // ======================== ASSESSMENT ========================
    @GetMapping("/assessment/{type}")
    public String assessmentPage(@PathVariable String type, HttpSession session, Model model) {
        Student student = getStudent(session);
        if (student == null) return "redirect:/login";

        List<Question> questions = assessmentService.getQuestions(type.toUpperCase());
        model.addAttribute("questions", questions);
        model.addAttribute("type", type.toUpperCase());
        model.addAttribute("student", student);

        // Check if already completed
        assessmentService.findByStudentAndType(student, type.toUpperCase())
                .ifPresent(a -> model.addAttribute("existingAssessment", a));

        return "student/assessment";
    }

    @PostMapping("/assessment/submit")
    public String submitAssessment(@RequestParam String assessmentType,
                                    @RequestParam Map<String, String> params,
                                    HttpSession session,
                                    RedirectAttributes redirect) {
        Student student = getStudent(session);
        if (student == null) return "redirect:/login";

        // Filter only question answers
        Map<String, String> answers = new HashMap<>();
        params.forEach((k, v) -> { if (k.startsWith("q_")) answers.put(k, v); });

        assessmentService.saveAssessment(student, assessmentType, answers);
        redirect.addFlashAttribute("success", assessmentType + " assessment submitted!");

        // If all done, generate AI report
        if (assessmentService.allAssessmentsComplete(student)) {
            assessmentService.generateAIReport(student);
            redirect.addFlashAttribute("info", "🎉 All assessments complete! AI career report generated.");
        }

        return "redirect:/student/dashboard";
    }

    // ======================== AI REPORT ========================
    @GetMapping("/report")
    public String reportPage(HttpSession session, Model model) {
        Student student = getStudent(session);
        if (student == null) return "redirect:/login";
        model.addAttribute("student", student);
        assessmentService.getAIReport(student).ifPresent(r -> model.addAttribute("report", r));
        return "student/report";
    }

    @PostMapping("/report/generate")
    public String generateReport(HttpSession session, RedirectAttributes redirect) {
        Student student = getStudent(session);
        if (student == null) return "redirect:/login";
        assessmentService.generateAIReport(student);
        redirect.addFlashAttribute("success", "AI Career Report generated!");
        return "redirect:/student/report";
    }

    // ======================== AI CHAT ========================
    @GetMapping("/chat")
    public String chatPage(HttpSession session, Model model) {
        Student student = getStudent(session);
        if (student == null) return "redirect:/login";
        model.addAttribute("student", student);
        model.addAttribute("chats", chatService.getChatHistory(student));
        return "student/chat";
    }

    @PostMapping("/chat/send")
    @ResponseBody
    public Map<String, String> sendChat(@RequestParam String message, HttpSession session) {
        Student student = getStudent(session);
        if (student == null) return Map.of("error", "Not logged in");
        ChatHistory chat = chatService.sendMessage(student, message);
        return Map.of("response", chat.getAiResponse());
    }

    // ======================== PDF DOWNLOAD ========================
    @GetMapping("/report/download")
    public ResponseEntity<byte[]> downloadPDF(HttpSession session) {
        Student student = getStudent(session);
        if (student == null) return ResponseEntity.badRequest().build();
        try {
            AIReport report = assessmentService.getAIReport(student).orElse(null);
            byte[] pdf = pdfService.generateReport(student, report);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=career-report.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ======================== EXPLORE ========================
    @GetMapping("/careers")
    public String careersPage(HttpSession session, Model model) {
        if (getStudent(session) == null) return "redirect:/login";
        model.addAttribute("careers", careerRepo.findByActive(true));
        return "student/careers";
    }

    @GetMapping("/courses")
    public String coursesPage(HttpSession session, Model model) {
        if (getStudent(session) == null) return "redirect:/login";
        model.addAttribute("courses", courseRepo.findByActive(true));
        return "student/courses";
    }

    @GetMapping("/jobs")
    public String jobsPage(HttpSession session, Model model) {
        if (getStudent(session) == null) return "redirect:/login";
        model.addAttribute("jobs", jobRepo.findByActive(true));
        return "student/jobs";
    }

    @GetMapping("/internships")
    public String internshipsPage(HttpSession session, Model model) {
        if (getStudent(session) == null) return "redirect:/login";
        model.addAttribute("internships", internshipRepo.findByActive(true));
        return "student/internships";
    }

    private Optional<Double> parseDouble(String val) {
        if (val == null || val.isBlank()) return Optional.empty();
        try { return Optional.of(Double.parseDouble(val)); }
        catch (NumberFormatException e) { return Optional.empty(); }
    }
}
