
package cr.ac.una.flowfx.controller;

import java.net.URL;
import java.util.ResourceBundle;
import cr.ac.una.flowfx.util.AnimationManager;
import cr.ac.una.flowfx.util.AppContext;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class PersonSignUpController extends Controller implements Initializable {

    @FXML private AnchorPane root;
    @FXML private VBox vbCover;
    @FXML private VBox vbPersonSignUpDisplay;
    @FXML private MFXButton btnReturn;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // no-op
    }

    @Override
    public void initialize() {
        // no-op
    }

    @FXML
    private void onActionBtnReturnProjectCreation(ActionEvent event) {
        AnimationManager.hidePopup(vbPersonSignUpDisplay, vbCover);
        Object nav = AppContext.getInstance().get("navigationBar");
        if (nav instanceof VBox) {
            ((VBox) nav).setDisable(false);
        }
    }

    @FXML
    private void onActionBtnSignUpPerson(ActionEvent event) {
        AnimationManager.showPopup(vbPersonSignUpDisplay, vbCover);
        Object nav = AppContext.getInstance().get("navigationBar");
        if (nav instanceof VBox) {
            ((VBox) nav).setDisable(true);
        }
    }
}
