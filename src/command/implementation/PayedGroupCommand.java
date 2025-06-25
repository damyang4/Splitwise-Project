package command.implementation;

import command.Command;
import command.session.SessionManager;
import exception.InvalidCommandArgumentsException;
import exception.UserNotLoggedInException;
import exception.splitgroup.UserNotInGroupException;
import user.RegisteredUsers;
import user.User;
import user.group.GroupDebt;
import user.group.GroupManager;

import java.nio.channels.SocketChannel;

public class PayedGroupCommand implements Command {
    private final RegisteredUsers registeredUsers;
    private final GroupManager groupManager;
    private final GroupDebt groupDebt;
    private final SessionManager sessionManager;

    private static final int EXPECTED_ARGUMENTS_COUNT = 2;
    private static final String COMMAND_NAME = "payed-group";
    private static final String COMMAND_PATTERN = " <group_name> <username>";
    private static final String SUCCESSFUL_PAYED_GROUP_MESSAGE = "Successful relief of the debt of group member '%s' " +
            "in group '%s'.";

    public PayedGroupCommand(RegisteredUsers registeredUsers,
                             GroupManager groupManager,
                             GroupDebt groupDebt,
                             SessionManager sessionManager) {
        this.registeredUsers = registeredUsers;
        this.groupManager = groupManager;
        this.groupDebt = groupDebt;
        this.sessionManager = sessionManager;
    }

    @Override
    public String execute(String sessionToken, String[] args, SocketChannel clientChannel) {
        validateRequest(sessionToken, args);

        User receivingUser = registeredUsers.getUser(sessionManager.getUsername(sessionToken));
        User owingUser = registeredUsers.getUser(args[1]);

        if (!groupManager.isPartOfGroup(args[0], owingUser)) {
            throw new UserNotInGroupException(owingUser.username(), args[1]);
        }

        groupDebt.payDebtOfGroupMember(args[0], owingUser, receivingUser);
        return String.format(SUCCESSFUL_PAYED_GROUP_MESSAGE, args[1], args[0]);
    }

    private void validateRequest(String sessionToken, String[] args) {
        if (!sessionManager.isAuthenticated(sessionToken)) {
            throw new UserNotLoggedInException();
        }

        if (args.length != EXPECTED_ARGUMENTS_COUNT) {
            throw new InvalidCommandArgumentsException(
                    COMMAND_NAME, EXPECTED_ARGUMENTS_COUNT, COMMAND_NAME + COMMAND_PATTERN);
        }
    }
}