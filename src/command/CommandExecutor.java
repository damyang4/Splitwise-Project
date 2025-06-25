package command;

import command.parser.CommandParser;
import command.parser.ParsedUserInput;
import user.RegisteredUsers;

import java.nio.channels.SocketChannel;
import java.util.Map;

public class CommandExecutor {
    private static final String UNKNOWN_COMMAND_ERROR = "Error: Unknown command.";

    private final RegisteredUsers registeredUsers;
    private final CommandRegistry commandRegistry;

    public CommandExecutor(RegisteredUsers registeredUsers, Map<SocketChannel, String> clientSessions) {
        this.registeredUsers = registeredUsers;
        this.commandRegistry = new CommandRegistry(registeredUsers, clientSessions);
    }

    public String execute(String sessionToken, String input, SocketChannel clientChannel) {
        ParsedUserInput parsedInput = CommandParser.parse(input);
        return handleCommandExecution(sessionToken, parsedInput, clientChannel);
    }

    private String handleCommandExecution(String sessionToken, ParsedUserInput input, SocketChannel clientChannel) {
        try {
            return commandRegistry.getCommand(input.name()).execute(sessionToken, input.args(), clientChannel);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public RegisteredUsers getRegisteredUsers() {
        return registeredUsers;
    }
}