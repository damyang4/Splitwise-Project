package user.friend;

import user.Notification;
import user.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendNotification {
    private final Map<User, List<Notification>> notifications = new HashMap<>();

    public void addNotification(User user, Notification notification) {
        notifications.computeIfAbsent(user, k -> new ArrayList<>()).add(notification);
    }

    public void removeNotification(User user, Notification notification) {
        List<Notification> userNotifications = notifications.get(user);
        if (userNotifications != null) {
            userNotifications.remove(notification);
        }
    }

    public List<Notification> getNotifications(User user) {
        return notifications.getOrDefault(user, Collections.emptyList());
    }

    public Notification findOweNotification(User owingUser, User receivingUser) {
        List<Notification> userNotifications = notifications.get(owingUser);
        if (userNotifications == null) {
            return null;
        }

        return userNotifications.stream()
                .filter(n -> n.type() == Notification.Type.OWE
                        && n.owingUser().equals(owingUser)
                        && n.receivingUser().equals(receivingUser))
                .findFirst()
                .orElse(null);
    }
}

