package user.group;

import exception.payedgroup.NoUserDebtForGroupException;
import user.User;
import user.friend.FriendDebt;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class GroupDebt {
    private final Map<String, FriendDebt> debts = new HashMap<>();
    private final GroupNotification notification;

    private static final int ROUNDING_SCALE = 2;

    public GroupDebt(GroupNotification notification) {
        this.notification = notification;
    }

    public void splitBillWithUserGroup(double amount, User user, String groupName, Set<User> groupMembers,
                                       String reason) {
        double splitAmount = amount / groupMembers.size();
        double splitAmountRounded = roundDouble(splitAmount);

        FriendDebt groupMembersDebt = debts.getOrDefault(groupName,
                new FriendDebt(notification.getGroupNotifications(groupName)));

        for (User member : groupMembers) {
            if (member == user) {
                continue;
            }

            groupMembersDebt.splitBillWithFriend(splitAmountRounded, member, user, reason);
        }

        debts.put(groupName, groupMembersDebt);
    }

    public static double roundDouble(Double value) {
        return new BigDecimal(value)
                .setScale(ROUNDING_SCALE, RoundingMode.HALF_UP)
                .doubleValue();
    }

    public Map<User, Double> getUserDebtByGroup(String groupName, User user, GroupManager groupManager) {
        return debts.get(groupName).getDebtsOwnedToUser(user).entrySet().stream()
                .filter(entry -> groupManager.isPartOfGroup(groupName, entry.getKey()) && entry.getValue() != 0)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }

    public void payDebtOfGroupMember(String groupName, User owingUser, User receivingUser) {
        FriendDebt groupDebtToTheReceivingUser = debts.get(groupName);
        if (groupDebtToTheReceivingUser == null) {
            throw new NoUserDebtForGroupException(receivingUser.username(), groupName);
        }

        groupDebtToTheReceivingUser.payDebtOfUser(receivingUser, owingUser);
    }

    public FriendDebt getDebts(String groupName) {
        return debts.get(groupName);
    }
}
