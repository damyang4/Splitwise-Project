package exception.split;

public class UserNotInFriendListException extends RuntimeException {
    public UserNotInFriendListException(String username) {
        super("User '" + username + "' is not in your friend list.");
    }
}
