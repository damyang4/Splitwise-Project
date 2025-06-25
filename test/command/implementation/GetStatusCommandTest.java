package command.implementation;

import command.session.SessionManager;
import exception.InvalidCommandArgumentsException;
import exception.UserNotLoggedInException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import user.RegisteredUsers;
import user.User;
import user.friend.FriendDebt;
import user.friend.FriendManager;
import user.group.GroupDebt;
import user.group.GroupManager;

import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetStatusCommandTest {

    private RegisteredUsers registeredUsers;
    private GroupManager groupManager;
    private FriendManager friendManager;
    private FriendDebt friendDebt;
    private GroupDebt groupDebt;
    private SessionManager sessionManager;
    private GetStatusCommand command;

    @BeforeEach
    void setUp() {
        registeredUsers = mock(RegisteredUsers.class);
        groupManager = mock(GroupManager.class);
        friendManager = mock(FriendManager.class);
        friendDebt = mock(FriendDebt.class);
        groupDebt = mock(GroupDebt.class);
        sessionManager = mock(SessionManager.class);
        command = new GetStatusCommand(registeredUsers, groupManager, friendManager, friendDebt, groupDebt, sessionManager);
    }

    @Test
    void testWithNoDebtsReturnsNoDebtsMessage() {
        String sessionToken = "validToken";
        User user = mock(User.class);
        when(sessionManager.isAuthenticated(sessionToken)).thenReturn(true);
        when(sessionManager.getUsername(sessionToken)).thenReturn("testUser");
        when(registeredUsers.getUser("testUser")).thenReturn(user);
        when(friendDebt.getUserFriendDebts(user, friendManager)).thenReturn(Map.of());
        when(groupManager.getGroupsContainingUser(user)).thenReturn(Map.of());

        String result = command.execute(sessionToken, new String[]{}, null);

        assertEquals("No outstanding debts. You neither owe nor are owed any money.", result);
    }

    @Test
    void testWithFriendDebtsReturnsFriendDebtsDetails() {
        String sessionToken = "validToken";
        User user = mock(User.class);
        User friend = mock(User.class);
        when(user.username()).thenReturn("testUser");
        when(friend.username()).thenReturn("friendUser");
        when(sessionManager.isAuthenticated(sessionToken)).thenReturn(true);
        when(sessionManager.getUsername(sessionToken)).thenReturn("testUser");
        when(registeredUsers.getUser("testUser")).thenReturn(user);
        when(friendDebt.getUserFriendDebts(user, friendManager)).thenReturn(Map.of(friend, 50.0));

        String result = command.execute(sessionToken, new String[]{}, null);

        assertEquals("Friends:\n* friendUser: You owe 50.00 LV\r\n", result);
    }

    @Test
    void testWithGroupDebtsReturnsGroupDebtsDetails() {
        String sessionToken = "validToken";
        User user = mock(User.class);
        User groupMember = mock(User.class);
        when(user.username()).thenReturn("testUser");
        when(groupMember.username()).thenReturn("groupMember");
        when(sessionManager.isAuthenticated(sessionToken)).thenReturn(true);
        when(sessionManager.getUsername(sessionToken)).thenReturn("testUser");
        when(registeredUsers.getUser("testUser")).thenReturn(user);
        when(groupManager.getGroupsContainingUser(user)).thenReturn(Map.of("TestGroup", Set.of(user, groupMember)));
        when(groupDebt.getUserDebtByGroup("TestGroup", user, groupManager)).thenReturn(Map.of(groupMember, -25.0));

        String result = command.execute(sessionToken, new String[]{}, null);

        assertEquals("\nGroups:\n* TestGroup\n- groupMember: Owes you 25.00 LV\r\n", result);
    }

    @Test
    void testWithInvalidArgumentsThrowsInvalidCommandArgumentsException() {
        String sessionToken = "validToken";
        when(sessionManager.isAuthenticated(sessionToken)).thenReturn(true);

        assertThrows(InvalidCommandArgumentsException.class, () -> command.execute(sessionToken, new String[]{"arg1"}, null));
    }

    @Test
    void testWithUnauthenticatedUserThrowsUserNotLoggedInException() {
        String sessionToken = "invalidToken";
        when(sessionManager.isAuthenticated(sessionToken)).thenReturn(false);

        assertThrows(UserNotLoggedInException.class, () -> command.execute(sessionToken, new String[]{}, null));
    }
}