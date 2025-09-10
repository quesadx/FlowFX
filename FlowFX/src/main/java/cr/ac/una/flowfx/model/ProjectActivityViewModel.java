package cr.ac.una.flowfx.model;

import java.util.Date;
import java.util.Objects;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * View model for ProjectActivity used by JavaFX bindings.
 *
 * <p>This class wraps observable properties and provides conversion to and from
 * {@link ProjectActivityDTO}. Public API is preserved to remain compatible with
 * existing consumers.</p>
 */
public class ProjectActivityViewModel {

    private final LongProperty id = new SimpleLongProperty();
    private final LongProperty projectId = new SimpleLongProperty();
    private final LongProperty responsibleId = new SimpleLongProperty(); // NEW: responsible FK
    private final StringProperty description = new SimpleStringProperty();
    private final ObjectProperty<String> status = new SimpleObjectProperty<>();
    private final ObjectProperty<Date> plannedStartDate =
        new SimpleObjectProperty<>();
    private final ObjectProperty<Date> plannedEndDate =
        new SimpleObjectProperty<>();
    private final ObjectProperty<Date> actualStartDate =
        new SimpleObjectProperty<>();
    private final ObjectProperty<Date> actualEndDate =
        new SimpleObjectProperty<>();
    private final IntegerProperty executionOrder = new SimpleIntegerProperty();
    private final ObjectProperty<Date> createdAt = new SimpleObjectProperty<>();
    private final ObjectProperty<Date> updatedAt = new SimpleObjectProperty<>();

    /**
     * Default constructor.
     */
    public ProjectActivityViewModel() {
        // no-op
    }

    /**
     * Initialize view model from a DTO. Null DTO is tolerated.
     *
     * @param dto source DTO
     */
    public ProjectActivityViewModel(ProjectActivityDTO dto) {
        if (dto != null) {
            setId(dto.getId() == null ? 0L : dto.getId());
            setProjectId(dto.getProjectId() == null ? 0L : dto.getProjectId());
            setResponsibleId(
                dto.getResponsibleId() == null ? 0L : dto.getResponsibleId()
            );
            setDescription(dto.getDescription());
            setStatus(dto.getStatus());
            setPlannedStartDate(dto.getPlannedStartDate());
            setPlannedEndDate(dto.getPlannedEndDate());
            setActualStartDate(dto.getActualStartDate());
            setActualEndDate(dto.getActualEndDate());
            setExecutionOrder(
                dto.getExecutionOrder() == null ? 0 : dto.getExecutionOrder()
            );
            setCreatedAt(dto.getCreatedAt());
            setUpdatedAt(dto.getUpdatedAt());
        }
    }

    /**
     * Convert this view model into a DTO.
     *
     * @return a ProjectActivityDTO reflecting the current state
     */
    public ProjectActivityDTO toDTO() {
        ProjectActivityDTO dto = new ProjectActivityDTO();
        dto.setId(getId() == 0L ? null : getId());
        dto.setProjectId(getProjectId() == 0L ? null : getProjectId());
        dto.setResponsibleId(
            getResponsibleId() == 0L ? null : getResponsibleId()
        );
        dto.setDescription(getDescription());
        dto.setStatus(getStatus());
        dto.setPlannedStartDate(getPlannedStartDate());
        dto.setPlannedEndDate(getPlannedEndDate());
        dto.setActualStartDate(getActualStartDate());
        dto.setActualEndDate(getActualEndDate());
        dto.setExecutionOrder(getExecutionOrder());
        dto.setCreatedAt(getCreatedAt());
        dto.setUpdatedAt(getUpdatedAt());
        return dto;
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

    public long getProjectId() {
        return projectId.get();
    }

    public void setProjectId(long value) {
        projectId.set(value);
    }

    public LongProperty projectIdProperty() {
        return projectId;
    }

    // --- ResponsibleId ---

    public long getResponsibleId() {
        return responsibleId.get();
    }

    public void setResponsibleId(long value) {
        responsibleId.set(value);
    }

    public LongProperty responsibleIdProperty() {
        return responsibleId;
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String value) {
        description.set(value);
    }

    public StringProperty descriptionProperty() {
        return description;
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

    public int getExecutionOrder() {
        return executionOrder.get();
    }

    public void setExecutionOrder(int value) {
        executionOrder.set(value);
    }

    public IntegerProperty executionOrderProperty() {
        return executionOrder;
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
        if (obj == null || !(obj instanceof ProjectActivityViewModel)) {
            return false;
        }
        ProjectActivityViewModel other = (ProjectActivityViewModel) obj;
        Long thisId = getId() == 0L ? null : getId();
        Long otherId = other.getId() == 0L ? null : other.getId();
        return Objects.equals(thisId, otherId);
    }

    @Override
    public String toString() {
        return (
            "ProjectActivityViewModel{id=" +
            (getId() == 0L ? "null" : getId()) +
            ", projectId=" +
            (getProjectId() == 0L ? "null" : getProjectId()) +
            ", responsibleId=" +
            (getResponsibleId() == 0L ? "null" : getResponsibleId()) +
            ", description=" +
            getDescription() +
            "}"
        );
    }
}
