package user.friend;

import org.junit.jupiter.api.Test;
import user.User;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FriendManagerTest {

    @Test
    void testAddFriendSuccessfulFriendAddition() {
        FriendManager friendManager = new FriendManager();
        User user1 = new User("User1", "password1");
        User user2 = new User("User2", "password2");

        boolean result = friendManager.addFriend(user1, user2);

        assertTrue(result);
        assertTrue(friendManager.areFriends(user1, user2));
        assertTrue(friendManager.areFriends(user2, user1));
    }

    @Test
    void testAddFriendSameUserFails() {
        FriendManager friendManager = new FriendManager();
        User user = new User("User", "password");

        boolean result = friendManager.addFriend(user, user);

        assertFalse(result);
    }

    @Test
    void testAddFriendAlreadyFriends() {
        FriendManager friendManager = new FriendManager();
        User user1 = new User("User1", "password1");
        User user2 = new User("User2", "password2");
        friendManager.addFriend(user1, user2);

        boolean result = friendManager.addFriend(user1, user2);

        assertTrue(result);
        assertTrue(friendManager.areFriends(user1, user2));
    }

    @Test
    void testAreFriendsNotFriends() {
        FriendManager friendManager = new FriendManager();
        User user1 = new User("User1", "password1");
        User user2 = new User("User2", "password2");

        boolean result = friendManager.areFriends(user1, user2);

        assertFalse(result);
    }

    @Test
    void testAreFriendsAfterFriendshipEstablished() {
        FriendManager friendManager = new FriendManager();
        User user1 = new User("User1", "password1");
        User user2 = new User("User2", "password2");
        friendManager.addFriend(user1, user2);

        boolean result1 = friendManager.areFriends(user1, user2);
        boolean result2 = friendManager.areFriends(user2, user1);

        assertTrue(result1);
        assertTrue(result2);
    }
}