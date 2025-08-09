package cr.ac.una.flowfx.model;

import javafx.beans.property.*;
import java.util.Date;

public class ProjectTrackingViewModel {
    private final LongProperty id = new SimpleLongProperty();
    private final StringProperty observations = new SimpleStringProperty();
    private final ObjectProperty<Date> trackingDate = new SimpleObjectProperty<>();
    private final IntegerProperty progressPercentage = new SimpleIntegerProperty();
    private final ObjectProperty<Date> createdAt = new SimpleObjectProperty<>();

    public ProjectTrackingViewModel() {}
    public ProjectTrackingViewModel(ProjectTrackingDTO dto) {
        if (dto != null) {
            setId(dto.getId() == null ? 0L : dto.getId());
            setObservations(dto.getObservations());
            setTrackingDate(dto.getTrackingDate());
            setProgressPercentage(dto.getProgressPercentage() == null ? 0 : dto.getProgressPercentage());
            setCreatedAt(dto.getCreatedAt());
        }
    }

    public ProjectTrackingDTO toDTO() {
        return new ProjectTrackingDTO(
            getId() == 0L ? null : getId(),
            getObservations(),
            getTrackingDate(),
            getProgressPercentage(),
            getCreatedAt()
        );
    }

    public long getId() { return id.get(); }
    public void setId(long value) { id.set(value); }
    public LongProperty idProperty() { return id; }

    public String getObservations() { return observations.get(); }
    public void setObservations(String value) { observations.set(value); }
    public StringProperty observationsProperty() { return observations; }

    public Date getTrackingDate() { return trackingDate.get(); }
    public void setTrackingDate(Date value) { trackingDate.set(value); }
    public ObjectProperty<Date> trackingDateProperty() { return trackingDate; }

    public int getProgressPercentage() { return progressPercentage.get(); }
    public void setProgressPercentage(int value) { progressPercentage.set(value); }
    public IntegerProperty progressPercentageProperty() { return progressPercentage; }

    public Date getCreatedAt() { return createdAt.get(); }
    public void setCreatedAt(Date value) { createdAt.set(value); }
    public ObjectProperty<Date> createdAtProperty() { return createdAt; }
}
