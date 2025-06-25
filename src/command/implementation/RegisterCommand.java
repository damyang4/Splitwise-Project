package command.implementation;

import command.Command;
import exception.InvalidCommandArgumentsException;
import exception.register.UsernameAlreadyTakenException;
import user.password.PasswordHasher;
import user.RegisteredUsers;
import user.User;

import java.nio.channels.SocketChannel;

public class RegisterCommand implements Command {
    private final RegisteredUsers registeredUsers;
    private final PasswordHasher passwordHasher;

    private static final int EXPECTED_ARGUMENTS_COUNT = 2;
    private static final String COMMAND_NAME = "register";
    private static final String COMMAND_PATTERN = " <username> <password>";
    private static final String SUCCESSFUL_REGISTRATION_MESSAGE =
            "User %s registered successfully.";

    public RegisterCommand(RegisteredUsers registeredUsers, PasswordHasher passwordHasher) {
        this.registeredUsers = registeredUsers;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public String execute(String sessionToken, String[] args, SocketChannel clientChannel) {
        validateCommandArgs(args);

        registeredUsers.register(new User(args[0], passwordHasher.hash(args[1])));

        return String.format(SUCCESSFUL_REGISTRATION_MESSAGE, args[0]);
    }

    private void validateCommandArgs(String[] args) {
        if (args.length != EXPECTED_ARGUMENTS_COUNT) {
            throw new InvalidCommandArgumentsException(COMMAND_NAME, EXPECTED_ARGUMENTS_COUNT, COMMAND_NAME +
                    COMMAND_PATTERN);
        }

        if (registeredUsers.getUser(args[0]) != null) {
            throw new UsernameAlreadyTakenException(args[0]);
        }
    }
}
