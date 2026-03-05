package com.passmanager.util;

import org.springframework.stereotype.Component;
import java.security.SecureRandom;

@Component
public class PasswordGeneratorUtil {

    private static final String UPPERCASE_NO_SIM = "ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static final String LOWERCASE_NO_SIM = "abcdefghjkmnpqrstuvwxyz";
    private static final String DIGITS_NO_SIM = "23456789";
    private static final String SPECIAL = "!@#$%^&*()_+-=[]{}|;:,.<>?";
    private static final String UPPERCASE_ALL = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE_ALL = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS_ALL = "0123456789";

    private final SecureRandom random = new SecureRandom();

    public String generate(int length, boolean uppercase, boolean lowercase,
                           boolean digits, boolean special, boolean excludeSimilar) {
        StringBuilder charset = new StringBuilder();
        if (uppercase) charset.append(excludeSimilar ? UPPERCASE_NO_SIM : UPPERCASE_ALL);
        if (lowercase) charset.append(excludeSimilar ? LOWERCASE_NO_SIM : LOWERCASE_ALL);
        if (digits) charset.append(excludeSimilar ? DIGITS_NO_SIM : DIGITS_ALL);
        if (special) charset.append(SPECIAL);
        if (charset.length() == 0) charset.append(LOWERCASE_ALL);

        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++) {
            password.append(charset.charAt(random.nextInt(charset.length())));
        }
        return password.toString();
    }
}
