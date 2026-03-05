package com.passmanager.controller;

import com.passmanager.dto.PasswordEntryDto;
import com.passmanager.entity.PasswordEntry;
import com.passmanager.entity.User;
import com.passmanager.service.UserService;
import com.passmanager.service.VaultService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.*;

@Controller
@RequestMapping("/vault")
public class VaultController {

    private final UserService userService;
    private final VaultService vaultService;

    public VaultController(UserService userService, VaultService vaultService) {
        this.userService = userService;
        this.vaultService = vaultService;
    }

    @GetMapping
    public String vaultList(@RequestParam(required = false) String search,
                            @RequestParam(required = false) String category,
                            @RequestParam(required = false) String sort,
                            Model model) {
        User user = userService.getCurrentUser();
        List<PasswordEntry> entries;

        if (search != null && !search.isBlank()) {
            entries = vaultService.searchEntries(user, search);
            model.addAttribute("search", search);
        } else if (category != null && !category.isBlank()) {
            try {
                entries = vaultService.filterByCategory(user, PasswordEntry.Category.valueOf(category));
                model.addAttribute("selectedCategory", category);
            } catch (IllegalArgumentException e) {
                entries = vaultService.getAllEntries(user);
            }
        } else {
            entries = vaultService.getAllEntries(user);
        }

        if ("name".equals(sort)) {
            entries = entries.stream().sorted(Comparator.comparing(PasswordEntry::getAccountName)).toList();
        }

        model.addAttribute("entries", entries);
        model.addAttribute("categories", PasswordEntry.Category.values());
        model.addAttribute("user", user);
        return "vault/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("entryDto", new PasswordEntryDto());
        model.addAttribute("categories", PasswordEntry.Category.values());
        return "vault/form";
    }

    @PostMapping("/add")
    public String addEntry(@ModelAttribute PasswordEntryDto dto,
                           @RequestParam String masterPassword,
                           RedirectAttributes ra, Model model) {
        User user = userService.getCurrentUser();
        if (!userService.verifyMasterPassword(masterPassword)) {
            model.addAttribute("error", "Incorrect master password");
            model.addAttribute("entryDto", dto);
            model.addAttribute("categories", PasswordEntry.Category.values());
            return "vault/form";
        }
        try {
            vaultService.addEntry(user, dto, masterPassword);
            ra.addFlashAttribute("success", "Password entry added successfully!");
            return "redirect:/vault";
        } catch (Exception e) {
            model.addAttribute("error", "Error adding entry: " + e.getMessage());
            model.addAttribute("entryDto", dto);
            model.addAttribute("categories", PasswordEntry.Category.values());
            return "vault/form";
        }
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        User user = userService.getCurrentUser();
        Optional<PasswordEntry> entryOpt = vaultService.findById(id, user);
        if (entryOpt.isEmpty()) {
            ra.addFlashAttribute("error", "Entry not found");
            return "redirect:/vault";
        }
        PasswordEntry entry = entryOpt.get();
        PasswordEntryDto dto = new PasswordEntryDto();
        dto.setId(entry.getId());
        dto.setAccountName(entry.getAccountName());
        dto.setWebsiteUrl(entry.getWebsiteUrl());
        dto.setUsernameOrEmail(entry.getUsernameOrEmail());
        dto.setCategory(entry.getCategory());
        dto.setFavorite(entry.isFavorite());
        model.addAttribute("entryDto", dto);
        model.addAttribute("categories", PasswordEntry.Category.values());
        model.addAttribute("editing", true);
        model.addAttribute("entryId", id);
        return "vault/form";
    }

    @PostMapping("/edit/{id}")
    public String updateEntry(@PathVariable Long id,
                              @ModelAttribute PasswordEntryDto dto,
                              @RequestParam String masterPassword,
                              RedirectAttributes ra, Model model) {
        User user = userService.getCurrentUser();
        if (!userService.verifyMasterPassword(masterPassword)) {
            model.addAttribute("error", "Incorrect master password");
            model.addAttribute("entryDto", dto);
            model.addAttribute("categories", PasswordEntry.Category.values());
            model.addAttribute("editing", true);
            model.addAttribute("entryId", id);
            return "vault/form";
        }
        try {
            vaultService.updateEntry(id, user, dto, masterPassword);
            ra.addFlashAttribute("success", "Entry updated successfully!");
            return "redirect:/vault";
        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
            model.addAttribute("entryDto", dto);
            model.addAttribute("categories", PasswordEntry.Category.values());
            model.addAttribute("editing", true);
            model.addAttribute("entryId", id);
            return "vault/form";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteEntry(@PathVariable Long id, RedirectAttributes ra) {
        User user = userService.getCurrentUser();
        try {
            vaultService.deleteEntry(id, user);
            ra.addFlashAttribute("success", "Entry deleted.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error deleting entry.");
        }
        return "redirect:/vault";
    }

    @GetMapping("/view/{id}")
    public String viewEntry(@PathVariable Long id, Model model, RedirectAttributes ra) {
        User user = userService.getCurrentUser();
        Optional<PasswordEntry> entryOpt = vaultService.findById(id, user);
        if (entryOpt.isEmpty()) {
            ra.addFlashAttribute("error", "Entry not found");
            return "redirect:/vault";
        }
        model.addAttribute("entry", entryOpt.get());
        model.addAttribute("requiresAuth", true);
        return "vault/view";
    }

    @PostMapping("/view/{id}/reveal")
    public String revealPassword(@PathVariable Long id,
                                 @RequestParam String masterPassword,
                                 Model model) {
        User user = userService.getCurrentUser();
        Optional<PasswordEntry> entryOpt = vaultService.findById(id, user);
        if (entryOpt.isEmpty())
            return "redirect:/vault";

        PasswordEntry entry = entryOpt.get();
        if (!userService.verifyMasterPassword(masterPassword)) {
            model.addAttribute("entry", entry);
            model.addAttribute("requiresAuth", true);
            model.addAttribute("error", "Incorrect master password");
            return "vault/view";
        }
        try {
            String decrypted = vaultService.decryptPassword(entry, masterPassword);
            String notes = vaultService.decryptNotes(entry, masterPassword);
            model.addAttribute("entry", entry);
            model.addAttribute("decryptedPassword", decrypted);
            model.addAttribute("decryptedNotes", notes);
            model.addAttribute("revealed", true);
            model.addAttribute("requiresAuth", false);
        } catch (Exception e) {
            model.addAttribute("entry", entry);
            model.addAttribute("requiresAuth", true);
            model.addAttribute("error",
                    "Failed to decrypt entry. The master password might be correct, but the encryption key may have changed.");
        }
        return "vault/view";
    }

    @PostMapping("/toggle-favorite/{id}")
    public String toggleFavorite(@PathVariable Long id, RedirectAttributes ra) {
        User user = userService.getCurrentUser();
        vaultService.toggleFavorite(id, user);
        ra.addFlashAttribute("success", "Favorite updated.");
        return "redirect:/vault";
    }

    @GetMapping("/favorites")
    public String favorites(Model model) {
        User user = userService.getCurrentUser();
        model.addAttribute("entries", vaultService.getFavorites(user));
        model.addAttribute("user", user);
        return "vault/favorites";
    }

    @GetMapping("/export")
    public String exportPage() {
        return "vault/export";
    }

    @PostMapping("/export")
    @ResponseBody
    public ResponseEntity<String> exportVault(@RequestParam String masterPassword) {
        User user = userService.getCurrentUser();
        if (!userService.verifyMasterPassword(masterPassword)) {
            return ResponseEntity.badRequest().body("Incorrect master password");
        }
        String encrypted = vaultService.exportVault(user, masterPassword);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=vault-backup.enc")
                .body(encrypted);
    }
}
