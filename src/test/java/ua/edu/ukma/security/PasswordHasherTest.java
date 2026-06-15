package ua.edu.ukma.security;

import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class PasswordHasherTest {

    @Test
    void createSaltReturnsBase64EncodedSixteenByteSalt() {
        String salt = PasswordHasher.createSalt();
        byte[] decoded = Base64.getDecoder().decode(salt);

        assertEquals(16, decoded.length);
    }

    @Test
    void hashPasswordIsStableForSamePasswordAndSalt() {
        String salt = PasswordHasher.createSalt();
        String firstHash = PasswordHasher.hashPassword("secret", salt);
        String secondHash = PasswordHasher.hashPassword("secret", salt);

        assertEquals(firstHash, secondHash);
    }

    @Test
    void verifyPasswordReturnsTrueForCorrectPassword() {
        String salt = PasswordHasher.createSalt();
        String hash = PasswordHasher.hashPassword("secret", salt);

        assertTrue(PasswordHasher.verifyPassword("secret", salt, hash));
    }

    @Test
    void verifyPasswordReturnsFalseForWrongPassword() {
        String salt = PasswordHasher.createSalt();
        String hash = PasswordHasher.hashPassword("secret", salt);

        assertFalse(PasswordHasher.verifyPassword("wrong", salt, hash));
    }

    @Test
    void samePasswordWithDifferentSaltsProducesDifferentHashes() {
        String firstSalt = PasswordHasher.createSalt();
        String secondSalt = PasswordHasher.createSalt();
        String firstHash = PasswordHasher.hashPassword("secret", firstSalt);
        String secondHash = PasswordHasher.hashPassword("secret", secondSalt);

        assertNotEquals(firstHash, secondHash);
    }
}
