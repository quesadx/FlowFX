package cr.ac.una.flowfx.model;

import java.util.Date;
import java.util.Objects;

/**
 * Data Transfer Object for {@code Project}.
 *
 * <p>This DTO preserves the original fields and accessors while adding basic
 * value semantics (equals, hashCode) and Javadoc for clarity.</p>
 */
public class ProjectDTO {

    private Long id;
    private String name;
    private Date plannedStartDate;
    private Date plannedEndDate;
    private Date actualStartDate;
    private Date actualEndDate;
    private String status;
    private Date createdAt;
    private Date updatedAt;
    private Long leaderUserId;
    private Long techLeaderId;
    private Long sponsorId;

    /** Default constructor. */
    public ProjectDTO() {
        // no-op
    }

    /**
     * Primary constructor without leader/tech/sponsor identifiers.
     */
    public ProjectDTO(
        Long id,
        String name,
        Date plannedStartDate,
        Date plannedEndDate,
        Date actualStartDate,
        Date actualEndDate,
        String status,
        Date createdAt,
        Date updatedAt
    ) {
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

    /**
     * Extended constructor including leader, tech leader and sponsor identifiers.
     */
    public ProjectDTO(
        Long id,
        String name,
        Date plannedStartDate,
        Date plannedEndDate,
        Date actualStartDate,
        Date actualEndDate,
        String status,
        Date createdAt,
        Date updatedAt,
        Long leaderUserId,
        Long techLeaderId,
        Long sponsorId
    ) {
        this(
            id,
            name,
            plannedStartDate,
            plannedEndDate,
            actualStartDate,
            actualEndDate,
            status,
            createdAt,
            updatedAt
        );
        this.leaderUserId = leaderUserId;
        this.techLeaderId = techLeaderId;
        this.sponsorId = sponsorId;
    }

    /** Identifier. */
    public Long getId() {
        return id;
    }

    /** Identifier. */
    public void setId(Long id) {
        this.id = id;
    }

    /** Project name. */
    public String getName() {
        return name;
    }

    /** Project name. */
    public void setName(String name) {
        this.name = name;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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

    public Long getLeaderUserId() {
        return leaderUserId;
    }

    public void setLeaderUserId(Long leaderUserId) {
        this.leaderUserId = leaderUserId;
    }

    public Long getTechLeaderId() {
        return techLeaderId;
    }

    public void setTechLeaderId(Long techLeaderId) {
        this.techLeaderId = techLeaderId;
    }

    public Long getSponsorId() {
        return sponsorId;
    }

    public void setSponsorId(Long sponsorId) {
        this.sponsorId = sponsorId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    /**
     * Equality is based on the identifier to preserve prior semantics.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof ProjectDTO)) {
            return false;
        }
        ProjectDTO other = (ProjectDTO) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return "ProjectDTO{id=" + id + ", name=" + name + "}";
    }
}
