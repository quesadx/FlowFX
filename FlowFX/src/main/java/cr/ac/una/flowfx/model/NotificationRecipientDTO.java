package cr.ac.una.flowfx.model;

import java.util.Objects;

/**
 * Data Transfer Object for {@link NotificationRecipient}.
 *
 * <p>Preserves the original simple structure while providing clearer
 * accessor methods and value semantics for equality and hashing.</p>
 */
public class NotificationRecipientDTO {

    private NotificationRecipientPK notificationRecipientPK;
    private String name;
    private String role;

    /**
     * Default constructor.
     */
    public NotificationRecipientDTO() {
        // no-op
    }

    /**
     * Full constructor.
     *
     * @param notificationRecipientPK composite key
     * @param name display name
     * @param role recipient role
     */
    public NotificationRecipientDTO(
        NotificationRecipientPK notificationRecipientPK,
        String name,
        String role
    ) {
        this.notificationRecipientPK = notificationRecipientPK;
        this.name = name;
        this.role = role;
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
        NotificationRecipientDTO other = (NotificationRecipientDTO) obj;
        return (
            Objects.equals(
                this.notificationRecipientPK,
                other.notificationRecipientPK
            ) &&
            Objects.equals(this.name, other.name) &&
            Objects.equals(this.role, other.role)
        );
    }

    @Override
    public String toString() {
        return (
            "NotificationRecipientDTO{notificationRecipientPK=" +
            notificationRecipientPK +
            ", name=" +
            name +
            ", role=" +
            role +
            "}"
        );
    }
}
