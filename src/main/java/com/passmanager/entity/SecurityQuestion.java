package com.passmanager.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "security_questions")
public class SecurityQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String question;

    @Column(nullable = false)
    private String answerHash;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;

    public SecurityQuestion() {}

    @PreUpdate
    public void preUpdate() { this.updatedAt = LocalDateTime.now(); }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public String getAnswerHash() { return answerHash; }
    public void setAnswerHash(String answerHash) { this.answerHash = answerHash; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Builder
    public static SecurityQuestionBuilder builder() { return new SecurityQuestionBuilder(); }

    public static class SecurityQuestionBuilder {
        private User user;
        private String question;
        private String answerHash;

        public SecurityQuestionBuilder user(User user) { this.user = user; return this; }
        public SecurityQuestionBuilder question(String question) { this.question = question; return this; }
        public SecurityQuestionBuilder answerHash(String answerHash) { this.answerHash = answerHash; return this; }

        public SecurityQuestion build() {
            SecurityQuestion sq = new SecurityQuestion();
            sq.user = this.user;
            sq.question = this.question;
            sq.answerHash = this.answerHash;
            return sq;
        }
    }
}
