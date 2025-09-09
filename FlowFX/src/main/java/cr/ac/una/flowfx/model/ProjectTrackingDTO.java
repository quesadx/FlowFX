package cr.ac.una.flowfx.model;

import java.util.Date;
import java.util.Objects;

/**
 * Data Transfer Object for project tracking entries.
 *
 * <p>This DTO carries tracking information between layers. It preserves the
 * existing public API (constructors and accessors) while providing improved
 * documentation, value semantics and a flexible setter for progress values.</p>
 */
public class ProjectTrackingDTO {

    private Long id;
    private Long projectId;
    private Long createdBy;
    private String observations;
    private Date trackingDate;
    private Integer progressPercentage;
    private Date createdAt;

    /**
     * Default constructor.
     */
    public ProjectTrackingDTO() {
        // no-op
    }

    /**
     * Basic constructor.
     *
     * @param id identifier
     * @param observations textual observations
     * @param trackingDate date of tracking
     * @param progressPercentage progress percentage as Integer
     * @param createdAt creation timestamp
     */
    public ProjectTrackingDTO(
        Long id,
        String observations,
        Date trackingDate,
        Integer progressPercentage,
        Date createdAt
    ) {
        this.id = id;
        this.observations = observations;
        this.trackingDate = trackingDate;
        this.progressPercentage = progressPercentage;
        this.createdAt = createdAt;
    }

    /**
     * Extended constructor including project and creator identifiers.
     *
     * @param id identifier
     * @param projectId linked project identifier
     * @param createdBy user who created the tracking entry
     * @param observations textual observations
     * @param trackingDate date of tracking
     * @param progressPercentage progress percentage as Integer
     * @param createdAt creation timestamp
     */
    public ProjectTrackingDTO(
        Long id,
        Long projectId,
        Long createdBy,
        String observations,
        Date trackingDate,
        Integer progressPercentage,
        Date createdAt
    ) {
        this.id = id;
        this.projectId = projectId;
        this.createdBy = createdBy;
        this.observations = observations;
        this.trackingDate = trackingDate;
        this.progressPercentage = progressPercentage;
        this.createdAt = createdAt;
    }

    // --- Accessors ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
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

    /**
     * Setter accepting Integer values.
     *
     * @param progressPercentage integer progress (0-100 expected)
     */
    public void setProgressPercentage(Integer progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    /**
     * Flexible setter accepting any {@link Number}. The value is converted to
     * Integer using rounding. Null input clears the value.
     *
     * @param progressPercentage numeric progress value
     */
    public void setProgressPercentage(Number progressPercentage) {
        this.progressPercentage = progressPercentage == null
            ? null
            : (int) Math.round(progressPercentage.doubleValue());
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    // --- Value semantics ---

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    /**
     * Equality is based on identifier to remain compatible with code that treats
     * DTO identity by id.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof ProjectTrackingDTO)) {
            return false;
        }
        ProjectTrackingDTO other = (ProjectTrackingDTO) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return "ProjectTrackingDTO{id=" + id + ", projectId=" + projectId + "}";
    }
}
