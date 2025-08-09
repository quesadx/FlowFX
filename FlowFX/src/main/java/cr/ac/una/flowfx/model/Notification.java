/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *
 * @author krist
 */
@Entity
@Table(name = "NOTIFICATION")
@NamedQueries({
    @NamedQuery(name = "Notification.findAll", query = "SELECT n FROM Notification n"),
    @NamedQuery(name = "Notification.findByNotificationId", query = "SELECT n FROM Notification n WHERE n.notificationId = :notificationId"),
    @NamedQuery(name = "Notification.findBySubject", query = "SELECT n FROM Notification n WHERE n.subject = :subject"),
    @NamedQuery(name = "Notification.findByMessage", query = "SELECT n FROM Notification n WHERE n.message = :message"),
    @NamedQuery(name = "Notification.findBySentAt", query = "SELECT n FROM Notification n WHERE n.sentAt = :sentAt"),
    @NamedQuery(name = "Notification.findByStatus", query = "SELECT n FROM Notification n WHERE n.status = :status"),
    @NamedQuery(name = "Notification.findByEventType", query = "SELECT n FROM Notification n WHERE n.eventType = :eventType")})
public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @Column(name = "NOTIFICATION_ID")
    private BigDecimal notificationId;
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
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "notification", fetch = FetchType.LAZY)
    private List<NotificationRecipient> notificationRecipientList;

    public Notification() {
    }

    public Notification(BigDecimal notificationId) {
        this.notificationId = notificationId;
    }

    public Notification(BigDecimal notificationId, String subject, String message, Character status, String eventType) {
        this.notificationId = notificationId;
        this.subject = subject;
        this.message = message;
        this.status = status;
        this.eventType = eventType;
    }

    public BigDecimal getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(BigDecimal notificationId) {
        this.notificationId = notificationId;
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

    public List<NotificationRecipient> getNotificationRecipientList() {
        return notificationRecipientList;
    }

    public void setNotificationRecipientList(List<NotificationRecipient> notificationRecipientList) {
        this.notificationRecipientList = notificationRecipientList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (notificationId != null ? notificationId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Notification)) {
            return false;
        }
        Notification other = (Notification) object;
        if ((this.notificationId == null && other.notificationId != null) || (this.notificationId != null && !this.notificationId.equals(other.notificationId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cr.ac.una.flowfx.model.Notification[ notificationId=" + notificationId + " ]";
    }
    
}
