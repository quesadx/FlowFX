/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.flowfx.model;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author krist
 */
@Entity
@Table(name = "PROJECT_TRACKING")
@NamedQueries({
    @NamedQuery(name = "ProjectTracking.findAll", query = "SELECT p FROM ProjectTracking p"),
    @NamedQuery(name = "ProjectTracking.findByTrackingId", query = "SELECT p FROM ProjectTracking p WHERE p.trackingId = :trackingId"),
    @NamedQuery(name = "ProjectTracking.findByObservations", query = "SELECT p FROM ProjectTracking p WHERE p.observations = :observations"),
    @NamedQuery(name = "ProjectTracking.findByTrackingDate", query = "SELECT p FROM ProjectTracking p WHERE p.trackingDate = :trackingDate"),
    @NamedQuery(name = "ProjectTracking.findByProgressPercentage", query = "SELECT p FROM ProjectTracking p WHERE p.progressPercentage = :progressPercentage"),
    @NamedQuery(name = "ProjectTracking.findByCreatedAt", query = "SELECT p FROM ProjectTracking p WHERE p.createdAt = :createdAt")})
public class ProjectTracking implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @Column(name = "TRACKING_ID")
    private BigDecimal trackingId;
    @Basic(optional = false)
    @Column(name = "OBSERVATIONS")
    private String observations;
    @Basic(optional = false)
    @Column(name = "TRACKING_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date trackingDate;
    @Basic(optional = false)
    @Column(name = "PROGRESS_PERCENTAGE")
    private BigDecimal progressPercentage;
    @Column(name = "CREATED_AT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @JoinColumn(name = "CREATED_BY", referencedColumnName = "PER_ID")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Person createdBy;
    @JoinColumn(name = "PROJECT_ID", referencedColumnName = "PROJECT_ID")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Project projectId;

    public ProjectTracking() {
    }

    public ProjectTracking(BigDecimal trackingId) {
        this.trackingId = trackingId;
    }

    public ProjectTracking(BigDecimal trackingId, String observations, Date trackingDate, BigDecimal progressPercentage) {
        this.trackingId = trackingId;
        this.observations = observations;
        this.trackingDate = trackingDate;
        this.progressPercentage = progressPercentage;
    }

    public BigDecimal getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(BigDecimal trackingId) {
        this.trackingId = trackingId;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public Date getTrackingDate() {
        return trackingDate;
    }

    public void setTrackingDate(Date trackingDate) {
        this.trackingDate = trackingDate;
    }

    public BigDecimal getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(BigDecimal progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
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
        hash += (trackingId != null ? trackingId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ProjectTracking)) {
            return false;
        }
        ProjectTracking other = (ProjectTracking) object;
        if ((this.trackingId == null && other.trackingId != null) || (this.trackingId != null && !this.trackingId.equals(other.trackingId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cr.ac.una.flowfx.model.ProjectTracking[ trackingId=" + trackingId + " ]";
    }
    
}
