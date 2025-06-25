package command.session;

import org.junit.jupiter.api.Test;
import user.User;

import static org.junit.jupiter.api.Assertions.*;

class SessionManagerTest {

    @Test
    void loginShouldGenerateUniqueSessionTokenForNewUser() {
        SessionManager sessionManager = new SessionManager();
        User user = new User("testUser", "password123");

        String token = sessionManager.login(user);

        assertNotNull(token);
        assertEquals("testUser", sessionManager.getUsername(token));
    }

    @Test
    void loginShouldStoreSessionTokenInActiveSessions() {
        SessionManager sessionManager = new SessionManager();
        User user = new User("testUser", "password123");

        String token = sessionManager.login(user);

        assertTrue(sessionManager.isAuthenticated(token));
    }

    @Test
    void loginShouldGenerateDifferentTokensForMultipleLogins() {
        SessionManager sessionManager = new SessionManager();
        User user1 = new User("user1", "password1");
        User user2 = new User("user2", "password2");

        String token1 = sessionManager.login(user1);
        String token2 = sessionManager.login(user2);

        assertNotNull(token1);
        assertNotNull(token2);
        assertNotEquals(token1, token2);
    }
}