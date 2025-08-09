package cr.ac.una.flowfx.model;

import java.util.Date;

public class ProjectDTO {
    private Long id;
    private String name;
    private Date plannedStartDate;
    private Date plannedEndDate;
    private Date actualStartDate;
    private Date actualEndDate;
    private Character status;
    private Date createdAt;
    private Date updatedAt;

    public ProjectDTO() {}

    public ProjectDTO(Long id, String name, Date plannedStartDate, Date plannedEndDate, Date actualStartDate, Date actualEndDate, Character status, Date createdAt, Date updatedAt) {
        this.id = id;
        this.name = name;
        this.plannedStartDate = plannedStartDate;
        this.plannedEndDate = plannedEndDate;
        this.actualStartDate = actualStartDate;
        this.actualEndDate = actualEndDate;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Date getPlannedStartDate() { return plannedStartDate; }
    public void setPlannedStartDate(Date plannedStartDate) { this.plannedStartDate = plannedStartDate; }
    public Date getPlannedEndDate() { return plannedEndDate; }
    public void setPlannedEndDate(Date plannedEndDate) { this.plannedEndDate = plannedEndDate; }
    public Date getActualStartDate() { return actualStartDate; }
    public void setActualStartDate(Date actualStartDate) { this.actualStartDate = actualStartDate; }
    public Date getActualEndDate() { return actualEndDate; }
    public void setActualEndDate(Date actualEndDate) { this.actualEndDate = actualEndDate; }
    public Character getStatus() { return status; }
    public void setStatus(Character status) { this.status = status; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
