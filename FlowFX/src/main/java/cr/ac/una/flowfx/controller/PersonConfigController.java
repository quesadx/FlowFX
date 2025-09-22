package cr.ac.una.flowfx.controller;

import cr.ac.una.flowfx.model.PersonDTO;
import cr.ac.una.flowfx.model.PersonViewModel;
import cr.ac.una.flowfx.model.ProjectActivityDTO;
import cr.ac.una.flowfx.model.ProjectDTO;
import cr.ac.una.flowfx.service.PersonService;
import cr.ac.una.flowfx.service.ProjectActivityService;
import cr.ac.una.flowfx.service.ProjectService;
import cr.ac.una.flowfx.util.AppContext;
import cr.ac.una.flowfx.util.Mensaje;
import cr.ac.una.flowfx.util.Respuesta;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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

    @FXML private AnchorPane root;
    @FXML private VBox vbCover;
    @FXML private VBox vbSignUpDisplay;
    @FXML private MFXTextField txfPersonId;
    @FXML private MFXTextField txfPersonFirstName;
    @FXML private MFXTextField txfPersonLastName;
    @FXML private MFXTextField txfPersonEmail;
    @FXML private MFXTextField txfPersonUsername;
    @FXML private MFXPasswordField pswPersonPassword;
    @FXML private MFXCheckbox cbIsAdmin;
    @FXML private MFXCheckbox cbIsActive;
    @FXML private MFXButton btnCancelChanges;
    @FXML private MFXButton btnCommitChanges;

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
                    "Configuración",
                    root.getScene().getWindow(),
                    "No se encontró una sesión de usuario. Por favor inicie sesión de nuevo."
                );
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error loading current user", ex);
            new Mensaje().showModal(
                Alert.AlertType.ERROR,
                "Configuración",
                root.getScene().getWindow(),
                "Error al cargar la información del usuario."
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
        
        // Disable admin checkbox if user is not currently an admin
        // This prevents non-admin users from granting themselves admin privileges
        boolean userIsAdmin = originalUser != null && originalUser.getIsAdmin() != null && 
            Character.toUpperCase(originalUser.getIsAdmin()) == 'Y';
        cbIsAdmin.setDisable(!userIsAdmin);
        
        if (!userIsAdmin) {
            LOGGER.info("Admin checkbox disabled - current user is not an administrator");
        }
        
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
                LOGGER.log(Level.WARNING, "No se puede cancelar: no hay datos de usuario originales");
                return;
            }

            // Show confirmation dialog
            boolean confirmed = new Mensaje().showConfirmation(
                "Cancelar cambios",
                root.getScene().getWindow(),
                "¿Está seguro de que desea cancelar todos los cambios? Esto recargará los datos originales."
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
                        "Cancelar cambios",
                        root.getScene().getWindow(),
                        "Error al recargar los datos del usuario: " + response.getMensaje()
                    );
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error canceling changes", ex);
            new Mensaje().showModal(
                Alert.AlertType.ERROR,
                "Cancelar cambios",
                root.getScene().getWindow(),
                "Error al cancelar los cambios."
            );
        }
    }

    @FXML
    private void onActionPrintRegistrationCard(ActionEvent event) {
    System.out.println("Generando tarjeta de identificación...");
        try {
            if (viewModel == null || originalUser == null) {
                System.out.println("No hay datos de usuario disponibles para imprimir.");
                new Mensaje().showModal(
                    Alert.AlertType.ERROR,
                    "Imprimir tarjeta de identificación",
                    root.getScene().getWindow(),
                    "No hay datos de usuario disponibles para imprimir."
                );
                return;
            }

            // Show file chooser for save location
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar tarjeta de identificación");
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Libro de Excel (*.xlsx)", "*.xlsx"),
                new FileChooser.ExtensionFilter("Todos los archivos", "*.*")
            );
            
            String defaultName = sanitizeFileName(
                (getTrimmedText(txfPersonFirstName) + "_" + getTrimmedText(txfPersonLastName) + "_ID_Card")
                    .replaceAll("\\s+", "_")
            );
            if (defaultName.isBlank()) {
                defaultName = "Tarjeta_Identificacion";
            }
            fileChooser.setInitialFileName(defaultName + ".xlsx");
            
            Stage stage = (Stage) root.getScene().getWindow();
            File selectedFile = fileChooser.showSaveDialog(stage);
            
            if (selectedFile != null) {
                // Ensure .xlsx extension
                if (!selectedFile.getName().toLowerCase().endsWith(".xlsx")) {
                    selectedFile = new File(selectedFile.getParentFile(), selectedFile.getName() + ".xlsx");
                }
                
                generateIdCard(selectedFile);
                
                new Mensaje().showModal(
                    Alert.AlertType.INFORMATION,
                    "Imprimir tarjeta de identificación",
                    root.getScene().getWindow(),
                    "La tarjeta de identificación se generó correctamente:\n" + selectedFile.getAbsolutePath()
                );
                
                LOGGER.log(Level.INFO, "Tarjeta de identificación generada: " + selectedFile.getAbsolutePath());
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error al generar la tarjeta de identificación", ex);
            new Mensaje().showModal(
                Alert.AlertType.ERROR,
                "Imprimir tarjeta de identificación",
                root.getScene().getWindow(),
                "Error al generar la tarjeta de identificación: " + ex.getMessage()
            );
        }
    }

    @FXML
    private void onActionPrintProjectReport(ActionEvent event) {
    System.out.println("Generando informe completo de proyectos...");
        try {
            if (viewModel == null || originalUser == null) {
                System.out.println("No hay datos de usuario disponibles para el informe del proyecto.");
                new Mensaje().showModal(
                    Alert.AlertType.ERROR,
                    "Informe de proyectos",
                    root.getScene().getWindow(),
                    "No hay datos de usuario disponibles para generar el informe del proyecto."
                );
                return;
            }

            // Show file chooser for save location
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar informe de proyectos");
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Libro de Excel (*.xlsx)", "*.xlsx"),
                new FileChooser.ExtensionFilter("Todos los archivos", "*.*")
            );
            
            String defaultName = sanitizeFileName(
                (getTrimmedText(txfPersonFirstName) + "_" + getTrimmedText(txfPersonLastName) + "_Project_Report")
                    .replaceAll("\\s+", "_")
            );
            if (defaultName.isBlank()) {
                defaultName = "Informe_Proyectos";
            }
            fileChooser.setInitialFileName(defaultName + ".xlsx");
            
            Stage stage = (Stage) root.getScene().getWindow();
            File selectedFile = fileChooser.showSaveDialog(stage);
            
            if (selectedFile != null) {
                // Ensure .xlsx extension
                if (!selectedFile.getName().toLowerCase().endsWith(".xlsx")) {
                    selectedFile = new File(selectedFile.getParentFile(), selectedFile.getName() + ".xlsx");
                }
                
                generatePersonProjectReport(selectedFile);
                
                new Mensaje().showModal(
                    Alert.AlertType.INFORMATION,
                    "Informe de proyectos",
                    root.getScene().getWindow(),
                    "El informe de proyectos se generó correctamente:\n" + selectedFile.getAbsolutePath()
                );
                
                LOGGER.log(Level.INFO, "Informe de proyectos generado: " + selectedFile.getAbsolutePath());
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error al generar el informe de proyectos", ex);
            new Mensaje().showModal(
                Alert.AlertType.ERROR,
                "Informe de proyectos",
                root.getScene().getWindow(),
                "Error al generar el informe de proyectos: " + ex.getMessage()
            );
        }
    }    @FXML
    private void onActionBtnCommitChanges(ActionEvent event) {
        try {
            if (viewModel == null) {
                LOGGER.log(Level.WARNING, "No se puede guardar: no hay modelo de vista");
                return;
            }

            // Validate required fields
            if (!validateFields()) {
                return;
            }

            // Show confirmation dialog
            boolean confirmed = new Mensaje().showConfirmation(
                "Guardar cambios",
                root.getScene().getWindow(),
                "¿Está seguro de que desea guardar estos cambios en su perfil?"
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
                            "Guardar cambios",
                            root.getScene().getWindow(),
                            "Su perfil se ha actualizado correctamente."
                        );
                        
                        LOGGER.log(Level.INFO, "User profile updated successfully");
                    }
                } else {
                    new Mensaje().showModal(
                        Alert.AlertType.ERROR,
                        "Guardar cambios",
                        root.getScene().getWindow(),
                        "Error al guardar los cambios: " + response.getMensaje()
                    );
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error committing changes", ex);
            new Mensaje().showModal(
                Alert.AlertType.ERROR,
                "Guardar cambios",
                root.getScene().getWindow(),
                "Error al guardar los cambios."
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
            errors.append("- El nombre es obligatorio\n");
        }
        
        if (lastName.isEmpty()) {
            errors.append("- El apellido es obligatorio\n");
        }
        
        if (email.isEmpty()) {
            errors.append("- El correo electrónico es obligatorio\n");
        }
        
        if (username.isEmpty()) {
            errors.append("- El nombre de usuario es obligatorio\n");
        }
        
        if (password == null || password.trim().isEmpty()) {
            errors.append("- La contraseña es obligatoria\n");
        }

        if (errors.length() > 0) {
            new Mensaje().showModal(
                Alert.AlertType.ERROR,
                "Error de validación",
                root.getScene().getWindow(),
                "Por favor corrija los siguientes errores:\n\n" + errors.toString()
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

    /**
     * Sanitizes a filename by removing illegal characters.
     */
    private String sanitizeFileName(String fileName) {
        if (fileName == null) return "";
        return fileName.replaceAll("[\\\\/:*?\"<>|]", "_").trim();
    }

    /**
     * Generates an ID card using Apache POI Excel format.
     */
    private void generateIdCard(File file) throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             FileOutputStream fileOutput = new FileOutputStream(file)) {
            
            Sheet sheet = workbook.createSheet("Tarjeta de identificación");
            
            // Create styles
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle labelStyle = createLabelStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle statusStyle = createStatusStyle(workbook);
            
            int currentRow = 0;
            
            // Header with organization name
            currentRow = addHeader(sheet, headerStyle, currentRow);
            currentRow++; // Empty row
            
            // ID Card title
            currentRow = addTitle(sheet, titleStyle, currentRow);
            currentRow++; // Empty row
            
            // Person photo placeholder
            currentRow = addPhotoPlaceholder(sheet, labelStyle, currentRow);
            currentRow++; // Empty row
            
            // Person information
            currentRow = addPersonInfo(sheet, labelStyle, dataStyle, currentRow);
            currentRow++; // Empty row
            
            // Status information
            currentRow = addStatusInfo(sheet, labelStyle, statusStyle, currentRow);
            currentRow++; // Empty row
            
            // Footer with issue date
            addFooter(sheet, labelStyle, currentRow);
            
            // Configure sheet presentation
            configureIdCardSheet(sheet);
            
            workbook.write(fileOutput);
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 18);
        font.setBold(true);
        font.setColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderTop(BorderStyle.THICK);
        style.setBorderBottom(BorderStyle.THICK);
        style.setBorderLeft(BorderStyle.THICK);
        style.setBorderRight(BorderStyle.THICK);
        return style;
    }

    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 16);
        font.setBold(true);
        font.setColor(IndexedColors.BLACK.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle createLabelStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 12);
        font.setBold(true);
        font.setColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 12);
        font.setColor(IndexedColors.BLACK.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createStatusStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 11);
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.DARK_GREEN.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderTop(BorderStyle.MEDIUM);
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.MEDIUM);
        style.setBorderRight(BorderStyle.MEDIUM);
        return style;
    }

    private int addHeader(Sheet sheet, CellStyle headerStyle, int startRow) {
        Row row = sheet.createRow(startRow);
        row.setHeight((short) 600);
        
    Cell cell = row.createCell(0);
    cell.setCellValue("UNIVERSIDAD DE COSTA RICA");
        cell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(startRow, startRow, 0, 5));
        
        Row subRow = sheet.createRow(startRow + 1);
        Cell subCell = subRow.createCell(0);
    subCell.setCellValue("SISTEMA FLOWFX - TARJETA DE IDENTIFICACIÓN");
        subCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(startRow + 1, startRow + 1, 0, 5));
        
        return startRow + 2;
    }

    private int addTitle(Sheet sheet, CellStyle titleStyle, int startRow) {
        Row row = sheet.createRow(startRow);
        row.setHeight((short) 400);
        
        Cell cell = row.createCell(0);
    cell.setCellValue("TARJETA DE IDENTIFICACIÓN DE EMPLEADO");
        cell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(startRow, startRow, 0, 5));
        
        return startRow + 1;
    }

    private int addPhotoPlaceholder(Sheet sheet, CellStyle labelStyle, int startRow) {
        Row row = sheet.createRow(startRow);
        row.setHeight((short) 1000);
        
    Cell cell = row.createCell(4);
    cell.setCellValue("[FOTO]");
        cell.setCellStyle(labelStyle);
        sheet.addMergedRegion(new CellRangeAddress(startRow, startRow + 3, 4, 5));
        
        return startRow + 4;
    }

    private int addPersonInfo(Sheet sheet, CellStyle labelStyle, CellStyle dataStyle, int startRow) {
        String[][] data = {
            {"ID:", String.valueOf(viewModel.getId())},
            {"Nombre:", viewModel.getFirstName() != null ? viewModel.getFirstName() : ""},
            {"Apellido:", viewModel.getLastName() != null ? viewModel.getLastName() : ""},
            {"Correo:", viewModel.getEmail() != null ? viewModel.getEmail() : ""},
            {"Usuario:", viewModel.getUsername() != null ? viewModel.getUsername() : ""}
        };
        
        for (String[] rowData : data) {
            Row row = sheet.createRow(startRow++);
            row.setHeight((short) 350);
            
            Cell labelCell = row.createCell(0);
            labelCell.setCellValue(rowData[0]);
            labelCell.setCellStyle(labelStyle);
            
            Cell dataCell = row.createCell(1);
            dataCell.setCellValue(rowData[1]);
            dataCell.setCellStyle(dataStyle);
            sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 1, 3));
        }
        
        return startRow;
    }

    private int addStatusInfo(Sheet sheet, CellStyle labelStyle, CellStyle statusStyle, int startRow) {
        Row adminRow = sheet.createRow(startRow++);
        adminRow.setHeight((short) 350);
        
    Cell adminLabelCell = adminRow.createCell(0);
    adminLabelCell.setCellValue("Administrador:");
        adminLabelCell.setCellStyle(labelStyle);
        
        Cell adminDataCell = adminRow.createCell(1);
        boolean isAdmin = viewModel.getIsAdmin() != null && 
            Character.toUpperCase(viewModel.getIsAdmin()) == 'Y';
    adminDataCell.setCellValue(isAdmin ? "SÍ" : "NO");
        adminDataCell.setCellStyle(statusStyle);
        
        Row statusRow = sheet.createRow(startRow++);
        statusRow.setHeight((short) 350);
        
    Cell statusLabelCell = statusRow.createCell(0);
    statusLabelCell.setCellValue("Estado:");
        statusLabelCell.setCellStyle(labelStyle);
        
        Cell statusDataCell = statusRow.createCell(1);
        boolean isActive = viewModel.getStatus() != null && 
            Character.toUpperCase(viewModel.getStatus()) == 'A';
    statusDataCell.setCellValue(isActive ? "ACTIVO" : "INACTIVO");
        statusDataCell.setCellStyle(statusStyle);
        
        return startRow;
    }

    private void addFooter(Sheet sheet, CellStyle labelStyle, int startRow) {
        Row row = sheet.createRow(startRow + 1);
    Cell cell = row.createCell(0);
    cell.setCellValue("Emitido: " + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        cell.setCellStyle(labelStyle);
        sheet.addMergedRegion(new CellRangeAddress(startRow + 1, startRow + 1, 0, 5));
    }

    private void configureIdCardSheet(Sheet sheet) {
        // Set column widths for ID card layout
        sheet.setColumnWidth(0, 4000);  // Label column
        sheet.setColumnWidth(1, 6000);  // Data column
        sheet.setColumnWidth(2, 3000);  // Extra space
        sheet.setColumnWidth(3, 3000);  // Extra space
        sheet.setColumnWidth(4, 4000);  // Photo column
        sheet.setColumnWidth(5, 2000);  // Border column
        
        // Set default row height
        sheet.setDefaultRowHeight((short) 300);
        
        // Set print area to fit ID card dimensions
        sheet.setPrintGridlines(false);
        sheet.setFitToPage(true);
        sheet.getPrintSetup().setFitWidth((short) 1);
        sheet.getPrintSetup().setFitHeight((short) 1);
    }

    /**
     * Generates a comprehensive project report showing user's projects, positions, and activities.
     */
    private void generatePersonProjectReport(File file) throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             FileOutputStream fileOutput = new FileOutputStream(file)) {
            
            Sheet sheet = workbook.createSheet("Informe de proyectos");
            ProjectReportStyleManager styleManager = new ProjectReportStyleManager(workbook);
            
            int currentRow = 0;
            
            // Generate report content
            currentRow = writePersonHeader(sheet, styleManager, currentRow);
            currentRow++; // Empty row
            currentRow = writePersonSummary(sheet, styleManager, currentRow);
            currentRow++; // Empty row
            currentRow = writeProjectsSection(sheet, styleManager, currentRow);
            currentRow++; // Empty row
            currentRow = writeActivitiesSection(sheet, styleManager, currentRow);
            currentRow++; // Empty row
            writeReportFooter(sheet, styleManager, currentRow);
            
            // Configure sheet presentation
            configureProjectReportSheet(sheet);
            
            workbook.write(fileOutput);
        }
    }

    /**
     * Writes the person header section.
     */
    private int writePersonHeader(Sheet sheet, ProjectReportStyleManager styleManager, int startRow) {
        Row titleRow = sheet.createRow(startRow++);
        titleRow.setHeight((short) 600);
        
        Cell titleCell = titleRow.createCell(0);
    titleCell.setCellValue("FLOWFX SYSTEM - INFORME COMPLETO DE PROYECTOS");
        titleCell.setCellStyle(styleManager.getTitleStyle());
        sheet.addMergedRegion(new CellRangeAddress(startRow - 1, startRow - 1, 0, 7));
        
        Row userRow = sheet.createRow(startRow++);
        userRow.setHeight((short) 400);
        
        Cell userCell = userRow.createCell(0);
        String userName = (getTrimmedText(txfPersonFirstName) + " " + getTrimmedText(txfPersonLastName)).trim();
    userCell.setCellValue("Empleado: " + userName);
        userCell.setCellStyle(styleManager.getSubHeaderStyle());
        sheet.addMergedRegion(new CellRangeAddress(startRow - 1, startRow - 1, 0, 7));
        
        return startRow;
    }

    /**
     * Writes the person summary section.
     */
    private int writePersonSummary(Sheet sheet, ProjectReportStyleManager styleManager, int startRow) {
        String[][] personData = {
            {"ID de empleado:", String.valueOf(viewModel.getId())},
            {"Nombre completo:", (getTrimmedText(txfPersonFirstName) + " " + getTrimmedText(txfPersonLastName)).trim()},
            {"Correo electrónico:", getTrimmedText(txfPersonEmail)},
            {"Nombre de usuario:", getTrimmedText(txfPersonUsername)},
            {"Rol:", cbIsAdmin.isSelected() ? "Administrador" : "Usuario"},
            {"Estado:", cbIsActive.isSelected() ? "Activo" : "Inactivo"}
        };
        
        for (String[] rowData : personData) {
            Row row = sheet.createRow(startRow++);
            row.setHeight((short) 350);
            
            Cell labelCell = row.createCell(0);
            labelCell.setCellValue(rowData[0]);
            labelCell.setCellStyle(styleManager.getLabelStyle());
            
            Cell dataCell = row.createCell(1);
            dataCell.setCellValue(rowData[1]);
            dataCell.setCellStyle(styleManager.getDataStyle());
            sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 1, 3));
        }
        
        return startRow;
    }

    /**
     * Writes the projects section showing all projects where user participates.
     */
    private int writeProjectsSection(Sheet sheet, ProjectReportStyleManager styleManager, int startRow) {
        // Section header
        Row sectionRow = sheet.createRow(startRow++);
        sectionRow.setHeight((short) 500);
        
        Cell sectionCell = sectionRow.createCell(0);
    sectionCell.setCellValue("PARTICIPACIÓN EN PROYECTOS");
        sectionCell.setCellStyle(styleManager.getSectionHeaderStyle());
        sheet.addMergedRegion(new CellRangeAddress(startRow - 1, startRow - 1, 0, 7));
        
        // Load projects for user
        List<ProjectDTO> userProjects = loadUserProjects();
        
        if (userProjects.isEmpty()) {
            Row noProjectsRow = sheet.createRow(startRow++);
            Cell noProjectsCell = noProjectsRow.createCell(0);
            noProjectsCell.setCellValue("No se encontraron proyectos para este usuario.");
            noProjectsCell.setCellStyle(styleManager.getDataStyle());
            sheet.addMergedRegion(new CellRangeAddress(startRow - 1, startRow - 1, 0, 7));
            return startRow;
        }
        
        // Projects table headers
        Row headerRow = sheet.createRow(startRow++);
        headerRow.setHeight((short) 400);
    String[] headers = {"Nombre del proyecto", "Rol", "Estado", "Inicio previsto", "Fin previsto", "Inicio real", "Fin real", "Creado"};
        
        for (int i = 0; i < headers.length; i++) {
            Cell headerCell = headerRow.createCell(i);
            headerCell.setCellValue(headers[i]);
            headerCell.setCellStyle(styleManager.getTableHeaderStyle());
        }
        
        // Projects data rows
        for (ProjectDTO project : userProjects) {
            Row projectRow = sheet.createRow(startRow++);
            projectRow.setHeight((short) 350);
            
            int col = 0;
            writeTextCell(projectRow, col++, project.getName() != null ? project.getName() : "", styleManager.getDataStyle());
            writeTextCell(projectRow, col++, determineUserRoleInProject(project), styleManager.getDataStyle());
            writeTextCell(projectRow, col++, mapStatusToDisplay(project.getStatus()), styleManager.getDataStyle());
            writeDateCell(projectRow.createCell(col++), project.getPlannedStartDate(), styleManager.getDateStyle());
            writeDateCell(projectRow.createCell(col++), project.getPlannedEndDate(), styleManager.getDateStyle());
            writeDateCell(projectRow.createCell(col++), project.getActualStartDate(), styleManager.getDateStyle());
            writeDateCell(projectRow.createCell(col++), project.getActualEndDate(), styleManager.getDateStyle());
            writeDateCell(projectRow.createCell(col++), project.getCreatedAt(), styleManager.getDateStyle());
        }
        
        return startRow;
    }

    /**
     * Writes the activities section showing user's recent activities.
     */
    private int writeActivitiesSection(Sheet sheet, ProjectReportStyleManager styleManager, int startRow) {
        // Section header
        Row sectionRow = sheet.createRow(startRow++);
        sectionRow.setHeight((short) 500);
        
        Cell sectionCell = sectionRow.createCell(0);
    sectionCell.setCellValue("ACTIVIDADES RECIENTES");
        sectionCell.setCellStyle(styleManager.getSectionHeaderStyle());
        sheet.addMergedRegion(new CellRangeAddress(startRow - 1, startRow - 1, 0, 7));
        
        // Load recent activities for user
        List<ProjectActivityDTO> userActivities = loadUserActivities();
        
        if (userActivities.isEmpty()) {
            Row noActivitiesRow = sheet.createRow(startRow++);
            Cell noActivitiesCell = noActivitiesRow.createCell(0);
            noActivitiesCell.setCellValue("No se encontraron actividades recientes para este usuario.");
            noActivitiesCell.setCellStyle(styleManager.getDataStyle());
            sheet.addMergedRegion(new CellRangeAddress(startRow - 1, startRow - 1, 0, 7));
            return startRow;
        }
        
        // Activities table headers
        Row headerRow = sheet.createRow(startRow++);
        headerRow.setHeight((short) 400);
    String[] headers = {"Descripción de la actividad", "Proyecto", "Estado", "Inicio previsto", "Fin previsto", "Inicio real", "Fin real", "Creado"};
        
        for (int i = 0; i < headers.length; i++) {
            Cell headerCell = headerRow.createCell(i);
            headerCell.setCellValue(headers[i]);
            headerCell.setCellStyle(styleManager.getTableHeaderStyle());
        }
        
        // Activities data rows
        for (ProjectActivityDTO activity : userActivities) {
            Row activityRow = sheet.createRow(startRow++);
            activityRow.setHeight((short) 350);
            
            int col = 0;
            writeTextCell(activityRow, col++, activity.getDescription() != null ? activity.getDescription() : "", styleManager.getDataStyle());
            writeTextCell(activityRow, col++, resolveProjectName(activity.getProjectId()), styleManager.getDataStyle());
            writeTextCell(activityRow, col++, mapStatusToDisplay(activity.getStatus()), styleManager.getDataStyle());
            writeDateCell(activityRow.createCell(col++), activity.getPlannedStartDate(), styleManager.getDateStyle());
            writeDateCell(activityRow.createCell(col++), activity.getPlannedEndDate(), styleManager.getDateStyle());
            writeDateCell(activityRow.createCell(col++), activity.getActualStartDate(), styleManager.getDateStyle());
            writeDateCell(activityRow.createCell(col++), activity.getActualEndDate(), styleManager.getDateStyle());
            writeDateCell(activityRow.createCell(col++), activity.getCreatedAt(), styleManager.getDateStyle());
        }
        
        return startRow;
    }

    /**
     * Writes the report footer with generation information.
     */
    private void writeReportFooter(Sheet sheet, ProjectReportStyleManager styleManager, int startRow) {
        Row footerRow = sheet.createRow(startRow);
        Cell footerCell = footerRow.createCell(0);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    footerCell.setCellValue("Generado el: " + timestamp + " | FlowFX System v3.0");
        footerCell.setCellStyle(styleManager.getFooterStyle());
        sheet.addMergedRegion(new CellRangeAddress(startRow, startRow, 0, 7));
    }

    /**
     * Loads projects where the user participates.
     */
    private List<ProjectDTO> loadUserProjects() {
        List<ProjectDTO> projects = new ArrayList<>();
        try {
            Long userId = originalUser.getId();
            if (userId == null) return projects;
            
            ProjectService projectService = new ProjectService();
            Respuesta response = projectService.findProjectsForUser(userId);
            
            if (Boolean.TRUE.equals(response.getEstado())) {
                Object result = response.getResultado("Projects");
                if (result instanceof List<?> list) {
                    for (Object item : list) {
                        if (item instanceof ProjectDTO project) {
                            projects.add(project);
                        }
                    }
                }
            } else {
                LOGGER.log(Level.WARNING, "Error al cargar proyectos del usuario: " + response.getMensaje());
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error loading user projects", ex);
        }
        
        // Sort by creation date descending
        projects.sort(Comparator.comparing(ProjectDTO::getCreatedAt, 
            Comparator.nullsLast(Comparator.reverseOrder())));
        
        return projects;
    }

    /**
     * Loads recent activities for the user.
     */
    private List<ProjectActivityDTO> loadUserActivities() {
        List<ProjectActivityDTO> activities = new ArrayList<>();
        try {
            Long userId = originalUser.getId();
            if (userId == null) return activities;
            
            ProjectActivityService activityService = new ProjectActivityService();
            Respuesta response = activityService.findRecentForUser(userId, 50); // Limit to 50 recent activities
            
            if (Boolean.TRUE.equals(response.getEstado())) {
                Object result = response.getResultado("ProjectActivities");
                if (result == null) {
                    result = response.getResultado("Activities");
                }
                if (result instanceof List<?> list) {
                    for (Object item : list) {
                        if (item instanceof ProjectActivityDTO activity) {
                            activities.add(activity);
                        }
                    }
                }
            } else {
                LOGGER.log(Level.WARNING, "Error al cargar actividades del usuario: " + response.getMensaje());
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error loading user activities", ex);
        }
        
        return activities;
    }

    /**
     * Determines the user's role in a specific project.
     */
    private String determineUserRoleInProject(ProjectDTO project) {
        Long userId = originalUser.getId();
        if (userId == null) return "Desconocido";
        
        List<String> roles = new ArrayList<>();
        
        if (userId.equals(project.getLeaderUserId())) {
            roles.add("Líder de Proyecto");
        }
        if (userId.equals(project.getTechLeaderId())) {
            roles.add("Líder Técnico");
        }
        if (userId.equals(project.getSponsorId())) {
            roles.add("Patrocinador");
        }
        
        return roles.isEmpty() ? "Miembro del equipo" : String.join(", ", roles);
    }

    /**
     * Resolves project name from project ID.
     */
    private String resolveProjectName(Long projectId) {
        if (projectId == null || projectId <= 0) return "Proyecto desconocido";
        
        try {
            ProjectService projectService = new ProjectService();
            Respuesta response = projectService.find(projectId);
            
            if (Boolean.TRUE.equals(response.getEstado())) {
                Object result = response.getResultado("Project");
                    if (result instanceof ProjectDTO project) {
                    return project.getName() != null ? project.getName() : "Proyecto #" + projectId;
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Error resolving project name for ID: " + projectId, ex);
        }
        
        return "Proyecto #" + projectId;
    }

    /**
     * Maps status codes to display names.
     */
    private String mapStatusToDisplay(String statusCode) {
        if (statusCode == null || statusCode.isBlank()) return "Desconocido";
        
        return switch (statusCode.trim().toUpperCase()) {
            case "P" -> "Planificado";
            case "R" -> "En curso";
            case "S" -> "Suspendido";
            case "C" -> "Completado";
            case "A" -> "Activo";
            case "I" -> "Inactivo";
            default -> statusCode.trim();
        };
    }

    /**
     * Writes a text cell with the specified style.
     */
    private void writeTextCell(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value != null ? value : "");
        cell.setCellStyle(style);
    }

    /**
     * Writes a date cell with the specified style.
     */
    private void writeDateCell(Cell cell, java.util.Date value, CellStyle dateStyle) {
        if (value == null) {
            cell.setCellValue("");
        } else {
            cell.setCellValue(value);
            cell.setCellStyle(dateStyle);
        }
    }

    /**
     * Configures the project report sheet presentation.
     */
    private void configureProjectReportSheet(Sheet sheet) {
        // Set column widths for optimal presentation
        sheet.setColumnWidth(0, 5000);  // Labels/Names
        sheet.setColumnWidth(1, 4000);  // Data/Role
        sheet.setColumnWidth(2, 3000);  // Status
        sheet.setColumnWidth(3, 3500);  // Planned Start
        sheet.setColumnWidth(4, 3500);  // Planned End
        sheet.setColumnWidth(5, 3500);  // Actual Start
        sheet.setColumnWidth(6, 3500);  // Actual End
        sheet.setColumnWidth(7, 3500);  // Created
        
        // Configure print settings
        sheet.setPrintGridlines(true);
        sheet.setFitToPage(true);
        sheet.getPrintSetup().setFitWidth((short) 1);
        sheet.getPrintSetup().setFitHeight((short) 0); // Allow multiple pages vertically
        sheet.getPrintSetup().setLandscape(true); // Landscape orientation for wide data
    }

    /**
     * Style manager for project report Excel generation.
     */
    private class ProjectReportStyleManager {
        private final CellStyle titleStyle;
        private final CellStyle subHeaderStyle;
        private final CellStyle sectionHeaderStyle;
        private final CellStyle tableHeaderStyle;
        private final CellStyle labelStyle;
        private final CellStyle dataStyle;
        private final CellStyle dateStyle;
        private final CellStyle footerStyle;
        
        public ProjectReportStyleManager(XSSFWorkbook workbook) {
            this.titleStyle = createTitleStyle(workbook);
            this.subHeaderStyle = createSubHeaderStyle(workbook);
            this.sectionHeaderStyle = createSectionHeaderStyle(workbook);
            this.tableHeaderStyle = createTableHeaderStyle(workbook);
            this.labelStyle = createLabelStyle(workbook);
            this.dataStyle = createDataStyle(workbook);
            this.dateStyle = createDateStyle(workbook);
            this.footerStyle = createFooterStyle(workbook);
        }
        
        private CellStyle createTitleStyle(XSSFWorkbook workbook) {
            CellStyle style = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setFontName("Arial");
            font.setFontHeightInPoints((short) 16);
            font.setBold(true);
            font.setColor(IndexedColors.WHITE.getIndex());
            style.setFont(font);
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            addBorders(style, BorderStyle.THICK);
            return style;
        }
        
        private CellStyle createSubHeaderStyle(XSSFWorkbook workbook) {
            CellStyle style = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setFontName("Arial");
            font.setFontHeightInPoints((short) 14);
            font.setBold(true);
            font.setColor(IndexedColors.DARK_BLUE.getIndex());
            style.setFont(font);
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            style.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            addBorders(style, BorderStyle.MEDIUM);
            return style;
        }
        
        private CellStyle createSectionHeaderStyle(XSSFWorkbook workbook) {
            CellStyle style = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setFontName("Arial");
            font.setFontHeightInPoints((short) 13);
            font.setBold(true);
            font.setColor(IndexedColors.WHITE.getIndex());
            style.setFont(font);
            style.setAlignment(HorizontalAlignment.LEFT);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            style.setFillForegroundColor(IndexedColors.DARK_GREEN.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            addBorders(style, BorderStyle.MEDIUM);
            return style;
        }
        
        private CellStyle createTableHeaderStyle(XSSFWorkbook workbook) {
            CellStyle style = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setFontName("Arial");
            font.setFontHeightInPoints((short) 11);
            font.setBold(true);
            font.setColor(IndexedColors.BLACK.getIndex());
            style.setFont(font);
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            addBorders(style, BorderStyle.THIN);
            return style;
        }
        
        private CellStyle createLabelStyle(XSSFWorkbook workbook) {
            CellStyle style = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setFontName("Arial");
            font.setFontHeightInPoints((short) 11);
            font.setBold(true);
            font.setColor(IndexedColors.DARK_BLUE.getIndex());
            style.setFont(font);
            style.setAlignment(HorizontalAlignment.LEFT);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            return style;
        }
        
        private CellStyle createDataStyle(XSSFWorkbook workbook) {
            CellStyle style = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setFontName("Arial");
            font.setFontHeightInPoints((short) 11);
            font.setColor(IndexedColors.BLACK.getIndex());
            style.setFont(font);
            style.setAlignment(HorizontalAlignment.LEFT);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            addBorders(style, BorderStyle.THIN);
            return style;
        }
        
        private CellStyle createDateStyle(XSSFWorkbook workbook) {
            CellStyle style = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setFontName("Arial");
            font.setFontHeightInPoints((short) 11);
            style.setFont(font);
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            style.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("yyyy-mm-dd"));
            addBorders(style, BorderStyle.THIN);
            return style;
        }
        
        private CellStyle createFooterStyle(XSSFWorkbook workbook) {
            CellStyle style = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setFontName("Arial");
            font.setFontHeightInPoints((short) 9);
            font.setItalic(true);
            font.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
            style.setFont(font);
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            return style;
        }
        
        private void addBorders(CellStyle style, BorderStyle borderStyle) {
            style.setBorderTop(borderStyle);
            style.setBorderBottom(borderStyle);
            style.setBorderLeft(borderStyle);
            style.setBorderRight(borderStyle);
        }
        
        // Getters
        public CellStyle getTitleStyle() { return titleStyle; }
        public CellStyle getSubHeaderStyle() { return subHeaderStyle; }
        public CellStyle getSectionHeaderStyle() { return sectionHeaderStyle; }
        public CellStyle getTableHeaderStyle() { return tableHeaderStyle; }
        public CellStyle getLabelStyle() { return labelStyle; }
        public CellStyle getDataStyle() { return dataStyle; }
        public CellStyle getDateStyle() { return dateStyle; }
        public CellStyle getFooterStyle() { return footerStyle; }
    }
}
