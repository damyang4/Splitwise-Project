package errorlogger;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ErrorLogger {
    private static final Logger LOGGER = Logger.getLogger(ErrorLogger.class.getName());
    private static final String SERVER_ERRORS_FILE_NAME = "server_errors.log";

    static {
        try {
            FileHandler fileHandler = new FileHandler(SERVER_ERRORS_FILE_NAME, true);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
            LOGGER.setLevel(Level.SEVERE);
        } catch (IOException e) {
            System.err.println("Error setting up log file: " + e.getMessage());
        }
    }

    public static void logError(String message, Throwable e) {
        if (e != null) {
            LOGGER.log(Level.SEVERE, message, e);
        } else {
            LOGGER.log(Level.SEVERE, message);
        }
    }
}
