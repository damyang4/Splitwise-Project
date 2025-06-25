package command.implementation;

import command.Command;
import command.session.SessionManager;
import exception.InvalidCommandArgumentsException;
import exception.UserNotLoggedInException;
import exception.addfriend.UserAlreadyFriendException;
import exception.addfriend.UserNotFoundException;
import exception.addfriend.UserSelfFriendshipException;
import user.RegisteredUsers;
import user.User;
import user.friend.FriendManager;

import java.nio.channels.SocketChannel;

public class AddFriendCommand implements Command {
    private final RegisteredUsers registeredUsers;
    private final FriendManager friendManager;
    private final SessionManager sessionManager;

    private static final int EXPECTED_ARGUMENTS_COUNT = 1;
    private static final String COMMAND_NAME = "add-friend";
    private static final String COMMAND_PATTERN = " <username>";
    private static final String SUCCESSFUL_ADD_FRIEND_MESSAGE =
            "User '%s' added successfully as a friend.";

    public AddFriendCommand(RegisteredUsers registeredUsers, FriendManager friendManager, SessionManager sessionManager) {
        this.registeredUsers = registeredUsers;
        this.friendManager = friendManager;
        this.sessionManager = sessionManager;
    }

    @Override
    public String execute(String sessionToken, String[] args, SocketChannel clientChannel) {
        validateRequest(sessionToken, args);

        User currentUser = registeredUsers.getUser(sessionManager.getUsername(sessionToken));

        User friend = validateFriend(args[0], currentUser);

        friendManager.addFriend(currentUser, friend);
        return String.format(SUCCESSFUL_ADD_FRIEND_MESSAGE, friend.username());
    }

    private void validateRequest(String sessionToken, String[] args) {
        if (!sessionManager.isAuthenticated(sessionToken)) {
            throw new UserNotLoggedInException();
        }

        if (args.length != EXPECTED_ARGUMENTS_COUNT) {
            throw new InvalidCommandArgumentsException(COMMAND_NAME, EXPECTED_ARGUMENTS_COUNT, COMMAND_NAME + COMMAND_PATTERN);
        }
    }

    private User validateFriend(String friendUsername, User currentUser) {
        User friend = registeredUsers.getUser(friendUsername);

        if (friend == null) {
            throw new UserNotFoundException(friendUsername);
        }

        if (currentUser.equals(friend)) {
            throw new UserSelfFriendshipException();
        }

        if (friendManager.areFriends(currentUser, friend)) {
            throw new UserAlreadyFriendException(friendUsername);
        }

        return friend;
    }
}
