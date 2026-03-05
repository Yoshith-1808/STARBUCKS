package com.passmanager.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "verification_codes")
public class VerificationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CodeType type;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private boolean used = false;

    private LocalDateTime createdAt = LocalDateTime.now();

    public VerificationCode() {}

    public enum CodeType { PASSWORD_RESET, SENSITIVE_ACTION, EMAIL_VERIFY }

    public boolean isExpired() { return LocalDateTime.now().isAfter(expiresAt) || used; }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public CodeType getType() { return type; }
    public void setType(CodeType type) { this.type = type; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Builder
    public static VerificationCodeBuilder builder() { return new VerificationCodeBuilder(); }

    public static class VerificationCodeBuilder {
        private User user;
        private String code;
        private CodeType type;
        private LocalDateTime expiresAt;

        public VerificationCodeBuilder user(User user) { this.user = user; return this; }
        public VerificationCodeBuilder code(String code) { this.code = code; return this; }
        public VerificationCodeBuilder type(CodeType type) { this.type = type; return this; }
        public VerificationCodeBuilder expiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; return this; }

        public VerificationCode build() {
            VerificationCode vc = new VerificationCode();
            vc.user = this.user;
            vc.code = this.code;
            vc.type = this.type;
            vc.expiresAt = this.expiresAt;
            return vc;
        }
    }
}
