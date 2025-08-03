/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @author krist
 */
@Entity
@Table(name = "ADMIN")
@NamedQueries({
    @NamedQuery(name = "Admin.findAll", query = "SELECT a FROM Admin a"),
    @NamedQuery(name = "Admin.findByAdminId", query = "SELECT a FROM Admin a WHERE a.adminId = :adminId"),
    @NamedQuery(name = "Admin.findByFirstName", query = "SELECT a FROM Admin a WHERE a.firstName = :firstName"),
    @NamedQuery(name = "Admin.findByLastName", query = "SELECT a FROM Admin a WHERE a.lastName = :lastName"),
    @NamedQuery(name = "Admin.findByEmail", query = "SELECT a FROM Admin a WHERE a.email = :email"),
    @NamedQuery(name = "Admin.findByUsername", query = "SELECT a FROM Admin a WHERE a.username = :username"),
    @NamedQuery(name = "Admin.findByPassword", query = "SELECT a FROM Admin a WHERE a.password = :password"),
    @NamedQuery(name = "Admin.findByStatus", query = "SELECT a FROM Admin a WHERE a.status = :status"),
    @NamedQuery(name = "Admin.findByCreatedAt", query = "SELECT a FROM Admin a WHERE a.createdAt = :createdAt"),
    @NamedQuery(name = "Admin.findByUpdatedAt", query = "SELECT a FROM Admin a WHERE a.updatedAt = :updatedAt")})
public class Admin implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @Column(name = "ADMIN_ID")
    private Long adminId;
    @Basic(optional = false)
    @Column(name = "FIRST_NAME")
    private String firstName;
    @Basic(optional = false)
    @Column(name = "LAST_NAME")
    private String lastName;
    @Basic(optional = false)
    @Column(name = "EMAIL")
    private String email;
    @Basic(optional = false)
    @Column(name = "USERNAME")
    private String username;
    @Basic(optional = false)
    @Column(name = "PASSWORD")
    private String password;
    @Basic(optional = false)
    @Column(name = "STATUS")
    private Character status;
    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;
    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "leaderUserId", fetch = FetchType.LAZY)
    private List<Project> projectList;
    @OneToMany(mappedBy = "sponsorId", fetch = FetchType.LAZY)
    private List<Project> projectList1;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "techLeaderId", fetch = FetchType.LAZY)
    private List<Project> projectList2;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "createdBy", fetch = FetchType.LAZY)
    private List<ProjectTracking> projectTrackingList;

    public Admin() {
    }

    public Admin(Long adminId) {
        this.adminId = adminId;
    }

    public Admin(Long adminId, String firstName, String lastName, String email, String username, String password, Character status) {
        this.adminId = adminId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.status = status;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Character getStatus() {
        return status;
    }

    public void setStatus(Character status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Project> getProjectList() {
        return projectList;
    }

    public void setProjectList(List<Project> projectList) {
        this.projectList = projectList;
    }

    public List<Project> getProjectList1() {
        return projectList1;
    }

    public void setProjectList1(List<Project> projectList1) {
        this.projectList1 = projectList1;
    }

    public List<Project> getProjectList2() {
        return projectList2;
    }

    public void setProjectList2(List<Project> projectList2) {
        this.projectList2 = projectList2;
    }

    public List<ProjectTracking> getProjectTrackingList() {
        return projectTrackingList;
    }

    public void setProjectTrackingList(List<ProjectTracking> projectTrackingList) {
        this.projectTrackingList = projectTrackingList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (adminId != null ? adminId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Admin)) {
            return false;
        }
        Admin other = (Admin) object;
        if ((this.adminId == null && other.adminId != null) || (this.adminId != null && !this.adminId.equals(other.adminId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cr.ac.una.flowfx.model.Admin[ adminId=" + adminId + " ]";
    }
    
}
