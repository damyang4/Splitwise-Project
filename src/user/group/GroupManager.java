package user.group;

import user.User;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class GroupManager {
    Map<String, Set<User>> groups = new HashMap<>();

    public boolean createGroup(String groupName, Set<User> members) {
        if (groups.get(groupName) != null) {
            return false;
        }

        groups.put(groupName, new HashSet<>(members));
        return true;
    }

    public Map<String, Set<User>> getGroupsContainingUser(User user) {
        return groups.entrySet().stream()
                .filter(entry -> entry.getValue().contains(user))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Set<User> getGroup(String groupName) {
        return groups.getOrDefault(groupName, Collections.emptySet());
    }

    public boolean isPartOfGroup(String groupName, User user) {
        return groups.get(groupName).contains(user);
    }
}
