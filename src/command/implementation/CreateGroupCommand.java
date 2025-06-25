package command.implementation;

import command.Command;
import command.session.SessionManager;
import exception.InvalidCommandArgumentsException;
import exception.UserNotLoggedInException;
import exception.addfriend.UserNotFoundException;
import exception.creategroup.GroupAlreadyExistsException;
import user.RegisteredUsers;
import user.User;
import user.group.GroupManager;

import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Set;

public class CreateGroupCommand implements Command {
    private final RegisteredUsers registeredUsers;
    private final GroupManager groupManager;
    private final SessionManager sessionManager;

    private static final int EXPECTED_ARGUMENTS_COUNT = 3;
    private static final String COMMAND_NAME = "create-group";
    private static final String COMMAND_PATTERN = " <group-name> <member1> <member2> ...";
    private static final String SUCCESSFUL_GROUP_CREATED_MESSAGE =
            "Group %s was created successfully.";

    public CreateGroupCommand(RegisteredUsers registeredUsers, GroupManager groupManager, SessionManager sessionManager) {
        this.registeredUsers = registeredUsers;
        this.groupManager = groupManager;
        this.sessionManager = sessionManager;
    }

    @Override
    public String execute(String sessionToken, String[] args, SocketChannel clientChannel) {
        validateRequest(sessionToken, args);
        User currentUser = registeredUsers.getUser(sessionManager.getUsername(sessionToken));

        Set<User> members = collectMembers(args);
        members.add(currentUser);

        if (!groupManager.createGroup(args[0], members)) {
            throw new GroupAlreadyExistsException();
        }

        return String.format(SUCCESSFUL_GROUP_CREATED_MESSAGE, args[0]);
    }

    private void validateRequest(String sessionToken, String[] args) {
        if (!sessionManager.isAuthenticated(sessionToken)) {
            throw new UserNotLoggedInException();
        }

        if (args.length < EXPECTED_ARGUMENTS_COUNT) {
            throw new InvalidCommandArgumentsException(
                    COMMAND_NAME, EXPECTED_ARGUMENTS_COUNT, COMMAND_NAME + COMMAND_PATTERN);
        }
    }

    private Set<User> collectMembers(String[] args) {
        Set<User> members = new HashSet<>();
        for (int i = 1; i < args.length; i++) {
            User user = registeredUsers.getUser(args[i]);
            if (user == null) {
                throw new UserNotFoundException(args[i]);
            }
            members.add(user);
        }
        return members;
    }
}
