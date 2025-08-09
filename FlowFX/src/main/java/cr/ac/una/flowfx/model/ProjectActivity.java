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
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 *
 * @author krist
 */
@Entity
@Table(name = "PROJECT_ACTIVITY")
@NamedQueries({
    @NamedQuery(name = "ProjectActivity.findAll", query = "SELECT p FROM ProjectActivity p"),
    @NamedQuery(name = "ProjectActivity.findByActivityId", query = "SELECT p FROM ProjectActivity p WHERE p.activityId = :activityId"),
    @NamedQuery(name = "ProjectActivity.findByDescription", query = "SELECT p FROM ProjectActivity p WHERE p.description = :description"),
    @NamedQuery(name = "ProjectActivity.findByStatus", query = "SELECT p FROM ProjectActivity p WHERE p.status = :status"),
    @NamedQuery(name = "ProjectActivity.findByPlannedStartDate", query = "SELECT p FROM ProjectActivity p WHERE p.plannedStartDate = :plannedStartDate"),
    @NamedQuery(name = "ProjectActivity.findByPlannedEndDate", query = "SELECT p FROM ProjectActivity p WHERE p.plannedEndDate = :plannedEndDate"),
    @NamedQuery(name = "ProjectActivity.findByActualStartDate", query = "SELECT p FROM ProjectActivity p WHERE p.actualStartDate = :actualStartDate"),
    @NamedQuery(name = "ProjectActivity.findByActualEndDate", query = "SELECT p FROM ProjectActivity p WHERE p.actualEndDate = :actualEndDate"),
    @NamedQuery(name = "ProjectActivity.findByExecutionOrder", query = "SELECT p FROM ProjectActivity p WHERE p.executionOrder = :executionOrder"),
    @NamedQuery(name = "ProjectActivity.findByCreatedAt", query = "SELECT p FROM ProjectActivity p WHERE p.createdAt = :createdAt"),
    @NamedQuery(name = "ProjectActivity.findByUpdatedAt", query = "SELECT p FROM ProjectActivity p WHERE p.updatedAt = :updatedAt")})
public class ProjectActivity implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @Column(name = "ACTIVITY_ID")
    private BigDecimal activityId;
    @Basic(optional = false)
    @Column(name = "DESCRIPTION")
    private String description;
    @Basic(optional = false)
    @Column(name = "STATUS")
    private Character status;
    @Basic(optional = false)
    @Column(name = "PLANNED_START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date plannedStartDate;
    @Basic(optional = false)
    @Column(name = "PLANNED_END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date plannedEndDate;
    @Column(name = "ACTUAL_START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date actualStartDate;
    @Column(name = "ACTUAL_END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date actualEndDate;
    @Basic(optional = false)
    @Column(name = "EXECUTION_ORDER")
    private BigInteger executionOrder;
    @Column(name = "CREATED_AT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Column(name = "UPDATED_AT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "activityId", fetch = FetchType.LAZY)
    private List<Notification> notificationList;
    @JoinColumn(name = "RESPONSIBLE_ID", referencedColumnName = "PER_ID")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Person responsibleId;
    @JoinColumn(name = "CREATED_BY", referencedColumnName = "PER_ID")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Person createdBy;
    @JoinColumn(name = "PROJECT_ID", referencedColumnName = "PROJECT_ID")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Project projectId;

    public ProjectActivity() {
    }

    public ProjectActivity(BigDecimal activityId) {
        this.activityId = activityId;
    }

    public ProjectActivity(BigDecimal activityId, String description, Character status, Date plannedStartDate, Date plannedEndDate, BigInteger executionOrder) {
        this.activityId = activityId;
        this.description = description;
        this.status = status;
        this.plannedStartDate = plannedStartDate;
        this.plannedEndDate = plannedEndDate;
        this.executionOrder = executionOrder;
    }

    public BigDecimal getActivityId() {
        return activityId;
    }

    public void setActivityId(BigDecimal activityId) {
        this.activityId = activityId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Character getStatus() {
        return status;
    }

    public void setStatus(Character status) {
        this.status = status;
    }

    public Date getPlannedStartDate() {
        return plannedStartDate;
    }

    public void setPlannedStartDate(Date plannedStartDate) {
        this.plannedStartDate = plannedStartDate;
    }

    public Date getPlannedEndDate() {
        return plannedEndDate;
    }

    public void setPlannedEndDate(Date plannedEndDate) {
        this.plannedEndDate = plannedEndDate;
    }

    public Date getActualStartDate() {
        return actualStartDate;
    }

    public void setActualStartDate(Date actualStartDate) {
        this.actualStartDate = actualStartDate;
    }

    public Date getActualEndDate() {
        return actualEndDate;
    }

    public void setActualEndDate(Date actualEndDate) {
        this.actualEndDate = actualEndDate;
    }

    public BigInteger getExecutionOrder() {
        return executionOrder;
    }

    public void setExecutionOrder(BigInteger executionOrder) {
        this.executionOrder = executionOrder;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Notification> getNotificationList() {
        return notificationList;
    }

    public void setNotificationList(List<Notification> notificationList) {
        this.notificationList = notificationList;
    }

    public Person getResponsibleId() {
        return responsibleId;
    }

    public void setResponsibleId(Person responsibleId) {
        this.responsibleId = responsibleId;
    }

    public Person getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Person createdBy) {
        this.createdBy = createdBy;
    }

    public Project getProjectId() {
        return projectId;
    }

    public void setProjectId(Project projectId) {
        this.projectId = projectId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (activityId != null ? activityId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ProjectActivity)) {
            return false;
        }
        ProjectActivity other = (ProjectActivity) object;
        if ((this.activityId == null && other.activityId != null) || (this.activityId != null && !this.activityId.equals(other.activityId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cr.ac.una.flowfx.model.ProjectActivity[ activityId=" + activityId + " ]";
    }
    
}
