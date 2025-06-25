package command.implementation;

import command.Command;
import command.session.SessionManager;
import exception.InvalidCommandArgumentsException;
import exception.login.InvalidUsernameOrPasswordException;
import user.group.GroupNotification;
import user.Notification;
import user.friend.FriendNotification;
import user.password.PasswordHasher;
import user.RegisteredUsers;
import user.User;
import user.group.GroupManager;

import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LoginCommand implements Command {
    private final RegisteredUsers registeredUsers;
    private final Map<SocketChannel, String> clientSessions;
    private final FriendNotification friendNotification;
    private final GroupNotification groupNotification;
    private final GroupManager groupManager;
    private final SessionManager sessionManager;
    private final PasswordHasher passwordHasher;

    private static final int EXPECTED_ARGUMENTS_COUNT = 2;
    private static final String COMMAND_NAME = "login";
    private static final String COMMAND_PATTERN = " <username> <password>";
    private static final String SUCCESSFUL_LOGIN_MESSAGE = "Login successful. Welcome, ";
    private static final String NO_NOTIFICATIONS_MESSAGE = "\nNo notifications to show.";
    private static final String NOTIFICATIONS_HEADER = "\n*** Notifications ***";

    public LoginCommand(RegisteredUsers registeredUsers,
                        Map<SocketChannel, String> clientSessions,
                        FriendNotification friendNotification,
                        GroupNotification groupNotification,
                        GroupManager groupManager,
                        SessionManager sessionManager,
                        PasswordHasher passwordHasher) {
        this.registeredUsers = registeredUsers;
        this.clientSessions = clientSessions;
        this.friendNotification = friendNotification;
        this.groupNotification = groupNotification;
        this.groupManager = groupManager;
        this.sessionManager = sessionManager;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public String execute(String ignoredSessionToken, String[] args, SocketChannel clientChannel) {
        validateArguments(args);
        User user = authenticateUser(args[0], args[1]);

        String newSessionToken = sessionManager.login(user);
        clientSessions.put(clientChannel, newSessionToken);

        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append(SUCCESSFUL_LOGIN_MESSAGE).append(user.username());
        loadNotifications(user, responseBuilder);

        return responseBuilder.toString();
    }

    private void validateArguments(String[] args) {
        if (args.length != EXPECTED_ARGUMENTS_COUNT) {
            throw new InvalidCommandArgumentsException(COMMAND_NAME, EXPECTED_ARGUMENTS_COUNT, COMMAND_NAME +
                    COMMAND_PATTERN);
        }
    }

    private User authenticateUser(String username, String password) {
        User user = registeredUsers.getUser(username);
        if (user == null || !passwordHasher.verify(password, user.password())) {
            throw new InvalidUsernameOrPasswordException();
        }
        return user;
    }

    private void loadNotifications(User user, StringBuilder responseBuilder) {
        StringBuilder friendNotifications = buildFriendNotifications(user);
        StringBuilder groupNotifications = buildGroupNotifications(user);

        if (friendNotifications.isEmpty() && groupNotifications.isEmpty()) {
            responseBuilder.append(NO_NOTIFICATIONS_MESSAGE);
        } else {
            responseBuilder.append(NOTIFICATIONS_HEADER)
                    .append(friendNotifications)
                    .append(groupNotifications);
        }
    }

    private StringBuilder buildFriendNotifications(User user) {
        List<Notification> notifications = friendNotification.getNotifications(user);
        StringBuilder builder = new StringBuilder();
        if (!notifications.isEmpty()) {
            builder.append("\nFriends:");
            for (Notification notification : notifications) {
                builder.append("\n").append(notification);
            }
        }
        return builder;
    }

    private StringBuilder buildGroupNotifications(User user) {
        StringBuilder builder = new StringBuilder();
        Set<String> groups = groupManager.getGroupsContainingUser(user).keySet();
        for (String group : groups) {
            FriendNotification groupNotifications = groupNotification.getGroupNotifications(group);
            List<Notification> notifications = groupNotifications.getNotifications(user);
            if (!notifications.isEmpty()) {
                if (builder.isEmpty()) {
                    builder.append("\nGroups:");
                }
                builder.append("\n* ").append(group).append(":");
                for (Notification notification : notifications) {
                    builder.append("\n").append(notification);
                }
            }
        }
        return builder;
    }
}
