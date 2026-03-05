package com.passmanager.controller;

import com.passmanager.dto.SecurityAuditDto;
import com.passmanager.entity.User;
import com.passmanager.service.UserService;
import com.passmanager.service.VaultService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/security")
public class SecurityController {

    private final UserService userService;
    private final VaultService vaultService;

    public SecurityController(UserService userService, VaultService vaultService) {
        this.userService = userService;
        this.vaultService = vaultService;
    }

    @GetMapping("/audit")
    public String auditPage(Model model) {
        return "security/audit-auth";
    }

    @PostMapping("/audit")
    public String runAudit(@RequestParam String masterPassword, Model model) {
        User user = userService.getCurrentUser();
        if (!userService.verifyMasterPassword(masterPassword)) {
            model.addAttribute("error", "Incorrect master password");
            return "security/audit-auth";
        }
        try {
            SecurityAuditDto audit = vaultService.generateAudit(user, masterPassword);
            model.addAttribute("audit", audit);
            return "security/audit-result";
        } catch (Exception e) {
            model.addAttribute("error", "Error running audit: " + e.getMessage());
            return "security/audit-auth";
        }
    }

    @GetMapping("/2fa")
    public String twoFactorPage(Model model) {
        User user = userService.getCurrentUser();
        model.addAttribute("user", user);
        model.addAttribute("twoFactorEnabled", user.isTwoFactorEnabled());
        return "security/two-factor";
    }

    @PostMapping("/2fa/enable")
    public String enable2FA(RedirectAttributes ra) {
        String secret = generateTotpSecret();
        userService.toggle2FA(true, secret);
        ra.addFlashAttribute("success", "2FA enabled! Your TOTP secret has been saved.");
        return "redirect:/security/2fa";
    }

    @PostMapping("/2fa/disable")
    public String disable2FA(@RequestParam String masterPassword, RedirectAttributes ra) {
        if (!userService.verifyMasterPassword(masterPassword)) {
            ra.addFlashAttribute("error", "Incorrect master password");
            return "redirect:/security/2fa";
        }
        userService.toggle2FA(false, null);
        ra.addFlashAttribute("success", "2FA disabled.");
        return "redirect:/security/2fa";
    }

    private String generateTotpSecret() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
        StringBuilder sb = new StringBuilder();
        java.util.Random r = new java.util.Random();
        for (int i = 0; i < 32; i++)
            sb.append(chars.charAt(r.nextInt(chars.length())));
        return sb.toString();
    }
}
