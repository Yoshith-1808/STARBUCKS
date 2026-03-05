package com.passmanager.controller;

import com.passmanager.entity.User;
import com.passmanager.service.EmailService;
import com.passmanager.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/forgot-password")
public class ForgotPasswordController {

    private final UserService userService;
    private final EmailService emailService;

    public ForgotPasswordController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    // --- 1. Select Method ---
    @GetMapping
    public String showMethodSelection() {
        return "auth/forgot-password-method";
    }

    // --- 2. Security Questions Flow ---
    @GetMapping("/questions")
    public String showQuestionsForm() {
        return "auth/forgot-password-questions";
    }

    @PostMapping("/questions")
    public String processQuestions(@RequestParam String email,
                                   @RequestParam String answer1,
                                   @RequestParam String answer2,
                                   HttpSession session,
                                   RedirectAttributes ra) {
        Optional<User> userOpt = userService.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (userService.verifySecurityAnswers(user, answer1, answer2)) {
                session.setAttribute("verifiedResetUser", email);
                return "redirect:/forgot-password/reset";
            }
        }
        ra.addFlashAttribute("error", "Invalid email or incorrect answers.");
        return "redirect:/forgot-password/questions";
    }

    // --- 3. Email OTP Flow ---
    @GetMapping("/email")
    public String showEmailForm() {
        return "auth/forgot-password-email";
    }

    @PostMapping("/send-otp")
    public String sendOtp(@RequestParam String email, HttpSession session, RedirectAttributes ra) {
        Optional<User> userOpt = userService.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String otp = userService.generateResetOtp(user);
            try {
                emailService.sendPasswordResetOtp(user.getEmail(), otp);
                session.setAttribute("resetEmail", email);
            } catch (Exception e) {
                e.printStackTrace();
                ra.addFlashAttribute("error", "Failed to send email. Check SMTP configuration.");
                return "redirect:/forgot-password/email";
            }
        }
        // Always redirect to verify-otp to prevent email enumeration, even if email
        // wasn't found
        ra.addFlashAttribute("success", "If an account with that email exists, an OTP has been sent.");
        return "redirect:/forgot-password/verify-otp";
    }

    @GetMapping("/verify-otp")
    public String showVerifyOtpForm(HttpSession session, Model model) {
        if (session.getAttribute("resetEmail") == null) {
            return "redirect:/forgot-password/email";
        }
        return "auth/forgot-password-verify-otp";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam String otp, HttpSession session, RedirectAttributes ra) {
        String email = (String) session.getAttribute("resetEmail");
        if (email == null)
            return "redirect:/forgot-password/email";

        Optional<User> userOpt = userService.findByEmail(email);
        if (userOpt.isPresent() && userService.verifyResetOtp(userOpt.get(), otp)) {
            session.setAttribute("verifiedResetUser", email);
            return "redirect:/forgot-password/reset";
        }

        ra.addFlashAttribute("error", "Invalid or expired OTP");
        return "redirect:/forgot-password/verify-otp";
    }

    // --- 4. Final Reset Flow ---
    @GetMapping("/reset")
    public String showResetForm(HttpSession session) {
        if (session.getAttribute("verifiedResetUser") == null) {
            return "redirect:/forgot-password";
        }
        return "auth/forgot-password-reset";
    }

    @PostMapping("/reset")
    public String processReset(@RequestParam String newPassword,
                               @RequestParam String confirmPassword,
                               HttpSession session,
                               RedirectAttributes ra) {

        String email = (String) session.getAttribute("verifiedResetUser");
        if (email == null) {
            return "redirect:/forgot-password";
        }

        if (!newPassword.equals(confirmPassword)) {
            ra.addFlashAttribute("error", "Passwords do not match");
            return "redirect:/forgot-password/reset";
        }

        Optional<User> userOpt = userService.findByEmail(email);
        if (userOpt.isPresent()) {
            userService.resetPassword(userOpt.get(), newPassword);
            session.removeAttribute("resetEmail");
            session.removeAttribute("verifiedResetUser");
            ra.addFlashAttribute("success", "Password has been reset successfully. Please log in.");
            return "redirect:/auth/login";
        }

        return "redirect:/forgot-password";
    }
}
