package cr.ac.una.flowfx.model;

import java.util.Date;
import java.util.Objects;

/**
 * Data Transfer Object for {@code Notification} entity.
 *
 * <p>Preserves the original fields and accessors so existing code can continue
 * to use this DTO without modification.</p>
 */
public class NotificationDTO {

    private Long id;
    private String subject;
    private String message;
    private Date sentAt;
    private Character status;
    private String eventType;

    public NotificationDTO() {}

    public NotificationDTO(
        Long id,
        String subject,
        String message,
        Date sentAt,
        Character status,
        String eventType
    ) {
        this.id = id;
        this.subject = subject;
        this.message = message;
        this.sentAt = sentAt;
        this.status = status;
        this.eventType = eventType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getSentAt() {
        return sentAt;
    }

    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }

    public Character getStatus() {
        return status;
    }

    public void setStatus(Character status) {
        this.status = status;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof NotificationDTO)) {
            return false;
        }
        NotificationDTO other = (NotificationDTO) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return "NotificationDTO{id=" + id + ", subject=" + subject + "}";
    }
}
