package user.friend;

import exception.payedfriend.NoUserDebtException;
import exception.payedfriend.UserDoesNotOweDebtToUserException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import user.Notification;
import user.User;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class FriendDebtTest {

    @Test
    void testSplitBillWithFriendShouldAddDebtForOwingUser() {
        User owingUser = new User("john_doe", "password123");
        User receivingUser = new User("jane_doe", "password456");
        FriendNotification mockNotification = mock(FriendNotification.class);
        FriendDebt friendDebt = new FriendDebt(mockNotification);

        double amount = 50.0;
        String reason = "Dinner";

        Notification result = friendDebt.splitBillWithFriend(amount, owingUser, receivingUser, reason);

        assertEquals(Notification.Type.OWE, result.type());
        assertEquals(owingUser, result.owingUser());
        assertEquals(receivingUser, result.receivingUser());
        assertEquals(-amount, result.amount());
        assertEquals(reason, result.reason());
    }

    @Test
    void testSplitBillWithFriendShouldUpdateExistingDebt() {
        User owingUser = new User("john_doe", "password123");
        User receivingUser = new User("jane_doe", "password456");
        FriendNotification mockNotification = mock(FriendNotification.class);
        FriendDebt friendDebt = new FriendDebt(mockNotification);

        double initialAmount = 30.0;
        double additionalAmount = 20.0;
        String reason = "Groceries";

        friendDebt.splitBillWithFriend(initialAmount, owingUser, receivingUser, reason);
        Notification result = friendDebt.splitBillWithFriend(additionalAmount, owingUser, receivingUser, reason);

        assertEquals(-50.0, result.amount());
    }

    @Test
    void testSplitBillWithFriendShouldCreateNotification() {
        User owingUser = new User("john_doe", "password123");
        User receivingUser = new User("jane_doe", "password456");
        FriendNotification mockNotification = mock(FriendNotification.class);
        FriendDebt friendDebt = new FriendDebt(mockNotification);

        double amount = 75.0;
        String reason = "Trip";

        friendDebt.splitBillWithFriend(amount, owingUser, receivingUser, reason);

        verify(mockNotification, times(1)).addNotification(eq(owingUser), any(Notification.class));
    }

    @Test
    void testSplitBillWithFriendShouldHandleZeroDebtGracefully() {
        User owingUser = new User("john_doe", "password123");
        User receivingUser = new User("jane_doe", "password456");
        FriendNotification mockNotification = mock(FriendNotification.class);
        FriendDebt friendDebt = new FriendDebt(mockNotification);

        double amount = 0.0;
        String reason = "No Actual Debt";

        Notification result = friendDebt.splitBillWithFriend(amount, owingUser, receivingUser, reason);

        assertEquals(0.0, Math.abs(result.amount()));
        assertEquals(reason, result.reason());
    }

    @Test
    void testSplitBillWithFriendShouldHandleNegativeAmounts() {
        User owingUser = new User("john_doe", "password123");
        User receivingUser = new User("jane_doe", "password456");
        FriendNotification mockNotification = mock(FriendNotification.class);
        FriendDebt friendDebt = new FriendDebt(mockNotification);

        double amount = -20.0;
        String reason = "Overpayment";

        Notification result = friendDebt.splitBillWithFriend(amount, owingUser, receivingUser, reason);

        assertEquals(Notification.Type.OWE, result.type());
        assertEquals(owingUser, result.owingUser());
        assertEquals(receivingUser, result.receivingUser());
        assertEquals(20.0, result.amount());
        assertEquals(reason, result.reason());
    }
}