package command.implementation;

import exception.InvalidCommandArgumentsException;
import exception.register.UsernameAlreadyTakenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import user.RegisteredUsers;
import user.User;
import user.password.PasswordHasher;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegisterCommandTest {
    private RegisteredUsers registeredUsersMock;
    private RegisterCommand registerCommand;
    private PasswordHasher passwordHasher;

    @BeforeEach
    void setUp() {
        registeredUsersMock = mock(RegisteredUsers.class);
        passwordHasher = mock(PasswordHasher.class);
        registerCommand = new RegisterCommand(registeredUsersMock, passwordHasher);
    }
    @Test
    public void executeShouldRegisterUserWhenArgumentsAreValid() {
        String[] args = {"testUser", "testPassword"};
        SocketChannel clientChannelMock = mock(SocketChannel.class);

        String result = registerCommand.execute(null, args, clientChannelMock);
        assertEquals("User testUser registered successfully.", result);
    }

    @Test
    public void executeShouldThrowInvalidCommandArgumentsExceptionWhenArgumentsAreMissing() {
        String[] args = {"testUser"};

        InvalidCommandArgumentsException exception = assertThrows(
                InvalidCommandArgumentsException.class,
                () -> registerCommand.execute(null, args, null)
        );
        assertEquals("Invalid arguments: Command 'register' expects 2 arguments. Example: register <username> <password>", exception.getMessage());
    }

    @Test
    public void executeShouldThrowUsernameAlreadyTakenExceptionWhenUsernameIsAlreadyTaken() {
        when(registeredUsersMock.getUser("existingUser")).thenReturn(new User("existingUser", "hashedPassword"));
        String[] args = {"existingUser", "testPassword"};

        UsernameAlreadyTakenException exception = assertThrows(
                UsernameAlreadyTakenException.class,
                () -> registerCommand.execute(null, args, null)
        );
        assertEquals("Username 'existingUser' is already taken.", exception.getMessage());
    }
}