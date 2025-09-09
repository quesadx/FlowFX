package cr.ac.una.flowfx.model;

import java.util.Objects;

/**
 * Data Transfer Object for {@code Person} entity.
 *
 * <p>This DTO preserves the original field names and public API so it can be
 * used interchangeably with existing application code that relies on these
 * getters/setters.</p>
 */
public class PersonDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String password;
    private Character status;
    private Character isAdmin;

    /**
     * Default constructor.
     */
    public PersonDTO() {}

    /**
     * Full constructor.
     *
     * @param id identifier
     * @param firstName first name
     * @param lastName last name
     * @param email email
     * @param username username
     * @param password password
     * @param status status flag
     * @param isAdmin admin flag
     */
    public PersonDTO(
        Long id,
        String firstName,
        String lastName,
        String email,
        String username,
        String password,
        Character status,
        Character isAdmin
    ) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.status = status;
        this.isAdmin = isAdmin;
    }

    /**
     * Returns the identifier.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the identifier.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the first name.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Returns the last name.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Returns the email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns the status flag.
     */
    public Character getStatus() {
        return status;
    }

    /**
     * Sets the status flag.
     */
    public void setStatus(Character status) {
        this.status = status;
    }

    /**
     * Returns whether the person is an admin.
     */
    public Character getIsAdmin() {
        return isAdmin;
    }

    /**
     * Sets the admin flag.
     */
    public void setIsAdmin(Character isAdmin) {
        this.isAdmin = isAdmin;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    /**
     * Equality is based on identifier to preserve prior DTO semantics.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof PersonDTO)) {
            return false;
        }
        PersonDTO other = (PersonDTO) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return (
            "PersonDTO{id=" +
            id +
            ", firstName=" +
            firstName +
            ", lastName=" +
            lastName +
            '}'
        );
    }
}
