package command.implementation;

import command.session.SessionManager;
import exception.InvalidCommandArgumentsException;
import exception.UserNotLoggedInException;
import exception.splitgroup.GroupDoesNotExistException;
import exception.split.InvalidAmountFormatException;
import exception.splitgroup.UserNotInGroupException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import user.RegisteredUsers;
import user.User;
import user.group.GroupDebt;
import user.group.GroupManager;

import java.nio.channels.SocketChannel;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SplitGroupCommandTest {

    private RegisteredUsers registeredUsers;
    private GroupManager groupManager;
    private GroupDebt groupDebt;
    private SessionManager sessionManager;
    private SocketChannel clientChannel;
    private SplitGroupCommand command;

    @BeforeEach
    void setUp() {
        registeredUsers = mock(RegisteredUsers.class);
        groupManager = mock(GroupManager.class);
        groupDebt = mock(GroupDebt.class);
        sessionManager = mock(SessionManager.class);
        clientChannel = mock(SocketChannel.class);
        command = new SplitGroupCommand(registeredUsers, groupManager, groupDebt, sessionManager);
    }

    @Test
    void testExecuteSuccessfullySplitsAmount() {
        String sessionToken = "validSessionToken";
        String[] args = {"100.0", "groupName", "Dinner"};
        String username = "testUser";
        User user = new User(username, "password");

        when(sessionManager.isAuthenticated(sessionToken)).thenReturn(true);
        when(sessionManager.getUsername(sessionToken)).thenReturn(username);
        when(registeredUsers.getUser(username)).thenReturn(user);
        when(groupManager.getGroup("groupName")).thenReturn(Set.of(user));

        String result = command.execute(sessionToken, args, clientChannel);

        verify(groupDebt).splitBillWithUserGroup(100.0, user, "groupName", Set.of(user), "Dinner");
        assertEquals("Successfully split 100.00 LV between you and the other members of group groupName.", result);
    }

    @Test
    void testExecuteThrowsUserNotLoggedInExceptionWhenNotAuthenticated() {
        String sessionToken = "invalidSessionToken";
        String[] args = {"100.0", "groupName", "Dinner"};

        when(sessionManager.isAuthenticated(sessionToken)).thenReturn(false);

        assertThrows(UserNotLoggedInException.class, () -> command.execute(sessionToken, args, clientChannel));
    }

    @Test
    void testExecuteThrowsInvalidCommandArgumentsExceptionWhenArgsCountIncorrect() {
        String sessionToken = "validSessionToken";
        String[] args = {"100.0", "groupName"};

        when(sessionManager.isAuthenticated(sessionToken)).thenReturn(true);

        assertThrows(InvalidCommandArgumentsException.class, () -> command.execute(sessionToken, args, clientChannel));
    }

    @Test
    void testExecuteThrowsInvalidAmountFormatExceptionWhenAmountIsInvalid() {
        String sessionToken = "validSessionToken";
        String[] args = {"invalidAmount", "groupName", "Dinner"};
        String username = "testUser";
        User user = new User(username, "password");

        when(sessionManager.isAuthenticated(sessionToken)).thenReturn(true);
        when(sessionManager.getUsername(sessionToken)).thenReturn(username);
        when(registeredUsers.getUser(username)).thenReturn(user);

        assertThrows(InvalidAmountFormatException.class, () -> command.execute(sessionToken, args, clientChannel));
    }

    @Test
    void testExecuteThrowsGroupDoesNotExistExceptionWhenGroupIsEmpty() {
        String sessionToken = "validSessionToken";
        String[] args = {"100.0", "nonExistentGroup", "Dinner"};
        String username = "testUser";
        User user = new User(username, "password");

        when(sessionManager.isAuthenticated(sessionToken)).thenReturn(true);
        when(sessionManager.getUsername(sessionToken)).thenReturn(username);
        when(registeredUsers.getUser(username)).thenReturn(user);
        when(groupManager.getGroup("nonExistentGroup")).thenReturn(Set.of());

        assertThrows(GroupDoesNotExistException.class, () -> command.execute(sessionToken, args, clientChannel));
    }

    @Test
    void testExecuteThrowsUserNotInGroupExceptionWhenUserNotInGroup() {
        String sessionToken = "validSessionToken";
        String[] args = {"100.0", "groupName", "Dinner"};
        String username = "testUser";
        User user = new User(username, "password");

        User otherUser = new User("otherUser", "password");

        when(sessionManager.isAuthenticated(sessionToken)).thenReturn(true);
        when(sessionManager.getUsername(sessionToken)).thenReturn(username);
        when(registeredUsers.getUser(username)).thenReturn(user);
        when(groupManager.getGroup("groupName")).thenReturn(Set.of(otherUser));

        assertThrows(UserNotInGroupException.class, () -> command.execute(sessionToken, args, clientChannel));
    }
}