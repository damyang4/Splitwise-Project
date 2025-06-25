package user.friend;

import org.junit.jupiter.api.Test;
import user.Notification;
import user.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FriendNotificationTest {

    @Test
    public void testAddNotificationForNewUser() {
        FriendNotification friendNotification = new FriendNotification();
        User user = new User("testUser", "password123");
        Notification notification = new Notification(
                Notification.Type.OWE,
                user,
                new User("otherUser", "password456"),
                100.0,
                "Dinner"
        );

        friendNotification.addNotification(user, notification);

        List<Notification> notifications = friendNotification.getNotifications(user);
        assertEquals(1, notifications.size());
        assertEquals(notification, notifications.get(0));
    }

    @Test
    public void testAddNotificationForExistingUserWithExistingNotifications() {
        FriendNotification friendNotification = new FriendNotification();
        User user = new User("testUser", "password123");
        Notification notification1 = new Notification(
                Notification.Type.OWE,
                user,
                new User("otherUser", "password456"),
                100.0,
                "Dinner"
        );
        Notification notification2 = new Notification(
                Notification.Type.PAYED,
                user,
                new User("thirdUser", "password789"),
                50.0,
                "Payment"
        );
        friendNotification.addNotification(user, notification1);

        friendNotification.addNotification(user, notification2);

        List<Notification> notifications = friendNotification.getNotifications(user);
        assertEquals(2, notifications.size());
        assertTrue(notifications.contains(notification1));
        assertTrue(notifications.contains(notification2));
    }

    @Test
    public void testAddNotificationForDifferentUsers() {
        FriendNotification friendNotification = new FriendNotification();
        User user1 = new User("user1", "password1");
        User user2 = new User("user2", "password2");
        Notification notification1 = new Notification(
                Notification.Type.OWE,
                user1,
                user2,
                200.0,
                "Fees"
        );
        Notification notification2 = new Notification(
                Notification.Type.PAYED,
                user2,
                user1,
                121.0,
                "Refund"
        );

        friendNotification.addNotification(user1, notification1);
        friendNotification.addNotification(user2, notification2);

        List<Notification> notificationsForUser1 = friendNotification.getNotifications(user1);
        List<Notification> notificationsForUser2 = friendNotification.getNotifications(user2);

        assertEquals(1, notificationsForUser1.size());
        assertEquals(notification1, notificationsForUser1.get(0));
        assertEquals(1, notificationsForUser2.size());
        assertEquals(notification2, notificationsForUser2.get(0));
    }
}