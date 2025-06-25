package user;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RegisteredUsers {
    private final Map<String, User> users = new HashMap<>();
    private boolean isModified = false;

    public void register(User user) {
        users.put(user.username(), user);
        isModified = true;
    }

    public User getUser(String username) {
        return users.get(username);
    }

    public Set<User> list() {
        return new HashSet<>(users.values());
    }

    public boolean isModified() {
        return isModified;
    }
}
