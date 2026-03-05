package com.passmanager.dto;

import java.util.List;

public class SecurityAuditDto {
    private long totalPasswords;
    private long weakPasswords;
    private long reusedPasswords;
    private long strongPasswords;
    private List<String> weakPasswordAccounts;
    private List<String> reusedPasswordAccounts;
    private int overallScore;
    private String overallGrade;

    public SecurityAuditDto() {}

    public long getTotalPasswords() { return totalPasswords; }
    public void setTotalPasswords(long totalPasswords) { this.totalPasswords = totalPasswords; }
    public long getWeakPasswords() { return weakPasswords; }
    public void setWeakPasswords(long weakPasswords) { this.weakPasswords = weakPasswords; }
    public long getReusedPasswords() { return reusedPasswords; }
    public void setReusedPasswords(long reusedPasswords) { this.reusedPasswords = reusedPasswords; }
    public long getStrongPasswords() { return strongPasswords; }
    public void setStrongPasswords(long strongPasswords) { this.strongPasswords = strongPasswords; }
    public List<String> getWeakPasswordAccounts() { return weakPasswordAccounts; }
    public void setWeakPasswordAccounts(List<String> weakPasswordAccounts) { this.weakPasswordAccounts = weakPasswordAccounts; }
    public List<String> getReusedPasswordAccounts() { return reusedPasswordAccounts; }
    public void setReusedPasswordAccounts(List<String> reusedPasswordAccounts) { this.reusedPasswordAccounts = reusedPasswordAccounts; }
    public int getOverallScore() { return overallScore; }
    public void setOverallScore(int overallScore) { this.overallScore = overallScore; }
    public String getOverallGrade() { return overallGrade; }
    public void setOverallGrade(String overallGrade) { this.overallGrade = overallGrade; }

    // Builder
    public static SecurityAuditDtoBuilder builder() { return new SecurityAuditDtoBuilder(); }

    public static class SecurityAuditDtoBuilder {
        private long totalPasswords;
        private long weakPasswords;
        private long reusedPasswords;
        private long strongPasswords;
        private List<String> weakPasswordAccounts;
        private List<String> reusedPasswordAccounts;
        private int overallScore;
        private String overallGrade;

        public SecurityAuditDtoBuilder totalPasswords(long v) { this.totalPasswords = v; return this; }
        public SecurityAuditDtoBuilder weakPasswords(long v) { this.weakPasswords = v; return this; }
        public SecurityAuditDtoBuilder reusedPasswords(long v) { this.reusedPasswords = v; return this; }
        public SecurityAuditDtoBuilder strongPasswords(long v) { this.strongPasswords = v; return this; }
        public SecurityAuditDtoBuilder weakPasswordAccounts(List<String> v) { this.weakPasswordAccounts = v; return this; }
        public SecurityAuditDtoBuilder reusedPasswordAccounts(List<String> v) { this.reusedPasswordAccounts = v; return this; }
        public SecurityAuditDtoBuilder overallScore(int v) { this.overallScore = v; return this; }
        public SecurityAuditDtoBuilder overallGrade(String v) { this.overallGrade = v; return this; }

        public SecurityAuditDto build() {
            SecurityAuditDto d = new SecurityAuditDto();
            d.totalPasswords = totalPasswords;
            d.weakPasswords = weakPasswords;
            d.reusedPasswords = reusedPasswords;
            d.strongPasswords = strongPasswords;
            d.weakPasswordAccounts = weakPasswordAccounts;
            d.reusedPasswordAccounts = reusedPasswordAccounts;
            d.overallScore = overallScore;
            d.overallGrade = overallGrade;
            return d;
        }
    }
}
