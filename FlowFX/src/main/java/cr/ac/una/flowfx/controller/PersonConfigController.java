package cr.ac.una.flowfx.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controller to sign up a new Person. Allows optional id (server generates).
 */
public class PersonConfigController extends Controller implements Initializable {

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

   

    @Override
    public void initialize(URL url, ResourceBundle rb) {
       
    }

    @Override
    public void initialize() {
        // no-op
    }

    @FXML
    private void onKeyPressedTxfPersonId(KeyEvent event) {
    }

    @FXML
    private void onActionBtnCancelChanges(ActionEvent event) {
    }

    @FXML
    private void onActionBtnCommitChanges(ActionEvent event) {
    }

    
}
