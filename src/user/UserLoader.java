package user;

import errorlogger.ErrorLogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class UserLoader {

    private static final String REG_USERS_FILE_NAME = "users.txt";

    public static RegisteredUsers loadFromFile() {
        createFileIfNotExists();

        RegisteredUsers registeredUsers = new RegisteredUsers();

        try (BufferedReader reader = new BufferedReader(new FileReader(REG_USERS_FILE_NAME))) {
            Set<User> users = reader.lines()
                    .map(UserLoader::parseUser)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            users.forEach(registeredUsers::register);
        } catch (IOException e) {
            System.err.println("Error loading registered users from file: " + e.getMessage());
        }

        return registeredUsers;
    }

    private static void createFileIfNotExists() {
        File userFile = new File(REG_USERS_FILE_NAME);
        try {
            if (userFile.createNewFile()) {
                System.out.println("File created: " + REG_USERS_FILE_NAME);
            } else {
                System.out.println("File already exists: " + REG_USERS_FILE_NAME);
            }
        } catch (IOException e) {
            System.out.println("Unable to create users file. Logs have been saved to 'server_errors.log'. " +
                    "Please contact the administrator.");
            ErrorLogger.logError("Failed to create file: " + REG_USERS_FILE_NAME, e);
        }
    }

    private static User parseUser(String line) {
        String[] parts = line.split(",");
        if (parts.length == 2) {
            return new User(parts[0].trim(), parts[1].trim());
        } else {
            System.err.println("Skipping invalid user entry: " + line);
            return null;
        }
    }
}
