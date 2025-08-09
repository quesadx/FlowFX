package cr.ac.una.flowfx.model;

import java.util.Date;

public class ProjectActivityDTO {
    private Long id;
    private String description;
    private Character status;
    private Date plannedStartDate;
    private Date plannedEndDate;
    private Date actualStartDate;
    private Date actualEndDate;
    private Integer executionOrder;
    private Date createdAt;
    private Date updatedAt;

    public ProjectActivityDTO() {}

    public ProjectActivityDTO(Long id, String description, Character status, Date plannedStartDate, Date plannedEndDate, Date actualStartDate, Date actualEndDate, Integer executionOrder, Date createdAt, Date updatedAt) {
        this.id = id;
        this.description = description;
        this.status = status;
        this.plannedStartDate = plannedStartDate;
        this.plannedEndDate = plannedEndDate;
        this.actualStartDate = actualStartDate;
        this.actualEndDate = actualEndDate;
        this.executionOrder = executionOrder;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Character getStatus() { return status; }
    public void setStatus(Character status) { this.status = status; }
    public Date getPlannedStartDate() { return plannedStartDate; }
    public void setPlannedStartDate(Date plannedStartDate) { this.plannedStartDate = plannedStartDate; }
    public Date getPlannedEndDate() { return plannedEndDate; }
    public void setPlannedEndDate(Date plannedEndDate) { this.plannedEndDate = plannedEndDate; }
    public Date getActualStartDate() { return actualStartDate; }
    public void setActualStartDate(Date actualStartDate) { this.actualStartDate = actualStartDate; }
    public Date getActualEndDate() { return actualEndDate; }
    public void setActualEndDate(Date actualEndDate) { this.actualEndDate = actualEndDate; }
    public Integer getExecutionOrder() { return executionOrder; }
    public void setExecutionOrder(Integer executionOrder) { this.executionOrder = executionOrder; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
