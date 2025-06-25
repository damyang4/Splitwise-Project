package exception.register;

public class UserAlreadyRegisteredException extends RuntimeException {
    public UserAlreadyRegisteredException(String username) {
        super("User '" + username + "' is already registered");
    }
}
