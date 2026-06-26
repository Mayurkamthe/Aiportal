package com.careerportal.controller;

import com.careerportal.entity.User;
import com.careerportal.service.UserService;
import com.careerportal.util.SessionUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Optional;

/**
 * AuthController - handles login and logout for all roles
 */
@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    /** Home page - redirect to login */
    @GetMapping("/")
    public String home(HttpSession session) {
        if (SessionUtil.isLoggedIn(session)) {
            return redirectByRole(SessionUtil.getRole(session));
        }
        return "redirect:/login";
    }

    /** Show login page */
    @GetMapping("/login")
    public String loginPage(HttpSession session, Model model) {
        if (SessionUtil.isLoggedIn(session)) {
            return redirectByRole(SessionUtil.getRole(session));
        }
        return "auth/login";
    }

    /** Process login form */
    @PostMapping("/login")
    public String processLogin(@RequestParam String username,
                                @RequestParam String password,
                                HttpSession session,
                                RedirectAttributes redirect) {
        Optional<User> userOpt = userService.login(username, password);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (!user.isActive()) {
                redirect.addFlashAttribute("error", "Your account is inactive. Please contact admin.");
                return "redirect:/login";
            }
            SessionUtil.setUser(session, user);
            return redirectByRole(user.getRole());
        } else {
            redirect.addFlashAttribute("error", "Invalid username or password.");
            return "redirect:/login";
        }
    }

    /** Logout */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        SessionUtil.clearSession(session);
        return "redirect:/login?logout";
    }

    /** Helper: redirect based on role */
    private String redirectByRole(String role) {
        return switch (role) {
            case "STUDENT" -> "redirect:/student/dashboard";
            case "PARENT" -> "redirect:/parent/dashboard";
            case "COUNSELOR" -> "redirect:/counselor/dashboard";
            case "ADMIN" -> "redirect:/admin/dashboard";
            default -> "redirect:/login";
        };
    }
}
