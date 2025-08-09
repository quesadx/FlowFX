/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cr.ac.una.flowfx.controller;

import java.net.URL;
import java.util.ResourceBundle;

import cr.ac.una.flowfx.util.AnimationManager;
import cr.ac.una.flowfx.util.AppContext;
import cr.ac.una.flowfx.util.FlowController;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author quesadx
 */
public class PersonSignUpController extends Controller implements Initializable {

    @FXML
    private AnchorPane root;
    @FXML
    private VBox vbCover, vbPersonSignUpDisplay;
    @FXML
    private MFXButton btnReturn;

    // TODO: CENTER HE ELEMENTS ON THE MAIN VBOX ################################################

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @Override
    public void initialize() {

    }

    @FXML
    private void onActionBtnReturnProjectCreation(ActionEvent event) {
        AnimationManager.hidePopup(vbPersonSignUpDisplay, vbCover);
        Object nav = AppContext.getInstance().get("NavigationBar");
        if (nav instanceof VBox) ((VBox) nav).setDisable(false);
    }

    @FXML
    private void onActionBtnSignUpPerson(ActionEvent event) {
        AnimationManager.showPopup(vbPersonSignUpDisplay, vbCover);
        Object nav = AppContext.getInstance().get("NavigationBar");
        if (nav instanceof VBox) ((VBox) nav).setDisable(true);
    }
}
