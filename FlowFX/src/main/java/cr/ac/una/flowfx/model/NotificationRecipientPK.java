/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.flowfx.model;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;


/**
 *
 * @author krist
 */
@Embeddable
public class NotificationRecipientPK implements Serializable {

    @Basic(optional = false)
    @Column(name = "NOTIFICATION_ID")
    private Long id;
    @Basic(optional = false)
    @Column(name = "EMAIL")
    private String email;

    public NotificationRecipientPK() {
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
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
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
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        if ((this.email == null && other.email != null) || (this.email != null && !this.email.equals(other.email))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
    return "cr.ac.una.flowfx.model.NotificationRecipientPK[ id=" + id + ", email=" + email + " ]";
    }
    
}
