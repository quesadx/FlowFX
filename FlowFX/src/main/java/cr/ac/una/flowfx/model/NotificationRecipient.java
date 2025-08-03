/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

/**
 *
 * @author krist
 */
@Entity
@Table(name = "NOTIFICATION_RECIPIENT")
@NamedQueries({
    @NamedQuery(name = "NotificationRecipient.findAll", query = "SELECT n FROM NotificationRecipient n"),
    @NamedQuery(name = "NotificationRecipient.findByNotificationId", query = "SELECT n FROM NotificationRecipient n WHERE n.notificationRecipientPK.notificationId = :notificationId"),
    @NamedQuery(name = "NotificationRecipient.findByEmail", query = "SELECT n FROM NotificationRecipient n WHERE n.notificationRecipientPK.email = :email"),
    @NamedQuery(name = "NotificationRecipient.findByName", query = "SELECT n FROM NotificationRecipient n WHERE n.name = :name"),
    @NamedQuery(name = "NotificationRecipient.findByRole", query = "SELECT n FROM NotificationRecipient n WHERE n.role = :role"),
    @NamedQuery(name = "NotificationRecipient.findByCreatedAt", query = "SELECT n FROM NotificationRecipient n WHERE n.createdAt = :createdAt")})
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
    @Column(name = "CREATED_AT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @JoinColumn(name = "NOTIFICATION_ID", referencedColumnName = "NOTIFICATION_ID", insertable = false, updatable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Notification notification;

    public NotificationRecipient() {
    }

    public NotificationRecipient(NotificationRecipientPK notificationRecipientPK) {
        this.notificationRecipientPK = notificationRecipientPK;
    }

    public NotificationRecipient(NotificationRecipientPK notificationRecipientPK, String name, String role) {
        this.notificationRecipientPK = notificationRecipientPK;
        this.name = name;
        this.role = role;
    }

    public NotificationRecipient(BigInteger notificationId, String email) {
        this.notificationRecipientPK = new NotificationRecipientPK(notificationId, email);
    }

    public NotificationRecipientPK getNotificationRecipientPK() {
        return notificationRecipientPK;
    }

    public void setNotificationRecipientPK(NotificationRecipientPK notificationRecipientPK) {
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (notificationRecipientPK != null ? notificationRecipientPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof NotificationRecipient)) {
            return false;
        }
        NotificationRecipient other = (NotificationRecipient) object;
        if ((this.notificationRecipientPK == null && other.notificationRecipientPK != null) || (this.notificationRecipientPK != null && !this.notificationRecipientPK.equals(other.notificationRecipientPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cr.ac.una.flowfx.model.NotificationRecipient[ notificationRecipientPK=" + notificationRecipientPK + " ]";
    }
    
}
