package com.passmanager.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class PasswordGeneratorTest {
    private PasswordGeneratorUtil generator;

    @BeforeEach
    void setUp() { generator = new PasswordGeneratorUtil(); }

    @Test
    void testPasswordLength() {
        assertEquals(16, generator.generate(16, true, true, true, true, false).length());
    }

    @Test
    void testMinLength() {
        assertEquals(8, generator.generate(8, true, true, false, false, false).length());
    }

    @Test
    void testMaxLength() {
        assertEquals(64, generator.generate(64, true, true, true, true, false).length());
    }

    @Test
    void testExcludeSimilar() {
        for (int i = 0; i < 50; i++) {
            String p = generator.generate(32, true, true, true, false, true);
            assertFalse(p.contains("0") || p.contains("O") || p.contains("l")
                || p.contains("1") || p.contains("I"));
        }
    }

    @Test
    void testUniqueness() {
        String p1 = generator.generate(16, true, true, true, true, false);
        String p2 = generator.generate(16, true, true, true, true, false);
        assertNotEquals(p1, p2);
    }
}
