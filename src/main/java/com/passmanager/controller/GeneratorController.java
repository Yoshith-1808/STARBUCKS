package com.passmanager.controller;

import com.passmanager.dto.PasswordEntryDto;
import com.passmanager.dto.PasswordGeneratorDto;
import com.passmanager.entity.User;
import com.passmanager.service.UserService;
import com.passmanager.service.VaultService;
import com.passmanager.util.PasswordGeneratorUtil;
import com.passmanager.util.PasswordStrengthUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/generator")
public class GeneratorController {

    private final PasswordGeneratorUtil generatorUtil;
    private final PasswordStrengthUtil strengthUtil;
    private final UserService userService;
    private final VaultService vaultService;

    public GeneratorController(PasswordGeneratorUtil generatorUtil, PasswordStrengthUtil strengthUtil,
                                UserService userService, VaultService vaultService) {
        this.generatorUtil = generatorUtil;
        this.strengthUtil = strengthUtil;
        this.userService = userService;
        this.vaultService = vaultService;
    }

    @GetMapping
    public String generatorPage(Model model) {
        model.addAttribute("dto", new PasswordGeneratorDto());
        return "generator/index";
    }

    @PostMapping("/generate")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> generate(@RequestBody PasswordGeneratorDto dto) {
        String password = generatorUtil.generate(
            dto.getLength(), dto.isUppercase(), dto.isLowercase(),
            dto.isDigits(), dto.isSpecial(), dto.isExcludeSimilar()
        );
        int score = strengthUtil.calculateScore(password);
        Map<String, Object> response = new HashMap<>();
        response.put("password", password);
        response.put("score", score);
        response.put("label", strengthUtil.getStrengthLabel(score));
        response.put("cssClass", strengthUtil.getStrengthClass(score));
        response.put("percent", strengthUtil.getStrengthPercent(score));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/save")
    public String saveToVault(@RequestParam String password,
                               @RequestParam String accountName,
                               @RequestParam(required = false) String websiteUrl,
                               @RequestParam(required = false) String username,
                               @RequestParam String masterPassword,
                               RedirectAttributes ra) {
        User user = userService.getCurrentUser();
        if (!userService.verifyMasterPassword(masterPassword)) {
            ra.addFlashAttribute("error", "Incorrect master password");
            return "redirect:/generator";
        }
        PasswordEntryDto dto = new PasswordEntryDto();
        dto.setAccountName(accountName);
        dto.setWebsiteUrl(websiteUrl);
        dto.setUsernameOrEmail(username);
        dto.setPassword(password);
        vaultService.addEntry(user, dto, masterPassword);
        ra.addFlashAttribute("success", "Password saved to vault!");
        return "redirect:/vault";
    }
}
