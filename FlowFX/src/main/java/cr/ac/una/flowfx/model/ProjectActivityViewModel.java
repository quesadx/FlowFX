package cr.ac.una.flowfx.model;

import javafx.beans.property.*;
import java.util.Date;

public class ProjectActivityViewModel {
    private final LongProperty id = new SimpleLongProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final ObjectProperty<String> status = new SimpleObjectProperty<>();
    private final ObjectProperty<Date> plannedStartDate = new SimpleObjectProperty<>();
    private final ObjectProperty<Date> plannedEndDate = new SimpleObjectProperty<>();
    private final ObjectProperty<Date> actualStartDate = new SimpleObjectProperty<>();
    private final ObjectProperty<Date> actualEndDate = new SimpleObjectProperty<>();
    private final IntegerProperty executionOrder = new SimpleIntegerProperty();
    private final ObjectProperty<Date> createdAt = new SimpleObjectProperty<>();
    private final ObjectProperty<Date> updatedAt = new SimpleObjectProperty<>();

    public ProjectActivityViewModel() {}
    public ProjectActivityViewModel(ProjectActivityDTO dto) {
        if (dto != null) {
            setId(dto.getId() == null ? 0L : dto.getId());
            setDescription(dto.getDescription());
            setStatus(dto.getStatus());
            setPlannedStartDate(dto.getPlannedStartDate());
            setPlannedEndDate(dto.getPlannedEndDate());
            setActualStartDate(dto.getActualStartDate());
            setActualEndDate(dto.getActualEndDate());
            setExecutionOrder(dto.getExecutionOrder() == null ? 0 : dto.getExecutionOrder());
            setCreatedAt(dto.getCreatedAt());
            setUpdatedAt(dto.getUpdatedAt());
        }
    }

    public ProjectActivityDTO toDTO() {
        ProjectActivityDTO dto = new ProjectActivityDTO();
        dto.setId(getId() == 0L ? null : getId());
        dto.setDescription(getDescription());
        dto.setStatus(getStatus()); // String
        dto.setPlannedStartDate(getPlannedStartDate());
        dto.setPlannedEndDate(getPlannedEndDate());
        dto.setActualStartDate(getActualStartDate());
        dto.setActualEndDate(getActualEndDate());
        dto.setExecutionOrder(getExecutionOrder());
        dto.setCreatedAt(getCreatedAt());
        dto.setUpdatedAt(getUpdatedAt());
        return dto;
    }

    public long getId() { return id.get(); }
    public void setId(long value) { id.set(value); }
    public LongProperty idProperty() { return id; }

    public String getDescription() { return description.get(); }
    public void setDescription(String value) { description.set(value); }
    public StringProperty descriptionProperty() { return description; }

    public String getStatus() { return status.get(); }
    public void setStatus(String value) { status.set(value); }
    public ObjectProperty<String> statusProperty() { return status; }

    public Date getPlannedStartDate() { return plannedStartDate.get(); }
    public void setPlannedStartDate(Date value) { plannedStartDate.set(value); }
    public ObjectProperty<Date> plannedStartDateProperty() { return plannedStartDate; }

    public Date getPlannedEndDate() { return plannedEndDate.get(); }
    public void setPlannedEndDate(Date value) { plannedEndDate.set(value); }
    public ObjectProperty<Date> plannedEndDateProperty() { return plannedEndDate; }

    public Date getActualStartDate() { return actualStartDate.get(); }
    public void setActualStartDate(Date value) { actualStartDate.set(value); }
    public ObjectProperty<Date> actualStartDateProperty() { return actualStartDate; }

    public Date getActualEndDate() { return actualEndDate.get(); }
    public void setActualEndDate(Date value) { actualEndDate.set(value); }
    public ObjectProperty<Date> actualEndDateProperty() { return actualEndDate; }

    public int getExecutionOrder() { return executionOrder.get(); }
    public void setExecutionOrder(int value) { executionOrder.set(value); }
    public IntegerProperty executionOrderProperty() { return executionOrder; }

    public Date getCreatedAt() { return createdAt.get(); }
    public void setCreatedAt(Date value) { createdAt.set(value); }
    public ObjectProperty<Date> createdAtProperty() { return createdAt; }

    public Date getUpdatedAt() { return updatedAt.get(); }
    public void setUpdatedAt(Date value) { updatedAt.set(value); }
    public ObjectProperty<Date> updatedAtProperty() { return updatedAt; }
}