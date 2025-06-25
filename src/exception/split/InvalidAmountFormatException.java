package exception.split;

public class InvalidAmountFormatException extends RuntimeException {
    public InvalidAmountFormatException() {
        super("Invalid amount format. Please enter a valid number.");
    }
}
