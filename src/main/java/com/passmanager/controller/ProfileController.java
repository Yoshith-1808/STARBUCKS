package com.passmanager.controller;

import com.passmanager.dto.ChangePasswordDto;
import com.passmanager.entity.User;
import com.passmanager.service.UserService;
import com.passmanager.service.VaultService;
import com.passmanager.entity.PasswordEntry;
import com.passmanager.dto.AccountAuditDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;
    private final VaultService vaultService;

    public ProfileController(UserService userService, VaultService vaultService) {
        this.userService = userService;
        this.vaultService = vaultService;
    }

    @GetMapping
    public String profilePage(Model model) {
        User user = userService.getCurrentUser();
        List<PasswordEntry> entries = vaultService.getAllEntries(user);

        long favoriteCount = entries.stream().filter(PasswordEntry::isFavorite).count();
        long totalAccounts = entries.size();

        model.addAttribute("user", user);
        model.addAttribute("entries", entries);
        model.addAttribute("totalAccounts", totalAccounts);
        model.addAttribute("favoriteCount", favoriteCount);
        model.addAttribute("auditUnlocked", false);
        return "profile/index";
    }

    @PostMapping("/audit")
    public String unlockDetailedAudit(@RequestParam String masterPassword, Model model, RedirectAttributes ra) {
        User user = userService.getCurrentUser();

        if (!userService.verifyMasterPassword(masterPassword)) {
            ra.addFlashAttribute("error", "Incorrect master password");
            return "redirect:/profile";
        }

        try {
            List<AccountAuditDto> detailedAudits = vaultService.generateDetailedAudit(user, masterPassword);
            long favoriteCount = detailedAudits.stream().filter(AccountAuditDto::isFavorite).count();

            model.addAttribute("user", user);
            model.addAttribute("entries", detailedAudits);
            model.addAttribute("totalAccounts", detailedAudits.size());
            model.addAttribute("favoriteCount", favoriteCount);
            model.addAttribute("auditUnlocked", true);

            return "profile/index";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Failed to unlock audit: " + e.getMessage());
            return "redirect:/profile";
        }
    }

    @PostMapping("/update")
    public String updateProfile(@RequestParam String firstName,
                                @RequestParam String lastName,
                                @RequestParam String email,
                                @RequestParam(required = false) String phoneNumber,
                                RedirectAttributes ra) {
        try {
            User updates = new User();
            updates.setFirstName(firstName);
            updates.setLastName(lastName);
            updates.setEmail(email);
            updates.setPhoneNumber(phoneNumber);
            userService.updateProfile(updates);
            ra.addFlashAttribute("success", "Profile updated successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/profile";
    }

    @GetMapping("/change-password")
    public String changePasswordPage(Model model) {
        model.addAttribute("dto", new ChangePasswordDto());
        return "profile/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@ModelAttribute ChangePasswordDto dto, RedirectAttributes ra) {
        if (!dto.getNewPassword().equals(dto.getConfirmNewPassword())) {
            ra.addFlashAttribute("error", "New passwords do not match");
            return "redirect:/profile/change-password";
        }
        try {
            userService.changeMasterPassword(dto.getCurrentPassword(), dto.getNewPassword());
            ra.addFlashAttribute("success", "Master password changed successfully!");
            return "redirect:/profile";
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/profile/change-password";
        }
    }
}
