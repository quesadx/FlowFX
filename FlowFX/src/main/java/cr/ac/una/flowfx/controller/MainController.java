/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cr.ac.una.flowfx.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
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
    private TilePane tpWorkplace;

    // Drag-related fields
    private Widget draggedWidget;
    private double dragOffsetX;
    private double dragOffsetY;
    private double initialX;
    private double initialY;
    private int draggedIndex;
    private Timeline wiggleTimeline;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @Override
    public void initialize() {
        // Fill the TilePane with Widget blocks
        for (int i = 0; i < 5; i++) {
            Widget widget = new Widget();
            tpWorkplace.getChildren().add(widget);
        }
    }
    
}
