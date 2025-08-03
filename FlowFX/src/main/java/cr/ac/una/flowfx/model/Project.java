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
import java.time.LocalDateTime;
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
    private Long projectId;
    @Basic(optional = false)
    @Column(name = "PROJECT_NAME")
    private String projectName;
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
    @Column(name = "STATUS")
    private Character status;
    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;
    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "project", fetch = FetchType.LAZY)
    private List<Notification> notificationList;
    
    // Líder de usuario (rol funcional)
    @ManyToOne
    @JoinColumn(name = "LEADER_USER_ID")
    private Admin leader;
    
    // Líder técnico
    @ManyToOne
    @JoinColumn(name = "TECH_LEADER_ID")
    private Admin techLeader;
    
    // Patrocinador
    @ManyToOne(optional = false)
    @JoinColumn(name = "SPONSOR_ID")
    private Admin sponsor;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "project", fetch = FetchType.LAZY)
    private List<ProjectTracking> projectTrackingList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "project", fetch = FetchType.LAZY)
    private List<ProjectActivity> projectActivityList;

    public Project() {
    }

    public Project(Long projectId) {
        this.projectId = projectId;
    }

    public Project(Long projectId, String projectName, LocalDateTime plannedStartDate, LocalDateTime plannedEndDate, Character status) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.plannedStartDate = plannedStartDate;
        this.plannedEndDate = plannedEndDate;
        this.status = status;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
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

    public Character getStatus() {
        return status;
    }

    public void setStatus(Character status) {
        this.status = status;
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

    public Admin getLeader() {
        return leader;
    }

    public void setLeader(Admin leader) {
        this.leader = leader;
    }

    public Admin getSponsor() {
        return sponsor;
    }

    public void setSponsor(Admin sponsor) {
        this.sponsor = sponsor;
    }

    public Admin getTechLeader() {
        return techLeader;
    }

    public void setTechLeader(Admin techLeader) {
        this.techLeader = techLeader;
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
