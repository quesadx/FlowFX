package cr.ac.una.flowfx.model;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * JPA entity representing a notification record.
 *
 * <p>This class maps to the NOTIFICATION table and preserves the original
 * named queries and relationships used by the application. Equals and hashCode
 * implementations are based on the identifier and implemented with
 * {@link java.util.Objects} for clarity.</p>
 */
@Entity
@Table(name = "NOTIFICATION")
@NamedQueries(
    {
        @NamedQuery(
            name = "Notification.findAll",
            query = "SELECT n FROM Notification n"
        ),
        @NamedQuery(
            name = "Notification.findById",
            query = "SELECT n FROM Notification n WHERE n.id = :id"
        ),
        @NamedQuery(
            name = "Notification.findBySubject",
            query = "SELECT n FROM Notification n WHERE n.subject = :subject"
        ),
        @NamedQuery(
            name = "Notification.findByMessage",
            query = "SELECT n FROM Notification n WHERE n.message = :message"
        ),
        @NamedQuery(
            name = "Notification.findBySentAt",
            query = "SELECT n FROM Notification n WHERE n.sentAt = :sentAt"
        ),
        @NamedQuery(
            name = "Notification.findByStatus",
            query = "SELECT n FROM Notification n WHERE n.status = :status"
        ),
        @NamedQuery(
            name = "Notification.findByEventType",
            query = "SELECT n FROM Notification n WHERE n.eventType = :eventType"
        ),
    }
)
public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @Column(name = "NOTIFICATION_ID")
    private Long id;

    @Basic(optional = false)
    @Column(name = "SUBJECT")
    private String subject;

    @Basic(optional = false)
    @Column(name = "MESSAGE")
    private String message;

    @Column(name = "SENT_AT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date sentAt;

    @Basic(optional = false)
    @Column(name = "STATUS")
    private Character status;

    @Basic(optional = false)
    @Column(name = "EVENT_TYPE")
    private String eventType;

    @JoinColumn(name = "PROJECT_ID", referencedColumnName = "PROJECT_ID")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Project projectId;

    @JoinColumn(name = "ACTIVITY_ID", referencedColumnName = "ACTIVITY_ID")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private ProjectActivity activityId;

    @OneToMany(
        cascade = CascadeType.ALL,
        mappedBy = "notification",
        fetch = FetchType.LAZY
    )
    private List<NotificationRecipient> recipientList;

    /**
     * Default constructor required by JPA.
     */
    public Notification() {}

    /**
     * Convenience constructor with identifier.
     *
     * @param id identifier
     */
    public Notification(Long id) {
        this.id = id;
    }

    /**
     * Convenience constructor preserving previous semantics.
     */
    public Notification(
        Long id,
        String subject,
        String message,
        Character status,
        String eventType
    ) {
        this.id = id;
        this.subject = subject;
        this.message = message;
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

    public Project getProjectId() {
        return projectId;
    }

    public void setProjectId(Project projectId) {
        this.projectId = projectId;
    }

    public ProjectActivity getActivityId() {
        return activityId;
    }

    public void setActivityId(ProjectActivity activityId) {
        this.activityId = activityId;
    }

    public List<NotificationRecipient> getRecipientList() {
        return recipientList;
    }

    public void setRecipientList(List<NotificationRecipient> recipientList) {
        this.recipientList = recipientList;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    /**
     * Equality is based on the entity identifier to preserve prior behaviour.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Notification other = (Notification) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return "cr.ac.una.flowfx.model.Notification[ id=" + id + " ]";
    }
}
