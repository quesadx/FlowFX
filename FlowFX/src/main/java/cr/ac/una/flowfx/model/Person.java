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
import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author krist
 */
@Entity
@Table(name = "PERSON")
@NamedQueries({
    @NamedQuery(name = "Person.findAll", query = "SELECT p FROM Person p"),
    @NamedQuery(name = "Person.findByPerId", query = "SELECT p FROM Person p WHERE p.perId = :perId"),
    @NamedQuery(name = "Person.findByPerFirstName", query = "SELECT p FROM Person p WHERE p.perFirstName = :perFirstName"),
    @NamedQuery(name = "Person.findByPerLastName", query = "SELECT p FROM Person p WHERE p.perLastName = :perLastName"),
    @NamedQuery(name = "Person.findByEmail", query = "SELECT p FROM Person p WHERE p.email = :email"),
    @NamedQuery(name = "Person.findByUsername", query = "SELECT p FROM Person p WHERE p.username = :username"),
    @NamedQuery(name = "Person.findByPassword", query = "SELECT p FROM Person p WHERE p.password = :password"),
    @NamedQuery(name = "Person.findByStatus", query = "SELECT p FROM Person p WHERE p.status = :status"),
    @NamedQuery(name = "Person.findByIsAdmin", query = "SELECT p FROM Person p WHERE p.isAdmin = :isAdmin")})
public class Person implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @Column(name = "PER_ID")
    private BigDecimal perId;
    @Basic(optional = false)
    @Column(name = "PER_FIRST_NAME")
    private String perFirstName;
    @Basic(optional = false)
    @Column(name = "PER_LAST_NAME")
    private String perLastName;
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
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "leaderUserId", fetch = FetchType.LAZY)
    private List<Project> projectList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "techLeaderId", fetch = FetchType.LAZY)
    private List<Project> projectList1;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sponsorId", fetch = FetchType.LAZY)
    private List<Project> projectList2;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "createdBy", fetch = FetchType.LAZY)
    private List<ProjectTracking> projectTrackingList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "responsibleId", fetch = FetchType.LAZY)
    private List<ProjectActivity> projectActivityList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "createdBy", fetch = FetchType.LAZY)
    private List<ProjectActivity> projectActivityList1;

    public Person() {
    }

    public Person(BigDecimal perId) {
        this.perId = perId;
    }

    public Person(BigDecimal perId, String perFirstName, String perLastName, String email, Character status, Character isAdmin) {
        this.perId = perId;
        this.perFirstName = perFirstName;
        this.perLastName = perLastName;
        this.email = email;
        this.status = status;
        this.isAdmin = isAdmin;
    }

    public BigDecimal getPerId() {
        return perId;
    }

    public void setPerId(BigDecimal perId) {
        this.perId = perId;
    }

    public String getPerFirstName() {
        return perFirstName;
    }

    public void setPerFirstName(String perFirstName) {
        this.perFirstName = perFirstName;
    }

    public String getPerLastName() {
        return perLastName;
    }

    public void setPerLastName(String perLastName) {
        this.perLastName = perLastName;
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

    public Character getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Character isAdmin) {
        this.isAdmin = isAdmin;
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

    public List<ProjectActivity> getProjectActivityList() {
        return projectActivityList;
    }

    public void setProjectActivityList(List<ProjectActivity> projectActivityList) {
        this.projectActivityList = projectActivityList;
    }

    public List<ProjectActivity> getProjectActivityList1() {
        return projectActivityList1;
    }

    public void setProjectActivityList1(List<ProjectActivity> projectActivityList1) {
        this.projectActivityList1 = projectActivityList1;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (perId != null ? perId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Person)) {
            return false;
        }
        Person other = (Person) object;
        if ((this.perId == null && other.perId != null) || (this.perId != null && !this.perId.equals(other.perId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cr.ac.una.flowfx.model.Person[ perId=" + perId + " ]";
    }
    
}
