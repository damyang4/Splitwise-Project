package command.implementation;

import command.session.SessionManager;
import exception.InvalidCommandArgumentsException;
import exception.login.InvalidUsernameOrPasswordException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import user.*;
import user.friend.FriendNotification;
import user.group.GroupManager;
import user.group.GroupNotification;
import user.password.PasswordHasher;

import java.nio.channels.SocketChannel;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginCommandTest {

    private RegisteredUsers registeredUsers;
    private Map<SocketChannel, String> clientSessions;
    private FriendNotification friendNotification;
    private GroupNotification groupNotification;
    private GroupManager groupManager;
    private SessionManager sessionManager;
    private PasswordHasher passwordHasher;
    private LoginCommand loginCommand;

    @BeforeEach
    void setUp() {
        registeredUsers = mock(RegisteredUsers.class);
        clientSessions = new HashMap<>();
        friendNotification = mock(FriendNotification.class);
        groupNotification = mock(GroupNotification.class);
        groupManager = mock(GroupManager.class);
        sessionManager = mock(SessionManager.class);
        passwordHasher = mock(PasswordHasher.class);

        loginCommand = new LoginCommand(registeredUsers, clientSessions, friendNotification, groupNotification, groupManager, sessionManager, passwordHasher);
    }

    @Test
    void executeWithValidCredentialsShouldReturnSuccessfulLoginMessage() {
        User user = mock(User.class);
        when(user.username()).thenReturn("testUser");
        when(user.password()).thenReturn("hashedPassword");
        when(registeredUsers.getUser("testUser")).thenReturn(user);

        when(passwordHasher.verify("password", "hashedPassword")).thenReturn(true);

        String sessionToken = "newSessionToken";
        when(sessionManager.login(user)).thenReturn(sessionToken);

        SocketChannel socketChannel = mock(SocketChannel.class);
        String result = loginCommand.execute(null, new String[]{"testUser", "password"}, socketChannel);

        assertEquals("Login successful. Welcome, testUser\nNo notifications to show.", result);
        assertEquals(sessionToken, clientSessions.get(socketChannel));
    }

    @Test
    void executeWithInvalidArgumentsShouldThrowInvalidCommandArgumentsException() {
        SocketChannel socketChannel = mock(SocketChannel.class);

        assertThrows(InvalidCommandArgumentsException.class, () -> loginCommand.execute(null, new String[]{"testUser"}, socketChannel));
    }

    @Test
    void executeWithInvalidCredentialsShouldThrowInvalidUsernameOrPasswordException() {
        when(registeredUsers.getUser("testUser")).thenReturn(null);

        SocketChannel socketChannel = mock(SocketChannel.class);

        assertThrows(InvalidUsernameOrPasswordException.class, () -> loginCommand.execute(null, new String[]{"testUser", "password"}, socketChannel));
    }

    @Test
    void executeWithValidCredentialsAndFriendNotificationsShouldIncludeFriendNotificationsInResponse() {
        User user = mock(User.class);
        when(user.username()).thenReturn("testUser");
        when(user.password()).thenReturn("hashedPassword");
        when(registeredUsers.getUser("testUser")).thenReturn(user);

        when(passwordHasher.verify("password", "hashedPassword")).thenReturn(true);

        String sessionToken = "newSessionToken";
        when(sessionManager.login(user)).thenReturn(sessionToken);

        Notification notification1 = mock(Notification.class);
        when(notification1.toString()).thenReturn("Friend request from Alice");
        when(friendNotification.getNotifications(user)).thenReturn(Collections.singletonList(notification1));

        SocketChannel socketChannel = mock(SocketChannel.class);
        String result = loginCommand.execute(null, new String[]{"testUser", "password"}, socketChannel);

        assertTrue(result.contains("Login successful. Welcome, testUser"));
        assertTrue(result.contains("*** Notifications ***"));
        assertTrue(result.contains("Friends:\nFriend request from Alice"));
        assertEquals(sessionToken, clientSessions.get(socketChannel));
    }
}
