package com.passmanager.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.jupiter.api.Assertions.*;

class EncryptionTest {
    private EncryptionUtil encryptionUtil;
    private final String masterPassword = "TestMasterPassword@123";

    @BeforeEach
    void setUp() {
        encryptionUtil = new EncryptionUtil();
        ReflectionTestUtils.setField(encryptionUtil, "encryptionSecret", "TestSecretKey12345678901234567890");
    }

    @Test
    void testRoundtrip() {
        String original = "MySecretP@ssw0rd!";
        assertEquals(original, encryptionUtil.decrypt(encryptionUtil.encrypt(original, masterPassword), masterPassword));
    }

    @Test
    void testEncryptedDiffers() {
        assertNotEquals("MyPassword", encryptionUtil.encrypt("MyPassword", masterPassword));
    }

    @Test
    void testWrongPasswordFails() {
        String enc = encryptionUtil.encrypt("Secret", masterPassword);
        assertThrows(RuntimeException.class, () -> encryptionUtil.decrypt(enc, "WrongPassword123!"));
    }

    @Test
    void testSpecialChars() {
        String orig = "P@$$w0rd!#%&*";
        assertEquals(orig, encryptionUtil.decrypt(encryptionUtil.encrypt(orig, masterPassword), masterPassword));
    }

    @Test
    void testDifferentIVEachTime() {
        String enc1 = encryptionUtil.encrypt("Same", masterPassword);
        String enc2 = encryptionUtil.encrypt("Same", masterPassword);
        assertNotEquals(enc1, enc2, "Each encryption should use a different IV");
    }
}
