package Notification;

public class EmailNotification implements Notification {
    private String emailAddress;

    public EmailNotification(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @Override
    public void sendNotification(String message) {
        System.out.println("[EMAIL → " + emailAddress + "] " + message);
    }

    @Override
    public String getNotificationType() {
        return "Email";
    }
}
