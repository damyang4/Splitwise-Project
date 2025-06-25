package command;

import command.implementation.AddFriendCommand;
import command.implementation.CreateGroupCommand;
import command.implementation.GetStatusCommand;
import command.implementation.PayedFriendCommand;
import command.implementation.PayedGroupCommand;
import command.implementation.SplitCommand;
import command.implementation.SplitGroupCommand;
import command.session.SessionManager;
import user.group.GroupNotification;
import user.friend.FriendNotification;
import user.friend.FriendDebt;
import user.friend.FriendManager;
import user.group.GroupDebt;
import user.group.GroupManager;
import command.implementation.LoginCommand;
import command.implementation.RegisterCommand;
import user.RegisteredUsers;
import user.password.PasswordHasher;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

public class CommandRegistry {
    private static final String REGISTER = "register";
    private static final String LOGIN = "login";
    private static final String ADD_FRIEND = "add-friend";
    private static final String CREATE_GROUP = "create-group";
    private static final String GET_STATUS = "get-status";
    private static final String SPLIT = "split";
    private static final String SPLIT_GROUP = "split-group";
    private static final String PAYED_FRIEND = "payed-friend";
    private static final String PAYED_GROUP = "payed-group";

    private final FriendNotification friendNotification = new FriendNotification();
    private final GroupNotification groupNotification = new GroupNotification();
    private final Map<String, Command> commandRegistry = new HashMap<>();
    private final FriendDebt friendDebt = new FriendDebt(friendNotification);
    private final GroupDebt groupDebt = new GroupDebt(groupNotification);
    private final FriendManager friendManager = new FriendManager();
    private final GroupManager groupManager = new GroupManager();
    private final SessionManager sessionManager = new SessionManager();
    private final PasswordHasher passwordHasher = new PasswordHasher();

    public CommandRegistry(RegisteredUsers registeredUsers, Map<SocketChannel, String> clientSessions) {
        initializeCommands(registeredUsers, clientSessions);
    }

    private void initializeCommands(RegisteredUsers registeredUsers, Map<SocketChannel, String> clientSessions) {
        registerCommand(REGISTER, new RegisterCommand(registeredUsers, passwordHasher));
        registerCommand(LOGIN, new LoginCommand(registeredUsers, clientSessions, friendNotification,
                groupNotification, groupManager, sessionManager, passwordHasher));
        registerCommand(ADD_FRIEND, new AddFriendCommand(registeredUsers, friendManager, sessionManager));
        registerCommand(CREATE_GROUP, new CreateGroupCommand(registeredUsers, groupManager, sessionManager));
        registerCommand(GET_STATUS, new GetStatusCommand(registeredUsers, groupManager, friendManager,
                friendDebt, groupDebt, sessionManager));
        registerCommand(SPLIT, new SplitCommand(registeredUsers, friendManager, friendDebt, sessionManager));
        registerCommand(SPLIT_GROUP, new SplitGroupCommand(registeredUsers, groupManager, groupDebt, sessionManager));
        registerCommand(PAYED_FRIEND, new PayedFriendCommand(registeredUsers, friendManager, friendDebt,
                sessionManager));
        registerCommand(PAYED_GROUP, new PayedGroupCommand(registeredUsers, groupManager, groupDebt, sessionManager));
    }

    private void registerCommand(String commandName, Command command) {
        commandRegistry.put(commandName, command);
    }

    public Command getCommand(String name) {
        return commandRegistry.getOrDefault(name, (session, args,
                                                   clientChannel) -> "Unknown command: " + name);
    }
}
