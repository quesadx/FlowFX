package cr.ac.una.flowfx.model;

import javafx.beans.property.*;

public class NotificationRecipientViewModel {
    private final ObjectProperty<NotificationRecipientPK> notificationRecipientPK = new SimpleObjectProperty<>();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty role = new SimpleStringProperty();

    public NotificationRecipientViewModel() {}
    public NotificationRecipientViewModel(NotificationRecipientDTO dto) {
        if (dto != null) {
            setNotificationRecipientPK(dto.getNotificationRecipientPK());
            setName(dto.getName());
            setRole(dto.getRole());
        }
    }

    public NotificationRecipientDTO toDTO() {
        return new NotificationRecipientDTO(
            getNotificationRecipientPK(),
            getName(),
            getRole()
        );
    }

    public NotificationRecipientPK getNotificationRecipientPK() { return notificationRecipientPK.get(); }
    public void setNotificationRecipientPK(NotificationRecipientPK value) { notificationRecipientPK.set(value); }
    public ObjectProperty<NotificationRecipientPK> notificationRecipientPKProperty() { return notificationRecipientPK; }

    public String getName() { return name.get(); }
    public void setName(String value) { name.set(value); }
    public StringProperty nameProperty() { return name; }

    public String getRole() { return role.get(); }
    public void setRole(String value) { role.set(value); }
    public StringProperty roleProperty() { return role; }
}
