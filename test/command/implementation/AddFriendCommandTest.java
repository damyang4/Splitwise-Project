package command.implementation;

import command.session.SessionManager;
import exception.InvalidCommandArgumentsException;
import exception.UserNotLoggedInException;
import exception.addfriend.UserAlreadyFriendException;
import exception.addfriend.UserNotFoundException;
import exception.addfriend.UserSelfFriendshipException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import user.RegisteredUsers;
import user.User;
import user.friend.FriendManager;

import java.nio.channels.SocketChannel;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AddFriendCommandTest {
    private RegisteredUsers registeredUsers;
    private FriendManager friendManager;
    private SessionManager sessionManager;
    private AddFriendCommand command;

    @BeforeEach
    void setUp() {
        registeredUsers = mock(RegisteredUsers.class);
        friendManager = mock(FriendManager.class);
        sessionManager = mock(SessionManager.class);
        command = new AddFriendCommand(registeredUsers, friendManager, sessionManager);
    }

    @Test
    void testExecuteSuccessfulFriendAddition() {
        String sessionToken = "validSession";
        String targetUsername = "friendUser";
        String[] args = {targetUsername};

        SocketChannel clientChannel = mock(SocketChannel.class);

        User currentUser = new User("currentUser", "hashedPassword");
        User friendUser = new User(targetUsername, "hashedPassword");

        when(sessionManager.isAuthenticated(sessionToken)).thenReturn(true);
        when(sessionManager.getUsername(sessionToken)).thenReturn("currentUser");
        when(registeredUsers.getUser("currentUser")).thenReturn(currentUser);
        when(registeredUsers.getUser(targetUsername)).thenReturn(friendUser);
        when(friendManager.areFriends(currentUser, friendUser)).thenReturn(false);

        String result = command.execute(sessionToken, args, clientChannel);

        Assertions.assertEquals(String.format("User '%s' added successfully as a friend.", targetUsername), result);
    }

    @Test
    void testExecuteUserNotLoggedInThrowsException() {
        String sessionToken = "invalidSession";
        String[] args = {"friendUser"};

        when(sessionManager.isAuthenticated(sessionToken)).thenReturn(false);

        Assertions.assertThrows(UserNotLoggedInException.class, () -> command.execute(sessionToken, args, mock(SocketChannel.class)));
    }

    @Test
    void testExecuteInvalidArgumentsThrowsException() {
        String sessionToken = "validSession";
        String[] args = {};

        when(sessionManager.isAuthenticated(sessionToken)).thenReturn(true);

        Assertions.assertThrows(InvalidCommandArgumentsException.class, () -> {
            command.execute(sessionToken, args, mock(SocketChannel.class));
        });
    }

    @Test
    void testExecuteUserNotFoundThrowsException() {
        String sessionToken = "validSession";
        String targetUsername = "nonexistentUser";
        String[] args = {targetUsername};

        when(sessionManager.isAuthenticated(sessionToken)).thenReturn(true);
        when(sessionManager.getUsername(sessionToken)).thenReturn("currentUser");
        when(registeredUsers.getUser("currentUser")).thenReturn(mock(User.class));
        when(registeredUsers.getUser(targetUsername)).thenReturn(null);

        Assertions.assertThrows(UserNotFoundException.class, () -> {
            command.execute(sessionToken, args, mock(SocketChannel.class));
        });
    }

    @Test
    void testExecuteUserSelfFriendshipThrowsException() {
        String sessionToken = "validSession";
        String targetUsername = "currentUser";
        String[] args = {targetUsername};

        User currentUser = mock(User.class);

        when(sessionManager.isAuthenticated(sessionToken)).thenReturn(true);
        when(sessionManager.getUsername(sessionToken)).thenReturn(targetUsername);
        when(registeredUsers.getUser(targetUsername)).thenReturn(currentUser);

        Assertions.assertThrows(UserSelfFriendshipException.class, () -> {
            command.execute(sessionToken, args, mock(SocketChannel.class));
        });
    }

    @Test
    void testExecuteUserAlreadyFriendThrowsException() {
        String sessionToken = "validSession";
        String targetUsername = "friendUser";
        String[] args = {targetUsername};

        User currentUser = mock(User.class);
        User friendUser = mock(User.class);

        when(sessionManager.isAuthenticated(sessionToken)).thenReturn(true);
        when(sessionManager.getUsername(sessionToken)).thenReturn("currentUser");
        when(registeredUsers.getUser("currentUser")).thenReturn(currentUser);
        when(registeredUsers.getUser(targetUsername)).thenReturn(friendUser);
        when(friendManager.areFriends(currentUser, friendUser)).thenReturn(true);

        Assertions.assertThrows(UserAlreadyFriendException.class, () -> {
            command.execute(sessionToken, args, mock(SocketChannel.class));
        });
    }
}