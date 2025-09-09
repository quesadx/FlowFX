package cr.ac.una.flowfx.model;

import java.util.Date;
import java.util.Objects;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * View model for {@link ProjectDTO} used by JavaFX bindings.
 *
 * <p>This class provides observable properties and convenience conversion to
 * and from {@link ProjectDTO}. Public API is kept compatible with existing
 * callers.</p>
 */
public class ProjectViewModel {

    private final LongProperty id = new SimpleLongProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final ObjectProperty<Date> plannedStartDate =
        new SimpleObjectProperty<>();
    private final ObjectProperty<Date> plannedEndDate =
        new SimpleObjectProperty<>();
    private final ObjectProperty<Date> actualStartDate =
        new SimpleObjectProperty<>();
    private final ObjectProperty<Date> actualEndDate =
        new SimpleObjectProperty<>();
    private final ObjectProperty<String> status = new SimpleObjectProperty<>();
    private final ObjectProperty<Date> createdAt = new SimpleObjectProperty<>();
    private final ObjectProperty<Date> updatedAt = new SimpleObjectProperty<>();
    private final LongProperty leaderUserId = new SimpleLongProperty();
    private final LongProperty techLeaderId = new SimpleLongProperty();
    private final LongProperty sponsorId = new SimpleLongProperty();

    /**
     * Default constructor.
     */
    public ProjectViewModel() {
        // no-op
    }

    /**
     * Create a view model from a DTO preserving null-to-default mapping used
     * by the original implementation.
     *
     * @param dto source DTO, may be null
     */
    public ProjectViewModel(ProjectDTO dto) {
        if (dto != null) {
            setId(dto.getId() == null ? 0L : dto.getId());
            setName(dto.getName());
            setPlannedStartDate(dto.getPlannedStartDate());
            setPlannedEndDate(dto.getPlannedEndDate());
            setActualStartDate(dto.getActualStartDate());
            setActualEndDate(dto.getActualEndDate());
            setStatus(dto.getStatus());
            setCreatedAt(dto.getCreatedAt());
            setUpdatedAt(dto.getUpdatedAt());
            setLeaderUserId(
                dto.getLeaderUserId() == null ? 0L : dto.getLeaderUserId()
            );
            setTechLeaderId(
                dto.getTechLeaderId() == null ? 0L : dto.getTechLeaderId()
            );
            setSponsorId(dto.getSponsorId() == null ? 0L : dto.getSponsorId());
        }
    }

    /**
     * Convert the view model back to a DTO. Keeps original null/default mapping.
     *
     * @return a ProjectDTO representing current state
     */
    public ProjectDTO toDTO() {
        return new ProjectDTO(
            getId() == 0L ? null : getId(),
            getName(),
            getPlannedStartDate(),
            getPlannedEndDate(),
            getActualStartDate(),
            getActualEndDate(),
            getStatus(),
            getCreatedAt(),
            getUpdatedAt(),
            getLeaderUserId() == 0L ? null : getLeaderUserId(),
            getTechLeaderId() == 0L ? null : getTechLeaderId(),
            getSponsorId() == 0L ? null : getSponsorId()
        );
    }

    public long getId() {
        return id.get();
    }

    public void setId(long value) {
        id.set(value);
    }

    public LongProperty idProperty() {
        return id;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String value) {
        name.set(value);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public Date getPlannedStartDate() {
        return plannedStartDate.get();
    }

    public void setPlannedStartDate(Date value) {
        plannedStartDate.set(value);
    }

    public ObjectProperty<Date> plannedStartDateProperty() {
        return plannedStartDate;
    }

    public Date getPlannedEndDate() {
        return plannedEndDate.get();
    }

    public void setPlannedEndDate(Date value) {
        plannedEndDate.set(value);
    }

    public ObjectProperty<Date> plannedEndDateProperty() {
        return plannedEndDate;
    }

    public Date getActualStartDate() {
        return actualStartDate.get();
    }

    public void setActualStartDate(Date value) {
        actualStartDate.set(value);
    }

    public ObjectProperty<Date> actualStartDateProperty() {
        return actualStartDate;
    }

    public Date getActualEndDate() {
        return actualEndDate.get();
    }

    public void setActualEndDate(Date value) {
        actualEndDate.set(value);
    }

    public ObjectProperty<Date> actualEndDateProperty() {
        return actualEndDate;
    }

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String value) {
        status.set(value);
    }

    public ObjectProperty<String> statusProperty() {
        return status;
    }

    public Date getCreatedAt() {
        return createdAt.get();
    }

    public void setCreatedAt(Date value) {
        createdAt.set(value);
    }

    public ObjectProperty<Date> createdAtProperty() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt.get();
    }

    public void setUpdatedAt(Date value) {
        updatedAt.set(value);
    }

    public ObjectProperty<Date> updatedAtProperty() {
        return updatedAt;
    }

    public long getLeaderUserId() {
        return leaderUserId.get();
    }

    public void setLeaderUserId(long value) {
        leaderUserId.set(value);
    }

    public LongProperty leaderUserIdProperty() {
        return leaderUserId;
    }

    public long getTechLeaderId() {
        return techLeaderId.get();
    }

    public void setTechLeaderId(long value) {
        techLeaderId.set(value);
    }

    public LongProperty techLeaderIdProperty() {
        return techLeaderId;
    }

    public long getSponsorId() {
        return sponsorId.get();
    }

    public void setSponsorId(long value) {
        sponsorId.set(value);
    }

    public LongProperty sponsorIdProperty() {
        return sponsorId;
    }

    @Override
    public int hashCode() {
        Long idVal = getId() == 0L ? null : getId();
        return Objects.hashCode(idVal);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof ProjectViewModel)) {
            return false;
        }
        ProjectViewModel other = (ProjectViewModel) obj;
        Long thisId = getId() == 0L ? null : getId();
        Long otherId = other.getId() == 0L ? null : other.getId();
        return Objects.equals(thisId, otherId);
    }

    @Override
    public String toString() {
        return (
            "ProjectViewModel{id=" +
            (getId() == 0L ? "null" : getId()) +
            ", name=" +
            getName() +
            "}"
        );
    }
}
