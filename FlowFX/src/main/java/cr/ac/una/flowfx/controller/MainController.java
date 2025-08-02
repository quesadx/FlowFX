/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cr.ac.una.flowfx.controller;

import java.net.URL;
import java.util.ResourceBundle;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author quesadx
 */
public class MainController extends Controller implements Initializable {

    @FXML
    private AnchorPane root;

    @FXML
    private GridPane gpDashboard;

    @FXML
    private VBox vbCover, vbLogInDisplay;

    @FXML
    private MFXButton btnLogIn;


    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }    

    @Override
    public void initialize() {
        vbCover.setEffect(new javafx.scene.effect.GaussianBlur());
        //imgBackground.fitHeightProperty().bind(root.heightProperty());
        //imgBackground.fitWidthProperty().bind(root.widthProperty());
    }


    public void addDefaultWidgetContent(Widget widget) {
        
    }

    @FXML
    private void onActionBtnLogIn(ActionEvent event) {
        vbLogInDisplay.setVisible(false);
        vbLogInDisplay.setManaged(false);
        vbCover.setEffect(null);
    }
    
}
