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
import javafx.animation.Animation;
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
public class ProjectManagementController extends Controller implements Initializable {

    @FXML
    private AnchorPane root;
    @FXML
    private TilePane tpProjects;
    @FXML
    private VBox vbCover, vbProjectCreationDisplay;
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

        Board board;
        for(int i = 0; i < 3; i++) {
            board = new Board();
            board.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE); // Allow board to grow
            board.getBtnExpandProject().setOnAction(e -> {
                FlowController.getInstance().goView("ProjectExpandView");
                Object nav = AppContext.getInstance().get("NavigationBar");
                if (nav instanceof VBox) ((VBox) nav).setDisable(true);
            });
            tpProjects.getChildren().add(board);
        }
    }

    @FXML
    private void onActionBtnReturnProjectCreation(ActionEvent event) {
        AnimationManager.hidePopup(vbProjectCreationDisplay, vbCover);
        Object nav = AppContext.getInstance().get("NavigationBar");
        if (nav instanceof VBox) ((VBox) nav).setDisable(false);
    }

    @FXML
    private void onActionCreateProject(ActionEvent event) {
        AnimationManager.showPopup(vbProjectCreationDisplay, vbCover);
        Object nav = AppContext.getInstance().get("NavigationBar");
        if (nav instanceof VBox) ((VBox) nav).setDisable(true);
    }

}
