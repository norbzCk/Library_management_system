package Notification;

// SMS channel. Same as EmailNotification: delivery is simulated silently,
// the message itself is stored in the member's in-app notifications.
public class SMSNotification implements Notification {
    private String phoneNumber;

    public SMSNotification(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public void sendNotification(String message) {
        // Intentionally silent: the message is already saved to the member's inbox.
    }

    @Override
    public String getNotificationType() {
        return "SMS";
    }
}
