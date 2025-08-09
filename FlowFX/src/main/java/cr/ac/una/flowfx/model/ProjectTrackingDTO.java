package cr.ac.una.flowfx.model;

import java.util.Date;

public class ProjectTrackingDTO {
    private Long id;
    private String observations;
    private Date trackingDate;
    private Integer progressPercentage;
    private Date createdAt;

    public ProjectTrackingDTO() {}

    public ProjectTrackingDTO(Long id, String observations, Date trackingDate, Integer progressPercentage, Date createdAt) {
        this.id = id;
        this.observations = observations;
        this.trackingDate = trackingDate;
        this.progressPercentage = progressPercentage;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }
    public Date getTrackingDate() { return trackingDate; }
    public void setTrackingDate(Date trackingDate) { this.trackingDate = trackingDate; }
    public Integer getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(Integer progressPercentage) { this.progressPercentage = progressPercentage; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
