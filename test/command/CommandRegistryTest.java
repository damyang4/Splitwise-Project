package command;

import command.implementation.AddFriendCommand;
import command.implementation.CreateGroupCommand;
import command.implementation.GetStatusCommand;
import command.implementation.LoginCommand;
import command.implementation.PayedFriendCommand;
import command.implementation.PayedGroupCommand;
import command.implementation.RegisterCommand;
import command.implementation.SplitCommand;
import command.implementation.SplitGroupCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import user.RegisteredUsers;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class CommandRegistryTest {

    private RegisteredUsers mockRegisteredUsers;
    private Map<SocketChannel, String> mockClientSessions;
    private CommandRegistry commandRegistry;

    @BeforeEach
    public void setup() {
        mockRegisteredUsers = mock(RegisteredUsers.class);
        mockClientSessions = new HashMap<>();
        commandRegistry = new CommandRegistry(mockRegisteredUsers, mockClientSessions);
    }

    @Test
    public void testGetCommandExistingCommand() {
        Command result = commandRegistry.getCommand("register");
        assertNotNull(result);
        assertTrue(result instanceof RegisterCommand);
    }

    @Test
    public void testGetCommandNonExistentCommand() {
        Command result = commandRegistry.getCommand("unknown-command");
        assertNotNull(result);
        assertEquals("Unknown command: unknown-command", result.execute(null, null, null));
    }

    @Test
    public void testGetCommandAddFriendCommand() {
        Command result = commandRegistry.getCommand("add-friend");
        assertNotNull(result);
        assertTrue(result instanceof AddFriendCommand);
    }

    @Test
    public void testGetCommandCreateGroupCommand() {
        Command result = commandRegistry.getCommand("create-group");
        assertNotNull(result);
        assertTrue(result instanceof CreateGroupCommand);
    }

    @Test
    public void testGetCommandGetStatusCommand() {
        Command result = commandRegistry.getCommand("get-status");
        assertNotNull(result);
        assertTrue(result instanceof GetStatusCommand);
    }

    @Test
    public void testGetCommandSplitCommand() {
        Command result = commandRegistry.getCommand("split");
        assertNotNull(result);
        assertTrue(result instanceof SplitCommand);
    }

    @Test
    public void testGetCommandSplitGroupCommand() {
        Command result = commandRegistry.getCommand("split-group");
        assertNotNull(result);
        assertTrue(result instanceof SplitGroupCommand);
    }

    @Test
    public void testGetCommandPayedFriendCommand() {
        Command result = commandRegistry.getCommand("payed-friend");
        assertNotNull(result);
        assertTrue(result instanceof PayedFriendCommand);
    }

    @Test
    public void testGetCommandPayedGroupCommand() {
        Command result = commandRegistry.getCommand("payed-group");
        assertNotNull(result);
        assertTrue(result instanceof PayedGroupCommand);
    }
}