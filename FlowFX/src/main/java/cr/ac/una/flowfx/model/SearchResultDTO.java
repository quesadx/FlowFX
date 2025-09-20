package cr.ac.una.flowfx.model;

/**
 * Data Transfer Object for search results.
 * 
 * Represents a search match containing person information, associated project,
 * and the role of the person in that project (Sponsor, Leader, Tech Leader).
 */
public class SearchResultDTO {
    
    private PersonDTO person;
    private ProjectDTO project;
    private String role;
    private String displayText;
    
    /**
     * Default constructor.
     */
    public SearchResultDTO() {}
    
    /**
     * Constructor with all fields.
     * 
     * @param person the person found in the search
     * @param project the project where this person is involved
     * @param role the role of the person in the project (e.g., "Patrocinador", "Líder", "Líder Técnico")
     */
    public SearchResultDTO(PersonDTO person, ProjectDTO project, String role) {
        this.person = person;
        this.project = project;
        this.role = role;
        this.displayText = buildDisplayText();
    }
    
    /**
     * Builds the display text for the search result.
     * Format: "John Doe (Patrocinador): Project Name" or "Project Name" for project-only results
     */
    private String buildDisplayText() {
        if (project == null) {
            return "";
        }
        
        String projectName = safe(project.getName());
        
        // If no person, show only project name
        if (person == null) {
            return projectName;
        }
        
        String personName = buildPersonName();
        String roleText = safe(role);
        
        return String.format("%s (%s): %s", personName, roleText, projectName);
    }
    
    /**
     * Builds the person's full name.
     */
    private String buildPersonName() {
        if (person == null) return "";
        
        String firstName = safe(person.getFirstName());
        String lastName = safe(person.getLastName());
        
        if (firstName.isEmpty() && lastName.isEmpty()) {
            return safe(person.getUsername());
        }
        
        return (firstName + " " + lastName).trim();
    }
    
    /**
     * Safe string utility to handle null values.
     */
    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
    
    /**
     * Refreshes the display text (useful after modifying person/project/role).
     */
    public void refreshDisplayText() {
        this.displayText = buildDisplayText();
    }
    
    // Getters and setters
    
    public PersonDTO getPerson() {
        return person;
    }
    
    public void setPerson(PersonDTO person) {
        this.person = person;
        refreshDisplayText();
    }
    
    public ProjectDTO getProject() {
        return project;
    }
    
    public void setProject(ProjectDTO project) {
        this.project = project;
        refreshDisplayText();
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
        refreshDisplayText();
    }
    
    public String getDisplayText() {
        return displayText;
    }
    
    @Override
    public String toString() {
        return displayText;
    }
}