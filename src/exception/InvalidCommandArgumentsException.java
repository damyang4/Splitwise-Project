package exception;

// User provides an invalid command format
public class InvalidCommandArgumentsException extends RuntimeException {
    public InvalidCommandArgumentsException(String commandName, int argsCount, String commandExample) {
        super("Invalid arguments: Command '" + commandName + "' expects " + argsCount
                + " arguments. Example: " + commandExample);
    }
}
