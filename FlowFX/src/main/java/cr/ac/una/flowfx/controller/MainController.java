/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
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

/**
 * FXML Controller class
 *
 * @author quesadx
 */
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
        System.out.println("Inicializando MainController");
        vbCover.setEffect(new javafx.scene.effect.GaussianBlur());
        AnimationManager.showPopup(vbLogInDisplay, vbCover);
        Object nav = AppContext.getInstance().get("NavigationBar");
        if (nav instanceof VBox) ((VBox) nav).setDisable(!userLoggedIn);
    }

    @Override
    public void initialize() {
        
    }

    @FXML
    private void onActionBtnLogIn(ActionEvent event) {
        String username = txfUsername.getText().trim();
        String password = psfUserPassword.getText();
        PersonService personService = new PersonService();
        Respuesta r = personService.validateCredentials(username, password);
        if (r.getEstado()) {
            user = (PersonDTO) r.getResultado("Person");
            AppContext.getInstance().set("user", user);
            userLoggedIn = true;
            AnimationManager.hidePopup(vbLogInDisplay, vbCover);
            Object nav = AppContext.getInstance().get("NavigationBar");
            if (nav instanceof VBox) {
                ((VBox) nav).setDisable(!userLoggedIn);
            }
        } else {
            new Mensaje().showModal(Alert.AlertType.ERROR, "Login", root.getScene().getWindow(), r.getMensaje());
        }
    }

    
    @FXML
    private void onMouseClickedLblSignUp(MouseEvent event) {
        AnimationManager.hidePopup(vbLogInDisplay);
        AnimationManager.showPopup(vbSignUpDisplay, vbCover);
    }
    
    @FXML
    private void onMouseClickedLblPasswordRecovery(MouseEvent event) {
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
        PersonService personService = new PersonService();
        Respuesta r = personService.create(newUser);
        if (Boolean.TRUE.equals(r.getEstado())) {
            new Mensaje().showModal(Alert.AlertType.INFORMATION, "Registro", root.getScene().getWindow(), "Usuario registrado correctamente.");
            AnimationManager.hidePopup(vbSignUpDisplay);
            AnimationManager.showPopup(vbLogInDisplay, vbCover);
            clearSignUpFields();
        } else {
            new Mensaje().showModal(Alert.AlertType.ERROR, "Registro", root.getScene().getWindow(), r.getMensaje());
        }
    }

    private PersonDTO extractPersonFromSignUp() {
        try {
            Long id = Long.parseLong(txfPersonId.getText().trim());
            String firstName = txfPersonFirstName.getText().trim();
            String lastName = txfPersonLastName.getText().trim();
            String email = txfPersonEmail.getText().trim();
            String username = txfPersonUsername.getText().trim();
            String password = pswPersonPassword.getText();
            char status = cbIsActive.isSelected() ? 'A' : 'I';
            char isAdmin = cbIsAdmin.isSelected() ? 'Y' : 'N';
            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()
                || username.isEmpty() || password.isEmpty()) {
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

    private PersonDTO extractFromLogIn() {
        // Query to db goes here
        return null;
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
