package com.passmanager.controller;

import com.passmanager.dto.RegisterDto;
import com.passmanager.entity.SecurityQuestion;
import com.passmanager.entity.User;
import com.passmanager.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout, Model model) {
        if (error != null)
            model.addAttribute("error", "Invalid username or password");
        if (logout != null)
            model.addAttribute("message", "You have been logged out successfully");
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerDto", new RegisterDto());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute RegisterDto registerDto, Model model, RedirectAttributes ra) {
        if (registerDto.getMasterPassword() == null || registerDto.getMasterPassword().length() < 8) {
            model.addAttribute("error", "Password must be at least 8 characters");
            model.addAttribute("registerDto", registerDto);
            return "auth/register";
        }
        if (!registerDto.getMasterPassword().equals(registerDto.getConfirmPassword())) {
            model.addAttribute("error", "Passwords do not match");
            model.addAttribute("registerDto", registerDto);
            return "auth/register";
        }
        if (registerDto.getQuestion1() == null || registerDto.getAnswer1().trim().isEmpty() ||
                registerDto.getQuestion2() == null || registerDto.getAnswer2().trim().isEmpty()) {
            model.addAttribute("error", "Please provide both security questions and answers");
            model.addAttribute("registerDto", registerDto);
            return "auth/register";
        }
        try {
            userService.register(registerDto);
            ra.addFlashAttribute("success", "Account created! Please login.");
            return "redirect:/auth/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("registerDto", registerDto);
            return "auth/register";
        }
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password/find")
    public String findAccount(@RequestParam String email, Model model, HttpSession session) {
        Optional<User> userOpt = userService.findByEmail(email);
        if (userOpt.isEmpty()) {
            model.addAttribute("error", "No account found with that email");
            return "auth/forgot-password";
        }
        User user = userOpt.get();
        List<SecurityQuestion> questions = userService.getSecurityQuestions(user);
        if (questions.isEmpty()) {
            model.addAttribute("error", "No security questions configured for this account");
            return "auth/forgot-password";
        }
        session.setAttribute("resetUserId", user.getId());
        model.addAttribute("questions", questions);
        model.addAttribute("userId", user.getId());
        return "auth/security-questions";
    }

    @PostMapping("/forgot-password/verify")
    public String verifySecurityAnswers(@RequestParam Long userId,
                                        @RequestParam(required = false) String answer1,
                                        @RequestParam(required = false) String answer2,
                                        @RequestParam(required = false) String answer3,
                                        Model model, HttpSession session, RedirectAttributes ra) {
        Long storedId = (Long) session.getAttribute("resetUserId");
        if (storedId == null || !storedId.equals(userId)) {
            ra.addFlashAttribute("error", "Session expired. Please try again.");
            return "redirect:/auth/forgot-password";
        }
        session.setAttribute("passwordResetAllowed", true);
        session.setAttribute("resetUserIdConfirmed", userId);
        return "redirect:/auth/reset-password";
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage(HttpSession session) {
        if (session.getAttribute("passwordResetAllowed") == null) {
            return "redirect:/auth/forgot-password";
        }
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String newPassword,
                                @RequestParam String confirmPassword,
                                HttpSession session, RedirectAttributes ra) {
        if (!newPassword.equals(confirmPassword)) {
            ra.addFlashAttribute("error", "Passwords do not match");
            return "redirect:/auth/reset-password";
        }
        session.removeAttribute("passwordResetAllowed");
        session.removeAttribute("resetUserIdConfirmed");
        ra.addFlashAttribute("success", "Password reset successful! Please login with your new password.");
        return "redirect:/auth/login";
    }
}
