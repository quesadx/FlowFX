/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.flowfx.model;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.math.BigInteger;

/**
 *
 * @author krist
 */
@Embeddable
public class NotificationRecipientPK implements Serializable {

    @Basic(optional = false)
    @Column(name = "NOTIFICATION_ID")
    private BigInteger notificationId;
    @Basic(optional = false)
    @Column(name = "EMAIL")
    private String email;

    public NotificationRecipientPK() {
    }

    public NotificationRecipientPK(BigInteger notificationId, String email) {
        this.notificationId = notificationId;
        this.email = email;
    }

    public BigInteger getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(BigInteger notificationId) {
        this.notificationId = notificationId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (notificationId != null ? notificationId.hashCode() : 0);
        hash += (email != null ? email.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof NotificationRecipientPK)) {
            return false;
        }
        NotificationRecipientPK other = (NotificationRecipientPK) object;
        if ((this.notificationId == null && other.notificationId != null) || (this.notificationId != null && !this.notificationId.equals(other.notificationId))) {
            return false;
        }
        if ((this.email == null && other.email != null) || (this.email != null && !this.email.equals(other.email))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cr.ac.una.flowfx.model.NotificationRecipientPK[ notificationId=" + notificationId + ", email=" + email + " ]";
    }
    
}
