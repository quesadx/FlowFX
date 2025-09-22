package cr.ac.una.flowfx.controller;

import cr.ac.una.flowfx.model.PersonDTO;
import cr.ac.una.flowfx.model.PersonViewModel;
import cr.ac.una.flowfx.service.PersonService;
import cr.ac.una.flowfx.util.AnimationManager;
import cr.ac.una.flowfx.util.AppContext;
import cr.ac.una.flowfx.util.FlowController;
import cr.ac.una.flowfx.util.Mensaje;
import cr.ac.una.flowfx.util.Respuesta;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controller to sign up a new Person. Allows optional id (server generates).
 */
public class PersonSignUpController extends Controller implements Initializable {

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
    @FXML private TableView<PersonViewModel> tbvPersons;
    @FXML private TableColumn<PersonViewModel, Number> tbvPersonid;
    @FXML private TableColumn<PersonViewModel, String> tbvPersonName;
    @FXML private TableColumn<PersonViewModel, String> tbcPersonLastName;
    @FXML private TableColumn<PersonViewModel, String> tbvPersonMail;

    private static final Logger LOGGER = Logger.getLogger(PersonSignUpController.class.getName());

    private final ObservableList<PersonViewModel> persons = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setTextFieldLimit(txfPersonId, 9);
        setTextFieldLimit(txfPersonFirstName, 20);
        setTextFieldLimit(txfPersonLastName, 20);
        setTextFieldLimit(txfPersonEmail, 35);
        setTextFieldLimit(txfPersonUsername, 20);
        setTextFieldLimit(pswPersonPassword, 20);

        // Sanitizar cédula: solo dígitos y máximo 9
        txfPersonId.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) return;
            String digits = newVal.replaceAll("\\D", "");
            if (digits.length() > 9) digits = digits.substring(0, 9);
            if (!digits.equals(newVal)) {
                txfPersonId.setText(digits);
            }
        });

        tbvPersonid.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getId()));
        tbvPersonName.setCellValueFactory(data -> data.getValue().firstNameProperty());
        tbcPersonLastName.setCellValueFactory(data -> data.getValue().lastNameProperty());
        tbvPersonMail.setCellValueFactory(data -> data.getValue().emailProperty());
        tbvPersons.setItems(persons);

        tbvPersons.setRowFactory(tv -> {
            TableRow<PersonViewModel> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    PersonViewModel vm = row.getItem();
                    AppContext.getInstance().set("selectedPerson", vm.toDTO());
                    AppContext.getInstance().set("personExpand.viewOnly", false);
                    FlowController.getInstance().goView("PersonExpandView");
                }
            });
            return row;
        });

        refreshPersons();
    }

    @Override
    public void initialize() {
        // Check if person list needs refresh (e.g., after edit from PersonExpandView)
        checkAndRefreshPersonList();
    }

    private void setTextFieldLimit(MFXTextField txf, int i) {
        txf.addEventFilter(KeyEvent.KEY_TYPED, e -> {
            if (txf.getText() != null && txf.getText().length() >= i) e.consume();
        });
    }

    private void setTextFieldLimit(MFXPasswordField txf, int i) {
        txf.addEventFilter(KeyEvent.KEY_TYPED, e -> {
            if (txf.getText() != null && txf.getText().length() >= i) e.consume();
        });
    }

    private void refreshPersons() {
        LOGGER.info("Refrescando lista de personas");
        persons.clear();
        PersonService s = new PersonService();
        Respuesta r = s.findAll();
        
        if (r == null) {
            LOGGER.warning("PersonService.findAll() devolvió null");
            return;
        }
        
        if (!Boolean.TRUE.equals(r.getEstado())) {
            LOGGER.warning("Error al obtener personas: " + r.getMensaje());
            return;
        }
        
        @SuppressWarnings("unchecked")
        List<PersonDTO> list = (List<PersonDTO>) r.getResultado("Persons");
        if (list == null) {
            LOGGER.warning("Lista de personas es null en la respuesta");
            return;
        }
        
        LOGGER.info("Obtenidas " + list.size() + " personas del servicio");
        List<PersonViewModel> vms = new ArrayList<>();
        for (PersonDTO dto : list) {
            vms.add(new PersonViewModel(dto));
        }
        persons.addAll(vms);
        LOGGER.fine("Lista de personas actualizada con " + persons.size() + " elementos");
    }
    
    /**
     * Checks if the person list needs refresh (e.g., after updates from other views).
     */
    private void checkAndRefreshPersonList() {
        Boolean needsRefresh = (Boolean) AppContext.getInstance().get("personListNeedsRefresh");
        if (Boolean.TRUE.equals(needsRefresh)) {
            LOGGER.info("Person list refresh requested, refreshing list");
            refreshPersons();
            // Clear the flag
            AppContext.getInstance().delete("personListNeedsRefresh");
        }
    }

    @FXML
    private void onActionBtnSignUpPerson(ActionEvent event) {
        AnimationManager.showPopup(vbSignUpDisplay, vbCover);
    }

    @FXML
    private void onKeyPressedTxfPersonId(KeyEvent event) {
        String text = event.getText();
        if (!text.matches("[0-9]")) event.consume();
    }

    @FXML
    private void onActionBtnCancelPersonSignUp(ActionEvent event) {
        AnimationManager.hidePopup(vbSignUpDisplay, vbCover);
        clearSignUpFields();
    }

    @FXML
    private void onActionBtnPersonSignUp(ActionEvent event) {
        LOGGER.info("Iniciando proceso de registro de persona");
        
        PersonDTO dto = extractPersonFromSignUp();
        if (dto == null) {
            LOGGER.warning("Validación de datos falló, cancelando registro");
            return;
        }
        
        LOGGER.info("Datos validados correctamente, enviando al servicio - ID: " + dto.getId() + 
            ", Nombre: " + dto.getFirstName() + " " + dto.getLastName() + 
            ", Estado: " + dto.getStatus() + ", Admin: " + dto.getIsAdmin());
        
        PersonService s = new PersonService();
        Respuesta r = s.create(dto);
        
        if (Boolean.TRUE.equals(r.getEstado())) {
            LOGGER.info("Persona registrada exitosamente en el servidor");
            new Mensaje().showModal(
                Alert.AlertType.INFORMATION,
                "Registro Exitoso",
                root.getScene().getWindow(),
                "La persona ha sido registrada exitosamente."
            );
            AnimationManager.hidePopup(vbSignUpDisplay, vbCover);
            clearSignUpFields();
            refreshPersons();
        } else {
            String errorMsg = r != null ? r.getMensaje() : "Error desconocido";
            String errorDetail = r != null ? r.getMensajeInterno() : "";
            
            LOGGER.log(Level.SEVERE, "Error en registro - Mensaje: {0}, Detalle: {1}", 
                new Object[] { errorMsg, errorDetail });
            
            // Show detailed error to help with debugging
            String detailedMsg = errorMsg;
            if (errorDetail != null && !errorDetail.trim().isEmpty()) {
                detailedMsg += "\n\nDetalle técnico: " + errorDetail;
            }
            
            new Mensaje().showModal(
                Alert.AlertType.ERROR,
                "Error de Registro",
                root.getScene().getWindow(),
                detailedMsg
            );
        }
    }

    private PersonDTO extractPersonFromSignUp() {
        try {
            // Extraer y preparar valores de la UI
            String idText = txfPersonId.getText();
            String firstName = getTrimmedText(txfPersonFirstName);
            String lastName = getTrimmedText(txfPersonLastName);
            String email = getTrimmedText(txfPersonEmail);
            String username = getTrimmedText(txfPersonUsername);
            String password = pswPersonPassword.getText();
            Character status = cbIsActive.isSelected() ? 'A' : 'I';
            Character isAdmin = cbIsAdmin.isSelected() ? 'Y' : 'N';

            // Debug
            LOGGER.info(
                "Validando campos de registro - ID: '" + idText + "', Nombre: '" + firstName +
                "', Apellido: '" + lastName + "', Email: '" + email + "', Usuario: '" + username +
                "', Contraseña: " + (password != null && !password.isEmpty() ? "[PRESENTE]" : "[VACÍA]")
            );

            // Validación de campos requeridos
            List<String> missing = new ArrayList<>();
            if (idText == null || idText.trim().isEmpty()) missing.add("Cédula");
            if (firstName.isEmpty()) missing.add("Nombre");
            if (lastName.isEmpty()) missing.add("Apellido");
            if (email.isEmpty()) missing.add("Correo electrónico");
            if (!missing.isEmpty()) {
                String fields = String.join(", ", missing);
                LOGGER.warning("Campos faltantes: " + fields);
                new Mensaje().showModal(
                    Alert.AlertType.ERROR,
                    "Campos Requeridos",
                    root.getScene().getWindow(),
                    "Por favor complete los siguientes campos requeridos: " + fields
                );
                return null;
            }

            // Validar cédula: exactamente 9 dígitos
            String idDigits = idText.trim();
            if (!idDigits.matches("\\d{9}")) {
                new Mensaje().showModal(
                    Alert.AlertType.ERROR,
                    "Registro",
                    root.getScene().getWindow(),
                    "La cédula debe tener exactamente 9 dígitos numéricos."
                );
                return null;
            }

            // Parsear cédula a Long (seguro tras validación)
            Long id = parseLongSafe(idDigits);
            if (id == null) {
                LOGGER.warning("ID inválido: '" + idDigits + "'");
                new Mensaje().showModal(
                    Alert.AlertType.ERROR,
                    "Cédula Inválida",
                    root.getScene().getWindow(),
                    "La cédula debe ser un número válido."
                );
                return null;
            }

            // Validación de usuario/contraseña
            boolean usernameEmpty = username.isEmpty();
            boolean passwordEmpty = (password == null || password.isEmpty());
            LOGGER.info(
                "Validación usuario/contraseña - Usuario vacío: " + usernameEmpty +
                ", Contraseña vacía: " + passwordEmpty
            );

            if (usernameEmpty && passwordEmpty) {
                // Ambos vacíos: permitido
                LOGGER.info("Creando persona sin credenciales de usuario");
                return new PersonDTO(id, firstName, lastName, email, null, null, status, isAdmin);
            }
            if (!usernameEmpty && !passwordEmpty) {
                // Ambos llenos: permitido
                LOGGER.info("Creando persona con credenciales de usuario");
                return new PersonDTO(id, firstName, lastName, email, username, password, status, isAdmin);
            }

            // Uno vacío y otro lleno: error
            LOGGER.warning(
                "Usuario y contraseña incompletos - Usuario: '" + username + "', Contraseña: " +
                (passwordEmpty ? "vacía" : "presente")
            );
            new Mensaje().showModal(
                Alert.AlertType.ERROR,
                "Credenciales Incompletas",
                root.getScene().getWindow(),
                "Debe proporcionar tanto el nombre de usuario como la contraseña, o dejar ambos campos vacíos."
            );
            return null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error inesperado validando datos de registro", e);
            new Mensaje().showModal(
                Alert.AlertType.ERROR,
                "Error de Validación",
                root.getScene().getWindow(),
                "Error inesperado al validar los datos: " + e.getMessage()
            );
            return null;
        }
    }

    private Long parseLongSafe(String text) {
        if (text == null) return null;
        String trimmed = text.trim();
        if (trimmed.isEmpty()) return null;
        try {
            return Long.parseLong(trimmed);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String getTrimmedText(MFXTextField field) {
        String text = field.getText();
        return text != null ? text.trim() : "";
    }

    private void clearSignUpFields() {
        txfPersonId.clear();
        txfPersonFirstName.clear();
        txfPersonLastName.clear();
        txfPersonEmail.clear();
        txfPersonUsername.clear();
        pswPersonPassword.clear();
        cbIsAdmin.setSelected(false);
        cbIsActive.setSelected(false);
    }
}
