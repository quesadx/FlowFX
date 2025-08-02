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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //tpWorkplace.setPrefTileWidth(200);    // Preferred width
        //tpWorkplace.setPrefTileHeight(150);   // Preferred height
    }    

    @Override
    public void initialize() {
        
        Widget widget = new Widget(300, 500);
        tpWorkplace.getChildren().add(widget);

        widget = new Widget(200, 300);
        tpWorkplace.getChildren().add(widget);

        widget = new Widget(200, 500);
        tpWorkplace.getChildren().add(widget);

    }

    public void addDefaultWidgetContent(Widget widget) {
        
    }
    
}
