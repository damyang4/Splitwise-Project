package command.implementation;

import command.session.SessionManager;
import exception.InvalidCommandArgumentsException;
import exception.UserNotLoggedInException;
import exception.split.InvalidAmountFormatException;
import exception.split.UserNotInFriendListException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import user.RegisteredUsers;
import user.User;
import user.friend.FriendDebt;
import user.friend.FriendManager;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SplitCommandTest {

    private RegisteredUsers registeredUsers;
    private FriendManager friendManager;
    private FriendDebt friendDebt;
    private SessionManager sessionManager;
    private SocketChannel clientChannel;
    private SplitCommand splitCommand;

    @BeforeEach
    void setUp() {
        registeredUsers = mock(RegisteredUsers.class);
        friendManager = mock(FriendManager.class);
        friendDebt = mock(FriendDebt.class);
        sessionManager = mock(SessionManager.class);
        clientChannel = mock(SocketChannel.class);
        splitCommand = new SplitCommand(registeredUsers, friendManager, friendDebt, sessionManager);
    }

    @Test
    void executeSuccessfulSplit() {
        String sessionToken = "validSessionToken";
        String[] args = {"100", "friendUsername", "Dinner"};
        User receivingUser = mock(User.class);
        User owingUser = mock(User.class);

        when(sessionManager.isAuthenticated(sessionToken)).thenReturn(true);
        when(sessionManager.getUsername(sessionToken)).thenReturn("currentUsername");
        when(registeredUsers.getUser("currentUsername")).thenReturn(receivingUser);
        when(friendManager.getFriendByUsername(receivingUser, "friendUsername")).thenReturn(owingUser);

        String result = splitCommand.execute(sessionToken, args, clientChannel);

        assertEquals("Successfully split 100.00 LV between you and 100.", result);
        verify(friendDebt).splitBillWithFriend(50.0, owingUser, receivingUser, "Dinner");
    }

    @Test
    void executeNotAuthenticatedThrowsUserNotLoggedInException() {
        String sessionToken = "invalidSessionToken";
        String[] args = {"100", "friendUsername", "Dinner"};

        when(sessionManager.isAuthenticated(sessionToken)).thenReturn(false);

        assertThrows(UserNotLoggedInException.class, () -> {
            splitCommand.execute(sessionToken, args, clientChannel);
        });
    }

    @Test
    void executeInvalidArgumentsThrowsInvalidCommandArgumentsException() {
        String sessionToken = "validSessionToken";
        String[] args = {"100", "friendUsername"};

        when(sessionManager.isAuthenticated(sessionToken)).thenReturn(true);

        assertThrows(InvalidCommandArgumentsException.class, () -> {
            splitCommand.execute(sessionToken, args, clientChannel);
        });
    }

    @Test
    void executeInvalidAmountFormatThrowsInvalidAmountFormatException() {
        String sessionToken = "validSessionToken";
        String[] args = {"invalidAmount", "friendUsername", "Dinner"};

        when(sessionManager.isAuthenticated(sessionToken)).thenReturn(true);
        when(sessionManager.getUsername(sessionToken)).thenReturn("currentUsername");
        when(registeredUsers.getUser("currentUsername")).thenReturn(mock(User.class));
        when(friendManager.getFriendByUsername(any(), eq("friendUsername"))).thenReturn(mock(User.class));

        assertThrows(InvalidAmountFormatException.class, () -> {
            splitCommand.execute(sessionToken, args, clientChannel);
        });
    }

    @Test
    void executeUserNotInFriendListThrowsUserNotInFriendListException() {
        String sessionToken = "validSessionToken";
        String[] args = {"100", "nonFriendUsername", "Dinner"};
        User receivingUser = mock(User.class);

        when(sessionManager.isAuthenticated(sessionToken)).thenReturn(true);
        when(sessionManager.getUsername(sessionToken)).thenReturn("currentUsername");
        when(registeredUsers.getUser("currentUsername")).thenReturn(receivingUser);
        when(friendManager.getFriendByUsername(receivingUser, "nonFriendUsername")).thenReturn(null);

        assertThrows(UserNotInFriendListException.class, () -> {
            splitCommand.execute(sessionToken, args, clientChannel);
        });
    }
}