package Notification;

public class SMSNotification implements Notification {
    private String phoneNumber;

    public SMSNotification(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public void sendNotification(String message) {
        System.out.println("[SMS → " + phoneNumber + "] " + message);
    }

    @Override
    public String getNotificationType() {
        return "SMS";
    }
}
