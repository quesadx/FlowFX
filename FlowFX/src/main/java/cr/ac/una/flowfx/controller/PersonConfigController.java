package cr.ac.una.flowfx.controller;

import cr.ac.una.flowfx.model.PersonDTO;
import cr.ac.una.flowfx.model.PersonViewModel;
import cr.ac.una.flowfx.service.PersonService;
import cr.ac.una.flowfx.util.AppContext;
import cr.ac.una.flowfx.util.Mensaje;
import cr.ac.una.flowfx.util.Respuesta;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controller for Person configuration/editing.
 * 
 * <p>Displays current user information from session and allows editing
 * with proper change detection and cancel/commit operations.</p>
 */
public class PersonConfigController extends Controller implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(
        PersonConfigController.class.getName()
    );

    @FXML
    private AnchorPane root;
    @FXML
    private VBox vbCover;
    @FXML
    private VBox vbSignUpDisplay;
    @FXML
    private MFXTextField txfPersonId;
    @FXML
    private MFXTextField txfPersonFirstName;
    @FXML
    private MFXTextField txfPersonLastName;
    @FXML
    private MFXTextField txfPersonEmail;
    @FXML
    private MFXTextField txfPersonUsername;
    @FXML
    private MFXPasswordField pswPersonPassword;
    @FXML
    private MFXCheckbox cbIsAdmin;
    @FXML
    private MFXCheckbox cbIsActive;
    @FXML
    private MFXButton btnCancelChanges;
    @FXML
    private MFXButton btnCommitChanges;

    private PersonViewModel viewModel;
    private PersonDTO originalUser;
    private final BooleanProperty hasChanges = new SimpleBooleanProperty(false);
    private boolean syncingFields = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadCurrentUser();
        setupViewModel();
        bindFields();
        setupChangeDetection();
        setupButtons();
    }

    @Override
    public void initialize() {
        // Called by FlowController - delegate to FXML initialize
    }

    /**
     * Loads current user from AppContext and web service.
     */
    private void loadCurrentUser() {
        try {
            Object userObj = AppContext.getInstance().get("user");
            if (userObj instanceof PersonDTO) {
                PersonDTO currentUser = (PersonDTO) userObj;
                
                // Reload fresh data from web service to ensure accuracy
                PersonService personService = new PersonService();
                Respuesta response = personService.find(currentUser.getId());
                
                if (Boolean.TRUE.equals(response.getEstado())) {
                    Object refreshedUser = response.getResultado("Person");
                    if (refreshedUser instanceof PersonDTO) {
                        originalUser = (PersonDTO) refreshedUser;
                    } else {
                        originalUser = currentUser; // Fallback
                    }
                } else {
                    originalUser = currentUser; // Fallback
                    LOGGER.log(Level.WARNING, 
                        "Could not refresh user data: " + response.getMensaje());
                }
            } else {
                LOGGER.log(Level.WARNING, "No user found in AppContext");
                new Mensaje().showModal(
                    Alert.AlertType.ERROR,
                    "Configuration",
                    root.getScene().getWindow(),
                    "No user session found. Please log in again."
                );
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error loading current user", ex);
            new Mensaje().showModal(
                Alert.AlertType.ERROR,
                "Configuration",
                root.getScene().getWindow(),
                "Error loading user information."
            );
        }
    }

    /**
     * Initializes the view model with current user data.
     */
    private void setupViewModel() {
        if (originalUser != null) {
            viewModel = new PersonViewModel(originalUser);
        } else {
            viewModel = new PersonViewModel();
        }
    }

    /**
     * Binds UI controls to view model properties.
     */
    private void bindFields() {
        if (viewModel == null) return;

        syncingFields = true;
        
        // Bind text fields bidirectionally
        txfPersonFirstName.textProperty().bindBidirectional(viewModel.firstNameProperty());
        txfPersonLastName.textProperty().bindBidirectional(viewModel.lastNameProperty());
        txfPersonEmail.textProperty().bindBidirectional(viewModel.emailProperty());
        txfPersonUsername.textProperty().bindBidirectional(viewModel.usernameProperty());
        pswPersonPassword.textProperty().bindBidirectional(viewModel.passwordProperty());

        // Handle ID field with custom conversion
        bindIdField();
        
        // Handle checkbox bindings
        bindCheckboxes();

        syncingFields = false;
    }

    /**
     * Binds the ID field with proper Long to String conversion.
     */
    private void bindIdField() {
        // Display ID as string
        String idText = (viewModel.getId() == 0L) ? "" : String.valueOf(viewModel.getId());
        txfPersonId.setText(idText);
        
        // Update view model when text changes
        txfPersonId.textProperty().addListener((obs, oldVal, newVal) -> {
            if (syncingFields) return;
            try {
                long id = (newVal == null || newVal.trim().isEmpty()) 
                    ? 0L 
                    : Long.parseLong(newVal.trim());
                if (id != viewModel.getId()) {
                    viewModel.setId(id);
                }
            } catch (NumberFormatException ignored) {
                // Invalid number - ignore
            }
        });
        
        // Update text when view model changes
        viewModel.idProperty().addListener((obs, oldVal, newVal) -> {
            String newText = (newVal == null || newVal.longValue() == 0L) 
                ? "" 
                : String.valueOf(newVal.longValue());
            String currentText = txfPersonId.getText();
            if (!newText.equals(currentText)) {
                syncingFields = true;
                txfPersonId.setText(newText);
                syncingFields = false;
            }
        });
    }

    /**
     * Binds checkboxes to character properties.
     */
    private void bindCheckboxes() {
        // Admin checkbox: 'Y' = selected, 'N' or null = not selected
        boolean isAdminSelected = viewModel.getIsAdmin() != null && 
            Character.toUpperCase(viewModel.getIsAdmin()) == 'Y';
        cbIsAdmin.setSelected(isAdminSelected);
        
        cbIsAdmin.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (!syncingFields) {
                viewModel.setIsAdmin(newVal ? 'Y' : 'N');
            }
        });
        
        viewModel.isAdminProperty().addListener((obs, oldVal, newVal) -> {
            boolean selected = newVal != null && Character.toUpperCase(newVal) == 'Y';
            if (cbIsAdmin.isSelected() != selected) {
                syncingFields = true;
                cbIsAdmin.setSelected(selected);
                syncingFields = false;
            }
        });

        // Active checkbox: 'A' = selected, 'I' or null = not selected
        boolean isActiveSelected = viewModel.getStatus() != null && 
            Character.toUpperCase(viewModel.getStatus()) == 'A';
        cbIsActive.setSelected(isActiveSelected);
        
        cbIsActive.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (!syncingFields) {
                viewModel.setStatus(newVal ? 'A' : 'I');
            }
        });
        
        viewModel.statusProperty().addListener((obs, oldVal, newVal) -> {
            boolean selected = newVal != null && Character.toUpperCase(newVal) == 'A';
            if (cbIsActive.isSelected() != selected) {
                syncingFields = true;
                cbIsActive.setSelected(selected);
                syncingFields = false;
            }
        });
    }

    /**
     * Sets up change detection for all fields.
     */
    private void setupChangeDetection() {
        if (originalUser == null || viewModel == null) return;

        // Create bindings to detect changes
        BooleanBinding idChanged = Bindings.createBooleanBinding(
            () -> {
                Long originalId = originalUser.getId();
                long currentId = viewModel.getId();
                return !((originalId == null && currentId == 0L) || 
                        (originalId != null && originalId.equals(currentId)));
            },
            viewModel.idProperty()
        );

        BooleanBinding firstNameChanged = Bindings.createBooleanBinding(
            () -> !isEqual(originalUser.getFirstName(), viewModel.getFirstName()),
            viewModel.firstNameProperty()
        );

        BooleanBinding lastNameChanged = Bindings.createBooleanBinding(
            () -> !isEqual(originalUser.getLastName(), viewModel.getLastName()),
            viewModel.lastNameProperty()
        );

        BooleanBinding emailChanged = Bindings.createBooleanBinding(
            () -> !isEqual(originalUser.getEmail(), viewModel.getEmail()),
            viewModel.emailProperty()
        );

        BooleanBinding usernameChanged = Bindings.createBooleanBinding(
            () -> !isEqual(originalUser.getUsername(), viewModel.getUsername()),
            viewModel.usernameProperty()
        );

        BooleanBinding passwordChanged = Bindings.createBooleanBinding(
            () -> !isEqual(originalUser.getPassword(), viewModel.getPassword()),
            viewModel.passwordProperty()
        );

        BooleanBinding statusChanged = Bindings.createBooleanBinding(
            () -> !isEqual(originalUser.getStatus(), viewModel.getStatus()),
            viewModel.statusProperty()
        );

        BooleanBinding adminChanged = Bindings.createBooleanBinding(
            () -> !isEqual(originalUser.getIsAdmin(), viewModel.getIsAdmin()),
            viewModel.isAdminProperty()
        );

        // Combine all change bindings
        hasChanges.bind(
            idChanged
                .or(firstNameChanged)
                .or(lastNameChanged)
                .or(emailChanged)
                .or(usernameChanged)
                .or(passwordChanged)
                .or(statusChanged)
                .or(adminChanged)
        );
    }

    /**
     * Sets up button enable/disable state based on changes.
     */
    private void setupButtons() {
        // Initially disable buttons
        btnCancelChanges.setDisable(true);
        btnCommitChanges.setDisable(true);
        
        // Enable buttons when changes are detected
        hasChanges.addListener((obs, oldVal, newVal) -> {
            btnCancelChanges.setDisable(!newVal);
            btnCommitChanges.setDisable(!newVal);
        });
    }

    /**
     * Utility method to compare two objects for equality (handles nulls).
     */
    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }

    /**
     * Gets trimmed text from a text field, returning empty string if null.
     */
    private String getTrimmedText(MFXTextField field) {
        String text = field.getText();
        return text == null ? "" : text.trim();
    }

    @FXML
    private void onKeyPressedTxfPersonId(KeyEvent event) {
        // Handle enter key to move focus
        if (event.getCode().toString().equals("ENTER")) {
            sendTabEvent(event);
        }
    }

    @FXML
    private void onActionBtnCancelChanges(ActionEvent event) {
        try {
            if (originalUser == null) {
                LOGGER.log(Level.WARNING, "Cannot cancel: no original user data");
                return;
            }

            // Show confirmation dialog
            boolean confirmed = new Mensaje().showConfirmation(
                "Cancel Changes",
                root.getScene().getWindow(),
                "Are you sure you want to cancel all changes? This will reload the original data."
            );

            if (confirmed) {
                // Reload fresh data from web service
                PersonService personService = new PersonService();
                Respuesta response = personService.find(originalUser.getId());
                
                if (Boolean.TRUE.equals(response.getEstado())) {
                    Object refreshedUser = response.getResultado("Person");
                    if (refreshedUser instanceof PersonDTO) {
                        originalUser = (PersonDTO) refreshedUser;
                        
                        // Reset view model to original data
                        syncingFields = true;
                        viewModel = new PersonViewModel(originalUser);
                        
                        // Rebind fields
                        unbindFields();
                        bindFields();
                        setupChangeDetection();
                        syncingFields = false;
                        
                        LOGGER.log(Level.INFO, "User data reset to original values");
                    }
                } else {
                    new Mensaje().showModal(
                        Alert.AlertType.ERROR,
                        "Cancel Changes",
                        root.getScene().getWindow(),
                        "Error reloading user data: " + response.getMensaje()
                    );
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error canceling changes", ex);
            new Mensaje().showModal(
                Alert.AlertType.ERROR,
                "Cancel Changes",
                root.getScene().getWindow(),
                "Error canceling changes."
            );
        }
    }

    @FXML
    private void onActionBtnCommitChanges(ActionEvent event) {
        try {
            if (viewModel == null) {
                LOGGER.log(Level.WARNING, "Cannot commit: no view model");
                return;
            }

            // Validate required fields
            if (!validateFields()) {
                return;
            }

            // Show confirmation dialog
            boolean confirmed = new Mensaje().showConfirmation(
                "Save Changes",
                root.getScene().getWindow(),
                "Are you sure you want to save these changes to your profile?"
            );

            if (confirmed) {
                // Convert to DTO and update
                PersonDTO updatedUser = viewModel.toDTO();
                PersonService personService = new PersonService();
                Respuesta response = personService.update(updatedUser);
                
                if (Boolean.TRUE.equals(response.getEstado())) {
                    // Update AppContext with new user data
                    Object savedUser = response.getResultado("Person");
                    if (savedUser instanceof PersonDTO) {
                        AppContext.getInstance().set("user", savedUser);
                        originalUser = (PersonDTO) savedUser;
                        
                        // Reset change detection
                        setupChangeDetection();
                        
                        new Mensaje().showModal(
                            Alert.AlertType.INFORMATION,
                            "Save Changes",
                            root.getScene().getWindow(),
                            "Your profile has been updated successfully."
                        );
                        
                        LOGGER.log(Level.INFO, "User profile updated successfully");
                    }
                } else {
                    new Mensaje().showModal(
                        Alert.AlertType.ERROR,
                        "Save Changes",
                        root.getScene().getWindow(),
                        "Error saving changes: " + response.getMensaje()
                    );
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error committing changes", ex);
            new Mensaje().showModal(
                Alert.AlertType.ERROR,
                "Save Changes",
                root.getScene().getWindow(),
                "Error saving changes."
            );
        }
    }

    /**
     * Validates required fields before saving.
     */
    private boolean validateFields() {
        StringBuilder errors = new StringBuilder();

        String firstName = getTrimmedText(txfPersonFirstName);
        String lastName = getTrimmedText(txfPersonLastName);
        String email = getTrimmedText(txfPersonEmail);
        String username = getTrimmedText(txfPersonUsername);
        String password = pswPersonPassword.getText();

        if (firstName.isEmpty()) {
            errors.append("- First name is required\n");
        }
        
        if (lastName.isEmpty()) {
            errors.append("- Last name is required\n");
        }
        
        if (email.isEmpty()) {
            errors.append("- Email is required\n");
        }
        
        if (username.isEmpty()) {
            errors.append("- Username is required\n");
        }
        
        if (password == null || password.trim().isEmpty()) {
            errors.append("- Password is required\n");
        }

        if (errors.length() > 0) {
            new Mensaje().showModal(
                Alert.AlertType.ERROR,
                "Validation Error",
                root.getScene().getWindow(),
                "Please correct the following errors:\n\n" + errors.toString()
            );
            return false;
        }
        
        return true;
    }

    /**
     * Unbinds all field properties (used during reset).
     */
    private void unbindFields() {
        if (viewModel == null) return;

        txfPersonFirstName.textProperty().unbindBidirectional(viewModel.firstNameProperty());
        txfPersonLastName.textProperty().unbindBidirectional(viewModel.lastNameProperty());
        txfPersonEmail.textProperty().unbindBidirectional(viewModel.emailProperty());
        txfPersonUsername.textProperty().unbindBidirectional(viewModel.usernameProperty());
        pswPersonPassword.textProperty().unbindBidirectional(viewModel.passwordProperty());
    }
}
