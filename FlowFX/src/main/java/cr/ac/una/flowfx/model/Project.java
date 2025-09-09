package cr.ac.una.flowfx.model;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * JPA entity representing a project.
 *
 * <p>This class is a direct mapping of the PROJECT table. Named queries and
 * relationships are preserved to maintain existing runtime behavior.</p>
 */
@Entity
@Table(name = "PROJECT")
@NamedQueries(
    {
        @NamedQuery(
            name = "Project.findAll",
            query = "SELECT p FROM Project p"
        ),
        @NamedQuery(
            name = "Project.findById",
            query = "SELECT p FROM Project p WHERE p.id = :id"
        ),
        @NamedQuery(
            name = "Project.findByName",
            query = "SELECT p FROM Project p WHERE p.name= :name"
        ),
        @NamedQuery(
            name = "Project.findByPlannedStartDate",
            query = "SELECT p FROM Project p WHERE p.plannedStartDate = :plannedStartDate"
        ),
        @NamedQuery(
            name = "Project.findByPlannedEndDate",
            query = "SELECT p FROM Project p WHERE p.plannedEndDate = :plannedEndDate"
        ),
        @NamedQuery(
            name = "Project.findByActualStartDate",
            query = "SELECT p FROM Project p WHERE p.actualStartDate = :actualStartDate"
        ),
        @NamedQuery(
            name = "Project.findByActualEndDate",
            query = "SELECT p FROM Project p WHERE p.actualEndDate = :actualEndDate"
        ),
        @NamedQuery(
            name = "Project.findByStatus",
            query = "SELECT p FROM Project p WHERE p.status = :status"
        ),
        @NamedQuery(
            name = "Project.findByCreatedAt",
            query = "SELECT p FROM Project p WHERE p.createdAt = :createdAt"
        ),
        @NamedQuery(
            name = "Project.findByUpdatedAt",
            query = "SELECT p FROM Project p WHERE p.updatedAt = :updatedAt"
        ),
    }
)
public class Project implements Serializable {

    private static final long serialVersionUID = 1L;

    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "SEQ_PROJECT_ID_GEN"
    )
    @SequenceGenerator(
        name = "SEQ_PROJECT_ID_GEN",
        sequenceName = "SEQ_PROJECT_ID",
        allocationSize = 1
    )
    @Basic(optional = false)
    @Column(name = "PROJECT_ID")
    private Long id;

    @Basic(optional = false)
    @Column(name = "PROJECT_NAME")
    private String name;

    @Basic(optional = false)
    @Column(name = "PLANNED_START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date plannedStartDate;

    @Basic(optional = false)
    @Column(name = "PLANNED_END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date plannedEndDate;

    @Column(name = "ACTUAL_START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date actualStartDate;

    @Column(name = "ACTUAL_END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date actualEndDate;

    @Basic(optional = false)
    @Column(name = "STATUS")
    private Character status;

    @Column(name = "CREATED_AT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "UPDATED_AT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @OneToMany(
        cascade = CascadeType.ALL,
        mappedBy = "projectId",
        fetch = FetchType.LAZY
    )
    private List<Notification> notificationList;

    @JoinColumn(name = "LEADER_USER_ID", referencedColumnName = "PER_ID")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Person leaderUserId;

    @JoinColumn(name = "TECH_LEADER_ID", referencedColumnName = "PER_ID")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Person techLeaderId;

    @JoinColumn(name = "SPONSOR_ID", referencedColumnName = "PER_ID")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Person sponsorId;

    @OneToMany(
        cascade = CascadeType.ALL,
        mappedBy = "projectId",
        fetch = FetchType.LAZY
    )
    private List<ProjectTracking> trackingList;

    @OneToMany(
        cascade = CascadeType.ALL,
        mappedBy = "projectId",
        fetch = FetchType.LAZY
    )
    private List<ProjectActivity> activityList;

    public Project() {}

    public Project(Long id) {
        this.id = id;
    }

    public Project(
        Long id,
        String name,
        Date plannedStartDate,
        Date plannedEndDate,
        Character status
    ) {
        this.id = id;
        this.name = name;
        this.plannedStartDate = plannedStartDate;
        this.plannedEndDate = plannedEndDate;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getPlannedStartDate() {
        return plannedStartDate;
    }

    public void setPlannedStartDate(Date plannedStartDate) {
        this.plannedStartDate = plannedStartDate;
    }

    public Date getPlannedEndDate() {
        return plannedEndDate;
    }

    public void setPlannedEndDate(Date plannedEndDate) {
        this.plannedEndDate = plannedEndDate;
    }

    public Date getActualStartDate() {
        return actualStartDate;
    }

    public void setActualStartDate(Date actualStartDate) {
        this.actualStartDate = actualStartDate;
    }

    public Date getActualEndDate() {
        return actualEndDate;
    }

    public void setActualEndDate(Date actualEndDate) {
        this.actualEndDate = actualEndDate;
    }

    public Character getStatus() {
        return status;
    }

    public void setStatus(Character status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Notification> getNotificationList() {
        return notificationList;
    }

    public void setNotificationList(List<Notification> notificationList) {
        this.notificationList = notificationList;
    }

    public Person getLeaderUserId() {
        return leaderUserId;
    }

    public void setLeaderUserId(Person leaderUserId) {
        this.leaderUserId = leaderUserId;
    }

    public Person getTechLeaderId() {
        return techLeaderId;
    }

    public void setTechLeaderId(Person techLeaderId) {
        this.techLeaderId = techLeaderId;
    }

    public Person getSponsorId() {
        return sponsorId;
    }

    public void setSponsorId(Person sponsorId) {
        this.sponsorId = sponsorId;
    }

    public List<ProjectTracking> getTrackingList() {
        return trackingList;
    }

    public void setTrackingList(List<ProjectTracking> trackingList) {
        this.trackingList = trackingList;
    }

    public List<ProjectActivity> getActivityList() {
        return activityList;
    }

    public void setActivityList(List<ProjectActivity> activityList) {
        this.activityList = activityList;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Project other = (Project) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return "cr.ac.una.flowfx.model.Project[ id=" + id + " ]";
    }
}
