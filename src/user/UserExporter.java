package user;

import errorlogger.ErrorLogger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

public class UserExporter {
    private final RegisteredUsers registeredUsers;

    private static final String REG_USERS_FILE_NAME = "users.txt";

    private static final String NO_CHANGES_MESSAGE = "No changes detected. Skipping save.";
    private static final String EMPTY_USER_LIST_MESSAGE = "User list is empty. Skipping save.";
    private static final String SAVE_SUCCESS_MESSAGE = "Registered users updated successfully in file.";
    private static final String ERROR_UPDATING_FILE_MESSAGE = "Error updating registered users in the file.";
    private static final String INVALID_USER_MESSAGE = "Skipping invalid user entry: ";
    private static final String FAILED_WRITE_MESSAGE = "Failed to write user to file: ";

    public UserExporter(RegisteredUsers registeredUsers) {
        this.registeredUsers = registeredUsers;
    }

    public void saveRegisteredUsersToFile() {
        if (!registeredUsers.isModified()) {
            System.out.println(NO_CHANGES_MESSAGE);
            return;
        }

        Set<User> registeredUsersList = registeredUsers.list();
        if (registeredUsersList.isEmpty()) {
            System.out.println(EMPTY_USER_LIST_MESSAGE);
            return;
        }

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(REG_USERS_FILE_NAME, false))) {
            for (User user : registeredUsersList) {
                writeUserToFile(bufferedWriter, user);
            }
            System.out.println(SAVE_SUCCESS_MESSAGE);
        } catch (IOException e) {
            handleIOException(e);
        }
    }

    private void writeUserToFile(BufferedWriter bufferedWriter, User user) {
        try {
            validateUser(user);
            bufferedWriter.write(user.username() + "," + user.password());
            bufferedWriter.newLine();
        } catch (IllegalArgumentException e) {
            System.err.println(INVALID_USER_MESSAGE + e.getMessage());
        } catch (IOException e) {
            System.err.println(FAILED_WRITE_MESSAGE + user.username());
        }
    }

    private void handleIOException(IOException exception) {
        System.err.println(ERROR_UPDATING_FILE_MESSAGE + " See logs for details.");
        ErrorLogger.logError(ERROR_UPDATING_FILE_MESSAGE, exception);
    }

    private void validateUser(User user) {
        if (user.username() == null || user.username().isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (user.password() == null || user.password().isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
    }
}
