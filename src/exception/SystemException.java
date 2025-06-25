package exception;

// General system error (e.g., I/O failure, database issues)
public class SystemException extends RuntimeException {
    public SystemException(String message, Throwable cause) {
    super(message, cause);
  }
}
