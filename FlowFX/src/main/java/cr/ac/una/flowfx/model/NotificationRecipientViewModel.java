package cr.ac.una.flowfx.model;

import java.util.Objects;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * View model for {@link NotificationRecipient} used by JavaFX bindings.
 *
 * <p>This class wraps observable properties and provides conversion to and from
 * {@link NotificationRecipientDTO}. It preserves the public API while improving
 * readability and adding value semantics.</p>
 */
public class NotificationRecipientViewModel {

    private final ObjectProperty<
        NotificationRecipientPK
    > notificationRecipientPK = new SimpleObjectProperty<>();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty role = new SimpleStringProperty();

    /**
     * Default constructor.
     */
    public NotificationRecipientViewModel() {
        // no-op
    }

    /**
     * Creates a view model initialized from the provided DTO.
     *
     * @param dto source DTO, may be null
     */
    public NotificationRecipientViewModel(NotificationRecipientDTO dto) {
        if (dto != null) {
            setNotificationRecipientPK(dto.getNotificationRecipientPK());
            setName(dto.getName());
            setRole(dto.getRole());
        }
    }

    /**
     * Converts this view model to a DTO.
     *
     * @return a new {@link NotificationRecipientDTO} instance
     */
    public NotificationRecipientDTO toDTO() {
        return new NotificationRecipientDTO(
            getNotificationRecipientPK(),
            getName(),
            getRole()
        );
    }

    public NotificationRecipientPK getNotificationRecipientPK() {
        return notificationRecipientPK.get();
    }

    public void setNotificationRecipientPK(NotificationRecipientPK value) {
        notificationRecipientPK.set(value);
    }

    public ObjectProperty<
        NotificationRecipientPK
    > notificationRecipientPKProperty() {
        return notificationRecipientPK;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String value) {
        name.set(value);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getRole() {
        return role.get();
    }

    public void setRole(String value) {
        role.set(value);
    }

    public StringProperty roleProperty() {
        return role;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(notificationRecipientPK.get());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof NotificationRecipientViewModel)) {
            return false;
        }
        NotificationRecipientViewModel other =
            (NotificationRecipientViewModel) obj;
        return Objects.equals(
            this.getNotificationRecipientPK(),
            other.getNotificationRecipientPK()
        );
    }

    @Override
    public String toString() {
        return (
            "NotificationRecipientViewModel{notificationRecipientPK=" +
            getNotificationRecipientPK() +
            ", name=" +
            getName() +
            ", role=" +
            getRole() +
            "}"
        );
    }
}
