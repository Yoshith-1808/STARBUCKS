package com.passmanager.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class PasswordStrengthTest {
    private PasswordStrengthUtil util;

    @BeforeEach
    void setUp() { util = new PasswordStrengthUtil(); }

    @Test
    void testWeakPassword() {
        // "abc" → only lowercase matches (score=1), no length/digit/special → score=1 ✅
        int score = util.calculateScore("abc");
        assertTrue(score <= 2, "Short simple password should score <= 2, got: " + score);
        assertEquals("Weak", util.getStrengthLabel(score));
    }

    @Test
    void testStrongPassword() {
        // Has length>=16, upper, lower, digit, special, no-repeats → score >= 6 ✅
        int score = util.calculateScore("SecurePass@2024!");
        assertTrue(score >= 6, "Complex password should score >= 6, got: " + score);
        assertNotEquals("Weak", util.getStrengthLabel(score));
    }

    @Test
    void testVeryStrongLabel() {
        assertEquals("Very Strong", util.getStrengthLabel(7));
    }

    @Test
    void testEmptyPassword() {
        assertEquals(0, util.calculateScore(""));
        assertEquals(0, util.calculateScore(null));
    }

    @Test
    void testIsWeak() {
        assertTrue(util.isWeak("abc"));
        assertFalse(util.isWeak("SecureP@ss123!Long"));
    }
}