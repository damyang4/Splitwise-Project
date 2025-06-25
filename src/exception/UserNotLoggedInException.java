package exception;

public class UserNotLoggedInException extends RuntimeException {
    public UserNotLoggedInException() {
        super("You must be logged in to execute this command.");
    }
}

