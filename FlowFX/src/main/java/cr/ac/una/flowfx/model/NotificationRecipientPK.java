package cr.ac.una.flowfx.model;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Composite primary key for {@link NotificationRecipient}.
 *
 * <p>This embeddable key consists of notification identifier and recipient
 * email. Equals and hashCode are implemented using {@link Objects} to ensure
 * stable behavior across different JVMs and to match value semantics.</p>
 */
@Embeddable
public class NotificationRecipientPK implements Serializable {

    private static final long serialVersionUID = 1L;

    @Basic(optional = false)
    @Column(name = "NOTIFICATION_ID")
    private Long id;

    @Basic(optional = false)
    @Column(name = "EMAIL")
    private String email;

    public NotificationRecipientPK() {
        // Default constructor required by JPA
    }

    public NotificationRecipientPK(Long id, String email) {
        this.id = id;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        NotificationRecipientPK other = (NotificationRecipientPK) obj;
        return (
            Objects.equals(this.id, other.id) &&
            Objects.equals(this.email, other.email)
        );
    }

    @Override
    public String toString() {
        return (
            "cr.ac.una.flowfx.model.NotificationRecipientPK{id=" +
            id +
            ", email=" +
            email +
            '}'
        );
    }
}
