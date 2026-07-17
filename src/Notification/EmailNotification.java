package Notification;

// Email channel. The actual delivery is simulated — the real notification
// lands in the member's in-app inbox (see LibrarySystem.notifyMember).
public class EmailNotification implements Notification {
    private String emailAddress;

    public EmailNotification(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @Override
    public void sendNotification(String message) {
        // Intentionally silent: we don't want email/SMS noise on the console.
    }

    @Override
    public String getNotificationType() {
        return "Email";
    }
}
