package com.passmanager.util;

import org.springframework.stereotype.Component;

@Component
public class PasswordStrengthUtil {

    public int calculateScore(String password) {
        if (password == null || password.isEmpty()) return 0;
        int score = 0;
        if (password.length() >= 8) score++;
        if (password.length() >= 12) score++;
        if (password.length() >= 16) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*[0-9].*")) score++;
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) score++;
        if (!password.matches(".*(.)\\1{2,}.*")) score++;
        return Math.min(score, 8);
    }

    public String getStrengthLabel(int score) {
        if (score <= 2) return "Weak";
        if (score <= 4) return "Medium";
        if (score <= 6) return "Strong";
        return "Very Strong";
    }

    public String getStrengthClass(int score) {
        if (score <= 2) return "danger";
        if (score <= 4) return "warning";
        if (score <= 6) return "success";
        return "primary";
    }

    public int getStrengthPercent(int score) {
        return (score * 100) / 8;
    }

    public boolean isWeak(String password) {
        return calculateScore(password) <= 2;
    }
}
