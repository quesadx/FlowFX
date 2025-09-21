/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cr.ac.una.flowfx.controller;

import cr.ac.una.flowfx.model.PersonDTO;
import cr.ac.una.flowfx.model.ProjectDTO;
import cr.ac.una.flowfx.model.ProjectTrackingDTO;
import cr.ac.una.flowfx.model.ProjectTrackingViewModel;
import cr.ac.una.flowfx.service.ProjectTrackingService;
import cr.ac.una.flowfx.util.AnimationManager;
import cr.ac.una.flowfx.util.AppContext;
import cr.ac.una.flowfx.util.FlowController;
import cr.ac.una.flowfx.util.Mensaje;
import cr.ac.una.flowfx.util.ObservationTableUtil;
import cr.ac.una.flowfx.util.PersonLabelUtil;
import cr.ac.una.flowfx.util.Respuesta;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXSlider;
import io.github.palexdev.materialfx.controls.MFXTextField;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class for Project Observations management.
 *
 * <p>This controller manages the observation/tracking view for projects including:
 * <ul>
 *   <li>Display of all project observations in a table format</li>
 *   <li>Creation of new observations through a modal popup form</li>
 *   <li>Real-time person name resolution for created by fields</li>
 *   <li>Date and percentage validation and formatting</li>
 *   <li>Integration with the project tracking web service</li>
 * </ul>
 * 
 * <p><strong>Navigation Pattern:</strong><br>
 * This view is accessed from ProjectExpandController.onActionExpandObservations()
 * and uses the "currentProject" from AppContext to determine which project's
 * observations to display and manage.
 * 
 * <p><strong>Data Flow:</strong><br>
 * Observations are loaded via ProjectTrackingService.findByProject() and displayed
 * using ProjectTrackingViewModel bindings. New observations are created through
 * the popup form and persisted via ProjectTrackingService.create().
 * 
 * @author FlowFX Development Team
 * @version 1.0
 * @since 3.0
 */
public class ProjectObservationsController extends Controller implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(ProjectObservationsController.class.getName());

    // FXML injected components
    @FXML
    private MFXDatePicker dpObservationDate;

    @FXML
    private Label lblPercentage; // Shows the current value of the slider

    @FXML
    private AnchorPane root;

    @FXML
    private MFXSlider sliderObservationPercentage;

    @FXML
    private TableColumn<ProjectTrackingViewModel, String> tbvObservationDate;

    @FXML
    private TableColumn<ProjectTrackingViewModel, String> tbvObservationPercentage;

    @FXML
    private TableColumn<ProjectTrackingViewModel, String> tbvObservationTitle;

    @FXML
    private TableColumn<ProjectTrackingViewModel, String> tbvObservationCreatedBy;

    @FXML
    private TableView<ProjectTrackingViewModel> tbvObservations;

    @FXML
    private TextArea txaObservationDescription;

    @FXML
    private MFXTextField txfObservationName;

    @FXML
    private VBox vbCover;

    @FXML
    private VBox vbObservationCreationPopUp;

    // State management fields
    private final ObservableList<ProjectTrackingViewModel> observations = FXCollections.observableArrayList();
    private ProjectDTO currentProject;
    private PersonDTO currentUser;

    @FXML
    void onActionBtnCreateObservation(ActionEvent event) {
        LOGGER.info("Opening observation creation popup");
        showObservationCreationPopup();
    }

    @FXML
    void onActionBtnReturnToProjectExpand(ActionEvent event) {
        LOGGER.info("Returning to project expand view");
        navigateBackToProjectExpand();
    }

    @FXML
    void onActionCancelObservationCreation(ActionEvent event) {
        LOGGER.info("Canceling observation creation");
        hideObservationCreationPopup();
        clearObservationForm();
    }

    @FXML
    void onActionConfirmObservationCreation(ActionEvent event) {
        LOGGER.info("Confirming observation creation");
        
        if (validateObservationForm()) {
            createNewObservation();
        } else {
            LOGGER.warning("Observation form validation failed");
            new Mensaje().show(AlertType.ERROR, "Error", "Por favor complete todos los campos requeridos.");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOGGER.fine("Initializing ProjectObservationsController with URL and ResourceBundle");
    }

    @Override
    public void initialize() {
        LOGGER.info("Initializing ProjectObservationsController");
        
        try {
            initializeProjectData();
            initializeCurrentUser();
            setupObservationForm();
            setupObservationsTable();
            loadObservationsForProject();
            
            LOGGER.info("ProjectObservationsController initialization completed successfully");
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error during ProjectObservationsController initialization", ex);
            new Mensaje().show(AlertType.ERROR, "Error", "Error al inicializar la vista de observaciones: " + ex.getMessage());
        }
    }

    /**
     * Initializes project data from the current context.
     */
    private void initializeProjectData() {
        Object projectContext = AppContext.getInstance().get("currentProject");
        
        if (projectContext instanceof ProjectDTO project) {
            this.currentProject = project;
            LOGGER.info("Loaded current project: " + project.getName() + " (ID: " + project.getId() + ")");
        } else {
            LOGGER.severe("No current project found in AppContext - cannot load observations");
            this.currentProject = null;
            new Mensaje().show(AlertType.ERROR, "Error", "No se pudo cargar el proyecto actual.");
        }
    }

    /**
     * Initializes current user data from the application context.
     */
    private void initializeCurrentUser() {
        Object userContext = AppContext.getInstance().get("user");
        
        if (userContext instanceof PersonDTO user) {
            this.currentUser = user;
            LOGGER.info("Loaded current user: " + user.getFirstName() + " " + user.getLastName() + " (ID: " + user.getId() + ")");
        } else {
            LOGGER.warning("No current user found in AppContext");
            this.currentUser = null;
        }
    }

    /**
     * Sets up the observation creation form with bindings and validation.
     */
    private void setupObservationForm() {
        LOGGER.fine("Setting up observation creation form");
        
        // Setup slider and percentage label binding
        if (sliderObservationPercentage != null && lblPercentage != null) {
            sliderObservationPercentage.setMin(0);
            sliderObservationPercentage.setMax(100);
            sliderObservationPercentage.setValue(0);
            
            // Bind label to slider value
            lblPercentage.textProperty().bind(
                sliderObservationPercentage.valueProperty().asString("%.0f%%")
            );
            
            LOGGER.fine("Configured percentage slider (0-100) with label binding");
        }
        
        // Set default date to today
        if (dpObservationDate != null) {
            dpObservationDate.setValue(LocalDate.now());
            LOGGER.fine("Set default observation date to today");
        }
    }

    /**
     * Sets up the observations table using ObservationTableUtil.
     */
    private void setupObservationsTable() {
        LOGGER.fine("Setting up observations table");
        
        if (ObservationTableUtil.validateTableConfiguration(tbvObservations, observations)) {
            ObservationTableUtil.setupObservationsTable(
                tbvObservations,
                observations,
                tbvObservationTitle,
                tbvObservationDate,
                tbvObservationPercentage,
                tbvObservationCreatedBy,
                this::showObservationDetail
            );
            
            LOGGER.info("Observations table configured successfully");
        } else {
            LOGGER.severe("Failed to validate table configuration");
            new Mensaje().show(AlertType.ERROR, "Error", "Error al configurar la tabla de observaciones.");
        }
    }

    /**
     * Loads observations for the current project from the web service.
     */
    private void loadObservationsForProject() {
        if (currentProject == null) {
            LOGGER.warning("Cannot load observations - no current project");
            return;
        }
        
        LOGGER.info("Loading observations for project ID: " + currentProject.getId());
        
        Task<List<ProjectTrackingDTO>> loadTask = createObservationLoadTask();
        configureObservationLoadTaskHandlers(loadTask);
        
        Thread taskThread = new Thread(loadTask);
        taskThread.setDaemon(true);
        taskThread.start();
    }

    /**
     * Creates a task to load observations from the web service.
     */
    private Task<List<ProjectTrackingDTO>> createObservationLoadTask() {
        return new Task<>() {
            @Override
            protected List<ProjectTrackingDTO> call() throws Exception {
                LOGGER.fine("Starting observation load task for project ID: " + currentProject.getId());
                
                ProjectTrackingService service = new ProjectTrackingService();
                Respuesta response = service.findByProject(currentProject.getId());
                
                if (Boolean.TRUE.equals(response.getEstado())) {
                    @SuppressWarnings("unchecked")
                    List<ProjectTrackingDTO> dtos = (List<ProjectTrackingDTO>) response.getResultado("ProjectTrackings");
                    
                    LOGGER.info("Successfully loaded " + (dtos != null ? dtos.size() : 0) + " observations");
                    return dtos;
                } else {
                    String errorMsg = "Failed to load observations: " + 
                        (response != null ? response.getMensaje() : "null response");
                    LOGGER.warning(errorMsg);
                    throw new RuntimeException(errorMsg);
                }
            }
        };
    }

    /**
     * Configures handlers for the observation load task.
     */
    private void configureObservationLoadTaskHandlers(Task<List<ProjectTrackingDTO>> loadTask) {
        loadTask.setOnSucceeded(event -> {
            @SuppressWarnings("unchecked")
            List<ProjectTrackingDTO> dtos = (List<ProjectTrackingDTO>) event.getSource().getValue();
            updateObservationsFromDTOs(dtos);
        });
        
        loadTask.setOnFailed(event -> {
            Throwable exception = event.getSource().getException();
            LOGGER.log(Level.SEVERE, "Failed to load observations", exception);
            Platform.runLater(() -> {
                new Mensaje().show(AlertType.ERROR, "Error", "Error al cargar las observaciones: " + 
                    (exception != null ? exception.getMessage() : "Error desconocido"));
            });
        });
    }

    /**
     * Updates observations list from DTOs and prefetches person labels.
     */
    private void updateObservationsFromDTOs(List<ProjectTrackingDTO> dtos) {
        Platform.runLater(() -> {
            observations.clear();
            
            if (dtos != null && !dtos.isEmpty()) {
                for (ProjectTrackingDTO dto : dtos) {
                    observations.add(new ProjectTrackingViewModel(dto));
                }
                
                LOGGER.info("Updated observations list with " + observations.size() + " items");
                
                // Prefetch person labels for better UI responsiveness
                prefetchCreatedByLabels();
                
                // Sort and refresh table
                ObservationTableUtil.sortAndRefreshTable(observations, tbvObservations);
            } else {
                LOGGER.info("No observations found for project");
            }
        });
    }

    /**
     * Pre-fetches person labels for observations to improve UI responsiveness.
     */
    private void prefetchCreatedByLabels() {
        LOGGER.fine("Pre-fetching created by labels for observations");
        
        long[] personIds = observations.stream()
            .mapToLong(ProjectTrackingViewModel::getCreatedBy)
            .filter(id -> id > 0)
            .distinct()
            .toArray();
        
        if (personIds.length > 0) {
            PersonLabelUtil.prefetchPersonLabels(personIds, () -> {
                LOGGER.fine("Completed prefetching person labels for " + personIds.length + " IDs");
            });
        }
    }

    /**
     * Shows the observation creation popup with animation.
     */
    private void showObservationCreationPopup() {
        if (vbObservationCreationPopUp != null) {
            vbObservationCreationPopUp.setVisible(true);
            AnimationManager.fadeIn(vbObservationCreationPopUp, javafx.util.Duration.millis(300));
            
            // Set focus to the name field
            if (txfObservationName != null) {
                Platform.runLater(() -> txfObservationName.requestFocus());
            }
            
            LOGGER.fine("Showed observation creation popup");
        }
    }

    /**
     * Hides the observation creation popup with animation.
     */
    private void hideObservationCreationPopup() {
        if (vbObservationCreationPopUp != null) {
            AnimationManager.fadeOut(vbObservationCreationPopUp, javafx.util.Duration.millis(200));
            Platform.runLater(() -> vbObservationCreationPopUp.setVisible(false));
            
            LOGGER.fine("Hid observation creation popup");
        }
    }

    /**
     * Validates the observation creation form.
     */
    private boolean validateObservationForm() {
        boolean isValid = true;
        StringBuilder validationErrors = new StringBuilder();
        
        // Validate observation name
        if (txfObservationName == null || txfObservationName.getText() == null || 
            txfObservationName.getText().trim().isEmpty()) {
            validationErrors.append("- Nombre de la observación es requerido\n");
            isValid = false;
        }
        
        // Validate observation description  
        if (txaObservationDescription == null || txaObservationDescription.getText() == null || 
            txaObservationDescription.getText().trim().isEmpty()) {
            validationErrors.append("- Descripción de la observación es requerida\n");
            isValid = false;
        }
        
        // Validate observation date
        if (dpObservationDate == null || dpObservationDate.getValue() == null) {
            validationErrors.append("- Fecha de la observación es requerida\n");
            isValid = false;
        }
        
        // Validate current project
        if (currentProject == null) {
            validationErrors.append("- Proyecto actual no encontrado\n");
            isValid = false;
        }
        
        // Validate current user
        if (currentUser == null) {
            validationErrors.append("- Usuario actual no encontrado\n");
            isValid = false;
        }
        
        if (!isValid) {
            LOGGER.warning("Form validation failed:\n" + validationErrors.toString());
        } else {
            LOGGER.fine("Form validation passed");
        }
        
        return isValid;
    }

    /**
     * Creates a new observation based on the form data.
     */
    private void createNewObservation() {
        LOGGER.info("Creating new observation for project: " + currentProject.getName());
        
        try {
            ProjectTrackingDTO dto = buildObservationDto();
            
            Task<Respuesta> createTask = new Task<>() {
                @Override
                protected Respuesta call() throws Exception {
                    ProjectTrackingService service = new ProjectTrackingService();
                    return service.create(dto);
                }
            };
            
            createTask.setOnSucceeded(event -> {
                Respuesta response = (Respuesta) event.getSource().getValue();
                handleObservationCreationResponse(response);
            });
            
            createTask.setOnFailed(event -> {
                Throwable exception = event.getSource().getException();
                LOGGER.log(Level.SEVERE, "Failed to create observation", exception);
                Platform.runLater(() -> {
                    new Mensaje().show(AlertType.ERROR, "Error", "Error al crear la observación: " +
                        (exception != null ? exception.getMessage() : "Error desconocido"));
                });
            });
            
            Thread createThread = new Thread(createTask);
            createThread.setDaemon(true);
            createThread.start();
            
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error building observation DTO", ex);
            new Mensaje().show(AlertType.ERROR, "Error", "Error al procesar los datos de la observación: " + ex.getMessage());
        }
    }

    /**
     * Builds an observation DTO from the form data.
     */
    private ProjectTrackingDTO buildObservationDto() {
        ProjectTrackingDTO dto = new ProjectTrackingDTO();
        
        // Set project and user references
        dto.setProjectId(currentProject.getId());
        dto.setCreatedBy(currentUser.getId());
        
        // Set form data
        dto.setObservations(buildObservationText());
        dto.setTrackingDate(convertLocalDateToDate(dpObservationDate.getValue()));
        dto.setProgressPercentage((int) Math.round(sliderObservationPercentage.getValue()));
        dto.setCreatedAt(new Date());
        
        LOGGER.info("Built observation DTO - Project: " + dto.getProjectId() + 
                   ", CreatedBy: " + dto.getCreatedBy() + 
                   ", Progress: " + dto.getProgressPercentage() + "%, Date: " + dto.getTrackingDate() + 
                   ", Observations: " + (dto.getObservations() != null ? dto.getObservations().substring(0, Math.min(50, dto.getObservations().length())) : "null"));
        
        return dto;
    }

    /**
     * Builds the complete observation text from name and description.
     */
    private String buildObservationText() {
        String name = txfObservationName.getText().trim();
        String description = txaObservationDescription.getText().trim();
        
        // Combine name and description for storage
        return name + ": " + description;
    }

    /**
     * Converts LocalDate to Date for DTO.
     */
    private Date convertLocalDateToDate(LocalDate localDate) {
        if (localDate == null) return null;
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Handles the response from observation creation.
     */
    private void handleObservationCreationResponse(Respuesta response) {
        Platform.runLater(() -> {
            if (Boolean.TRUE.equals(response.getEstado())) {
                LOGGER.info("Observation created successfully");
                
                // Hide popup and clear form
                hideObservationCreationPopup();
                clearObservationForm();
                
                // Reload observations to show the new one
                loadObservationsForProject();
                
                new Mensaje().show(AlertType.INFORMATION, "Éxito", "Observación creada exitosamente.");
            } else {
                String errorMsg = response != null ? response.getMensaje() : "Error desconocido";
                LOGGER.warning("Failed to create observation: " + errorMsg);
                new Mensaje().show(AlertType.ERROR, "Error", "Error al crear la observación: " + errorMsg);
            }
        });
    }

    /**
     * Clears the observation creation form.
     */
    private void clearObservationForm() {
        if (txfObservationName != null) {
            txfObservationName.clear();
        }
        
        if (txaObservationDescription != null) {
            txaObservationDescription.clear();
        }
        
        if (sliderObservationPercentage != null) {
            sliderObservationPercentage.setValue(0);
        }
        
        if (dpObservationDate != null) {
            dpObservationDate.setValue(LocalDate.now());
        }
        
        LOGGER.fine("Cleared observation form");
    }

    /**
     * Shows detailed view for the selected observation (placeholder for future enhancement).
     */
    private void showObservationDetail(ProjectTrackingViewModel observation) {
        if (observation != null) {
            LOGGER.info("Showing detail for observation ID: " + observation.getId());
            // Future enhancement: could open a detail/edit dialog
            new Mensaje().show(AlertType.INFORMATION, "Información", "Observación: " + observation.getObservations());
        }
    }

    /**
     * Navigates back to the project expand view.
     */
    private void navigateBackToProjectExpand() {
        try {
            FlowController.getInstance().goView("ProjectExpandView");
            LOGGER.info("Navigation back to ProjectExpandView completed");
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error navigating back to ProjectExpandView", ex);
            new Mensaje().show(AlertType.ERROR, "Error", "Error al navegar de vuelta: " + ex.getMessage());
        }
    }
}
