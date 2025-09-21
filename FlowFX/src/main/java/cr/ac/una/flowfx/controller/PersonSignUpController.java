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
    private TableView<PersonViewModel> tbvPersons;

    @FXML
    private TableColumn<PersonViewModel, Number> tbvPersonid;

    @FXML
    private TableColumn<PersonViewModel, String> tbvPersonName;

    @FXML
    private TableColumn<PersonViewModel, String> tbcPersonLastName;

    @FXML
    private TableColumn<PersonViewModel, String> tbvPersonMail;

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
        // no-op
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
        persons.clear();
        PersonService s = new PersonService();
        Respuesta r = s.findAll();
        if (r == null || !Boolean.TRUE.equals(r.getEstado())) return;
        @SuppressWarnings("unchecked")
        List<PersonDTO> list = (List<PersonDTO>) r.getResultado("Persons");
        if (list == null) return;
        List<PersonViewModel> vms = new ArrayList<>();
        for (PersonDTO dto : list) vms.add(new PersonViewModel(dto));
        persons.addAll(vms);
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
        PersonDTO dto = extractPersonFromSignUp();
        if (dto == null) return;
        PersonService s = new PersonService();
        Respuesta r = s.create(dto);
        if (Boolean.TRUE.equals(r.getEstado())) {
            new Mensaje().showModal(
                Alert.AlertType.INFORMATION,
                "Registro",
                root.getScene().getWindow(),
                "Persona registrada exitosamente."
            );
            AnimationManager.hidePopup(vbSignUpDisplay, vbCover);
            clearSignUpFields();
            refreshPersons();
        } else {
            LOGGER.log(
                Level.WARNING,
                "P. mensaje={0}, mensajeInterno={1}",
                new Object[] { r == null ? null : r.getMensaje(), r == null ? null : r.getMensajeInterno() }
            );
            new Mensaje().showModal(
                Alert.AlertType.ERROR,
                "Registro",
                root.getScene().getWindow(),
                r == null ? "Error misterioso." : r.getMensaje()
            );
        }
    }

    private PersonDTO extractPersonFromSignUp() {
        try {
            Long id = parseLongSafe(txfPersonId.getText());
            String firstName = getTrimmedText(txfPersonFirstName);
            String lastName = getTrimmedText(txfPersonLastName);
            String email = getTrimmedText(txfPersonEmail);
            String username = getTrimmedText(txfPersonUsername);
            String password = pswPersonPassword.getText();
            Character status = cbIsActive.isSelected() ? 'A' : 'I';
            Character isAdmin = cbIsAdmin.isSelected() ? 'Y' : 'N';

            if (id == null || firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
                new Mensaje().showModal(
                    Alert.AlertType.ERROR,
                    "Registro",
                    root.getScene().getWindow(),
                    "Por favor complete todos los campos requeridos (ID, Nombre, Apellido, Correo electrónico)."
                );
                return null;
            }

            boolean usernameEmpty = username.isEmpty();
            boolean passwordEmpty = password == null || password.isEmpty();
            if (usernameEmpty && passwordEmpty) {
                return new PersonDTO(id, firstName, lastName, email, null, null, status, isAdmin);
            } else if (!usernameEmpty && !passwordEmpty) {
                return new PersonDTO(id, firstName, lastName, email, username, password, status, isAdmin);
            } else {
                new Mensaje().showModal(
                    Alert.AlertType.ERROR,
                    "Registro",
                    root.getScene().getWindow(),
                    "Proporcione tanto el Nombre de usuario como la Contraseña, o deje ambos en blanco."
                );
                return null;
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error validando datos", e);
            new Mensaje().showModal(
                Alert.AlertType.ERROR,
                "Registro",
                root.getScene().getWindow(),
                "Error validando datos: " + (e == null ? "?" : e.getMessage())
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
