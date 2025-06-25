package exception.payedfriend;

public class NoUserDebtException extends RuntimeException {
    public NoUserDebtException(String username) {
        super("User '" + username + "' has no debts.");
    }
}
