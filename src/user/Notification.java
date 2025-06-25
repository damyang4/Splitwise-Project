package user;

import java.io.Serializable;
import java.lang.foreign.SegmentAllocator;
import java.util.Objects;

public record Notification(Notification.Type type,
                           User owingUser,
                           User receivingUser,
                           double amount,
                           String reason) implements Serializable {
    public enum Type {
        OWE, PAYED
    }

    @Override
    public String toString() {
        return switch (type) {
            case OWE -> "You owe " + receivingUser.username() + " " + -amount + " LV [" + reason + "].";
            case PAYED -> "User '" + receivingUser.username() + "' approved your payment " + -amount +
                    " LV [" + reason + "].";
        };
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Notification that = (Notification) obj;
        return Double.compare(amount, that.amount) == 0 &&
                type == that.type &&
                Objects.equals(owingUser, that.owingUser) &&
                Objects.equals(receivingUser, that.receivingUser) &&
                Objects.equals(reason, that.reason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, owingUser, receivingUser, amount, reason);
    }
}