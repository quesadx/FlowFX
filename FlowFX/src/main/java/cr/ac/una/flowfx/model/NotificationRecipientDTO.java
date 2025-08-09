package cr.ac.una.flowfx.model;

public class NotificationRecipientDTO {
    private NotificationRecipientPK notificationRecipientPK;
    private String name;
    private String role;

    public NotificationRecipientDTO() {}

    public NotificationRecipientDTO(NotificationRecipientPK notificationRecipientPK, String name, String role) {
        this.notificationRecipientPK = notificationRecipientPK;
        this.name = name;
        this.role = role;
    }

    public NotificationRecipientPK getNotificationRecipientPK() { return notificationRecipientPK; }
    public void setNotificationRecipientPK(NotificationRecipientPK notificationRecipientPK) { this.notificationRecipientPK = notificationRecipientPK; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
