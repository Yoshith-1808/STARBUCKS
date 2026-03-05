package com.passmanager.controller;

import com.passmanager.entity.PasswordEntry;
import com.passmanager.entity.User;
import com.passmanager.service.UserService;
import com.passmanager.service.VaultService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final UserService userService;
    private final VaultService vaultService;

    public DashboardController(UserService userService, VaultService vaultService) {
        this.userService = userService;
        this.vaultService = vaultService;
    }

    @GetMapping
    public String dashboard(Model model) {
        User user = userService.getCurrentUser();
        List<PasswordEntry> allEntries = vaultService.getAllEntries(user);
        List<PasswordEntry> recent = allEntries.stream().limit(5).toList();
        List<PasswordEntry> favorites = vaultService.getFavorites(user);
        long categoryCount = allEntries.stream().map(PasswordEntry::getCategory).distinct().count();

        model.addAttribute("user", user);
        model.addAttribute("totalPasswords", allEntries.size());
        model.addAttribute("recentEntries", recent);
        model.addAttribute("favoriteCount", favorites.size());
        model.addAttribute("categoryCount", categoryCount);
        return "dashboard/index";
    }
}
