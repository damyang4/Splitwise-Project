package exception.splitgroup;

public class GroupDoesNotExistException extends RuntimeException {
    public GroupDoesNotExistException(String groupName) {
        super("Group " + groupName + " does not exist.");
    }
}
