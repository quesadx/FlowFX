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
import java.util.Date;
import java.util.Objects;

/**
 * JPA entity representing a project tracking entry.
 *
 * <p>This class maps to the PROJECT_TRACKING table and preserves the existing
 * named queries and relationships used by the application. Equals and hashCode
 * implementations are based on the identifier using {@link Objects} for clarity
 * and stability.</p>
 */
@Entity
@Table(name = "PROJECT_TRACKING")
@NamedQueries(
    {
        @NamedQuery(
            name = "ProjectTracking.findAll",
            query = "SELECT p FROM ProjectTracking p"
        ),
        @NamedQuery(
            name = "ProjectTracking.findById",
            query = "SELECT p FROM ProjectTracking p WHERE p.id = :id"
        ),
        @NamedQuery(
            name = "ProjectTracking.findByObservations",
            query = "SELECT p FROM ProjectTracking p WHERE p.observations = :observations"
        ),
        @NamedQuery(
            name = "ProjectTracking.findByTrackingDate",
            query = "SELECT p FROM ProjectTracking p WHERE p.trackingDate = :trackingDate"
        ),
        @NamedQuery(
            name = "ProjectTracking.findByProgressPercentage",
            query = "SELECT p FROM ProjectTracking p WHERE p.progressPercentage = :progressPercentage"
        ),
        @NamedQuery(
            name = "ProjectTracking.findByCreatedAt",
            query = "SELECT p FROM ProjectTracking p WHERE p.createdAt = :createdAt"
        ),
    }
)
public class ProjectTracking implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @Column(name = "TRACKING_ID")
    private Long id;

    @Basic(optional = false)
    @Column(name = "OBSERVATIONS")
    private String observations;

    @Basic(optional = false)
    @Column(name = "TRACKING_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date trackingDate;

    @Basic(optional = false)
    @Column(name = "PROGRESS_PERCENTAGE")
    private Integer progressPercentage;

    @Column(name = "CREATED_AT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @JoinColumn(name = "CREATED_BY", referencedColumnName = "PER_ID")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Person createdBy;

    @JoinColumn(name = "PROJECT_ID", referencedColumnName = "PROJECT_ID")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Project projectId;

    /**
     * Default constructor required by JPA.
     */
    public ProjectTracking() {}

    /**
     * Convenience constructor with identifier.
     *
     * @param id identifier
     */
    public ProjectTracking(Long id) {
        this.id = id;
    }

    public ProjectTracking(
        Long id,
        String observations,
        Date trackingDate,
        Integer progressPercentage
    ) {
        this.id = id;
        this.observations = observations;
        this.trackingDate = trackingDate;
        this.progressPercentage = progressPercentage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(Integer progressPercentage) {
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
        return Objects.hashCode(id);
    }

    /**
     * Equality is based on the identifier to preserve previous semantics.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ProjectTracking other = (ProjectTracking) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return "cr.ac.una.flowfx.model.ProjectTracking[ id=" + id + " ]";
    }
}
