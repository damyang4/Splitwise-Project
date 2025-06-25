package command.implementation;

import command.session.SessionManager;
import exception.InvalidCommandArgumentsException;
import exception.UserNotLoggedInException;
import exception.addfriend.UserNotFoundException;
import exception.creategroup.GroupAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import user.RegisteredUsers;
import user.User;
import user.group.GroupManager;

import java.nio.channels.SocketChannel;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CreateGroupCommandTest {

    private RegisteredUsers registeredUsers;
    private GroupManager groupManager;
    private SessionManager sessionManager;
    private SocketChannel clientChannel;

    @BeforeEach
    public void setup() {
        registeredUsers = mock(RegisteredUsers.class);
        groupManager = mock(GroupManager.class);
        sessionManager = mock(SessionManager.class);
        clientChannel = mock(SocketChannel.class);
    }

    @Test
    public void testExecuteSuccessfullyCreatesGroup() {
        String sessionToken = "validToken";
        String[] args = {"groupName", "user1", "user2"};
        User currentUser = mock(User.class);
        User user1 = mock(User.class);
        User user2 = mock(User.class);

        when(sessionManager.isAuthenticated(sessionToken)).thenReturn(true);
        when(sessionManager.getUsername(sessionToken)).thenReturn("currentUser");
        when(registeredUsers.getUser("currentUser")).thenReturn(currentUser);
        when(registeredUsers.getUser("user1")).thenReturn(user1);
        when(registeredUsers.getUser("user2")).thenReturn(user2);
        when(groupManager.createGroup("groupName", Set.of(currentUser, user1, user2))).thenReturn(true);

        CreateGroupCommand command = new CreateGroupCommand(registeredUsers, groupManager, sessionManager);
        String result = command.execute(sessionToken, args, clientChannel);

        assertEquals("Group groupName was created successfully.", result);
    }

    @Test
    public void testExecuteThrowsUserNotLoggedInException() {
        String sessionToken = "invalidToken";
        String[] args = {"groupName", "user1", "user2"};

        when(sessionManager.isAuthenticated(sessionToken)).thenReturn(false);

        CreateGroupCommand command = new CreateGroupCommand(registeredUsers, groupManager, sessionManager);

        assertThrows(UserNotLoggedInException.class, () -> command.execute(sessionToken, args, clientChannel));
    }

    @Test
    public void testExecuteThrowsInvalidCommandArgumentsException() {
        String sessionToken = "validToken";
        String[] args = {"groupName"};

        when(sessionManager.isAuthenticated(sessionToken)).thenReturn(true);

        CreateGroupCommand command = new CreateGroupCommand(registeredUsers, groupManager, sessionManager);

        assertThrows(InvalidCommandArgumentsException.class, () -> command.execute(sessionToken, args, clientChannel));
    }

    @Test
    public void testExecuteThrowsUserNotFoundException() {
        String sessionToken = "validToken";
        String[] args = {"groupName", "user1", "registeredUser"};
        User currentUser = mock(User.class);

        when(sessionManager.isAuthenticated(sessionToken)).thenReturn(true);
        when(sessionManager.getUsername(sessionToken)).thenReturn("currentUser");
        when(registeredUsers.getUser("currentUser")).thenReturn(currentUser);
        when(registeredUsers.getUser("user1")).thenReturn(null);

        CreateGroupCommand command = new CreateGroupCommand(registeredUsers, groupManager, sessionManager);

        assertThrows(UserNotFoundException.class, () -> command.execute(sessionToken, args, clientChannel));
    }

    @Test
    public void testExecuteThrowsGroupAlreadyExistsException() {
        String sessionToken = "validToken";
        String[] args = {"groupName", "user1", "user2"};
        User currentUser = mock(User.class);
        User user1 = mock(User.class);
        User user2 = mock(User.class);

        when(sessionManager.isAuthenticated(sessionToken)).thenReturn(true);
        when(sessionManager.getUsername(sessionToken)).thenReturn("currentUser");
        when(registeredUsers.getUser("currentUser")).thenReturn(currentUser);
        when(registeredUsers.getUser("user1")).thenReturn(user1);
        when(registeredUsers.getUser("user2")).thenReturn(user2);
        when(groupManager.createGroup("groupName", Set.of(currentUser, user1, user2))).thenReturn(false);

        CreateGroupCommand command = new CreateGroupCommand(registeredUsers, groupManager, sessionManager);

        assertThrows(GroupAlreadyExistsException.class, () -> command.execute(sessionToken, args, clientChannel));
    }
}