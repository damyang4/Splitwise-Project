package exception.addfriend;

public class UserSelfFriendshipException extends RuntimeException {
    public UserSelfFriendshipException() {
        super("You cannot add yourself as a friend.");
    }
}
