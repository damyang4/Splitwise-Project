package user.friend;

import exception.payedfriend.NoUserDebtException;
import exception.payedfriend.UserDoesNotOweDebtToUserException;
import user.Notification;
import user.User;
import user.group.GroupDebt;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class FriendDebt {
    private final Map<User, Map<User, Double>> debts = new HashMap<>();
    private final FriendNotification notification;

    public FriendDebt(FriendNotification notification) {
        this.notification = notification;
    }

    public Notification splitBillWithFriend(double amount, User owingUser, User receivingUser, String reason) {
        debts.putIfAbsent(owingUser, new HashMap<>());
        debts.putIfAbsent(receivingUser, new HashMap<>());

        double currentOwingUserDebt = GroupDebt.roundDouble(debts.get(owingUser)
                .getOrDefault(receivingUser, 0.0) + amount);
        debts.get(owingUser).put(receivingUser, currentOwingUserDebt);
        debts.get(receivingUser).put(owingUser, debts.get(receivingUser).getOrDefault(owingUser, 0.0) - amount);

        return addOweNotification(owingUser, receivingUser, -currentOwingUserDebt, reason);
    }

    private Notification addOweNotification(User owingUser, User receivingUser, double amount, String reason) {
        Notification notificationMessage = new Notification(Notification.Type.OWE, owingUser, receivingUser, amount,
                reason);
        notification.addNotification(owingUser, notificationMessage);

        serializeToFile("debts.txt");
        return notificationMessage;
    }

    public Map<User, Double> getUserFriendDebts(User user, FriendManager friendManager) {
        if (debts.get(user) == null) {
            return Collections.emptyMap();
        }

        return debts.get(user).entrySet().stream()
                .filter(entry -> friendManager.areFriends(user, entry.getKey()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }

    public Map<User, Double> getDebtsOwnedToUser(User user) {
        return debts.getOrDefault(user, new HashMap<>());
    }

    public void payDebtOfUser(User receivingUser, User owingUser) {
        Map<User, Double> receivingUserDebts = debts.get(receivingUser);
        if (receivingUserDebts == null) {
            throw new NoUserDebtException(receivingUser.username());
        }

        if (receivingUserDebts.get(owingUser) >= 0) {
            throw new UserDoesNotOweDebtToUserException(owingUser.username(), receivingUser.username());
        }

        double amount = receivingUserDebts.get(owingUser);

        debts.get(owingUser).remove(receivingUser);
        debts.get(receivingUser).remove(owingUser);

        setPayedNotification(owingUser, receivingUser, amount);
    }

    private void setPayedNotification(User owingUser, User receivingUser, double amount) {
        Notification oweNotification = notification.findOweNotification(owingUser, receivingUser);
        if (oweNotification == null) {
            throw new IllegalStateException("No matching owe notification found!");
        }

        notification.removeNotification(owingUser, oweNotification);

        String reason = oweNotification.reason();

        notification.addNotification(owingUser, new Notification(
                Notification.Type.PAYED,
                owingUser, receivingUser, amount,  reason
        ));
    }

    public void serializeToFile(String fileName) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(this);
            System.out.println("FriendDebt object has been serialized to file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static FriendDebt deserializeFromFile(String filename, FriendNotification notification) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            FriendDebt friendDebt = (FriendDebt) ois.readObject();
            System.out.println("FriendDebt object has been deserialized from file.");
            return friendDebt;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

}
