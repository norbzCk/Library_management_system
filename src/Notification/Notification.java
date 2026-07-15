package Notification;

public interface Notification {
    void sendNotification(String message);

    String getNotificationType();
}
