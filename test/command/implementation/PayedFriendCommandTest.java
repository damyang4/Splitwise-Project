package command.implementation;

import command.session.SessionManager;
import exception.InvalidCommandArgumentsException;
import exception.UserNotLoggedInException;
import exception.split.UserNotInFriendListException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import user.RegisteredUsers;
import user.User;
import user.friend.FriendDebt;
import user.friend.FriendManager;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PayedFriendCommandTest {

    private RegisteredUsers registeredUsers;
    private FriendManager friendManager;
    private FriendDebt friendDebt;
    private SessionManager sessionManager;
    private SocketChannel clientChannel;

    @BeforeEach
    void setUp() {
        registeredUsers = mock(RegisteredUsers.class);
        friendManager = mock(FriendManager.class);
        friendDebt = mock(FriendDebt.class);
        sessionManager = mock(SessionManager.class);
        clientChannel = mock(SocketChannel.class);
    }

    @Test
    void executeShouldRelieveDebtSuccessfully() {
        String sessionToken = "testToken";
        String[] args = {"friendUsername"};
        User currentUser = new User("currentUsername", "hashedPassword");
        User friendUser = new User("friendUsername", "hashedPassword");

        when(sessionManager.isAuthenticated(sessionToken)).thenReturn(true);
        when(sessionManager.getUsername(sessionToken)).thenReturn("currentUsername");
        when(registeredUsers.getUser("currentUsername")).thenReturn(currentUser);
        when(friendManager.getFriendByUsername(currentUser, "friendUsername")).thenReturn(friendUser);

        PayedFriendCommand command = new PayedFriendCommand(registeredUsers, friendManager, friendDebt, sessionManager);

        String result = command.execute(sessionToken, args, clientChannel);

        assertEquals("Successful relief of the debt of friend 'friendUsername'.", result);
        verify(friendDebt).payDebtOfUser(currentUser, friendUser);
    }

    @Test
    void executeShouldThrowExceptionWhenUserNotLoggedIn() {
        String sessionToken = "testToken";
        String[] args = {"friendUsername"};
        SocketChannel clientChannel = mock(SocketChannel.class);

        when(sessionManager.isAuthenticated(sessionToken)).thenReturn(false);

        PayedFriendCommand command = new PayedFriendCommand(registeredUsers, friendManager, friendDebt, sessionManager);

        assertThrows(UserNotLoggedInException.class,
                () -> command.execute(sessionToken, args, clientChannel));
    }

    @Test
    void executeShouldThrowExceptionForInvalidArguments() {
        String sessionToken = "testToken";
        String[] args = {}; // Invalid arguments
        SocketChannel clientChannel = mock(SocketChannel.class);

        when(sessionManager.isAuthenticated(sessionToken)).thenReturn(true);

        PayedFriendCommand command = new PayedFriendCommand(registeredUsers, friendManager, friendDebt, sessionManager);

        assertThrows(InvalidCommandArgumentsException.class,
                () -> command.execute(sessionToken, args, clientChannel));
    }
}