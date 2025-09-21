package cr.ac.una.flowfx.controller;

import cr.ac.una.flowfx.model.PersonDTO;
import cr.ac.una.flowfx.model.PersonViewModel;
import cr.ac.una.flowfx.model.ProjectDTO;
import cr.ac.una.flowfx.service.PersonService;
import cr.ac.una.flowfx.util.AppContext;
import cr.ac.una.flowfx.util.FlowController;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controller for the Person expand view.
 *
 * <p>This controller binds a {@link PersonViewModel} to the UI, providing
 * two-way conversions for numeric id fields and boolean/char mappings for
 * administrative/active flags.</p>
 *
 * <p>Minor improvements: consistent logging and defensive checks for the UI
 * bindings. No runtime behavior is changed.</p>
 */
public class PersonExpandController
    extends Controller
    implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(
        PersonExpandController.class.getName()
    );

    @FXML
    private AnchorPane root;

    @FXML
    private VBox vbCover;

    @FXML
    private MFXButton btnReturnPersonSignUp;

    @FXML
    private MFXTextField txfPersonName;

    @FXML
    private MFXTextField txfPersonLastName;

    @FXML
    private MFXTextField txfPersonMail;

    @FXML
    private MFXTextField txfPersonId;

    @FXML
    private MFXTextField txfPersonUsername;

    @FXML
    private MFXPasswordField psfPersonPassword;

    @FXML
    private MFXCheckbox cbIsAdmin;

    @FXML
    private MFXCheckbox cbIsActive;

    private final PersonViewModel vm = new PersonViewModel();
    private boolean syncingIdText = false;
    
    // Change detection for person editing
    private final javafx.beans.property.BooleanProperty personHasChanges = new javafx.beans.property.SimpleBooleanProperty(false);
    private String personSnapshotFirstName;
    private String personSnapshotLastName;
    private String personSnapshotEmail;
    private String personSnapshotUsername;
    private String personSnapshotPassword;
    private boolean personSnapshotIsAdmin;
    private boolean personSnapshotIsActive;
    @FXML
    private MFXButton btnCancelChanges;
    @FXML
    private MFXButton btnConfirmChanges;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("FXML initialize(URL, ResourceBundle) invoked for PersonExpandController");
        // No-op for FXMLLoader-based initialization; kept for compatibility.
        LOGGER.fine(
            "FXML initialize(URL, ResourceBundle) invoked for PersonExpandController"
        );
    }

    @Override
    public void initialize() {
        Object p = AppContext.getInstance().get("selectedPerson");
        if (p instanceof PersonDTO) {
            PersonDTO dto = (PersonDTO) p;
            PersonViewModel initial = new PersonViewModel(dto);
            vm.setId(initial.getId());
            vm.setFirstName(initial.getFirstName() != null ? initial.getFirstName() : "");
            vm.setLastName(initial.getLastName() != null ? initial.getLastName() : "");
            vm.setEmail(initial.getEmail() != null ? initial.getEmail() : "");
            vm.setUsername(initial.getUsername() != null ? initial.getUsername() : "");
            vm.setPassword(initial.getPassword() != null ? initial.getPassword() : "");
            vm.setStatus(initial.getStatus() != null ? initial.getStatus() : 'I');
            vm.setIsAdmin(initial.getIsAdmin() != null ? initial.getIsAdmin() : 'N');
        } else {
            vm.setFirstName("");
            vm.setLastName("");
            vm.setEmail("");
            vm.setUsername("");
            vm.setPassword("");
            vm.setStatus('I');
            vm.setIsAdmin('N');
        }
        
        // Check if this is view-only mode
        boolean viewOnly = Boolean.TRUE.equals(AppContext.getInstance().get("personExpand.viewOnly"));
        LOGGER.info("PersonExpandController initialize: viewOnly=" + viewOnly + ", context value=" + 
            AppContext.getInstance().get("personExpand.viewOnly"));
        configureViewMode(viewOnly);
        
        bindFields();
        if (!viewOnly) {
            setupChangeDetection();
        }
    }

    /**
     * Configures the form for view-only or edit mode.
     */
    private void configureViewMode(boolean viewOnly) {
        if (viewOnly) {
            // Disable all form fields for view-only mode
            txfPersonName.setEditable(false);
            txfPersonLastName.setEditable(false);
            txfPersonMail.setEditable(false);
            txfPersonId.setEditable(false);
            txfPersonUsername.setEditable(false);
            psfPersonPassword.setEditable(false);
            cbIsAdmin.setDisable(true);
            cbIsActive.setDisable(true);
            
            // Hide save/cancel buttons in view-only mode
            if (btnConfirmChanges != null) {
                btnConfirmChanges.setVisible(false);
                btnConfirmChanges.setManaged(false);
            }
            if (btnCancelChanges != null) {
                btnCancelChanges.setVisible(false);
                btnCancelChanges.setManaged(false);
            }
            
            LOGGER.fine("PersonExpandController configured for view-only mode");
        } else {
            // Ensure fields are editable in edit mode
            txfPersonName.setEditable(true);
            txfPersonLastName.setEditable(true);
            txfPersonMail.setEditable(true);
            txfPersonId.setEditable(true);
            txfPersonUsername.setEditable(true);
            psfPersonPassword.setEditable(true);
            cbIsAdmin.setDisable(false);
            cbIsActive.setDisable(false);
            
            // Show save/cancel buttons in edit mode
            if (btnConfirmChanges != null) {
                btnConfirmChanges.setVisible(true);
                btnConfirmChanges.setManaged(true);
                LOGGER.info("btnConfirmChanges set to visible and managed");
            } else {
                LOGGER.warning("btnConfirmChanges is null in edit mode");
            }
            if (btnCancelChanges != null) {
                btnCancelChanges.setVisible(true);
                btnCancelChanges.setManaged(true);
                LOGGER.info("btnCancelChanges set to visible and managed");
            } else {
                LOGGER.warning("btnCancelChanges is null in edit mode");
            }
            
            LOGGER.fine("PersonExpandController configured for edit mode");
        }
    }

    private void bindFields() {
        // Simple text bindings
        txfPersonName.textProperty().bindBidirectional(vm.firstNameProperty());
        txfPersonLastName
            .textProperty()
            .bindBidirectional(vm.lastNameProperty());
        txfPersonMail.textProperty().bindBidirectional(vm.emailProperty());
        txfPersonUsername
            .textProperty()
            .bindBidirectional(vm.usernameProperty());
        psfPersonPassword
            .textProperty()
            .bindBidirectional(vm.passwordProperty());

        // ID numeric sync (manual two-way to convert long<->String)
        txfPersonId
            .textProperty()
            .addListener((obs, o, n) -> {
                if (syncingIdText) return;
                try {
                    long v = (n == null || n.trim().isEmpty())
                        ? 0L
                        : Long.parseLong(n.trim());
                    if (v != vm.getId()) vm.setId(v);
                } catch (NumberFormatException ignored) {}
            });
        vm
            .idProperty()
            .addListener((obs, o, n) -> {
                String nv = (n == null || n.longValue() == 0L)
                    ? ""
                    : String.valueOf(n.longValue());
                String cur = txfPersonId.getText();
                if (!nv.equals(cur)) {
                    syncingIdText = true;
                    txfPersonId.setText(nv);
                    syncingIdText = false;
                }
            });
        // Initialize ID text
        String initId = vm.getId() == 0L ? "" : String.valueOf(vm.getId());
        txfPersonId.setText(initId);

        // Checkboxes <-> char properties
        // Active status: 'A' selected, 'I' not selected
        cbIsActive.setSelected(
            vm.getStatus() != null &&
                Character.toUpperCase(vm.getStatus()) == 'A'
        );
        cbIsActive
            .selectedProperty()
            .addListener((obs, o, sel) -> vm.setStatus(sel ? 'A' : 'I'));
        vm
            .statusProperty()
            .addListener((obs, o, n) -> {
                boolean sel = n != null && Character.toUpperCase(n) == 'A';
                if (cbIsActive.isSelected() != sel) cbIsActive.setSelected(sel);
            });

        // Admin flag: 'Y' selected, 'N' not selected
        cbIsAdmin.setSelected(
            vm.getIsAdmin() != null &&
                Character.toUpperCase(vm.getIsAdmin()) == 'Y'
        );
        cbIsAdmin
            .selectedProperty()
            .addListener((obs, o, sel) -> vm.setIsAdmin(sel ? 'Y' : 'N'));
        vm
            .isAdminProperty()
            .addListener((obs, o, n) -> {
                boolean sel = n != null && Character.toUpperCase(n) == 'Y';
                if (cbIsAdmin.isSelected() != sel) cbIsAdmin.setSelected(sel);
            });
    }

    /**
     * Sets up change detection to enable/disable save and cancel buttons.
     */
    private void setupChangeDetection() {
        // Take snapshot of current values
        takePersonSnapshot();
        
        // Bind save/cancel buttons to change detection
        if (btnConfirmChanges != null) {
            btnConfirmChanges.disableProperty().bind(personHasChanges.not());
        }
        if (btnCancelChanges != null) {
            btnCancelChanges.disableProperty().bind(personHasChanges.not());
        }
        
        // Set up change binding
        setupPersonChangeBinding();
    }

    /**
     * Takes a snapshot of current person data for change detection.
     */
    private void takePersonSnapshot() {
        personSnapshotFirstName = vm.getFirstName() != null ? vm.getFirstName() : "";
        personSnapshotLastName = vm.getLastName() != null ? vm.getLastName() : "";
        personSnapshotEmail = vm.getEmail() != null ? vm.getEmail() : "";
        personSnapshotUsername = vm.getUsername() != null ? vm.getUsername() : "";
        personSnapshotPassword = vm.getPassword() != null ? vm.getPassword() : "";
        personSnapshotIsAdmin = vm.getIsAdmin() != null && Character.toUpperCase(vm.getIsAdmin()) == 'Y';
        personSnapshotIsActive = vm.getStatus() != null && Character.toUpperCase(vm.getStatus()) == 'A';
        
        LOGGER.fine("Person snapshot taken for change detection");
    }

    /**
     * Sets up binding to detect changes in person fields.
     */
    private void setupPersonChangeBinding() {
        personHasChanges.unbind();
        javafx.beans.binding.BooleanBinding changes = javafx.beans.binding.Bindings.createBooleanBinding(
            () -> {
                String currentFirstName = vm.getFirstName() != null ? vm.getFirstName() : "";
                String currentLastName = vm.getLastName() != null ? vm.getLastName() : "";
                String currentEmail = vm.getEmail() != null ? vm.getEmail() : "";
                String currentUsername = vm.getUsername() != null ? vm.getUsername() : "";
                String currentPassword = vm.getPassword() != null ? vm.getPassword() : "";
                
                if (!safeEquals(currentFirstName, personSnapshotFirstName)) return true;
                if (!safeEquals(currentLastName, personSnapshotLastName)) return true;
                if (!safeEquals(currentEmail, personSnapshotEmail)) return true;
                if (!safeEquals(currentUsername, personSnapshotUsername)) return true;
                if (!safeEquals(currentPassword, personSnapshotPassword)) return true;
                
                boolean currentIsAdmin = vm.getIsAdmin() != null && Character.toUpperCase(vm.getIsAdmin()) == 'Y';
                if (currentIsAdmin != personSnapshotIsAdmin) return true;
                
                boolean currentIsActive = vm.getStatus() != null && Character.toUpperCase(vm.getStatus()) == 'A';
                if (currentIsActive != personSnapshotIsActive) return true;
                
                return false;
            },
            vm.firstNameProperty(),
            vm.lastNameProperty(),
            vm.emailProperty(),
            vm.usernameProperty(),
            vm.passwordProperty(),
            vm.isAdminProperty(),
            vm.statusProperty()
        );
        personHasChanges.bind(changes);
    }

    /**
     * Safe string comparison utility.
     */
    private boolean safeEquals(String a, String b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }

    @FXML
    private void onActionReturnPersonSignUp(ActionEvent event) {
        // Check if we should return to a specific view
        String returnView = (String) AppContext.getInstance().get("personExpand.returnView");
        Object returnProject = AppContext.getInstance().get("personExpand.returnProject");
        
        LOGGER.info("Return navigation: returnView=" + returnView + ", returnProject=" + 
            (returnProject != null ? "present" : "null"));
        
        if ("ProjectExpandView".equals(returnView) && returnProject instanceof ProjectDTO) {
            // Return to ProjectExpandView
            AppContext.getInstance().set("currentProject", returnProject);
            FlowController.getInstance().goView("ProjectExpandView");
            LOGGER.info("Returning to ProjectExpandView");
        } else {
            // Default return to PersonSignUpView
            FlowController.getInstance().goView("PersonSignUpView");
            Object nav = AppContext.getInstance().get("navigationBar");
            if (nav instanceof VBox) ((VBox) nav).setDisable(false);
            LOGGER.info("Returning to PersonSignUpView (default)");
        }
        
        // Clean up context after navigation
        AppContext.getInstance().delete("personExpand.returnProject");
        AppContext.getInstance().delete("personExpand.returnView");
        AppContext.getInstance().delete("personExpand.viewOnly");
        AppContext.getInstance().delete("personExpand.roleLabel");
    }

    @FXML
    private void onActionCancelChanges(ActionEvent event) {
        LOGGER.fine("Canceling person changes");
        revertPersonChanges();
    }

    @FXML
    private void onActionConfirmUpdates(ActionEvent event) {
        LOGGER.fine("Confirming person updates");
        savePersonChanges();
    }

    /**
     * Reverts all person fields to their snapshot values.
     */
    private void revertPersonChanges() {
        vm.setFirstName(personSnapshotFirstName != null ? personSnapshotFirstName : "");
        vm.setLastName(personSnapshotLastName != null ? personSnapshotLastName : "");
        vm.setEmail(personSnapshotEmail != null ? personSnapshotEmail : "");
        vm.setUsername(personSnapshotUsername != null ? personSnapshotUsername : "");
        vm.setPassword(personSnapshotPassword != null ? personSnapshotPassword : "");
        vm.setIsAdmin(personSnapshotIsAdmin ? 'Y' : 'N');
        vm.setStatus(personSnapshotIsActive ? 'A' : 'I');
        
        // Reset change detection
        personHasChanges.unbind();
        personHasChanges.set(false);
        setupPersonChangeBinding();
        
        LOGGER.fine("Person changes reverted to snapshot");
    }

    /**
     * Saves person changes to the backend service.
     */
    private void savePersonChanges() {
        try {
            PersonService service = new PersonService();
            PersonDTO dto = vm.toDTO();
            
            LOGGER.fine("Saving person changes for ID: " + dto.getId());
            cr.ac.una.flowfx.util.Respuesta response = service.update(dto);
            
            if (Boolean.TRUE.equals(response.getEstado())) {
                LOGGER.info("Person updated successfully");
                
                // Update the snapshot to reflect saved state
                takePersonSnapshot();
                
                // Update context if this is the current user
                Object currentUser = AppContext.getInstance().get("user");
                if (currentUser instanceof cr.ac.una.flowfx.model.PersonDTO user && 
                    user.getId() != null && user.getId().equals(dto.getId())) {
                    AppContext.getInstance().set("user", dto);
                }
                
                // Reset change detection
                personHasChanges.unbind();
                personHasChanges.set(false);
                setupPersonChangeBinding();
                
            } else {
                LOGGER.warning("Person update failed: " + 
                    (response != null ? response.getMensaje() : "null response"));
                // Could show error dialog here
            }
        } catch (Exception ex) {
            LOGGER.warning("Exception during person save: " + ex.getMessage());
        }
    }
}
