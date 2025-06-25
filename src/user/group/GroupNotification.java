package user.group;

import user.friend.FriendNotification;

import java.util.HashMap;
import java.util.Map;

public class GroupNotification {
    private final Map<String, FriendNotification> notifications = new HashMap<>();

    public FriendNotification getGroupNotifications(String groupName) {
        return notifications.computeIfAbsent(groupName, k -> new FriendNotification());
    }
}
