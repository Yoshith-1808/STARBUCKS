package com.passmanager.dto;

public class PasswordGeneratorDto {
    private int length = 16;
    private boolean uppercase = true;
    private boolean lowercase = true;
    private boolean digits = true;
    private boolean special = true;
    private boolean excludeSimilar = false;
    private String generatedPassword;

    public PasswordGeneratorDto() {}

    public int getLength() { return length; }
    public void setLength(int length) { this.length = length; }
    public boolean isUppercase() { return uppercase; }
    public void setUppercase(boolean uppercase) { this.uppercase = uppercase; }
    public boolean isLowercase() { return lowercase; }
    public void setLowercase(boolean lowercase) { this.lowercase = lowercase; }
    public boolean isDigits() { return digits; }
    public void setDigits(boolean digits) { this.digits = digits; }
    public boolean isSpecial() { return special; }
    public void setSpecial(boolean special) { this.special = special; }
    public boolean isExcludeSimilar() { return excludeSimilar; }
    public void setExcludeSimilar(boolean excludeSimilar) { this.excludeSimilar = excludeSimilar; }
    public String getGeneratedPassword() { return generatedPassword; }
    public void setGeneratedPassword(String generatedPassword) { this.generatedPassword = generatedPassword; }
}
