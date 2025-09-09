package cr.ac.una.flowfx.model;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

/**
 * JPA entity representing a notification recipient.
 *
 * <p>This entity maps to NOTIFICATION_RECIPIENT. The composite key is stored in
 * {@link NotificationRecipientPK}. Equality and hashCode are based on the
 * embedded primary key to preserve original semantics.</p>
 */
@Entity
@Table(name = "NOTIFICATION_RECIPIENT")
@NamedQueries(
    {
        @NamedQuery(
            name = "NotificationRecipient.findAll",
            query = "SELECT n FROM NotificationRecipient n"
        ),
        @NamedQuery(
            name = "NotificationRecipient.findById",
            query = "SELECT n FROM NotificationRecipient n WHERE n.notificationRecipientPK.id = :id"
        ),
        @NamedQuery(
            name = "NotificationRecipient.findByEmail",
            query = "SELECT n FROM NotificationRecipient n WHERE n.notificationRecipientPK.email = :email"
        ),
        @NamedQuery(
            name = "NotificationRecipient.findByName",
            query = "SELECT n FROM NotificationRecipient n WHERE n.name = :name"
        ),
        @NamedQuery(
            name = "NotificationRecipient.findByRole",
            query = "SELECT n FROM NotificationRecipient n WHERE n.role = :role"
        ),
    }
)
public class NotificationRecipient implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    protected NotificationRecipientPK notificationRecipientPK;

    @Basic(optional = false)
    @Column(name = "NAME")
    private String name;

    @Basic(optional = false)
    @Column(name = "ROLE")
    private String role;

    @JoinColumn(
        name = "NOTIFICATION_ID",
        referencedColumnName = "NOTIFICATION_ID",
        insertable = false,
        updatable = false
    )
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Notification notification;

    /**
     * Default constructor required by JPA.
     */
    public NotificationRecipient() {
        // no-op
    }

    /**
     * Creates a recipient with the provided composite primary key.
     *
     * @param notificationRecipientPK composite key
     */
    public NotificationRecipient(
        NotificationRecipientPK notificationRecipientPK
    ) {
        this.notificationRecipientPK = notificationRecipientPK;
    }

    /**
     * Creates a recipient with key, name and role.
     *
     * @param notificationRecipientPK composite key
     * @param name display name
     * @param role recipient role
     */
    public NotificationRecipient(
        NotificationRecipientPK notificationRecipientPK,
        String name,
        String role
    ) {
        this.notificationRecipientPK = notificationRecipientPK;
        this.name = name;
        this.role = role;
    }

    /**
     * Convenience constructor using individual key parts.
     *
     * @param notificationId notification identifier
     * @param email recipient email
     */
    public NotificationRecipient(Long notificationId, String email) {
        this.notificationRecipientPK = new NotificationRecipientPK(
            notificationId,
            email
        );
    }

    public NotificationRecipientPK getNotificationRecipientPK() {
        return notificationRecipientPK;
    }

    public void setNotificationRecipientPK(
        NotificationRecipientPK notificationRecipientPK
    ) {
        this.notificationRecipientPK = notificationRecipientPK;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(notificationRecipientPK);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        NotificationRecipient other = (NotificationRecipient) obj;
        return Objects.equals(
            this.notificationRecipientPK,
            other.notificationRecipientPK
        );
    }

    @Override
    public String toString() {
        return (
            "cr.ac.una.flowfx.model.NotificationRecipient{notificationRecipientPK=" +
            notificationRecipientPK +
            '}'
        );
    }
}
