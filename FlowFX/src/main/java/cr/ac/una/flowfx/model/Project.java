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
@Table(name = "PROJECT")
@NamedQueries({
    @NamedQuery(name = "Project.findAll", query = "SELECT p FROM Project p"),
    @NamedQuery(name = "Project.findByProjectId", query = "SELECT p FROM Project p WHERE p.projectId = :projectId"),
    @NamedQuery(name = "Project.findByProjectName", query = "SELECT p FROM Project p WHERE p.projectName = :projectName"),
    @NamedQuery(name = "Project.findByPlannedStartDate", query = "SELECT p FROM Project p WHERE p.plannedStartDate = :plannedStartDate"),
    @NamedQuery(name = "Project.findByPlannedEndDate", query = "SELECT p FROM Project p WHERE p.plannedEndDate = :plannedEndDate"),
    @NamedQuery(name = "Project.findByActualStartDate", query = "SELECT p FROM Project p WHERE p.actualStartDate = :actualStartDate"),
    @NamedQuery(name = "Project.findByActualEndDate", query = "SELECT p FROM Project p WHERE p.actualEndDate = :actualEndDate"),
    @NamedQuery(name = "Project.findByStatus", query = "SELECT p FROM Project p WHERE p.status = :status"),
    @NamedQuery(name = "Project.findByCreatedAt", query = "SELECT p FROM Project p WHERE p.createdAt = :createdAt"),
    @NamedQuery(name = "Project.findByUpdatedAt", query = "SELECT p FROM Project p WHERE p.updatedAt = :updatedAt")})
public class Project implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @Column(name = "PROJECT_ID")
    private BigDecimal projectId;
    @Basic(optional = false)
    @Column(name = "PROJECT_NAME")
    private String projectName;
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
    @Column(name = "STATUS")
    private Character status;
    @Column(name = "CREATED_AT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Column(name = "UPDATED_AT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "projectId", fetch = FetchType.LAZY)
    private List<Notification> notificationList;
    @JoinColumn(name = "LEADER_USER_ID", referencedColumnName = "PER_ID")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Person leaderUserId;
    @JoinColumn(name = "TECH_LEADER_ID", referencedColumnName = "PER_ID")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Person techLeaderId;
    @JoinColumn(name = "SPONSOR_ID", referencedColumnName = "PER_ID")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Person sponsorId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "projectId", fetch = FetchType.LAZY)
    private List<ProjectTracking> projectTrackingList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "projectId", fetch = FetchType.LAZY)
    private List<ProjectActivity> projectActivityList;

    public Project() {
    }

    public Project(BigDecimal projectId) {
        this.projectId = projectId;
    }

    public Project(BigDecimal projectId, String projectName, Date plannedStartDate, Date plannedEndDate, Character status) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.plannedStartDate = plannedStartDate;
        this.plannedEndDate = plannedEndDate;
        this.status = status;
    }

    public BigDecimal getProjectId() {
        return projectId;
    }

    public void setProjectId(BigDecimal projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
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

    public Character getStatus() {
        return status;
    }

    public void setStatus(Character status) {
        this.status = status;
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

    public Person getLeaderUserId() {
        return leaderUserId;
    }

    public void setLeaderUserId(Person leaderUserId) {
        this.leaderUserId = leaderUserId;
    }

    public Person getTechLeaderId() {
        return techLeaderId;
    }

    public void setTechLeaderId(Person techLeaderId) {
        this.techLeaderId = techLeaderId;
    }

    public Person getSponsorId() {
        return sponsorId;
    }

    public void setSponsorId(Person sponsorId) {
        this.sponsorId = sponsorId;
    }

    public List<ProjectTracking> getProjectTrackingList() {
        return projectTrackingList;
    }

    public void setProjectTrackingList(List<ProjectTracking> projectTrackingList) {
        this.projectTrackingList = projectTrackingList;
    }

    public List<ProjectActivity> getProjectActivityList() {
        return projectActivityList;
    }

    public void setProjectActivityList(List<ProjectActivity> projectActivityList) {
        this.projectActivityList = projectActivityList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (projectId != null ? projectId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Project)) {
            return false;
        }
        Project other = (Project) object;
        if ((this.projectId == null && other.projectId != null) || (this.projectId != null && !this.projectId.equals(other.projectId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cr.ac.una.flowfx.model.Project[ projectId=" + projectId + " ]";
    }
    
}
