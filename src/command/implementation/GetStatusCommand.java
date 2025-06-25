package command.implementation;

import command.Command;
import command.session.SessionManager;
import exception.InvalidCommandArgumentsException;
import exception.UserNotLoggedInException;
import user.RegisteredUsers;
import user.User;
import user.friend.FriendDebt;
import user.friend.FriendManager;
import user.group.GroupDebt;
import user.group.GroupManager;

import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Set;

public class GetStatusCommand implements Command {
    private final RegisteredUsers registeredUsers;
    private final GroupManager groupManager;
    private final FriendManager friendManager;
    private final FriendDebt friendDebt;
    private final GroupDebt groupDebt;
    private final SessionManager sessionManager;

    private static final String COMMAND_NAME = "get-status";
    private static final String NO_DEBTS_MESSAGE = "No outstanding debts. You neither owe nor are owed any money.";
    private static final String FRIENDS_HEADER = "Friends:\n";
    private static final String GROUPS_HEADER = "\nGroups:\n";

    public GetStatusCommand(RegisteredUsers registeredUsers, GroupManager groupManager, FriendManager friendManager,
                            FriendDebt friendDebt, GroupDebt groupDebt, SessionManager sessionManager) {
        this.registeredUsers = registeredUsers;
        this.groupManager = groupManager;
        this.friendManager = friendManager;
        this.friendDebt = friendDebt;
        this.groupDebt = groupDebt;
        this.sessionManager = sessionManager;
    }

    @Override
    public String execute(String sessionToken, String[] args, SocketChannel clientChannel) {
        validateRequest(sessionToken, args);
        User currentUser = registeredUsers.getUser(sessionManager.getUsername(sessionToken));

        StringBuilder result = new StringBuilder();
        appendFriendDebtDetails(currentUser, result);
        appendGroupDebtDetails(currentUser, result);

        return result.isEmpty() ? NO_DEBTS_MESSAGE : result.toString();
    }

    private void validateRequest(String sessionToken, String[] args) {
        if (!sessionManager.isAuthenticated(sessionToken)) {
            throw new UserNotLoggedInException();
        }
        if (args.length != 0) {
            throw new InvalidCommandArgumentsException(COMMAND_NAME, 0, COMMAND_NAME);
        }
    }

    private void appendFriendDebtDetails(User user, StringBuilder result) {
        Map<User, Double> debts = friendDebt.getUserFriendDebts(user, friendManager);
        if (debts != null && !debts.isEmpty()) {
            result.append(FRIENDS_HEADER);
            debts.forEach((friend, amount) -> {
                String debtString = buildFriendDebtString(friend, amount);
                if (!debtString.isEmpty()) result.append(debtString);
            });
        }
    }

    private String buildFriendDebtString(User friend, double amount) {
        if (amount == 0) return "";
        String direction = amount > 0 ? "You owe " : "Owes you ";
        return String.format("* %s: %s%.2f LV%n", friend.username(), direction, Math.abs(amount));
    }

    private void appendGroupDebtDetails(User user, StringBuilder result) {
        Map<String, Set<User>> userGroups = groupManager.getGroupsContainingUser(user);
        if (userGroups != null && !userGroups.isEmpty()) {
            StringBuilder allGroupDebt = loadAllGroupsDebtForUser(userGroups, user);
            if (!allGroupDebt.isEmpty()) {
                result.append(GROUPS_HEADER).append(allGroupDebt);
            }
        }
    }

    private StringBuilder loadAllGroupsDebtForUser(Map<String, Set<User>> userGroups, User user) {
        StringBuilder result = new StringBuilder();
        userGroups.forEach((groupName, members) -> {
            Map<User, Double> debts = groupDebt.getUserDebtByGroup(groupName, user, groupManager);
            if (!debts.isEmpty()) {
                result.append("* ").append(groupName).append("\n");
                members.stream()
                        .filter(member -> !member.equals(user))
                        .forEach(member -> {
                            String debtString = buildGroupDebtString(member, debts.getOrDefault(member, 0.0));
                            if (!debtString.isEmpty()) result.append(debtString);
                        });
            }
        });
        return result;
    }

    private String buildGroupDebtString(User member, double amount) {
        if (amount == 0) return "";
        String direction = amount > 0 ? "You owe " : "Owes you ";
        return String.format("- %s: %s%.2f LV%n", member.username(), direction, Math.abs(amount));
    }
}

