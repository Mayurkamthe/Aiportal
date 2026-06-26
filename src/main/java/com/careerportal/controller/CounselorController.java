package com.careerportal.controller;

import com.careerportal.entity.*;
import com.careerportal.repository.CounselorNoteRepository;
import com.careerportal.service.*;
import com.careerportal.util.SessionUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

/**
 * CounselorController - handles counselor dashboard and student management
 */
@Controller
@RequestMapping("/counselor")
public class CounselorController {

    @Autowired private StudentService studentService;
    @Autowired private AssessmentService assessmentService;
    @Autowired private CounselorNoteRepository noteRepo;
    @Autowired private PDFService pdfService;

    private User getCounselor(HttpSession session) {
        if (!SessionUtil.isLoggedIn(session) || !"COUNSELOR".equals(SessionUtil.getRole(session))) return null;
        return SessionUtil.getUser(session);
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User counselor = getCounselor(session);
        if (counselor == null) return "redirect:/login";

        List<Student> students = studentService.findByCounselorId(counselor.getId());
        model.addAttribute("counselor", counselor);
        model.addAttribute("students", students);
        model.addAttribute("totalStudents", students.size());
        return "counselor/dashboard";
    }

    @GetMapping("/students")
    public String studentsPage(HttpSession session, Model model) {
        User counselor = getCounselor(session);
        if (counselor == null) return "redirect:/login";
        model.addAttribute("students", studentService.findByCounselorId(counselor.getId()));
        model.addAttribute("counselor", counselor);
        return "counselor/students";
    }

    @GetMapping("/student/{id}")
    public String studentDetail(@PathVariable Long id, HttpSession session, Model model) {
        User counselor = getCounselor(session);
        if (counselor == null) return "redirect:/login";

        Student student = studentService.findById(id).orElse(null);
        if (student == null) return "redirect:/counselor/students";

        model.addAttribute("student", student);
        model.addAttribute("assessments", assessmentService.getStudentAssessments(student));
        assessmentService.getAIReport(student).ifPresent(r -> model.addAttribute("report", r));
        model.addAttribute("notes", noteRepo.findByStudentIdOrderByCreatedAtDesc(id));
        model.addAttribute("counselor", counselor);
        return "counselor/student-detail";
    }

    @PostMapping("/note/add")
    public String addNote(@RequestParam Long studentId,
                           @RequestParam String noteText,
                           HttpSession session,
                           RedirectAttributes redirect) {
        User counselor = getCounselor(session);
        if (counselor == null) return "redirect:/login";

        CounselorNote note = new CounselorNote();
        note.setCounselorId(counselor.getId());
        note.setStudentId(studentId);
        note.setNoteText(noteText);
        noteRepo.save(note);

        redirect.addFlashAttribute("success", "Note added successfully!");
        return "redirect:/counselor/student/" + studentId;
    }

    @GetMapping("/student/{id}/report")
    public ResponseEntity<byte[]> downloadStudentReport(@PathVariable Long id, HttpSession session) {
        if (getCounselor(session) == null) return ResponseEntity.badRequest().build();
        try {
            Student student = studentService.findById(id).orElseThrow();
            AIReport report = assessmentService.getAIReport(student).orElse(null);
            byte[] pdf = pdfService.generateReport(student, report);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=student-report-" + id + ".pdf")
                    .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                    .body(pdf);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
