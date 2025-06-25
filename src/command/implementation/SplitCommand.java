package command.implementation;

import command.Command;
import command.session.SessionManager;
import exception.InvalidCommandArgumentsException;
import exception.UserNotLoggedInException;
import exception.split.InvalidAmountFormatException;
import exception.split.UserNotInFriendListException;
import user.RegisteredUsers;
import user.User;
import user.friend.FriendDebt;
import user.friend.FriendManager;

import java.nio.channels.SocketChannel;

public class SplitCommand implements Command {
    private final RegisteredUsers registeredUsers;
    private final FriendManager friendManager;
    private final FriendDebt friendDebt;
    private final SessionManager sessionManager;

    private static final int COMMAND_ARGS_COUNT = 3;
    private static final String COMMAND_NAME = "split";
    private static final String COMMAND_PATTERN = " <amount> <username> <reason_for_payment>";
    private static final String SUCCESSFUL_SPLIT_MESSAGE = "Successfully split %.2f LV between you and %s.";

    public SplitCommand(RegisteredUsers registeredUsers,
                        FriendManager friendManager,
                        FriendDebt friendDebt,
                        SessionManager sessionManager) {
        this.registeredUsers = registeredUsers;
        this.friendManager = friendManager;
        this.friendDebt = friendDebt;
        this.sessionManager = sessionManager;
    }

    @Override
    public String execute(String sessionToken, String[] args, SocketChannel clientChannel) {
        validateRequest(sessionToken, args);

        User receivingUser = registeredUsers.getUser(sessionManager.getUsername(sessionToken));
        User owingUser = friendManager.getFriendByUsername(receivingUser, args[1]);
        if (owingUser == null) {
            throw new UserNotInFriendListException(args[1]);
        }

        double amount;
        try {
            amount = Double.parseDouble(args[0]);
        } catch (NumberFormatException e) {
            throw new InvalidAmountFormatException();
        }

        double splitAmount = amount / 2;

        friendDebt.splitBillWithFriend(splitAmount, owingUser, receivingUser, args[2]);
        return String.format(SUCCESSFUL_SPLIT_MESSAGE, amount, args[0]);
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
}

