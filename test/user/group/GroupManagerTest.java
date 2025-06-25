package user.group;

import org.junit.jupiter.api.Test;
import user.User;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GroupManagerTest {

    @Test
    void createGroupWithNewGroupName() {
        GroupManager groupManager = new GroupManager();
        Set<User> members = new HashSet<>();
        members.add(new User("user1", "password1"));
        members.add(new User("user2", "password2"));
        String groupName = "group1";

        boolean result = groupManager.createGroup(groupName, members);

        assertTrue(result);
        assertEquals(members, groupManager.getGroup(groupName));
    }

    @Test
    void createGroupWithExistingGroupName() {
        GroupManager groupManager = new GroupManager();
        String groupName = "group1";
        Set<User> membersFirstGroup = new HashSet<>();
        membersFirstGroup.add(new User("user1", "password1"));

        Set<User> membersSecondGroup = new HashSet<>();
        membersSecondGroup.add(new User("user2", "password2"));

        groupManager.createGroup(groupName, membersFirstGroup);

        boolean result = groupManager.createGroup(groupName, membersSecondGroup);

        assertFalse(result);
        assertEquals(membersFirstGroup, groupManager.getGroup(groupName));
    }

    @Test
    void createEmptyGroup() {
        GroupManager groupManager = new GroupManager();
        String groupName = "group1";
        Set<User> emptyMembers = new HashSet<>();

        boolean result = groupManager.createGroup(groupName, emptyMembers);

        assertTrue(result);
        assertTrue(groupManager.getGroup(groupName).isEmpty());
    }

    @Test
    void createGroupsWithSameMembers() {
        GroupManager groupManager = new GroupManager();
        Set<User> members = new HashSet<>();
        members.add(new User("user1", "password1"));

        String groupName1 = "group1";
        String groupName2 = "group2";

        boolean result1 = groupManager.createGroup(groupName1, members);
        boolean result2 = groupManager.createGroup(groupName2, members);

        assertTrue(result1);
        assertTrue(result2);
        assertEquals(members, groupManager.getGroup(groupName1));
        assertEquals(members, groupManager.getGroup(groupName2));
    }
}