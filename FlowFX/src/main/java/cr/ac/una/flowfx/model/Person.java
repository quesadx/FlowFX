package cr.ac.una.flowfx.model;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * JPA entity representing a person in the system.
 *
 * <p>This class is a direct mapping of the PERSON table and preserves the
 * existing named queries used by the application.</p>
 */
@Entity
@Table(name = "PERSON")
@NamedQueries(
    {
        @NamedQuery(name = "Person.findAll", query = "SELECT p FROM Person p"),
        @NamedQuery(
            name = "Person.findById",
            query = "SELECT p FROM Person p WHERE p.id = :id"
        ),
        @NamedQuery(
            name = "Person.findByPerFirstName",
            query = "SELECT p FROM Person p WHERE p.firstName = :firstName"
        ),
        @NamedQuery(
            name = "Person.findByPerLastName",
            query = "SELECT p FROM Person p WHERE p.lastName = :lastName"
        ),
        @NamedQuery(
            name = "Person.findByEmail",
            query = "SELECT p FROM Person p WHERE p.email = :email"
        ),
        @NamedQuery(
            name = "Person.findByUsername",
            query = "SELECT p FROM Person p WHERE p.username = :username"
        ),
        @NamedQuery(
            name = "Person.findByPassword",
            query = "SELECT p FROM Person p WHERE p.password = :password"
        ),
        @NamedQuery(
            name = "Person.findByStatus",
            query = "SELECT p FROM Person p WHERE p.status = :status"
        ),
        @NamedQuery(
            name = "Person.findByIsAdmin",
            query = "SELECT p FROM Person p WHERE p.isAdmin = :isAdmin"
        ),
    }
)
public class Person implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @Column(name = "PER_ID")
    private Long id;

    @Basic(optional = false)
    @Column(name = "PER_FIRST_NAME")
    private String firstName;

    @Basic(optional = false)
    @Column(name = "PER_LAST_NAME")
    private String lastName;

    @Basic(optional = false)
    @Column(name = "EMAIL")
    private String email;

    @Column(name = "USERNAME")
    private String username;

    @Column(name = "PASSWORD")
    private String password;

    @Basic(optional = false)
    @Column(name = "STATUS")
    private Character status;

    @Basic(optional = false)
    @Column(name = "IS_ADMIN")
    private Character isAdmin;

    @OneToMany(
        cascade = CascadeType.ALL,
        mappedBy = "leaderUserId",
        fetch = FetchType.LAZY
    )
    private List<Project> projectsAsLeader;

    @OneToMany(
        cascade = CascadeType.ALL,
        mappedBy = "techLeaderId",
        fetch = FetchType.LAZY
    )
    private List<Project> projectsAsTechLeader;

    @OneToMany(
        cascade = CascadeType.ALL,
        mappedBy = "sponsorId",
        fetch = FetchType.LAZY
    )
    private List<Project> projectsAsSponsor;

    @OneToMany(
        cascade = CascadeType.ALL,
        mappedBy = "createdBy",
        fetch = FetchType.LAZY
    )
    private List<ProjectTracking> projectTrackingList;

    @OneToMany(
        cascade = CascadeType.ALL,
        mappedBy = "responsibleId",
        fetch = FetchType.LAZY
    )
    private List<ProjectActivity> activitiesAsResponsible;

    @OneToMany(
        cascade = CascadeType.ALL,
        mappedBy = "createdBy",
        fetch = FetchType.LAZY
    )
    private List<ProjectActivity> activitiesCreatedBy;

    /**
     * Default constructor required by JPA.
     */
    public Person() {}

    /**
     * Convenience constructor with id.
     *
     * @param id identifier
     */
    public Person(Long id) {
        this.id = id;
    }

    /**
     * Partial constructor preserving original semantics.
     */
    public Person(
        Long id,
        String firstName,
        String lastName,
        String email,
        Character status,
        Character isAdmin
    ) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.status = status;
        this.isAdmin = isAdmin;
    }

    /** Returns the identifier. */
    public Long getId() {
        return id;
    }

    /** Sets the identifier. */
    public void setId(Long id) {
        this.id = id;
    }

    /** Returns the first name. */
    public String getFirstName() {
        return firstName;
    }

    /** Sets the first name. */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /** Returns the last name. */
    public String getLastName() {
        return lastName;
    }

    /** Sets the last name. */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /** Returns the email. */
    public String getEmail() {
        return email;
    }

    /** Sets the email. */
    public void setEmail(String email) {
        this.email = email;
    }

    /** Returns the username. */
    public String getUsername() {
        return username;
    }

    /** Sets the username. */
    public void setUsername(String username) {
        this.username = username;
    }

    /** Returns the password. */
    public String getPassword() {
        return password;
    }

    /** Sets the password. */
    public void setPassword(String password) {
        this.password = password;
    }

    /** Returns the status. */
    public Character getStatus() {
        return status;
    }

    /** Sets the status. */
    public void setStatus(Character status) {
        this.status = status;
    }

    /** Returns whether the person is admin. */
    public Character getIsAdmin() {
        return isAdmin;
    }

    /** Sets the admin flag. */
    public void setIsAdmin(Character isAdmin) {
        this.isAdmin = isAdmin;
    }

    public List<Project> getProjectsAsLeader() {
        return projectsAsLeader;
    }

    public void setProjectsAsLeader(List<Project> projectsAsLeader) {
        this.projectsAsLeader = projectsAsLeader;
    }

    public List<Project> getProjectsAsTechLeader() {
        return projectsAsTechLeader;
    }

    public void setProjectsAsTechLeader(List<Project> projectsAsTechLeader) {
        this.projectsAsTechLeader = projectsAsTechLeader;
    }

    public List<Project> getProjectsAsSponsor() {
        return projectsAsSponsor;
    }

    public void setProjectsAsSponsor(List<Project> projectsAsSponsor) {
        this.projectsAsSponsor = projectsAsSponsor;
    }

    public List<ProjectTracking> getProjectTrackingList() {
        return projectTrackingList;
    }

    public void setProjectTrackingList(
        List<ProjectTracking> projectTrackingList
    ) {
        this.projectTrackingList = projectTrackingList;
    }

    public List<ProjectActivity> getActivitiesAsResponsible() {
        return activitiesAsResponsible;
    }

    public void setActivitiesAsResponsible(
        List<ProjectActivity> activitiesAsResponsible
    ) {
        this.activitiesAsResponsible = activitiesAsResponsible;
    }

    public List<ProjectActivity> getActivitiesCreatedBy() {
        return activitiesCreatedBy;
    }

    public void setActivitiesCreatedBy(
        List<ProjectActivity> activitiesCreatedBy
    ) {
        this.activitiesCreatedBy = activitiesCreatedBy;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Equality is based on identifier identity, matching previous semantics but
     * implemented using {@link Objects#equals(Object, Object)} for clarity.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof Person)) {
            return false;
        }
        Person other = (Person) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return "cr.ac.una.flowfx.model.Person[ id=" + id + " ]";
    }
}
