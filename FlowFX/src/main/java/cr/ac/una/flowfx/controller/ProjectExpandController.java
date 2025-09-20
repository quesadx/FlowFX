package cr.ac.una.flowfx.controller;

import cr.ac.una.flowfx.model.PersonDTO;
import cr.ac.una.flowfx.model.ProjectActivityDTO;
import cr.ac.una.flowfx.model.ProjectActivityViewModel;
import cr.ac.una.flowfx.model.ProjectDTO;
import cr.ac.una.flowfx.model.ProjectViewModel;
import cr.ac.una.flowfx.service.PersonService;
import cr.ac.una.flowfx.service.ProjectActivityService;
import cr.ac.una.flowfx.service.ProjectService;
import cr.ac.una.flowfx.util.AnimationManager;
import cr.ac.una.flowfx.util.AppContext;
import cr.ac.una.flowfx.util.BindingUtils;
import cr.ac.una.flowfx.util.FlowController;
import cr.ac.una.flowfx.util.ProjectExcelExportUtil;
import cr.ac.una.flowfx.util.Respuesta;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCircleToggleNode;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;

/**
 * Controller for the Project Expand view providing comprehensive project management functionality.
 * 
 * <p>This controller manages the detailed view of projects including:
 * <ul>
 *   <li>Project metadata display and editing with automatic person name resolution</li>
 *   <li>Real-time status synchronization with web service backend</li>
 *   <li>Activity management with drag-and-drop reordering capabilities</li>
 *   <li>Excel export functionality for project schedules</li>
 *   <li>Stakeholder management and information display</li>
 * </ul>
 * 
 * <p><strong>Status Management:</strong><br>
 * Project status is handled using string codes (P=Planned, R=Running, S=Suspended, C=Completed)
 * with automatic persistence to maintain data consistency. Changes are synchronized asynchronously
 * to preserve UI responsiveness.
 * 
 * <p><strong>Person Name Resolution:</strong><br>
 * All person references display full names (First Last) instead of numeric IDs.
 * Names are cached in AppContext for performance optimization and UI consistency.
 * 
 * <p><strong>Activity Ordering:</strong><br>
 * Activities support drag-and-drop reordering with automatic execution order updates
 * and immediate persistence to the backend service.
 * 
 * @author FlowFX Development Team
 * @version 3.0 - Enhanced with improved status persistence and Excel export
 * @since 1.0
 */
public class ProjectExpandController extends Controller implements Initializable {

    private static final java.util.logging.Logger LOGGER =
        java.util.logging.Logger.getLogger(ProjectExpandController.class.getName());

    // Constants
    private static final DataFormat ACTIVITY_INDEX = new DataFormat("application/x-flowfx-activity-index");

    // FXML injected components
    @FXML private AnchorPane root;
    @FXML private VBox vbCover;
    @FXML private VBox vbDisplayActivityExpand;
    @FXML private MFXButton btnReturnManagement, btnConfirmActivityChanges, btnCancelActivityChanges;
    @FXML private MFXTextField txfProjectName;
    @FXML private MFXTextField txfSponsorId;
    @FXML private MFXTextField txfLeaderId;
    @FXML private MFXTextField txfTechLeaderId;
    @FXML private MFXDatePicker dpProjectStartDate;
    @FXML private MFXDatePicker dpProjectActualStartDate;
    @FXML private MFXCircleToggleNode tgProjectStatusPending;
    @FXML private MFXCircleToggleNode tgProjectStatusRunning;
    @FXML private MFXCircleToggleNode tgProjectStatusSuspended;
    @FXML private MFXCircleToggleNode tgProjectStatusCompleted;
    @FXML private ToggleGroup ProjectStatus;
    @FXML private TableView<ProjectActivityViewModel> tbvActivities;
    @FXML private TableColumn<ProjectActivityViewModel, String> tbcActivityName;
    @FXML private TableColumn<ProjectActivityViewModel, String> tbcActivityStatus;
    @FXML private TableColumn<ProjectActivityViewModel, String> tbcActivityResponsible;
    @FXML private MFXTextField txfResponsible;
    @FXML private MFXTextField txfCreatedBy;
    @FXML private MFXDatePicker dpLastUpdate;
    @FXML private MFXDatePicker dpCreationDate;
    @FXML private MFXDatePicker dpPlannedStartDate;
    @FXML private MFXDatePicker dpActualStartDate;
    @FXML private MFXDatePicker dpPlannedEndDate;
    @FXML private MFXDatePicker dpActualEndDate;
    @FXML private TextArea txaDescription;
    @FXML private VBox vbDisplayActivityCreation;
    @FXML private MFXTextField txfResponsableCreation;
    @FXML private TextArea txaDescriptionCreation;
    @FXML private MFXDatePicker dpPlannedStartDateCreation;
    @FXML private MFXDatePicker dpPlannedEndDateCreation;

    // State management fields
    private final ProjectViewModel vm = new ProjectViewModel();
    private final ObservableList<ProjectActivityViewModel> activities = FXCollections.observableArrayList();
    private ProjectActivityViewModel selectedActivity;
    private boolean statusPersistInProgress = false;
    private boolean activityStatusPersistInProgress = false;
    private String activityCreationStatus = "P"; // Default status for new activities

    // Change detection for activity detail popup
    private final javafx.beans.property.BooleanProperty activityHasChanges = new javafx.beans.property.SimpleBooleanProperty(false);
    private String activitySnapshotDescription;
    private LocalDate snapshotCreationDate;
    private LocalDate snapshotLastUpdateDate;
    private LocalDate snapshotPlannedStartDate;
    private LocalDate snapshotPlannedEndDate;
    private LocalDate snapshotActualStartDate;
    private LocalDate snapshotActualEndDate;
    @FXML
    private MFXCircleToggleNode tgActivityDetailStatusSuspended;
    @FXML
    private ToggleGroup ActivityDetailStatusGroup;
    @FXML
    private MFXCircleToggleNode tgActivityDetailStatusCompleted;
    @FXML
    private MFXCircleToggleNode tgActivityDetailStatusPending;
    @FXML
    private MFXCircleToggleNode tgActivityDetailStatusRunning;
    @FXML
    private MFXCircleToggleNode tgActivityCreateStatusSuspended;
    @FXML
    private ToggleGroup ActivityCreateStatusGroup;
    @FXML
    private MFXCircleToggleNode tgActivityCreateStatusCompleted;
    @FXML
    private MFXCircleToggleNode tgActivityCreateStatusPending;
    @FXML
    private MFXCircleToggleNode tgActivityCreateStatusRunning;

    // Activity Detail Status Toggle Actions
    @FXML
    private void onActionTgActivityDetailStatusSuspended(ActionEvent event) {
        updateActivityDetailStatus("S");
    }

    @FXML
    private void onActionTgActivityDetailStatusCompleted(ActionEvent event) {
        updateActivityDetailStatus("C");
    }

    @FXML
    private void onActionTgActivityDetailStatusPending(ActionEvent event) {
        updateActivityDetailStatus("P");
    }

    @FXML
    private void onActionTgActivityDetailStatusRunning(ActionEvent event) {
        updateActivityDetailStatus("R");
    }

    // Activity Creation Status Toggle Actions
    @FXML
    private void onActionTgActivityCreateStatusSuspended(ActionEvent event) {
        activityCreationStatus = "S";
    }

    @FXML
    private void onActionTgActivityCreateStatusCompleted(ActionEvent event) {
        activityCreationStatus = "C";
    }

    @FXML
    private void onActionTgActivityCreateStatusPending(ActionEvent event) {
        activityCreationStatus = "P";
    }

    @FXML
    private void onActionTgActivityCreateStatusRunning(ActionEvent event) {
        activityCreationStatus = "R";
    }

    /**
     * Enumeration for person roles in the project.
     */
    private enum PersonRole {
        SPONSOR("ProjectExpand.sponsor.selected"),
        LEADER("ProjectExpand.leader.selected"),
        TECH_LEADER("ProjectExpand.techleader.selected");
        
        private final String contextKey;
        
        PersonRole(String contextKey) {
            this.contextKey = contextKey;
        }
        
        public String getContextKey() {
            return contextKey;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LOGGER.fine("Initializing ProjectExpandController with URL and ResourceBundle");
    }

    @Override
    public void initialize() {
        LOGGER.fine("Initializing ProjectExpandController");
        initializeProjectData();
        bindFields();
        setupActivitiesTable();
        loadActivitiesForProject();
        loadPersonNamesImmediately();
        refreshProjectFromServer();

        // Bind activity change buttons to change detection
        if (btnConfirmActivityChanges != null) {
            // Confirm is disabled until there are changes
            btnConfirmActivityChanges.disableProperty().bind(activityHasChanges.not());
        }
        if (btnCancelActivityChanges != null) {
            // Cancel should always be enabled to close the popup
            btnCancelActivityChanges.disableProperty().unbind();
            btnCancelActivityChanges.setDisable(false);
        }
    }

    /**
     * Initializes project data from the current context or sets defaults.
     */
    private void initializeProjectData() {
        Object projectContext = AppContext.getInstance().get("currentProject");
        if (projectContext instanceof ProjectDTO dto) {
            ProjectViewModel initial = new ProjectViewModel(dto);
            copyProjectDataToViewModel(initial);
        } else {
            vm.setStatus("P"); // Default to Pending status
        }
    }

    /**
     * Copies project data from DTO to view model.
     */
    private void copyProjectDataToViewModel(ProjectViewModel source) {
        vm.setId(source.getId());
        vm.setName(source.getName());
        vm.setPlannedStartDate(source.getPlannedStartDate());
        vm.setPlannedEndDate(source.getPlannedEndDate());
        vm.setActualStartDate(source.getActualStartDate());
        vm.setActualEndDate(source.getActualEndDate());
        vm.setStatus(source.getStatus());
        vm.setCreatedAt(source.getCreatedAt());
        vm.setUpdatedAt(source.getUpdatedAt());
        vm.setLeaderUserId(source.getLeaderUserId());
        vm.setTechLeaderId(source.getTechLeaderId());
        vm.setSponsorId(source.getSponsorId());
    }

    /**
     * Binds UI components to the view model properties.
     */
    private void bindFields() {
        bindBasicFields();
        bindDateFields();
        bindStatusFields();
        bindPersonFields();
    }

    /**
     * Binds basic text fields to view model properties.
     */
    private void bindBasicFields() {
        txfProjectName.textProperty().bindBidirectional(vm.nameProperty());
    }

    /**
     * Binds date picker components to view model date properties.
     */
    private void bindDateFields() {
        bindDatePicker(dpProjectStartDate, true);
        bindDatePicker(dpProjectActualStartDate, false);
    }

    /**
     * Binds status toggle group to view model with automatic persistence.
     */
    private void bindStatusFields() {
        configureStatusToggles();
        setupStatusChangeListener();
        setDefaultStatusIfEmpty();
    }

    /**
     * Configures status toggle user data.
     */
    private void configureStatusToggles() {
        tgProjectStatusPending.setUserData("P");
        tgProjectStatusRunning.setUserData("R");
        tgProjectStatusSuspended.setUserData("S");
        tgProjectStatusCompleted.setUserData("C");
        
        // Configure activity detail status toggles
        if (tgActivityDetailStatusPending != null) {
            tgActivityDetailStatusPending.setUserData("P");
            tgActivityDetailStatusRunning.setUserData("R");
            tgActivityDetailStatusSuspended.setUserData("S");
            tgActivityDetailStatusCompleted.setUserData("C");
        }
        
        // Configure activity creation status toggles
        if (tgActivityCreateStatusPending != null) {
            tgActivityCreateStatusPending.setUserData("P");
            tgActivityCreateStatusRunning.setUserData("R");
            tgActivityCreateStatusSuspended.setUserData("S");
            tgActivityCreateStatusCompleted.setUserData("C");
            
            // Set default selection for activity creation (Pending)
            ActivityCreateStatusGroup.selectToggle(tgActivityCreateStatusPending);
        }
    }

    /**
     * Sets up automatic status change persistence.
     */
    private void setupStatusChangeListener() {
        vm.statusProperty().addListener((observable, oldStatus, newStatus) -> {
            if (shouldPersistStatusChange(oldStatus, newStatus)) {
                // Auto-assign actual dates based on project status
                String code = newStatus == null ? null : newStatus.trim().toUpperCase();
                Date now = new Date();
                if ("R".equals(code) && vm.getActualStartDate() == null) {
                    vm.setActualStartDate(now);
                }
                if ("C".equals(code)) {
                    if (vm.getActualStartDate() == null) vm.setActualStartDate(now);
                    if (vm.getActualEndDate() == null) vm.setActualEndDate(now);
                }

                // Hook for notifications (to integrate with backend service)
                enqueueStatusChangeNotification("PROJECT", vm.getId(), code);

                updateProjectStatusSilent(newStatus);
            }
        });
        
        BindingUtils.bindToggleGroupToProperty(ProjectStatus, vm.statusProperty());
    }

    /**
     * Determines if status change should be persisted.
     */
    private boolean shouldPersistStatusChange(String oldStatus, String newStatus) {
        return newStatus != null && 
               !newStatus.equals(oldStatus) && 
               !statusPersistInProgress && 
               oldStatus != null;
    }

    /**
     * Sets default status if empty and updates toggle selection.
     */
    private void setDefaultStatusIfEmpty() {
        if (vm.getStatus() == null || vm.getStatus().isBlank()) {
            vm.setStatus("P"); // Triggers persistence
        } else {
            selectToggleForStatus(vm.getStatus());
        }
    }

    /**
     * Configures person display fields and sets up name resolution.
     */
    private void bindPersonFields() {
        configurePersonFields();
        refreshPersonLabels();
        setupPersonFieldListeners();
    }

    /**
     * Configures person fields as non-editable.
     */
    private void configurePersonFields() {
        txfLeaderId.setEditable(false);
        txfTechLeaderId.setEditable(false);
        txfSponsorId.setEditable(false);
    }

    /**
     * Sets up listeners for person ID changes.
     */
    private void setupPersonFieldListeners() {
        vm.leaderUserIdProperty().addListener((obs, oldVal, newVal) -> refreshLeaderLabel());
        vm.techLeaderIdProperty().addListener((obs, oldVal, newVal) -> refreshTechLeaderLabel());
        vm.sponsorIdProperty().addListener((obs, oldVal, newVal) -> refreshSponsorLabel());
    }

    // Action Handlers
    @FXML
    private void onActionBtnReturnToManagement(ActionEvent event) {
        FlowController.getInstance().goView("ProjectManagementView");
        enableNavigationBar();
    }

    /**
     * Enables the navigation bar.
     */
    private void enableNavigationBar() {
        Object navigationBar = AppContext.getInstance().get("navigationBar");
        if (navigationBar instanceof VBox vbox) {
            vbox.setDisable(false);
        }
    }

    @FXML
    private void onActionBtnSelectActivityResponsible(ActionEvent event) {
        selectPersonForActivityCreation();
    }

    @FXML
    private void onActionBtnSelectSponsor(ActionEvent event) {
        openPersonSelector(txfSponsorId, PersonRole.SPONSOR.getContextKey());
    }

    @FXML
    private void onActionBtnSelectLeader(ActionEvent event) {
        openPersonSelector(txfLeaderId, PersonRole.LEADER.getContextKey());
    }

    @FXML
    private void onActionBtnSelectTechLeader(ActionEvent event) {
        openPersonSelector(txfTechLeaderId, PersonRole.TECH_LEADER.getContextKey());
    }

    @FXML
    private void onActionBtnPrintReport(ActionEvent event) {
        Stage stage = (Stage) root.getScene().getWindow();
        ProjectExcelExportUtil.exportProjectSchedule(
            stage,
            vm,
            activities,
            this::resolvePersonNameSync,
            ProjectExpandController::mapStatusToSpanish
        );
    }

    @FXML
    private void onActionBtnReturnActivityExpand(ActionEvent event) {
        AnimationManager.hidePopup(vbDisplayActivityExpand, vbCover);
    }

    @FXML
    private void onActionCreateActivity(ActionEvent event) {
        clearActivityCreationForm();
        AnimationManager.showPopup(vbDisplayActivityCreation, vbCover);
    }

    @FXML
    private void onActionCancelCreateActivity(ActionEvent event) {
        clearActivityCreationForm();
        AnimationManager.hidePopup(vbDisplayActivityCreation, vbCover);
    }

    @FXML
    private void onActionConfirmCreateActivity(ActionEvent event) {
        createNewActivity();
    }

    @FXML
    private void onActionConfirmUpdates(ActionEvent event) {
        updateSelectedActivity();
        AnimationManager.hidePopup(vbDisplayActivityExpand, vbCover);
    }

    @FXML
    private void onActionTxfSponsorId(ActionEvent event) {
        openPersonInformation(vm.getSponsorId(), "Sponsor");
    }

    @FXML
    private void onActionTxfLeaderId(ActionEvent event) {
        openPersonInformation(vm.getLeaderUserId(), "Project Leader");
    }

    @FXML
    private void onActionTechLeaderId(ActionEvent event) {
        openPersonInformation(vm.getTechLeaderId(), "Technical Leader");
    }

    // Status toggle handlers (kept for FXML compatibility)
    @FXML private void onActionTgProjectStatusPending(ActionEvent event) { /* Auto-handled by binding */ }
    @FXML private void onActionTgProjectStatusRunning(ActionEvent event) { /* Auto-handled by binding */ }
    @FXML private void onActionTgProjectStatusSuspended(ActionEvent event) { /* Auto-handled by binding */ }
    @FXML private void onActionTgProjectStatusCompleted(ActionEvent event) { /* Auto-handled by binding */ }

    // Activity Status Management
    
    /**
     * Updates the selected activity's status and persists the change.
     */
    private void updateActivityDetailStatus(String statusCode) {
        if (selectedActivity == null || activityStatusPersistInProgress) {
            return;
        }
        
        String oldStatus = selectedActivity.getStatus();
        if (statusCode != null && !statusCode.equals(oldStatus)) {
            LOGGER.log(Level.INFO, "Updating activity status: {0} -> {1} for activity ID: {2}", 
                new Object[]{oldStatus, statusCode, selectedActivity.getId()});
            
            selectedActivity.setStatus(statusCode);
            
            // Debug: Log the DTO before sending
            ProjectActivityDTO dto = selectedActivity.toDTO();
            LOGGER.log(Level.INFO, "Activity DTO status before sending: {0}, ID: {1}", 
                new Object[]{dto.getStatus(), dto.getId()});
            
            persistActivityStatusAsync(selectedActivity, statusCode);
            
            // Apply automatic actual date assignment
            applyAutomaticActualDatesForActivity(selectedActivity);
        }
    }
    
    /**
     * Persists activity status change asynchronously.
     */
    private void persistActivityStatusAsync(ProjectActivityViewModel activity, String statusCode) {
        activityStatusPersistInProgress = true;
        
        new Thread(() -> {
            try {
                ProjectActivityService service = new ProjectActivityService();
                ProjectActivityDTO dto = activity.toDTO();
                
                Respuesta response = service.update(dto);
                
                Platform.runLater(() -> {
                    handleActivityStatusUpdateResponse(response, activity, statusCode);
                });
                
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Error persisting activity status", ex);
                Platform.runLater(() -> {
                    activityStatusPersistInProgress = false;
                });
            }
        }, "activity-status-update").start();
    }
    
    /**
     * Handles the response from activity status update.
     */
    private void handleActivityStatusUpdateResponse(Respuesta response, 
            ProjectActivityViewModel activity, String statusCode) {
        if (!Boolean.TRUE.equals(response.getEstado())) {
            LOGGER.log(Level.WARNING, "Activity status update failed: {0}", response.getMensaje());
            // Revert status change on failure
            // Note: This is a simple revert, in production you might want to show user notification
        } else {
            LOGGER.log(Level.INFO, "Activity status successfully persisted: {0}", statusCode);
            
            // Refresh the table display
            tbvActivities.refresh();
            
            // Enqueue notification if needed
            enqueueStatusChangeNotification("ACTIVITY", activity.getId(), statusCode);
        }
        activityStatusPersistInProgress = false;
    }
    
    /**
     * Sets up activity detail status toggle binding when an activity is selected.
     */
    private void bindActivityDetailStatusToggles(ProjectActivityViewModel activity) {
        if (ActivityDetailStatusGroup == null || activity == null) {
            return;
        }
        
        // Select the appropriate toggle based on activity status
        String status = activity.getStatus();
        if (status != null) {
            selectActivityDetailToggleForStatus(status.trim().toUpperCase());
        }
    }
    
    /**
     * Selects the appropriate activity detail toggle for the given status.
     */
    private void selectActivityDetailToggleForStatus(String status) {
        if (ActivityDetailStatusGroup == null || status == null || status.isBlank()) {
            return;
        }
        
        ActivityDetailStatusGroup.getToggles().stream()
            .filter(toggle -> toggle.getUserData() != null && 
                status.equalsIgnoreCase(toggle.getUserData().toString()))
            .findFirst()
            .ifPresent(ActivityDetailStatusGroup::selectToggle);
    }
    
    /**
     * Handles person selection for activity creation.
     */
    private void selectPersonForActivityCreation() {
        txfResponsableCreation.setEditable(true);
        
        FlowController.getInstance().goViewInWindowModal(
            "PersonSelectionView", (Stage) root.getScene().getWindow(), false);
        
        Object selection = AppContext.getInstance().get("personSelectionResult");
        if (selection instanceof PersonDTO person) {
            String personLabel = buildPersonLabel(person);
            
            if (person.getId() != null) {
                cachePersonLabel(person.getId(), personLabel);
            }
            
            txfResponsableCreation.setText(personLabel);
            AppContext.getInstance().set("ProjectExpand.activityResponsible", person);
            txfResponsableCreation.setEditable(false);
        }
    }

    /**
     * Opens the person selection dialog and updates the target field.
     */
    private void openPersonSelector(MFXTextField targetField, String contextKey) {
        targetField.setEditable(true);
        
        FlowController.getInstance().goViewInWindowModal(
            "PersonSelectionView", (Stage) root.getScene().getWindow(), false);
        
        Object selection = AppContext.getInstance().get("personSelectionResult");
        if (selection instanceof PersonDTO person) {
            handlePersonSelection(targetField, person, contextKey);
        }
    }

    /**
     * Handles the result of person selection.
     */
    private void handlePersonSelection(MFXTextField targetField, PersonDTO person, String contextKey) {
        String personLabel = buildPersonLabel(person);
        
        // Cache before updating to prevent async overwrite
        if (person.getId() != null) {
            cachePersonLabel(person.getId(), personLabel);
        }
        
        updateViewModelWithSelectedPerson(targetField, person);
        AppContext.getInstance().set(contextKey, person);
        targetField.setEditable(false);
    }

    /**
     * Updates the view model with the selected person.
     */
    private void updateViewModelWithSelectedPerson(MFXTextField targetField, PersonDTO person) {
        Long personId = person.getId() == null ? 0L : person.getId();
        
        if (targetField == txfLeaderId) {
            vm.setLeaderUserId(personId);
        } else if (targetField == txfTechLeaderId) {
            vm.setTechLeaderId(personId);
        } else if (targetField == txfSponsorId) {
            vm.setSponsorId(personId);
        }
    }

    // Person Label Management

    /**
     * Builds a display label for a person.
     */
    private String buildPersonLabel(PersonDTO person) {
        if (person == null) return "";
        
        String firstName = person.getFirstName() == null ? "" : person.getFirstName().trim();
        String lastName = person.getLastName() == null ? "" : person.getLastName().trim();
        
        return (firstName + " " + lastName).trim();
    }

    /**
     * Caches a person label in the application context.
     */
    private void cachePersonLabel(Long personId, String label) {
        if (personId != null && label != null && !label.isBlank()) {
            AppContext.getInstance().set("person." + personId + ".label", label);
        }
    }

    /**
     * Retrieves a cached person label.
     */
    private String getCachedPersonLabel(long personId) {
        Object label = AppContext.getInstance().get("person." + personId + ".label");
        return label instanceof String stringLabel ? stringLabel : null;
    }

    /**
     * Refreshes all person label displays.
     */
    private void refreshPersonLabels() {
        refreshLeaderLabel();
        refreshTechLeaderLabel();
        refreshSponsorLabel();
    }

    private void refreshLeaderLabel() {
        updatePersonLabelIntoField(vm.getLeaderUserId(), txfLeaderId);
    }

    private void refreshTechLeaderLabel() {
        updatePersonLabelIntoField(vm.getTechLeaderId(), txfTechLeaderId);
    }

    private void refreshSponsorLabel() {
        updatePersonLabelIntoField(vm.getSponsorId(), txfSponsorId);
    }

    /**
     * Updates a text field with the resolved person name.
     */
    private void updatePersonLabelIntoField(long personId, MFXTextField targetField) {
        if (targetField == null || personId <= 0) {
            if (targetField != null) targetField.setText("");
            return;
        }
        
        String cachedLabel = getCachedPersonLabel(personId);
        if (cachedLabel != null && !cachedLabel.isBlank()) {
            targetField.setText(cachedLabel);
            return;
        }
        
        targetField.setText("Loading...");
        fetchPersonLabelAsync(personId, targetField);
    }

    /**
     * Fetches a person label asynchronously.
     */
    private void fetchPersonLabelAsync(long personId, MFXTextField targetField) {
        Thread fetchThread = new Thread(() -> {
            try {
                PersonService personService = new PersonService();
                Respuesta response = personService.find(personId);
                
                String displayText = processPersonResponse(response, personId);
                Platform.runLater(() -> targetField.setText(displayText));
            } catch (Exception ex) {
                LOGGER.fine("Async person fetch failed for ID " + personId + ": " + ex.getMessage());
                Platform.runLater(() -> targetField.setText("ID: " + personId));
            }
        }, "person-label-fetch-" + personId);
        
        fetchThread.setDaemon(true);
        fetchThread.start();
    }

    /**
     * Processes person service response and returns display text.
     */
    private String processPersonResponse(Respuesta response, long personId) {
        if (Boolean.TRUE.equals(response.getEstado())) {
            Object personData = response.getResultado("Person");
            if (personData instanceof PersonDTO person) {
                String personLabel = buildPersonLabel(person);
                if (!personLabel.isBlank()) {
                    cachePersonLabel(personId, personLabel);
                    return personLabel;
                }
            }
        }
        return "ID: " + personId;
    }

    /**
     * Loads person names immediately during initialization.
     */
    private void loadPersonNamesImmediately() {
        loadPersonNameIfValid(vm.getLeaderUserId(), txfLeaderId);
        loadPersonNameIfValid(vm.getTechLeaderId(), txfTechLeaderId);
        loadPersonNameIfValid(vm.getSponsorId(), txfSponsorId);
    }

    /**
     * Loads a person name if the ID is valid.
     */
    private void loadPersonNameIfValid(long personId, MFXTextField targetField) {
        if (personId > 0) {
            String personName = resolvePersonNameSync(personId);
            if (personName != null && !personName.equals(String.valueOf(personId))) {
                Platform.runLater(() -> targetField.setText(personName));
            }
        }
    }

    /**
     * Resolves a person's display name synchronously.
     */
    private String resolvePersonNameSync(long personId) {
        if (personId <= 0) return null;
        
        String cachedLabel = getCachedPersonLabel(personId);
        if (cachedLabel != null && !cachedLabel.isBlank()) {
            return cachedLabel;
        }
        
        try {
            PersonService personService = new PersonService();
            Respuesta response = personService.find(personId);
            
            if (Boolean.TRUE.equals(response.getEstado())) {
                Object personData = response.getResultado("Person");
                if (personData instanceof PersonDTO person) {
                    String personName = buildPersonLabel(person);
                    if (!personName.isBlank()) {
                        cachePersonLabel(personId, personName);
                        return personName;
                    }
                }
            }
        } catch (Exception ignored) {
            // Service unavailable, fallback to ID
        }
        
        return String.valueOf(personId);
    }

    /**
     * Opens detailed person information dialog.
     */
    private void openPersonInformation(long personId, String roleLabel) {
        if (personId <= 0) {
            LOGGER.warning("Invalid person ID provided: " + personId);
            return;
        }
        
        PersonService service = new PersonService();
        Respuesta response = service.find(personId);
        
        if (!Boolean.TRUE.equals(response.getEstado())) {
            LOGGER.warning("Could not retrieve person with ID " + personId + ": " + 
                (response != null ? response.getMensaje() : "null response"));
            return;
        }
        
        Object personData = response.getResultado("Person");
        if (!(personData instanceof PersonDTO person)) {
            LOGGER.warning("Service did not return valid PersonDTO for ID " + personId);
            return;
        }
        
        showPersonInformationModal(person, roleLabel);
    }

    /**
     * Shows the person information modal dialog.
     */
    private void showPersonInformationModal(PersonDTO person, String roleLabel) {
        AppContext.getInstance().set("personInformation.person", person);
        AppContext.getInstance().set("personInformation.role", roleLabel);
        
        try {
            FlowController.getInstance().goViewInWindowModal(
                "PersonInformationView", (Stage) root.getScene().getWindow(), false);
        } finally {
            AppContext.getInstance().delete("personInformation.person");
            AppContext.getInstance().delete("personInformation.role");
        }
    }

    // Date Binding Utilities

    /**
     * Binds a date picker to the appropriate view model property.
     */
    private void bindDatePicker(MFXDatePicker picker, boolean isStartDate) {
        if (isStartDate) {
            bindPlannedStartDate(picker);
        } else {
            bindPlannedEndDate(picker);
        }
    }

    /**
     * Binds the planned start date picker.
     */
    private void bindPlannedStartDate(MFXDatePicker picker) {
        vm.plannedStartDateProperty().addListener((observable, oldDate, newDate) -> {
            LocalDate localDate = convertDateToLocalDate(newDate);
            if (!isLocalDateEqual(localDate, picker.getValue())) {
                picker.setValue(localDate);
            }
        });
        
        picker.valueProperty().addListener((observable, oldLocalDate, newLocalDate) -> {
            Date date = convertLocalDateToDate(newLocalDate);
            if (!isDateEqual(date, vm.getPlannedStartDate())) {
                vm.setPlannedStartDate(date);
            }
        });
        
        initializeDatePicker(picker, vm.getPlannedStartDate());
    }

    /**
     * Binds the planned end date picker.
     */
    private void bindPlannedEndDate(MFXDatePicker picker) {
        vm.plannedEndDateProperty().addListener((observable, oldDate, newDate) -> {
            LocalDate localDate = convertDateToLocalDate(newDate);
            if (!isLocalDateEqual(localDate, picker.getValue())) {
                picker.setValue(localDate);
            }
        });
        
        picker.valueProperty().addListener((observable, oldLocalDate, newLocalDate) -> {
            Date date = convertLocalDateToDate(newLocalDate);
            if (!isDateEqual(date, vm.getPlannedEndDate())) {
                vm.setPlannedEndDate(date);
            }
        });
        
        initializeDatePicker(picker, vm.getPlannedEndDate());
    }

    /**
     * Initializes a date picker with an initial value.
     */
    private void initializeDatePicker(MFXDatePicker picker, Date initialDate) {
        if (initialDate != null) {
            picker.setValue(convertDateToLocalDate(initialDate));
        }
    }

    /**
     * Converts Date to LocalDate.
     */
    private LocalDate convertDateToLocalDate(Date date) {
        return date == null ? null : Instant.ofEpochMilli(date.getTime())
            .atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Converts LocalDate to Date.
     */
    private Date convertLocalDateToDate(LocalDate localDate) {
        return localDate == null ? null : Date.from(
            localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Checks if two dates are equal.
     */
    private boolean isDateEqual(Date date1, Date date2) {
        if (date1 == null && date2 == null) return true;
        if (date1 == null || date2 == null) return false;
        return date1.equals(date2);
    }

    /**
     * Checks if two LocalDates are equal.
     */
    private boolean isLocalDateEqual(LocalDate date1, LocalDate date2) {
        if (date1 == null && date2 == null) return true;
        if (date1 == null || date2 == null) return false;
        return date1.equals(date2);
    }

    // Project Status Management

    /**
     * Updates the project status and persists changes.
     */
    private void updateProjectStatusSilent(String statusCode) {
        if (statusPersistInProgress || statusCode == null || statusCode.isBlank()) {
            if (statusPersistInProgress) {
                LOGGER.fine("Project status update already in progress; ignoring change.");
            } else {
                LOGGER.fine("Invalid status code provided: " + statusCode);
            }
            return;
        }
        
        LOGGER.info("Updating project status to: " + statusCode + " for project ID: " + vm.getId());
        statusPersistInProgress = true;
        
        persistProjectStatusAsync(statusCode);
    }

    /**
     * Persists project status asynchronously.
     */
    private void persistProjectStatusAsync(String statusCode) {
        new Thread(() -> {
            try {
                ProjectService service = new ProjectService();
                Respuesta response = service.update(vm.toDTO());
                
                Platform.runLater(() -> handleStatusUpdateResponse(response));
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    LOGGER.warning("Exception during project status update: " + ex.getMessage());
                    statusPersistInProgress = false;
                });
            }
        }, "project-status-update").start();
    }

    /**
     * Handles the response from status update.
     */
    private void handleStatusUpdateResponse(Respuesta response) {
        if (!Boolean.TRUE.equals(response.getEstado())) {
            LOGGER.warning("Project status update failed: " +
                (response != null ? response.getMensaje() : "null") + " | " +
                (response != null ? response.getMensajeInterno() : "null"));
        } else {
            LOGGER.info("Project status update successful. Message: " + response.getMensaje());
            Object updatedData = response.getResultado("Project");
            if (updatedData instanceof ProjectDTO project) {
                updateProjectFromServer(project);
            }
        }
        statusPersistInProgress = false;
    }

    /**
     * Selects the appropriate toggle for the given status.
     */
    private void selectToggleForStatus(String status) {
        if (ProjectStatus == null) return;
        
        if (status == null || status.isBlank()) {
            ProjectStatus.selectToggle(tgProjectStatusPending);
            return;
        }
        
        String statusCode = status.trim().toUpperCase();
        ProjectStatus.getToggles().stream()
            .filter(toggle -> toggle.getUserData() != null && 
                statusCode.equalsIgnoreCase(toggle.getUserData().toString()))
            .findFirst()
            .ifPresent(ProjectStatus::selectToggle);
    }

    // Server Synchronization

    /**
     * Refreshes project data from the server.
     */
    private void refreshProjectFromServer() {
        long projectId = vm.getId();
        if (projectId <= 0) return;

        Task<Respuesta> refreshTask = createProjectRefreshTask(projectId);
        configureRefreshTaskHandlers(refreshTask);
        
        Thread refreshThread = new Thread(refreshTask, "project-refresh");
        refreshThread.setDaemon(true);
        refreshThread.start();
    }

    /**
     * Creates a task to refresh project data.
     */
    private Task<Respuesta> createProjectRefreshTask(long projectId) {
        return new Task<>() {
            @Override
            protected Respuesta call() {
                ProjectService service = new ProjectService();
                return service.find(projectId);
            }
        };
    }

    /**
     * Configures handlers for the refresh task.
     */
    private void configureRefreshTaskHandlers(Task<Respuesta> refreshTask) {
        refreshTask.setOnSucceeded(event -> {
            Respuesta response = refreshTask.getValue();
            if (response != null && Boolean.TRUE.equals(response.getEstado())) {
                Object projectData = response.getResultado("Project");
                if (projectData instanceof ProjectDTO project) {
                    updateProjectFromServer(project);
                }
            } else {
                LOGGER.fine("Project refresh: No update from server. Estado=" + 
                    (response != null ? response.getEstado() : null));
            }
        });

        refreshTask.setOnFailed(event -> 
            LOGGER.fine("Project refresh failed: " + refreshTask.getException()));
    }

    /**
     * Updates view model and UI with server data.
     */
    private void updateProjectFromServer(ProjectDTO project) {
        AppContext.getInstance().set("currentProject", project);
        
        updateViewModelFromProject(project);
        selectToggleForStatus(project.getStatus());
        refreshPersonLabels();
    }

    /**
     * Updates view model fields from project data.
     */
    private void updateViewModelFromProject(ProjectDTO project) {
        vm.setName(project.getName());
        vm.setPlannedStartDate(project.getPlannedStartDate());
        vm.setPlannedEndDate(project.getPlannedEndDate());
        vm.setActualStartDate(project.getActualStartDate());
        vm.setActualEndDate(project.getActualEndDate());
        vm.setStatus(project.getStatus());
        vm.setCreatedAt(project.getCreatedAt());
        vm.setUpdatedAt(project.getUpdatedAt());
        vm.setLeaderUserId(project.getLeaderUserId() == null ? 0L : project.getLeaderUserId());
        vm.setTechLeaderId(project.getTechLeaderId() == null ? 0L : project.getTechLeaderId());
        vm.setSponsorId(project.getSponsorId() == null ? 0L : project.getSponsorId());
    }

    // Status Mapping

    /**
     * Maps status codes to Spanish display names.
     */
    public static String mapStatusToSpanish(String code) {
        if (code == null || code.isBlank()) return "-";
        
        return switch (code.trim().toUpperCase()) {
            case "P" -> "Planificada";
            case "R" -> "En curso";
            case "S" -> "Suspendida";
            case "C" -> "Finalizada";
            default -> code.trim();
        };
    }

    // Status mapping method moved to public static for use by utility class

    // Excel Export Functionality delegated to ProjectExcelExportUtil

    // Activity Management Implementation
    
    /**
     * Sets up the activities table with columns, sorting, and drag-and-drop functionality.
     */
    private void setupActivitiesTable() {
        configureTableColumns();
        configureTableSorting();
        configureTableRowFactory();
    }

    /**
     * Configures table columns and their value factories.
     */
    private void configureTableColumns() {
        if (tbcActivityName.getCellValueFactory() == null) {
            tbcActivityName.setCellValueFactory(new PropertyValueFactory<>("description"));
        }
        
        tbcActivityStatus.setCellValueFactory(cellData -> {
            String statusCode = cellData.getValue() != null ? cellData.getValue().getStatus() : null;
            String statusText = mapStatusToSpanish(statusCode);
            return Bindings.createStringBinding(() -> statusText);
        });
        
        tbcActivityResponsible.setCellValueFactory(cellData -> {
            ProjectActivityViewModel activity = cellData.getValue();
            return Bindings.createStringBinding(() -> {
                if (activity == null) return "-";
                
                long responsibleId = activity.getResponsibleId();
                if (responsibleId <= 0) return "-";
                
                String cachedLabel = getCachedPersonLabel(responsibleId);
                if (cachedLabel != null && !cachedLabel.isBlank()) {
                    return cachedLabel;
                }
                
                // Trigger async fetch for next refresh
                fetchPersonLabelForTable(responsibleId);
                return "-";
            });
        });
    }

    /**
     * Fetches person label for table display asynchronously.
     */
    private void fetchPersonLabelForTable(long responsibleId) {
        new Thread(() -> {
            try {
                PersonService personService = new PersonService();
                Respuesta response = personService.find(responsibleId);
                
                if (Boolean.TRUE.equals(response.getEstado())) {
                    Object personData = response.getResultado("Person");
                    if (personData instanceof PersonDTO person) {
                        String personName = buildPersonLabel(person);
                        if (!personName.isBlank()) {
                            cachePersonLabel(responsibleId, personName);
                            Platform.runLater(() -> tbvActivities.refresh());
                        }
                    }
                }
            } catch (Exception ignored) {
                // Service unavailable, will show "-"
            }
        }, "resp-label-fetch-" + responsibleId).start();
    }

    /**
     * Configures table sorting behavior.
     */
    private void configureTableSorting() {
        tbvActivities.setItems(activities);
        tbvActivities.getSortOrder().clear();
        tbvActivities.setSortPolicy(tableView -> false); // Disable column sorting
        
        // Disable individual column sorting
        tbcActivityName.setSortable(false);
        tbcActivityStatus.setSortable(false);
        tbcActivityResponsible.setSortable(false);
        
        // Sort by execution order
        activities.sort(Comparator.comparingInt(ProjectActivityViewModel::getExecutionOrder));
    }

    /**
     * Configures table row factory for double-click and drag-and-drop.
     */
    private void configureTableRowFactory() {
        tbvActivities.setRowFactory(tableView -> {
            TableRow<ProjectActivityViewModel> row = createStyledTableRow();
            
            // Double click to open detail
            row.setOnMouseClicked(this::handleRowDoubleClick);
            
            // Drag and drop support
            row.setOnDragDetected(event -> handleRowDragDetected(row, event));
            row.setOnDragOver(event -> handleRowDragOver(row, event));
            row.setOnDragDropped(event -> handleRowDragDropped(row, event));
            
            return row;
        });
    }

    /**
     * Creates a styled table row with alternating colors.
     */
    private TableRow<ProjectActivityViewModel> createStyledTableRow() {
        return new TableRow<>() {
            @Override
            protected void updateItem(ProjectActivityViewModel item, boolean empty) {
                super.updateItem(item, empty);
                
                if (empty || item == null) {
                    setStyle("");
                    setCursor(Cursor.DEFAULT);
                } else {
                    int index = getIndex();
                    String backgroundColor = switch (index % 3) {
                        case 0 -> "-fx-surface";
                        case 1 -> "-fx-surface-variant";
                        default -> "#f6f8ff";
                    };
                    setStyle("-fx-background-color: " + backgroundColor + "; -fx-background-radius: 12;");
                    setCursor(Cursor.OPEN_HAND);
                }
            }
        };
    }

    /**
     * Handles double-click events on table rows.
     */
    private void handleRowDoubleClick(MouseEvent event) {
        if (event.getClickCount() == 2) {
            TableRow<?> row = (TableRow<?>) event.getSource();
            if (!row.isEmpty() && row.getItem() instanceof ProjectActivityViewModel activity) {
                showActivityDetail(activity);
            }
        }
    }

    /**
     * Handles drag detection on table rows.
     */
    private void handleRowDragDetected(TableRow<ProjectActivityViewModel> row, MouseEvent event) {
        if (!row.isEmpty()) {
            Integer index = row.getIndex();
            Dragboard dragboard = row.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.put(ACTIVITY_INDEX, index);
            dragboard.setContent(content);
            
            // Create drag view
            WritableImage snapshot = row.snapshot(new SnapshotParameters(), null);
            dragboard.setDragView(snapshot, snapshot.getWidth() / 2, snapshot.getHeight() / 2);
            
            event.consume();
        }
    }

    /**
     * Handles drag over events on table rows.
     */
    private void handleRowDragOver(TableRow<ProjectActivityViewModel> row, DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        if (dragboard.hasContent(ACTIVITY_INDEX)) {
            Integer draggedIndex = (Integer) dragboard.getContent(ACTIVITY_INDEX);
            if (row.getIndex() != draggedIndex.intValue()) {
                event.acceptTransferModes(TransferMode.MOVE);
                event.consume();
            }
        }
    }

    /**
     * Handles drag drop events on table rows.
     */
    private void handleRowDragDropped(TableRow<ProjectActivityViewModel> row, DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        boolean success = false;
        
        if (dragboard.hasContent(ACTIVITY_INDEX)) {
            int draggedIndex = (Integer) dragboard.getContent(ACTIVITY_INDEX);
            ProjectActivityViewModel draggedItem = tbvActivities.getItems().remove(draggedIndex);
            
            int dropIndex = row.isEmpty() ? tbvActivities.getItems().size() : row.getIndex();
            
            tbvActivities.getItems().add(dropIndex, draggedItem);
            renumberExecutionOrder();
            tbvActivities.getSelectionModel().select(dropIndex);
            tbvActivities.refresh();
            
            logActivityOrder();
            success = true;
        }
        
        event.setDropCompleted(success);
        event.consume();
    }

    /**
     * Logs the current activity order for debugging.
     */
    private void logActivityOrder() {
        LOGGER.fine("=== Activities new order (index -> id:order) ===");
        for (int i = 0; i < activities.size(); i++) {
            ProjectActivityViewModel activity = activities.get(i);
            LOGGER.fine(i + " -> " + activity.getId() + ":" + activity.getExecutionOrder());
        }
    }

    /**
     * Renumbers execution order for activities and persists changes.
     */
    private void renumberExecutionOrder() {
        boolean hasChanges = false;
        
        for (int i = 0; i < activities.size(); i++) {
            ProjectActivityViewModel activity = activities.get(i);
            int newOrder = i + 1;
            
            if (activity.getExecutionOrder() != newOrder) {
                activity.setExecutionOrder(newOrder);
                hasChanges = true;
            }
        }
        
        activities.sort(Comparator.comparingInt(ProjectActivityViewModel::getExecutionOrder));
        tbvActivities.refresh();
        
        if (hasChanges) {
            persistActivityOrderChanges();
        }
    }

    /**
     * Persists activity order changes to the web service.
     */
    private void persistActivityOrderChanges() {
        ProjectActivityService service = new ProjectActivityService();
        
        for (ProjectActivityViewModel activity : activities) {
            try {
                Respuesta response = service.update(activity.toDTO());
                if (!Boolean.TRUE.equals(response.getEstado())) {
                    LOGGER.fine("Activity reorder persist failed for ID " + activity.getId() + ": " +
                        (response != null ? response.getMensaje() : "null"));
                }
            } catch (Exception ex) {
                LOGGER.fine("Exception persisting activity reorder for ID " + activity.getId() + ": " +
                    ex.getMessage());
            }
        }
    }

    /**
     * Loads activities for the current project from the web service.
     */
    private void loadActivitiesForProject() {
        long projectId = vm.getId();
        if (projectId <= 0) {
            activities.clear();
            return;
        }
        
        Task<List<ProjectActivityDTO>> loadTask = createActivityLoadTask();
        configureActivityLoadTaskHandlers(loadTask, projectId);
        
        Thread loadThread = new Thread(loadTask, "load-activities");
        loadThread.setDaemon(true);
        loadThread.start();
    }

    /**
     * Creates a task to load activities.
     */
    private Task<List<ProjectActivityDTO>> createActivityLoadTask() {
        return new Task<>() {
            @Override
            protected List<ProjectActivityDTO> call() {
                ProjectActivityService service = new ProjectActivityService();
                Respuesta response = service.findAll();
                
                if (response != null && Boolean.TRUE.equals(response.getEstado())) {
                    Object listData = response.getResultado("ProjectActivities");
                    if (listData instanceof List<?> list) {
                        @SuppressWarnings("unchecked")
                        List<ProjectActivityDTO> dtos = (List<ProjectActivityDTO>) list;
                        return dtos;
                    }
                }
                return List.of();
            }
        };
    }

    /**
     * Configures handlers for the activity load task.
     */
    private void configureActivityLoadTaskHandlers(Task<List<ProjectActivityDTO>> loadTask, long projectId) {
        loadTask.setOnSucceeded(event -> {
            List<ProjectActivityDTO> dtos = loadTask.getValue();
            LOGGER.fine("Activities loaded from service: " + 
                (dtos != null ? dtos.size() : 0) + 
                " total, filtering by project ID " + projectId);
            
            updateActivitiesFromDTOs(dtos, projectId);
            prefetchResponsibleLabels();
        });
        
        loadTask.setOnFailed(event -> {
            LOGGER.warning("Failed to load activities: " + loadTask.getException());
            activities.clear();
        });
    }

    /**
     * Updates activities list from DTOs.
     */
    private void updateActivitiesFromDTOs(List<ProjectActivityDTO> dtos, long projectId) {
        activities.clear();
        dtos.stream()
            .filter(dto -> dto.getProjectId() != null && dto.getProjectId().longValue() == projectId)
            .map(ProjectActivityViewModel::new)
            .sorted(Comparator.comparingInt(ProjectActivityViewModel::getExecutionOrder))
            .forEach(activities::add);
        
        activities.sort(Comparator.comparingInt(ProjectActivityViewModel::getExecutionOrder));
        tbvActivities.refresh();
        
        LOGGER.fine("Activities filtered and loaded: " + activities.size());

        // Auto-assign actual dates based on activity status and persist if needed
        for (ProjectActivityViewModel act : activities) {
            applyAutomaticActualDatesForActivity(act);
        }
    }

    /**
     * Pre-fetches person labels for activities to improve UI responsiveness.
     */
    private void prefetchResponsibleLabels() {
        List<Long> personIds = activities.stream()
            .map(ProjectActivityViewModel::getResponsibleId)
            .filter(id -> id > 0)
            .distinct()
            .toList();
        
        if (personIds.isEmpty()) return;
        
        new Thread(() -> {
            PersonService personService = new PersonService();
            boolean hasUpdates = false;
            
            for (Long personId : personIds) {
                if (getCachedPersonLabel(personId) != null) continue;
                
                try {
                    Respuesta response = personService.find(personId);
                    if (Boolean.TRUE.equals(response.getEstado())) {
                        Object personData = response.getResultado("Person");
                        if (personData instanceof PersonDTO person) {
                            String personName = buildPersonLabel(person);
                            if (!personName.isBlank()) {
                                cachePersonLabel(personId, personName);
                                hasUpdates = true;
                            }
                        }
                    }
                } catch (Exception ignored) {
                    // Service unavailable, continue with others
                }
            }
            
            if (hasUpdates) {
                Platform.runLater(() -> tbvActivities.refresh());
            }
        }, "prefetch-person-labels").start();
    }

    /**
     * Creates a new activity based on the form data.
     */
    private void createNewActivity() {
        ProjectActivityDTO activityDto = buildActivityDto();
        if (activityDto == null) return;

        Long responsibleId = getResponsibleIdForNewActivity();
        ProjectActivityService service = new ProjectActivityService();
        Respuesta response = service.create(activityDto, vm.getId(), responsibleId);

        if (Boolean.TRUE.equals(response.getEstado())) {
            LOGGER.info("Activity created successfully.");
            handleSuccessfulActivityCreation(response);
            clearActivityCreationForm();
            AnimationManager.hidePopup(vbDisplayActivityCreation, vbCover);
            loadActivitiesForProject(); // Defensive reload to stay synchronized
        } else {
            LOGGER.warning("Activity creation failed: " + response.getMensaje() + 
                " | " + response.getMensajeInterno());
        }
    }

    /**
     * Builds an activity DTO from the form data.
     */
    private ProjectActivityDTO buildActivityDto() {
        long projectId = vm.getId();
        if (projectId <= 0) {
            LOGGER.warning("Project ID missing; aborting activity creation.");
            return null;
        }

        ProjectActivityDTO dto = new ProjectActivityDTO();
        dto.setProjectId(projectId);
        dto.setDescription(getTextOrNull(txaDescriptionCreation.getText()));
        dto.setStatus(activityCreationStatus); // Use selected status from toggles
        dto.setPlannedStartDate(fromPicker(dpPlannedStartDateCreation));
        dto.setPlannedEndDate(fromPicker(dpPlannedEndDateCreation));
        dto.setExecutionOrder(activities.size() + 1);

        return dto;
    }

    /**
     * Gets the responsible person ID for new activity creation.
     */
    private Long getResponsibleIdForNewActivity() {
        Object responsibleSelection = AppContext.getInstance().get("ProjectExpand.activityResponsible");
        if (responsibleSelection instanceof PersonDTO person) {
            return person.getId();
        }

        // Fallback to current user
        Object currentUser = AppContext.getInstance().get("user");
        if (currentUser instanceof PersonDTO user) {
            return user.getId();
        }

        return null;
    }

    /**
     * Handles successful activity creation response.
     */
    private void handleSuccessfulActivityCreation(Respuesta response) {
        Object createdData = response.getResultado("ProjectActivity");
        if (createdData instanceof ProjectActivityDTO createdDto) {
            if (createdDto.getProjectId() == null) {
                createdDto.setProjectId(vm.getId());
            }
            
            ProjectActivityViewModel activityVm = new ProjectActivityViewModel(createdDto);
            if (activityVm.getExecutionOrder() == 0) {
                activityVm.setExecutionOrder(activities.size() + 1);
            }
            
            activities.add(activityVm);
            renumberExecutionOrder();
            Platform.runLater(() -> tbvActivities.refresh());
        }
    }

    /**
     * Updates the currently selected activity with form data.
     */
    private void updateSelectedActivity() {
        if (selectedActivity == null) return;

        updateActivityFromForm();
        logActivityUpdate();
        persistActivityChanges(selectedActivity);

        // After confirming, consider changes applied
        activityHasChanges.unbind();
        activityHasChanges.set(false);
    }

    /**
     * Updates activity from form data.
     */
    private void updateActivityFromForm() {
        selectedActivity.setDescription(txaDescription.getText());
        selectedActivity.setCreatedAt(fromPicker(dpCreationDate));
        selectedActivity.setUpdatedAt(fromPicker(dpLastUpdate));
        selectedActivity.setPlannedStartDate(fromPicker(dpPlannedStartDate));
        selectedActivity.setActualStartDate(fromPicker(dpActualStartDate));
        selectedActivity.setPlannedEndDate(fromPicker(dpPlannedEndDate));
        selectedActivity.setActualEndDate(fromPicker(dpActualEndDate));
    }

    /**
     * Logs activity update for debugging.
     */
    private void logActivityUpdate() {
        LOGGER.fine("Updating activity: ID=" + selectedActivity.getId() + 
            ", Order=" + selectedActivity.getExecutionOrder() + 
            ", Description=" + selectedActivity.getDescription());
    }

    /**
     * Persists activity changes to the web service.
     */
    private void persistActivityChanges(ProjectActivityViewModel activity) {
        try {
            ProjectActivityService service = new ProjectActivityService();
            Respuesta response = service.update(activity.toDTO());
            
            if (!Boolean.TRUE.equals(response.getEstado())) {
                LOGGER.warning("Activity update failed: " + 
                    (response != null ? response.getMensaje() : "null") + 
                    " | " + (response != null ? response.getMensajeInterno() : "null"));
            }
        } catch (Exception ex) {
            LOGGER.warning("Exception while persisting activity changes: " + ex.getMessage());
        }
    }

    /**
     * Clears the activity creation form.
     */
    private void clearActivityCreationForm() {
        txfResponsableCreation.clear();
        txaDescriptionCreation.clear();
        dpPlannedStartDateCreation.setValue(null);
        dpPlannedEndDateCreation.setValue(null);
        
        // Reset status to default (Pending)
        activityCreationStatus = "P";
        if (ActivityCreateStatusGroup != null && tgActivityCreateStatusPending != null) {
            ActivityCreateStatusGroup.selectToggle(tgActivityCreateStatusPending);
        }
        
        AppContext.getInstance().delete("ProjectExpand.activityResponsible");
    }

    /**
     * Shows detailed view for the selected activity.
     */
    private void showActivityDetail(ProjectActivityViewModel activity) {
        this.selectedActivity = activity;
        if (activity == null) return;
        
        populateActivityDetailForm(activity);
        bindActivityDetailStatusToggles(activity);
        AnimationManager.showPopup(vbDisplayActivityExpand, vbCover);
    }

    /**
     * Populates the activity detail form.
     */
    private void populateActivityDetailForm(ProjectActivityViewModel activity) {
        // Resolve and display responsible person
        long responsibleId = activity.getResponsibleId();
        String responsibleName = responsibleId > 0 ? resolvePersonNameSync(responsibleId) : "-";
        txfResponsible.setText(responsibleName);
        
        // Resolve and display creator
        long createdById = activity.getCreatedById();
        String createdByName = createdById > 0 ? resolvePersonNameSync(createdById) : "-";
        txfCreatedBy.setText(createdByName);
        
        // Set form fields
        txaDescription.setText(activity.getDescription());
        
        // Set date pickers
        setPickerFromDate(dpCreationDate, activity.getCreatedAt());
        setPickerFromDate(dpLastUpdate, activity.getUpdatedAt());
        setPickerFromDate(dpPlannedStartDate, activity.getPlannedStartDate());
        setPickerFromDate(dpActualStartDate, activity.getActualStartDate());
        setPickerFromDate(dpPlannedEndDate, activity.getPlannedEndDate());
        setPickerFromDate(dpActualEndDate, activity.getActualEndDate());

        // Take a snapshot of current values for change detection
        activitySnapshotDescription = txaDescription.getText();
        snapshotCreationDate = dpCreationDate.getValue();
        snapshotLastUpdateDate = dpLastUpdate.getValue();
        snapshotPlannedStartDate = dpPlannedStartDate.getValue();
        snapshotPlannedEndDate = dpPlannedEndDate.getValue();
        snapshotActualStartDate = dpActualStartDate.getValue();
        snapshotActualEndDate = dpActualEndDate.getValue();

        // Setup binding to enable confirm/cancel only when there are changes
        setupActivityChangeBinding();
    }

    /**
     * Binds the activityHasChanges flag to UI fields vs. snapshot values.
     */
    private void setupActivityChangeBinding() {
        activityHasChanges.unbind();
        javafx.beans.binding.BooleanBinding changes = javafx.beans.binding.Bindings.createBooleanBinding(
            () -> {
                if (!safeEquals(txaDescription.getText(), activitySnapshotDescription)) return true;
                if (!safeEqualsLocalDate(dpCreationDate.getValue(), snapshotCreationDate)) return true;
                if (!safeEqualsLocalDate(dpLastUpdate.getValue(), snapshotLastUpdateDate)) return true;
                if (!safeEqualsLocalDate(dpPlannedStartDate.getValue(), snapshotPlannedStartDate)) return true;
                if (!safeEqualsLocalDate(dpPlannedEndDate.getValue(), snapshotPlannedEndDate)) return true;
                if (!safeEqualsLocalDate(dpActualStartDate.getValue(), snapshotActualStartDate)) return true;
                if (!safeEqualsLocalDate(dpActualEndDate.getValue(), snapshotActualEndDate)) return true;
                return false;
            },
            txaDescription.textProperty(),
            dpCreationDate.valueProperty(),
            dpLastUpdate.valueProperty(),
            dpPlannedStartDate.valueProperty(),
            dpPlannedEndDate.valueProperty(),
            dpActualStartDate.valueProperty(),
            dpActualEndDate.valueProperty()
        );
        activityHasChanges.bind(changes);
    }

    private boolean safeEquals(String a, String b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }

    private boolean safeEqualsLocalDate(LocalDate a, LocalDate b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }

    /**
     * Applies automatic actual date assignment to an activity based on its status.
     * If changes are applied, persists the activity and enqueues a notification hook.
     */
    private void applyAutomaticActualDatesForActivity(ProjectActivityViewModel activity) {
        if (activity == null) return;
        String status = activity.getStatus();
        if (status == null || status.isBlank()) return;
        String code = status.trim().toUpperCase();

        boolean modified = false;
        Date now = new Date();
        if ("R".equals(code)) {
            if (activity.getActualStartDate() == null) {
                activity.setActualStartDate(now);
                modified = true;
            }
        } else if ("C".equals(code)) {
            if (activity.getActualStartDate() == null) {
                activity.setActualStartDate(now);
                modified = true;
            }
            if (activity.getActualEndDate() == null) {
                activity.setActualEndDate(now);
                modified = true;
            }
        }

        if (modified) {
            persistActivityChanges(activity);
            enqueueStatusChangeNotification("ACTIVITY", activity.getId(), code);
        }
    }

    /**
     * Placeholder for sending status change notifications to the backend notification system.
     * Integrate with NOTIFICATION and NOTIFICATION_RECIPIENT tables via a NotificationService when available.
     */
    private void enqueueStatusChangeNotification(String entityType, long entityId, String newStatus) {
        try {
            LOGGER.fine("[Notification] Entity=" + entityType + ", ID=" + entityId + ", status=" + newStatus);
            // TODO: Integrate with NotificationService once available, e.g.:
            // NotificationService svc = new NotificationService();
            // svc.createStatusChange(entityType, entityId, newStatus);
        } catch (Exception ignored) {
            // Best-effort: do not block main flow
        }
    }
    
    // Utility methods
    private String getTextOrNull(String text) {
        return (text == null || text.trim().isEmpty()) ? null : text.trim();
    }
    
    private Date fromPicker(MFXDatePicker picker) {
        return picker == null ? null : convertLocalDateToDate(picker.getValue());
    }
    
    private void setPickerFromDate(MFXDatePicker picker, Date date) {
        if (picker != null) {
            LocalDate localDate = convertDateToLocalDate(date);
            if (Platform.isFxApplicationThread()) {
                picker.setValue(localDate);
            } else {
                Platform.runLater(() -> picker.setValue(localDate));
            }
        }
    }
}