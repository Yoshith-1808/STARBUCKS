package com.passmanager.dto;

import com.passmanager.entity.PasswordEntry;

public class PasswordEntryDto {
    private Long id;
    private String accountName;
    private String websiteUrl;
    private String usernameOrEmail;
    private String password;
    private PasswordEntry.Category category;
    private String notes;
    private boolean favorite;

    public PasswordEntryDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }
    public String getWebsiteUrl() { return websiteUrl; }
    public void setWebsiteUrl(String websiteUrl) { this.websiteUrl = websiteUrl; }
    public String getUsernameOrEmail() { return usernameOrEmail; }
    public void setUsernameOrEmail(String usernameOrEmail) { this.usernameOrEmail = usernameOrEmail; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public PasswordEntry.Category getCategory() { return category; }
    public void setCategory(PasswordEntry.Category category) { this.category = category; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public boolean isFavorite() { return favorite; }
    public void setFavorite(boolean favorite) { this.favorite = favorite; }
}
