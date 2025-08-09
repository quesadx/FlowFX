package cr.ac.una.flowfx.model;

import javafx.beans.property.*;
import java.util.Date;

public class NotificationViewModel {
    private final LongProperty id = new SimpleLongProperty();
    private final StringProperty subject = new SimpleStringProperty();
    private final StringProperty message = new SimpleStringProperty();
    private final ObjectProperty<Date> sentAt = new SimpleObjectProperty<>();
    private final ObjectProperty<Character> status = new SimpleObjectProperty<>();
    private final StringProperty eventType = new SimpleStringProperty();

    public NotificationViewModel() {}
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

    public long getId() { return id.get(); }
    public void setId(long value) { id.set(value); }
    public LongProperty idProperty() { return id; }

    public String getSubject() { return subject.get(); }
    public void setSubject(String value) { subject.set(value); }
    public StringProperty subjectProperty() { return subject; }

    public String getMessage() { return message.get(); }
    public void setMessage(String value) { message.set(value); }
    public StringProperty messageProperty() { return message; }

    public Date getSentAt() { return sentAt.get(); }
    public void setSentAt(Date value) { sentAt.set(value); }
    public ObjectProperty<Date> sentAtProperty() { return sentAt; }

    public Character getStatus() { return status.get(); }
    public void setStatus(Character value) { status.set(value); }
    public ObjectProperty<Character> statusProperty() { return status; }

    public String getEventType() { return eventType.get(); }
    public void setEventType(String value) { eventType.set(value); }
    public StringProperty eventTypeProperty() { return eventType; }
}
