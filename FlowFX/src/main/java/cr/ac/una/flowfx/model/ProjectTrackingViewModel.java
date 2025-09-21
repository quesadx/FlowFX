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
 * View model for project tracking used by JavaFX bindings.
 *
 * <p>Provides observable properties and convenient conversion to/from
 * {@link ProjectTrackingDTO}. Public API and behavior are preserved while
 * improving readability and value semantics (equals/hashCode).</p>
 */
public class ProjectTrackingViewModel {

    private final LongProperty id = new SimpleLongProperty();
    private final LongProperty projectId = new SimpleLongProperty();
    private final LongProperty createdBy = new SimpleLongProperty();
    private final StringProperty observations = new SimpleStringProperty();
    private final ObjectProperty<Date> trackingDate =
        new SimpleObjectProperty<>();
    private final IntegerProperty progressPercentage =
        new SimpleIntegerProperty();
    private final ObjectProperty<Date> createdAt = new SimpleObjectProperty<>();

    /**
     * Default constructor.
     */
    public ProjectTrackingViewModel() {
        // no-op
    }

    /**
     * Creates a view model initialized from the provided DTO.
     *
     * @param dto source DTO, may be null
     */
    public ProjectTrackingViewModel(ProjectTrackingDTO dto) {
        if (dto != null) {
            setId(dto.getId() == null ? 0L : dto.getId());
            setProjectId(dto.getProjectId() == null ? 0L : dto.getProjectId());
            setCreatedBy(dto.getCreatedBy() == null ? 0L : dto.getCreatedBy());
            setObservations(dto.getObservations());
            setTrackingDate(dto.getTrackingDate());
            setProgressPercentage(
                dto.getProgressPercentage() == null
                    ? 0
                    : dto.getProgressPercentage()
            );
            setCreatedAt(dto.getCreatedAt());
        }
    }

    /**
     * Converts this view model to a DTO.
     *
     * @return a ProjectTrackingDTO representing the current state
     */
    public ProjectTrackingDTO toDTO() {
        ProjectTrackingDTO dto = new ProjectTrackingDTO();
        dto.setId(getId() == 0L ? null : getId());
        dto.setProjectId(getProjectId() == 0L ? null : getProjectId());
        dto.setCreatedBy(getCreatedBy() == 0L ? null : getCreatedBy());
        dto.setObservations(getObservations());
        dto.setTrackingDate(getTrackingDate());
        dto.setProgressPercentage(getProgressPercentage());
        dto.setCreatedAt(getCreatedAt());
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

    public String getObservations() {
        return observations.get();
    }

    public void setObservations(String value) {
        observations.set(value);
    }

    public StringProperty observationsProperty() {
        return observations;
    }

    public Date getTrackingDate() {
        return trackingDate.get();
    }

    public void setTrackingDate(Date value) {
        trackingDate.set(value);
    }

    public ObjectProperty<Date> trackingDateProperty() {
        return trackingDate;
    }

    public int getProgressPercentage() {
        return progressPercentage.get();
    }

    public void setProgressPercentage(int value) {
        progressPercentage.set(value);
    }

    public IntegerProperty progressPercentageProperty() {
        return progressPercentage;
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

    public long getProjectId() {
        return projectId.get();
    }

    public void setProjectId(long value) {
        projectId.set(value);
    }

    public LongProperty projectIdProperty() {
        return projectId;
    }

    public long getCreatedBy() {
        return createdBy.get();
    }

    public void setCreatedBy(long value) {
        createdBy.set(value);
    }

    public LongProperty createdByProperty() {
        return createdBy;
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
        if (obj == null || !(obj instanceof ProjectTrackingViewModel)) {
            return false;
        }
        ProjectTrackingViewModel other = (ProjectTrackingViewModel) obj;
        Long thisId = getId() == 0L ? null : getId();
        Long otherId = other.getId() == 0L ? null : other.getId();
        return Objects.equals(thisId, otherId);
    }

    @Override
    public String toString() {
        return (
            "ProjectTrackingViewModel{id=" +
            (getId() == 0L ? "null" : getId()) +
            ", observations=" +
            getObservations() +
            "}"
        );
    }
}
