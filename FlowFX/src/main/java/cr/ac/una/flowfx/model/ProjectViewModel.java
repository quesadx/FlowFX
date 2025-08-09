package cr.ac.una.flowfx.model;

import javafx.beans.property.*;
import java.util.Date;

public class ProjectViewModel {
    private final LongProperty id = new SimpleLongProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final ObjectProperty<Date> plannedStartDate = new SimpleObjectProperty<>();
    private final ObjectProperty<Date> plannedEndDate = new SimpleObjectProperty<>();
    private final ObjectProperty<Date> actualStartDate = new SimpleObjectProperty<>();
    private final ObjectProperty<Date> actualEndDate = new SimpleObjectProperty<>();
    private final ObjectProperty<Character> status = new SimpleObjectProperty<>();
    private final ObjectProperty<Date> createdAt = new SimpleObjectProperty<>();
    private final ObjectProperty<Date> updatedAt = new SimpleObjectProperty<>();

    public ProjectViewModel() {}
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
        }
    }

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
            getUpdatedAt()
        );
    }

    public long getId() { return id.get(); }
    public void setId(long value) { id.set(value); }
    public LongProperty idProperty() { return id; }

    public String getName() { return name.get(); }
    public void setName(String value) { name.set(value); }
    public StringProperty nameProperty() { return name; }

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

    public Character getStatus() { return status.get(); }
    public void setStatus(Character value) { status.set(value); }
    public ObjectProperty<Character> statusProperty() { return status; }

    public Date getCreatedAt() { return createdAt.get(); }
    public void setCreatedAt(Date value) { createdAt.set(value); }
    public ObjectProperty<Date> createdAtProperty() { return createdAt; }

    public Date getUpdatedAt() { return updatedAt.get(); }
    public void setUpdatedAt(Date value) { updatedAt.set(value); }
    public ObjectProperty<Date> updatedAtProperty() { return updatedAt; }
}
