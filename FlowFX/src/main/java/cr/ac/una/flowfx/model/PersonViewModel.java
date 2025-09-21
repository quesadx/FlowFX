package cr.ac.una.flowfx.model;

import javafx.beans.property.*;

public class PersonViewModel {
    private final LongProperty id = new SimpleLongProperty();
    private final StringProperty firstName = new SimpleStringProperty("");
    private final StringProperty lastName = new SimpleStringProperty("");
    private final StringProperty email = new SimpleStringProperty("");
    private final StringProperty username = new SimpleStringProperty("");
    private final StringProperty password = new SimpleStringProperty("");
    private final ObjectProperty<Character> status = new SimpleObjectProperty<>();
    private final ObjectProperty<Character> isAdmin = new SimpleObjectProperty<>();

    public PersonViewModel() {}
    public PersonViewModel(PersonDTO dto) {
        if (dto != null) {
            setId(dto.getId() == null ? 0L : dto.getId());
            setFirstName(dto.getFirstName());
            setLastName(dto.getLastName());
            setEmail(dto.getEmail());
            setUsername(dto.getUsername());
            setPassword(dto.getPassword());
            setStatus(dto.getStatus());
            setIsAdmin(dto.getIsAdmin());
        }
    }

    public PersonDTO toDTO() {
        return new PersonDTO(
            getId() == 0L ? null : getId(),
            getFirstName(),
            getLastName(),
            getEmail(),
            getUsername(),
            getPassword(),
            getStatus(),
            getIsAdmin()
        );
    }

    public long getId() { return id.get(); }
    public void setId(long value) { id.set(value); }
    public LongProperty idProperty() { return id; }

    public String getFirstName() { return firstName.get(); }
    public void setFirstName(String value) { firstName.set(value == null ? "" : value); }
    public StringProperty firstNameProperty() { return firstName; }

    public String getLastName() { return lastName.get(); }
    public void setLastName(String value) { lastName.set(value == null ? "" : value); }
    public StringProperty lastNameProperty() { return lastName; }

    public String getEmail() { return email.get(); }
    public void setEmail(String value) { email.set(value == null ? "" : value); }
    public StringProperty emailProperty() { return email; }

    public String getUsername() { return username.get(); }
    public void setUsername(String value) { username.set(value == null ? "" : value); }
    public StringProperty usernameProperty() { return username; }

    public String getPassword() { return password.get(); }
    public void setPassword(String value) { password.set(value == null ? "" : value); }
    public StringProperty passwordProperty() { return password; }

    public Character getStatus() { return status.get(); }
    public void setStatus(Character value) { status.set(value); }
    public ObjectProperty<Character> statusProperty() { return status; }

    public Character getIsAdmin() { return isAdmin.get(); }
    public void setIsAdmin(Character value) { isAdmin.set(value); }
    public ObjectProperty<Character> isAdminProperty() { return isAdmin; }
}
