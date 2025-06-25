package exception.addfriend;

public class UserAlreadyFriendException extends RuntimeException {
    public UserAlreadyFriendException(String username) {
        super("User '" + username + "' is already a friend.");
    }
}
