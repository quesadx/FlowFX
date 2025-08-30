package cr.ac.una.flowfx.model;

import java.util.Date;

/**
 * Data Transfer Object for project tracking.
 * - Added createdBy field to support setCreatedBy(Long).
 * - progressPercentage remains Integer; an overloaded setter accepts Number.
 */
public class ProjectTrackingDTO {
    private Long id;
    private Long projectId;
    private Long createdBy;               // Added
    private String observations;
    private Date trackingDate;
    private Integer progressPercentage;   // Stored as Integer
    private Date createdAt;

    public ProjectTrackingDTO() {}

    public ProjectTrackingDTO(Long id,
                              String observations,
                              Date trackingDate,
                              Integer progressPercentage,
                              Date createdAt) {
        this.id = id;
        this.observations = observations;
        this.trackingDate = trackingDate;
        this.progressPercentage = progressPercentage;
        this.createdAt = createdAt;
    }

    // Optionally, a constructor including createdBy if needed by callers.
    public ProjectTrackingDTO(Long id,
                              Long projectId,
                              Long createdBy,
                              String observations,
                              Date trackingDate,
                              Integer progressPercentage,
                              Date createdAt) {
        this.id = id;
        this.projectId = projectId;
        this.createdBy = createdBy;
        this.observations = observations;
        this.trackingDate = trackingDate;
        this.progressPercentage = progressPercentage;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public Long getCreatedBy() { return createdBy; }                 // Added
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; } // Added

    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }

    public Date getTrackingDate() { return trackingDate; }
    public void setTrackingDate(Date trackingDate) { this.trackingDate = trackingDate; }

    public Integer getProgressPercentage() { return progressPercentage; }

    /**
     * Setter accepting Integer.
     */
    public void setProgressPercentage(Integer progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    /**
     * Overloaded setter to accept any numeric type (e.g., Double).
     * Converts to Integer via rounding. Null-safe.
     */
    public void setProgressPercentage(Number progressPercentage) {
        this.progressPercentage = (progressPercentage == null)
                ? null
                : (int) Math.round(progressPercentage.doubleValue());
    }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}