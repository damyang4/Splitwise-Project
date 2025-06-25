package exception.payedgroup;

public class NoUserDebtForGroupException extends RuntimeException {
    public NoUserDebtForGroupException(String username, String groupName) {
        super("User '" + username + "' has no debts int the group '" + groupName + "'.");
    }
}
