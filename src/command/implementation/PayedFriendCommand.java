package command.implementation;

import command.Command;
import command.session.SessionManager;
import exception.InvalidCommandArgumentsException;
import exception.UserNotLoggedInException;
import exception.split.UserNotInFriendListException;
import user.RegisteredUsers;
import user.User;
import user.friend.FriendDebt;
import user.friend.FriendManager;

import java.nio.channels.SocketChannel;

public class PayedFriendCommand implements Command {
    private final RegisteredUsers registeredUsers;
    private final FriendManager friendManager;
    private final FriendDebt friendDebt;
    private final SessionManager sessionManager;

    private static final int EXPECTED_ARGUMENTS_COUNT = 1;
    private static final String COMMAND_NAME = "payed-friend";
    private static final String COMMAND_PATTERN = " <username>";
    private static final String SUCCESSFUL_PAYED_FRIEND_MESSAGE = "Successful relief of the debt of friend '%s'.";

    public PayedFriendCommand(RegisteredUsers registeredUsers,
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
        User owingUser = friendManager.getFriendByUsername(receivingUser, args[0]);
        if (owingUser == null) {
            throw new UserNotInFriendListException(args[1]);
        }

        friendDebt.payDebtOfUser(receivingUser, owingUser);
        return String.format(SUCCESSFUL_PAYED_FRIEND_MESSAGE, args[0]);
    }

    private void validateRequest(String sessionToken, String[] args) {
        if (!sessionManager.isAuthenticated(sessionToken)) {
            throw new UserNotLoggedInException();
        }

        if (args.length != EXPECTED_ARGUMENTS_COUNT) {
            throw new InvalidCommandArgumentsException(
                    COMMAND_NAME, EXPECTED_ARGUMENTS_COUNT, COMMAND_NAME + COMMAND_PATTERN); // could change
        }
    }
}
