package exception.creategroup;

public class GroupAlreadyExistsException extends RuntimeException {
    public GroupAlreadyExistsException() {
        super("Group already exists.");
    }
}
