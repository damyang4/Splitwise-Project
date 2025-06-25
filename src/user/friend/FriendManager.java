package user.friend;

import user.User;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FriendManager {
    Map<User, Set<User>> friends = new HashMap<>();

    public boolean addFriend(User user1, User user2) {
        if (user1.equals(user2)) return false;

        friends.computeIfAbsent(user1, k -> new HashSet<>()).add(user2);
        friends.computeIfAbsent(user2, k -> new HashSet<>()).add(user1);
        return true;
    }

    public boolean areFriends(User user1, User user2) {
        return friends.getOrDefault(user1, new HashSet<>()).contains(user2);
    }

    public User getFriendByUsername(User user, String friendUsername) {
        Set<User> userFriends = friends.get(user);

        if (userFriends == null) {
            return null;
        }

        for (User currUser : userFriends) {
            if (currUser.username().equals(friendUsername)) {
                return currUser;
            }
        }

        return null;
    }
}
