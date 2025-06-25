package exception.payedfriend;

public class UserDoesNotOweDebtToUserException extends RuntimeException {
    public UserDoesNotOweDebtToUserException(String owingUsername, String receivingUser) {
        super("User '" + owingUsername + "' does not have any outstanding debt to user '" + receivingUser + "'.");
    }
}
