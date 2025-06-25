package user.password;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordHasherTest {

    @Test
    void testVerifyValidPassword() {
        PasswordHasher passwordHasher = new PasswordHasher();
        String password = "securePassword123!";
        String hash = passwordHasher.hash(password);

        boolean isVerified = passwordHasher.verify(password, hash);

        assertTrue(isVerified, "Valid password should successfully verify against the stored hash.");
    }

    @Test
    void testVerifyInvalidPassword() {
        PasswordHasher passwordHasher = new PasswordHasher();
        String password = "securePassword123!";
        String wrongPassword = "wrongPassword456";
        String hash = passwordHasher.hash(password);

        boolean isVerified = passwordHasher.verify(wrongPassword, hash);

        assertFalse(isVerified, "Invalid password should not verify against the stored hash.");
    }

    @Test
    void testVerifyEmptyPassword() {
        PasswordHasher passwordHasher = new PasswordHasher();
        String password = "";
        String hash = passwordHasher.hash(password);

        boolean isVerified = passwordHasher.verify(password, hash);

        assertTrue(isVerified, "Empty password should successfully verify against the stored hash.");
    }

    @Test
    void testVerifySpecialCharactersInPassword() {
        PasswordHasher passwordHasher = new PasswordHasher();
        String password = "!@#$%^&*()_+=-{}[]|\\;:'\",.<>?/`~";
        String hash = passwordHasher.hash(password);

        boolean isVerified = passwordHasher.verify(password, hash);

        assertTrue(isVerified, "Password with special characters should successfully verify against the stored hash.");
    }
}