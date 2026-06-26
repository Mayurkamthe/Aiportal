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
import java.util.List;

/**
 * ParentController - handles parent dashboard
 */
@Controller
@RequestMapping("/parent")
public class ParentController {

    @Autowired private StudentService studentService;
    @Autowired private AssessmentService assessmentService;
    @Autowired private CounselorNoteRepository noteRepo;
    @Autowired private PDFService pdfService;

    private User getParent(HttpSession session) {
        if (!SessionUtil.isLoggedIn(session) || !"PARENT".equals(SessionUtil.getRole(session))) return null;
        return SessionUtil.getUser(session);
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User parent = getParent(session);
        if (parent == null) return "redirect:/login";

        List<Student> children = studentService.findByParentId(parent.getId());
        model.addAttribute("parent", parent);
        model.addAttribute("children", children);

        // Load first child's data if available
        if (!children.isEmpty()) {
            Student child = children.get(0);
            model.addAttribute("student", child);
            model.addAttribute("assessments", assessmentService.getStudentAssessments(child));
            assessmentService.getAIReport(child).ifPresent(r -> model.addAttribute("report", r));
            model.addAttribute("notes", noteRepo.findByStudentIdOrderByCreatedAtDesc(child.getId()));
        }
        return "parent/dashboard";
    }

    @GetMapping("/student/{id}")
    public String studentProgress(@PathVariable Long id, HttpSession session, Model model) {
        User parent = getParent(session);
        if (parent == null) return "redirect:/login";

        Student student = studentService.findById(id).orElse(null);
        if (student == null) return "redirect:/parent/dashboard";

        model.addAttribute("student", student);
        model.addAttribute("parent", parent);
        model.addAttribute("assessments", assessmentService.getStudentAssessments(student));
        assessmentService.getAIReport(student).ifPresent(r -> model.addAttribute("report", r));
        model.addAttribute("notes", noteRepo.findByStudentIdOrderByCreatedAtDesc(id));
        return "parent/student-progress";
    }

    @GetMapping("/student/{id}/report")
    public ResponseEntity<byte[]> downloadReport(@PathVariable Long id, HttpSession session) {
        if (getParent(session) == null) return ResponseEntity.badRequest().build();
        try {
            Student student = studentService.findById(id).orElseThrow();
            AIReport report = assessmentService.getAIReport(student).orElse(null);
            byte[] pdf = pdfService.generateReport(student, report);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=career-report.pdf")
                    .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                    .body(pdf);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
