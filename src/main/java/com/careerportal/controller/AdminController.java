package com.careerportal.controller;

import com.careerportal.entity.*;
import com.careerportal.repository.*;
import com.careerportal.service.*;
import com.careerportal.util.SessionUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.*;

/**
 * AdminController - full admin panel
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private UserService userService;
    @Autowired private StudentService studentService;
    @Autowired private AssessmentService assessmentService;
    @Autowired private QuestionRepository questionRepo;
    @Autowired private CareerRepository careerRepo;
    @Autowired private CourseRepository courseRepo;
    @Autowired private JobRepository jobRepo;
    @Autowired private InternshipRepository internshipRepo;
    @Autowired private SystemSettingsRepository settingsRepo;
    @Autowired private AIReportRepository aiReportRepo;

    private User getAdmin(HttpSession session) {
        if (!SessionUtil.isLoggedIn(session) || !"ADMIN".equals(SessionUtil.getRole(session))) return null;
        return SessionUtil.getUser(session);
    }

    // ======================== DASHBOARD ========================
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (getAdmin(session) == null) return "redirect:/login";
        model.addAttribute("admin", SessionUtil.getUser(session));
        model.addAttribute("totalStudents", userService.countByRole("STUDENT"));
        model.addAttribute("totalParents", userService.countByRole("PARENT"));
        model.addAttribute("totalCounselors", userService.countByRole("COUNSELOR"));
        model.addAttribute("assessmentsCompleted", assessmentService.countCompletedAssessments());
        model.addAttribute("reportsGenerated", assessmentService.countReports());
        return "admin/dashboard";
    }

    // ======================== USERS ========================
    @GetMapping("/users")
    public String usersPage(HttpSession session, Model model) {
        if (getAdmin(session) == null) return "redirect:/login";
        model.addAttribute("users", userService.findAll());
        return "admin/users";
    }

    @GetMapping("/users/new")
    public String newUserPage(HttpSession session, Model model) {
        if (getAdmin(session) == null) return "redirect:/login";
        model.addAttribute("user", new User());
        return "admin/user-form";
    }

    @PostMapping("/users/save")
    public String saveUser(@RequestParam Map<String, String> params,
                            HttpSession session, RedirectAttributes redirect) {
        if (getAdmin(session) == null) return "redirect:/login";

        User user = new User();
        if (params.containsKey("id") && !params.get("id").isBlank()) {
            user = userService.findById(Long.parseLong(params.get("id"))).orElse(new User());
        }
        user.setUsername(params.get("username"));
        user.setPassword(params.get("password"));
        user.setRole(params.get("role"));
        user.setFullName(params.get("fullName"));
        user.setEmail(params.get("email"));
        user.setPhone(params.get("phone"));

        User saved = userService.save(user);

        // Auto-create student record if role is STUDENT
        if ("STUDENT".equals(user.getRole())) {
            studentService.findByUserId(saved.getId()).orElseGet(() -> {
                Student s = new Student();
                s.setUser(saved);
                return studentService.save(s);
            });
        }

        redirect.addFlashAttribute("success", "User saved successfully!");
        return "redirect:/admin/users";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, HttpSession session, RedirectAttributes redirect) {
        if (getAdmin(session) == null) return "redirect:/login";
        userService.delete(id);
        redirect.addFlashAttribute("success", "User deleted.");
        return "redirect:/admin/users";
    }

    // ======================== QUESTIONS ========================
    @GetMapping("/questions")
    public String questionsPage(HttpSession session, Model model) {
        if (getAdmin(session) == null) return "redirect:/login";
        model.addAttribute("questions", questionRepo.findAll());
        return "admin/questions";
    }

    @GetMapping("/questions/new")
    public String newQuestion(HttpSession session, Model model) {
        if (getAdmin(session) == null) return "redirect:/login";
        model.addAttribute("question", new Question());
        return "admin/question-form";
    }

    @PostMapping("/questions/save")
    public String saveQuestion(@RequestParam Map<String, String> params,
                                HttpSession session, RedirectAttributes redirect) {
        if (getAdmin(session) == null) return "redirect:/login";
        Question q = new Question();
        if (params.containsKey("id") && !params.get("id").isBlank()) {
            q = questionRepo.findById(Long.parseLong(params.get("id"))).orElse(new Question());
        }
        q.setQuestionType(params.get("questionType"));
        q.setQuestionText(params.get("questionText"));
        q.setOptionA(params.get("optionA"));
        q.setOptionB(params.get("optionB"));
        q.setOptionC(params.get("optionC"));
        q.setOptionD(params.get("optionD"));
        q.setCorrectAnswer(params.get("correctAnswer"));
        questionRepo.save(q);
        redirect.addFlashAttribute("success", "Question saved!");
        return "redirect:/admin/questions";
    }

    @GetMapping("/questions/delete/{id}")
    public String deleteQuestion(@PathVariable Long id, HttpSession session, RedirectAttributes redirect) {
        if (getAdmin(session) == null) return "redirect:/login";
        questionRepo.deleteById(id);
        redirect.addFlashAttribute("success", "Question deleted.");
        return "redirect:/admin/questions";
    }

    // ======================== CAREERS ========================
    @GetMapping("/careers")
    public String careersPage(HttpSession session, Model model) {
        if (getAdmin(session) == null) return "redirect:/login";
        model.addAttribute("careers", careerRepo.findAll());
        return "admin/careers";
    }

    @PostMapping("/careers/save")
    public String saveCareer(@RequestParam Map<String, String> p, HttpSession session, RedirectAttributes redirect) {
        if (getAdmin(session) == null) return "redirect:/login";
        Career c = new Career();
        if (p.containsKey("id") && !p.get("id").isBlank()) {
            c = careerRepo.findById(Long.parseLong(p.get("id"))).orElse(new Career());
        }
        c.setName(p.get("name")); c.setCategory(p.get("category")); c.setDescription(p.get("description"));
        c.setSalaryRange(p.get("salaryRange")); c.setRequiredSkills(p.get("requiredSkills"));
        c.setEducationRequired(p.get("educationRequired"));
        careerRepo.save(c);
        redirect.addFlashAttribute("success", "Career saved!");
        return "redirect:/admin/careers";
    }

    @GetMapping("/careers/delete/{id}")
    public String deleteCareer(@PathVariable Long id, HttpSession session, RedirectAttributes redirect) {
        if (getAdmin(session) == null) return "redirect:/login";
        careerRepo.deleteById(id);
        redirect.addFlashAttribute("success", "Career deleted.");
        return "redirect:/admin/careers";
    }

    // ======================== SETTINGS ========================
    @GetMapping("/settings")
    public String settingsPage(HttpSession session, Model model) {
        if (getAdmin(session) == null) return "redirect:/login";
        model.addAttribute("settings", settingsRepo.findAll());
        return "admin/settings";
    }

    @PostMapping("/settings/save")
    public String saveSettings(@RequestParam Map<String, String> params,
                                HttpSession session, RedirectAttributes redirect) {
        if (getAdmin(session) == null) return "redirect:/login";
        params.forEach((key, value) -> {
            if (key.startsWith("setting_")) {
                String settingKey = key.substring(8);
                settingsRepo.findBySettingKey(settingKey).ifPresent(s -> {
                    s.setSettingValue(value);
                    settingsRepo.save(s);
                });
            }
        });
        redirect.addFlashAttribute("success", "Settings updated!");
        return "redirect:/admin/settings";
    }

    // ======================== REPORTS ========================
    @GetMapping("/reports")
    public String reportsPage(HttpSession session, Model model) {
        if (getAdmin(session) == null) return "redirect:/login";
        model.addAttribute("reports", aiReportRepo.findAll());
        model.addAttribute("students", studentService.findAll());
        return "admin/reports";
    }

    // ======================== COURSES, JOBS, INTERNSHIPS ========================
    @GetMapping("/courses")
    public String coursesPage(HttpSession session, Model model) {
        if (getAdmin(session) == null) return "redirect:/login";
        model.addAttribute("courses", courseRepo.findAll());
        return "admin/courses";
    }

    @PostMapping("/courses/save")
    public String saveCourse(@RequestParam Map<String, String> p, HttpSession session, RedirectAttributes redirect) {
        if (getAdmin(session) == null) return "redirect:/login";
        Course c = new Course();
        if (p.containsKey("id") && !p.get("id").isBlank()) {
            c = courseRepo.findById(Long.parseLong(p.get("id"))).orElse(new Course());
        }
        c.setTitle(p.get("title")); c.setProvider(p.get("provider")); c.setCategory(p.get("category"));
        c.setDescription(p.get("description")); c.setDuration(p.get("duration")); c.setPrice(p.get("price"));
        c.setUrl(p.get("url")); c.setLevel(p.get("level"));
        courseRepo.save(c);
        redirect.addFlashAttribute("success", "Course saved!");
        return "redirect:/admin/courses";
    }

    @GetMapping("/jobs")
    public String jobsPage(HttpSession session, Model model) {
        if (getAdmin(session) == null) return "redirect:/login";
        model.addAttribute("jobs", jobRepo.findAll());
        return "admin/jobs";
    }

    @PostMapping("/jobs/save")
    public String saveJob(@RequestParam Map<String, String> p, HttpSession session, RedirectAttributes redirect) {
        if (getAdmin(session) == null) return "redirect:/login";
        Job j = new Job();
        if (p.containsKey("id") && !p.get("id").isBlank()) {
            j = jobRepo.findById(Long.parseLong(p.get("id"))).orElse(new Job());
        }
        j.setTitle(p.get("title")); j.setCompany(p.get("company")); j.setLocation(p.get("location"));
        j.setType(p.get("type")); j.setDescription(p.get("description")); j.setSalaryRange(p.get("salaryRange"));
        j.setRequiredSkills(p.get("requiredSkills")); j.setExperience(p.get("experience"));
        jobRepo.save(j);
        redirect.addFlashAttribute("success", "Job saved!");
        return "redirect:/admin/jobs";
    }

    @GetMapping("/internships")
    public String internshipsPage(HttpSession session, Model model) {
        if (getAdmin(session) == null) return "redirect:/login";
        model.addAttribute("internships", internshipRepo.findAll());
        return "admin/internships";
    }
}
