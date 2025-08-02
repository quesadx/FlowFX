/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cr.ac.una.flowfx.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author quesadx
 */
public class Board extends Pane implements Initializable {
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //if (hbHandle != null) {
            //hbHandle.setStyle("-fx-cursor: hand;");
        //}

    }
    
    public Board(){
        FXMLLoader fxmlLoader = new FXMLLoader(
            getClass().getResource("/cr/ac/una/flowfx/view/BoardComponent.fxml")
        );
        fxmlLoader.setController(this);
        try {
            Pane root = fxmlLoader.load();
            this.getChildren().add(root);
        } catch (IOException exception) {
            throw new RuntimeException(
                "Failed to load Board FXML. Check the file path and ensure it exists.",
                exception
            );
        }

        //this.vbWidget.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE); // Allow widget to grow
    }

}
