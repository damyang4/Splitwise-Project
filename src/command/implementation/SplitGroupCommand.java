package command.implementation;

import command.Command;
import command.session.SessionManager;
import exception.InvalidCommandArgumentsException;
import exception.UserNotLoggedInException;
import exception.splitgroup.GroupDoesNotExistException;
import exception.split.InvalidAmountFormatException;
import exception.splitgroup.UserNotInGroupException;
import user.RegisteredUsers;
import user.User;
import user.group.GroupDebt;
import user.group.GroupManager;

import java.nio.channels.SocketChannel;
import java.util.Set;

public class SplitGroupCommand implements Command {
    private final RegisteredUsers registeredUsers;
    private final GroupManager groupManager;
    private final GroupDebt groupDebt;
    private final SessionManager sessionManager;

    private static final String COMMAND_NAME = "split-group";
    private static final String COMMAND_PATTERN = " <amount> <group_name> <reason_for_payment>";
    private static final int COMMAND_ARGS_COUNT = 3;
    private static final String SUCCESSFUL_SPLIT_GROUP_MESSAGE =
            "Successfully split %.2f LV between you and the other members of group %s.";

    public SplitGroupCommand(RegisteredUsers registeredUsers,
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
        User payer = registeredUsers.getUser(sessionManager.getUsername(sessionToken));

        double amount;
        try {
            amount = Double.parseDouble(args[0]);
        } catch (NumberFormatException e) {
            throw new InvalidAmountFormatException();
        }

        Set<User> groupMembers = groupManager.getGroup(args[1]);
        validateGroupMembers(groupMembers, payer, args[1]);

        groupDebt.splitBillWithUserGroup(amount, payer, args[1], groupMembers, args[2]);
        return String.format(SUCCESSFUL_SPLIT_GROUP_MESSAGE, amount, args[1]);
    }

    private void validateRequest(String sessionToken, String[] args) {
        if (!sessionManager.isAuthenticated(sessionToken)) {
            throw new UserNotLoggedInException();
        }

        if (args.length != COMMAND_ARGS_COUNT) {
            throw new InvalidCommandArgumentsException(
                    COMMAND_NAME, COMMAND_ARGS_COUNT, COMMAND_NAME + COMMAND_PATTERN);
        }
    }

    private void validateGroupMembers(Set<User> groupMembers, User user, String groupName) {
        if (groupMembers.isEmpty()) {
            throw new GroupDoesNotExistException(groupName);
        }

        if (!groupMembers.contains(user)) {
            throw new UserNotInGroupException(user.username(), groupName);
        }
    }
}
