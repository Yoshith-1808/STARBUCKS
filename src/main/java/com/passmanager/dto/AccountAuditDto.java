package com.passmanager.dto;

public class AccountAuditDto {
    private Long id;
    private String accountName;
    private String websiteUrl;
    private String username;
    private String category;
    private boolean isFavorite;

    // Security details
    private boolean isWeak;
    private boolean isReused;

    // UI Helper
    public String getSecurityBadge() {
        if (isReused)
            return "danger";
        if (isWeak)
            return "warning";
        return "success";
    }

    public String getSecurityLabel() {
        if (isReused)
            return "Reused";
        if (isWeak)
            return "Weak";
        return "Strong";
    }

    public AccountAuditDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public String getUsernameOrEmail() {
        return username;
    }

    public void setUsernameOrEmail(String username) {
        this.username = username;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public boolean isWeak() {
        return isWeak;
    }

    public void setWeak(boolean isWeak) {
        this.isWeak = isWeak;
    }

    public boolean isReused() {
        return isReused;
    }

    public void setReused(boolean isReused) {
        this.isReused = isReused;
    }
}
