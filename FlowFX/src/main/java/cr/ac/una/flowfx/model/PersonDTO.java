package cr.ac.una.flowfx.model;

public class PersonDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String password;
    private Character status;
    private Character isAdmin;

    public PersonDTO() {}

    public PersonDTO(Long id, String firstName, String lastName, String email, String username, String password, Character status, Character isAdmin) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.status = status;
        this.isAdmin = isAdmin;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Character getStatus() { return status; }
    public void setStatus(Character status) { this.status = status; }

    public Character getIsAdmin() { return isAdmin; }
    public void setIsAdmin(Character isAdmin) { this.isAdmin = isAdmin; }
}
