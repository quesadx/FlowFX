
package cr.ac.una.flowfx.controller;

import java.net.URL;
import java.util.ResourceBundle;
import cr.ac.una.flowfx.model.PersonDTO;
import cr.ac.una.flowfx.service.PersonService;
import cr.ac.una.flowfx.util.AnimationManager;
import cr.ac.una.flowfx.util.AppContext;
import cr.ac.una.flowfx.util.Mensaje;
import cr.ac.una.flowfx.util.Respuesta;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckListView;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class MainController extends Controller implements Initializable {

    @FXML private AnchorPane root;
    @FXML private VBox vbCover;
    @FXML private VBox vbLogInDisplay;
    @FXML private VBox vbSignUpDisplay;
    @FXML private VBox vBoxCentral;

    @FXML private MFXTextField txfUsername;
    @FXML private MFXPasswordField psfUserPassword;
    @FXML private MFXButton btnLogIn;

    @FXML private MFXCheckListView<?> checkListDashboard;
    @FXML private DatePicker datePKDashboard;
    @FXML private PieChart pieChartDashboard;

    @FXML private MFXTextField txfPersonId;
    @FXML private MFXTextField txfPersonFirstName;
    @FXML private MFXTextField txfPersonLastName;
    @FXML private MFXTextField txfPersonEmail;
    @FXML private MFXTextField txfPersonUsername;
    @FXML private MFXPasswordField pswPersonPassword;
    @FXML private MFXCheckbox cbIsAdmin;
    @FXML private MFXCheckbox cbIsActive;

    private boolean userLoggedIn = false;
    private PersonDTO user;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        vbCover.setEffect(new javafx.scene.effect.GaussianBlur());
        AnimationManager.showPopup(vbLogInDisplay, vbCover);
        Object nav = AppContext.getInstance().get("navigationBar");
        if (nav instanceof VBox) {
            ((VBox) nav).setDisable(!userLoggedIn);
        }
    }

    @Override
    public void initialize() {
        // no-op
    }

    @FXML
    private void onActionBtnLogIn(ActionEvent event) {
        String username = getTrimmedText(txfUsername);
        String password = psfUserPassword.getText();
        if (username.isEmpty() || password.isEmpty()) {
            new Mensaje().showModal(Alert.AlertType.ERROR, "Login", root.getScene().getWindow(), "Complete todos los campos requeridos.");
            return;
        }
        PersonService personService = new PersonService();
        Respuesta response = personService.validateCredentials(username, password);
        if (Boolean.TRUE.equals(response.getEstado())) {
            user = (PersonDTO) response.getResultado("Person");
            AppContext.getInstance().set("user", user);
            userLoggedIn = true;
            AnimationManager.hidePopup(vbLogInDisplay, vbCover);
            Object nav = AppContext.getInstance().get("navigationBar");
            if (nav instanceof VBox) {
                ((VBox) nav).setDisable(!userLoggedIn);
            }
        } else {
            new Mensaje().showModal(Alert.AlertType.ERROR, "Login", root.getScene().getWindow(), response.getMensaje());
        }
    }

    @FXML
    private void onMouseClickedLblSignUp(MouseEvent event) {
        AnimationManager.hidePopup(vbLogInDisplay);
        AnimationManager.showPopup(vbSignUpDisplay, vbCover);
    }

    @FXML
    private void onMouseClickedLblPasswordRecovery(MouseEvent event) {
        // no-op
    }

    @FXML
    private void onActionBtnCancelPersonSignUp(ActionEvent event) {
        AnimationManager.hidePopup(vbSignUpDisplay);
        AnimationManager.showPopup(vbLogInDisplay, vbCover);
        clearSignUpFields();
    }

    @FXML
    private void onActionBtnPersonSignUp(ActionEvent event) {
        PersonDTO newUser = extractPersonFromSignUp();
        if (newUser == null) return;
        PersonService service = new PersonService();
        Respuesta response = service.create(newUser);
        if (Boolean.TRUE.equals(response.getEstado())) {
            new Mensaje().showModal(Alert.AlertType.INFORMATION, "Registro", root.getScene().getWindow(), "Usuario registrado correctamente.");
            AnimationManager.hidePopup(vbSignUpDisplay);
            AnimationManager.showPopup(vbLogInDisplay, vbCover);
            clearSignUpFields();
        } else {
            new Mensaje().showModal(Alert.AlertType.ERROR, "Registro", root.getScene().getWindow(), response.getMensaje());
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
            char status = cbIsActive.isSelected() ? 'A' : 'I';
            char isAdmin = cbIsAdmin.isSelected() ? 'Y' : 'N';
            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                new Mensaje().showModal(Alert.AlertType.ERROR, "Login", root.getScene().getWindow(), "Complete todos los campos requeridos.");
                return null;
            }
            return new PersonDTO(id, firstName, lastName, email, username, password, status, isAdmin);
        } catch (NumberFormatException nfe) {
            new Mensaje().showModal(Alert.AlertType.ERROR, "SignUp", root.getScene().getWindow(), "La cédula debe ser numérica.");
            return null;
        } catch (Exception e) {
            System.err.println("Error al crear usuario por falta de datos: " + e.getMessage());
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

    private void clearLogInFields() {
        txfUsername.clear();
        psfUserPassword.clear();
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
