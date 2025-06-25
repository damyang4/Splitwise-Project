package command.implementation;

import command.session.SessionManager;
import exception.InvalidCommandArgumentsException;
import exception.UserNotLoggedInException;
import exception.splitgroup.UserNotInGroupException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import user.RegisteredUsers;
import user.User;
import user.group.GroupDebt;
import user.group.GroupManager;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class PayedGroupCommandTest {

    private RegisteredUsers registeredUsers;
    private GroupManager groupManager;
    private GroupDebt groupDebt;
    private SessionManager sessionManager;
    private SocketChannel clientChannel;
    private PayedGroupCommand payedGroupCommand;

    @BeforeEach
    void setUp() {
        registeredUsers = mock(RegisteredUsers.class);
        groupManager = mock(GroupManager.class);
        groupDebt = mock(GroupDebt.class);
        sessionManager = mock(SessionManager.class);
        clientChannel = mock(SocketChannel.class);

        payedGroupCommand = new PayedGroupCommand(registeredUsers, groupManager, groupDebt, sessionManager);
    }

    @Test
    void testExecuteSuccessfulPayment() {
        String sessionToken = "valid_session_token";
        String[] args = {"group1", "user2"};

        User receivingUser = new User("user1", "password1");
        User owingUser = new User("user2", "password2");

        when(sessionManager.isAuthenticated(sessionToken)).thenReturn(true);
        when(sessionManager.getUsername(sessionToken)).thenReturn("user1");
        when(registeredUsers.getUser("user1")).thenReturn(receivingUser);
        when(registeredUsers.getUser("user2")).thenReturn(owingUser);
        when(groupManager.isPartOfGroup("group1", owingUser)).thenReturn(true);

        String result = payedGroupCommand.execute(sessionToken, args, clientChannel);

        assertEquals("Successful relief of the debt of group member 'user2' in group 'group1'.", result);
        verify(groupDebt).payDebtOfGroupMember("group1", owingUser, receivingUser);
    }

    @Test
    void testExecuteThrowsUserNotLoggedInException() {
        String sessionToken = "invalid_session_token";
        String[] args = {"group1", "user2"};

        when(sessionManager.isAuthenticated(sessionToken)).thenReturn(false);

        assertThrows(UserNotLoggedInException.class, () -> payedGroupCommand.execute(sessionToken, args, clientChannel));
    }

    @Test
    void testExecuteThrowsInvalidCommandArgumentsException() {
        String sessionToken = "valid_session_token";
        String[] args = {"group1"};

        when(sessionManager.isAuthenticated(sessionToken)).thenReturn(true);

        assertThrows(InvalidCommandArgumentsException.class, () -> payedGroupCommand.execute(sessionToken, args, clientChannel));
    }

    @Test
    void testExecuteThrowsUserNotInGroupException() {
        String sessionToken = "valid_session_token";
        String[] args = {"group1", "user2"};

        User receivingUser = new User("user1", "password1");
        User owingUser = new User("user2", "password2");

        when(sessionManager.isAuthenticated(sessionToken)).thenReturn(true);
        when(sessionManager.getUsername(sessionToken)).thenReturn("user1");
        when(registeredUsers.getUser("user1")).thenReturn(receivingUser);
        when(registeredUsers.getUser("user2")).thenReturn(owingUser);
        when(groupManager.isPartOfGroup("group1", owingUser)).thenReturn(false);

        assertThrows(UserNotInGroupException.class, () -> payedGroupCommand.execute(sessionToken, args, clientChannel));
    }
}