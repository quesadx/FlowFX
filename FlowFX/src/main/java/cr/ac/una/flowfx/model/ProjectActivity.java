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
import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;
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
    @NamedQuery(name = "ProjectActivity.findByResponsible", query = "SELECT p FROM ProjectActivity p WHERE p.responsible = :responsible"),
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
    private Long activityId;
    @Basic(optional = false)
    @Column(name = "DESCRIPTION")
    private String description;
    @Basic(optional = false)
    @Column(name = "RESPONSIBLE")
    private String responsible;
    @Basic(optional = false)
    @Column(name = "STATUS")
    private Character status;
    @Basic(optional = false)
    @Column(name = "PLANNED_START_DATE")
    private LocalDateTime plannedStartDate;
    @Basic(optional = false)
    @Column(name = "PLANNED_END_DATE")
    private LocalDateTime plannedEndDate;
    @Column(name = "ACTUAL_START_DATE")
    private LocalDateTime actualStartDate;
    @Column(name = "ACTUAL_END_DATE")
    private LocalDateTime actualEndDate;
    @Basic(optional = false)
    @Column(name = "EXECUTION_ORDER")
    private BigInteger executionOrder;
    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;
    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "activity", fetch = FetchType.LAZY)
    private List<Notification> notificationList;
    @JoinColumn(name = "PROJECT_ID", referencedColumnName = "PROJECT_ID")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Project project;

    public ProjectActivity() {
    }

    public ProjectActivity(Long activityId) {
        this.activityId = activityId;
    }

    public ProjectActivity(Long activityId, String description, String responsible, Character status, LocalDateTime plannedStartDate, LocalDateTime plannedEndDate, BigInteger executionOrder) {
        this.activityId = activityId;
        this.description = description;
        this.responsible = responsible;
        this.status = status;
        this.plannedStartDate = plannedStartDate;
        this.plannedEndDate = plannedEndDate;
        this.executionOrder = executionOrder;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getResponsible() {
        return responsible;
    }

    public void setResponsible(String responsible) {
        this.responsible = responsible;
    }

    public Character getStatus() {
        return status;
    }

    public void setStatus(Character status) {
        this.status = status;
    }

    public LocalDateTime getPlannedStartDate() {
        return plannedStartDate;
    }

    public void setPlannedStartDate(LocalDateTime plannedStartDate) {
        this.plannedStartDate = plannedStartDate;
    }

    public LocalDateTime getPlannedEndDate() {
        return plannedEndDate;
    }

    public void setPlannedEndDate(LocalDateTime plannedEndDate) {
        this.plannedEndDate = plannedEndDate;
    }

    public LocalDateTime getActualStartDate() {
        return actualStartDate;
    }

    public void setActualStartDate(LocalDateTime actualStartDate) {
        this.actualStartDate = actualStartDate;
    }

    public LocalDateTime getActualEndDate() {
        return actualEndDate;
    }

    public void setActualEndDate(LocalDateTime actualEndDate) {
        this.actualEndDate = actualEndDate;
    }

    public BigInteger getExecutionOrder() {
        return executionOrder;
    }

    public void setExecutionOrder(BigInteger executionOrder) {
        this.executionOrder = executionOrder;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Notification> getNotificationList() {
        return notificationList;
    }

    public void setNotificationList(List<Notification> notificationList) {
        this.notificationList = notificationList;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
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
