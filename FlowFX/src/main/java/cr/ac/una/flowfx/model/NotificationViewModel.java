package cr.ac.una.flowfx.model;

import java.util.Date;
import java.util.Objects;
import javafx.beans.property.*;

/**
 * View model for notifications used by JavaFX bindings.
 *
 * <p>This class wraps observable properties and provides convenience conversion
 * to and from {@link NotificationDTO} while preserving existing behavior.</p>
 */
public class NotificationViewModel {

    private final LongProperty id = new SimpleLongProperty();
    private final StringProperty subject = new SimpleStringProperty();
    private final StringProperty message = new SimpleStringProperty();
    private final ObjectProperty<Date> sentAt = new SimpleObjectProperty<>();
    private final ObjectProperty<Character> status =
        new SimpleObjectProperty<>();
    private final StringProperty eventType = new SimpleStringProperty();

    /**
     * Default constructor.
     */
    public NotificationViewModel() {
        // Intentionally empty.
    }

    /**
     * Creates a view model initialized from a DTO.
     *
     * @param dto source DTO, may be null
     */
    public NotificationViewModel(NotificationDTO dto) {
        if (dto != null) {
            setId(dto.getId() == null ? 0L : dto.getId());
            setSubject(dto.getSubject());
            setMessage(dto.getMessage());
            setSentAt(dto.getSentAt());
            setStatus(dto.getStatus());
            setEventType(dto.getEventType());
        }
    }

    /**
     * Converts this view model to a DTO.
     *
     * @return a NotificationDTO representing the current state
     */
    public NotificationDTO toDTO() {
        return new NotificationDTO(
            getId() == 0L ? null : getId(),
            getSubject(),
            getMessage(),
            getSentAt(),
            getStatus(),
            getEventType()
        );
    }

    public long getId() {
        return id.get();
    }

    public void setId(long value) {
        id.set(value);
    }

    public LongProperty idProperty() {
        return id;
    }

    public String getSubject() {
        return subject.get();
    }

    public void setSubject(String value) {
        subject.set(value);
    }

    public StringProperty subjectProperty() {
        return subject;
    }

    public String getMessage() {
        return message.get();
    }

    public void setMessage(String value) {
        message.set(value);
    }

    public StringProperty messageProperty() {
        return message;
    }

    public Date getSentAt() {
        return sentAt.get();
    }

    public void setSentAt(Date value) {
        sentAt.set(value);
    }

    public ObjectProperty<Date> sentAtProperty() {
        return sentAt;
    }

    public Character getStatus() {
        return status.get();
    }

    public void setStatus(Character value) {
        status.set(value);
    }

    public ObjectProperty<Character> statusProperty() {
        return status;
    }

    public String getEventType() {
        return eventType.get();
    }

    public void setEventType(String value) {
        eventType.set(value);
    }

    public StringProperty eventTypeProperty() {
        return eventType;
    }

    @Override
    public int hashCode() {
        // Use DTO identity semantics (id) for hashCode
        return Objects.hashCode(id.get() == 0L ? null : id.get());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof NotificationViewModel)) {
            return false;
        }
        NotificationViewModel other = (NotificationViewModel) obj;
        Long thisId = getId() == 0L ? null : getId();
        Long otherId = other.getId() == 0L ? null : other.getId();
        return Objects.equals(thisId, otherId);
    }

    @Override
    public String toString() {
        return (
            "NotificationViewModel{id=" +
            (getId() == 0L ? "null" : getId()) +
            ", subject=" +
            getSubject() +
            "}"
        );
    }
}
