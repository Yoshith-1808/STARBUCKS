package com.passmanager.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "password_entries")
public class PasswordEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String accountName;

    private String websiteUrl;
    private String usernameOrEmail;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String encryptedPassword;

    @Enumerated(EnumType.STRING)
    private Category category = Category.OTHER;

    @Column(columnDefinition = "TEXT")
    private String encryptedNotes;

    private boolean favorite = false;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;
    private LocalDateTime lastAccessed;

    @Transient
    private String decryptedPassword;

    public PasswordEntry() {
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum Category {
        SOCIAL_MEDIA, BANKING, EMAIL, SHOPPING, WORK, OTHER
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
        return usernameOrEmail;
    }

    public void setUsernameOrEmail(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getEncryptedNotes() {
        return encryptedNotes;
    }

    public void setEncryptedNotes(String encryptedNotes) {
        this.encryptedNotes = encryptedNotes;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getLastAccessed() {
        return lastAccessed;
    }

    public void setLastAccessed(LocalDateTime lastAccessed) {
        this.lastAccessed = lastAccessed;
    }

    public String getDecryptedPassword() {
        return decryptedPassword;
    }

    public void setDecryptedPassword(String decryptedPassword) {
        this.decryptedPassword = decryptedPassword;
    }

    // Builder
    public static PasswordEntryBuilder builder() {
        return new PasswordEntryBuilder();
    }

    public static class PasswordEntryBuilder {
        private User user;
        private String accountName;
        private String websiteUrl;
        private String usernameOrEmail;
        private String encryptedPassword;
        private Category category = Category.OTHER;
        private String encryptedNotes;
        private boolean favorite = false;

        public PasswordEntryBuilder user(User user) {
            this.user = user;
            return this;
        }

        public PasswordEntryBuilder accountName(String accountName) {
            this.accountName = accountName;
            return this;
        }

        public PasswordEntryBuilder websiteUrl(String websiteUrl) {
            this.websiteUrl = websiteUrl;
            return this;
        }

        public PasswordEntryBuilder usernameOrEmail(String usernameOrEmail) {
            this.usernameOrEmail = usernameOrEmail;
            return this;
        }

        public PasswordEntryBuilder encryptedPassword(String encryptedPassword) {
            this.encryptedPassword = encryptedPassword;
            return this;
        }

        public PasswordEntryBuilder category(Category category) {
            this.category = category;
            return this;
        }

        public PasswordEntryBuilder encryptedNotes(String encryptedNotes) {
            this.encryptedNotes = encryptedNotes;
            return this;
        }

        public PasswordEntryBuilder favorite(boolean favorite) {
            this.favorite = favorite;
            return this;
        }

        public PasswordEntry build() {
            PasswordEntry e = new PasswordEntry();
            e.user = this.user;
            e.accountName = this.accountName;
            e.websiteUrl = this.websiteUrl;
            e.usernameOrEmail = this.usernameOrEmail;
            e.encryptedPassword = this.encryptedPassword;
            e.category = this.category;
            e.encryptedNotes = this.encryptedNotes;
            e.favorite = this.favorite;
            return e;
        }
    }
}
