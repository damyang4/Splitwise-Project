package exception.splitgroup;

public class UserNotInGroupException extends RuntimeException {
    public UserNotInGroupException(String username, String groupName) {
        super("User '" + username + "' is not a member of the group " + groupName);
    }
}
