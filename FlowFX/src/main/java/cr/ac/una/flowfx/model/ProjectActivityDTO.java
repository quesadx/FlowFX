package cr.ac.una.flowfx.model;

import java.util.Date;
import java.util.Objects;

/**
 * Data Transfer Object for project activities.
 *
 * <p>This DTO carries activity data between layers. It preserves the original
 * constructors used in the codebase and provides backward-compatible overloads
 * that omit {@code projectId}.</p>
 */
public class ProjectActivityDTO {

    private Long id;
    private Long projectId;
    private String description;
    private String status;
    private Integer executionOrder;

    private Date plannedStartDate;
    private Date plannedEndDate;
    private Date actualStartDate;
    private Date actualEndDate;
    private Date createdAt;
    private Date updatedAt;

    /** Default constructor. */
    public ProjectActivityDTO() {
        // no-op
    }

    /**
     * Full constructor including project identifier.
     */
    public ProjectActivityDTO(
        Long id,
        Long projectId,
        String description,
        String status,
        Integer executionOrder,
        Date plannedStartDate,
        Date plannedEndDate,
        Date actualStartDate,
        Date actualEndDate,
        Date createdAt,
        Date updatedAt
    ) {
        this.id = id;
        this.projectId = projectId;
        this.description = description;
        this.status = status;
        this.executionOrder = executionOrder;
        this.plannedStartDate = plannedStartDate;
        this.plannedEndDate = plannedEndDate;
        this.actualStartDate = actualStartDate;
        this.actualEndDate = actualEndDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Backward-compatible overload without {@code projectId} (primitive executionOrder).
     */
    public ProjectActivityDTO(
        Long id,
        String description,
        String status,
        Date plannedStartDate,
        Date plannedEndDate,
        Date actualStartDate,
        Date actualEndDate,
        int executionOrder,
        Date createdAt,
        Date updatedAt
    ) {
        this(
            id,
            null,
            description,
            status,
            Integer.valueOf(executionOrder),
            plannedStartDate,
            plannedEndDate,
            actualStartDate,
            actualEndDate,
            createdAt,
            updatedAt
        );
    }

    /**
     * Backward-compatible overload without {@code projectId}.
     */
    public ProjectActivityDTO(
        Long id,
        String description,
        String status,
        Date plannedStartDate,
        Date plannedEndDate,
        Date actualStartDate,
        Date actualEndDate,
        Integer executionOrder,
        Date createdAt,
        Date updatedAt
    ) {
        this(
            id,
            null,
            description,
            status,
            executionOrder,
            plannedStartDate,
            plannedEndDate,
            actualStartDate,
            actualEndDate,
            createdAt,
            updatedAt
        );
    }

    // --- Getters / Setters ---

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getExecutionOrder() {
        return executionOrder;
    }

    public void setExecutionOrder(Integer executionOrder) {
        this.executionOrder = executionOrder;
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

    // --- Value semantics ---

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    /**
     * Equality is based on the identifier to remain compatible with existing
     * code that treats DTO identity by id.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof ProjectActivityDTO)) {
            return false;
        }
        ProjectActivityDTO other = (ProjectActivityDTO) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return (
            "ProjectActivityDTO{id=" +
            id +
            ", projectId=" +
            projectId +
            ", description=" +
            description +
            "}"
        );
    }
}
