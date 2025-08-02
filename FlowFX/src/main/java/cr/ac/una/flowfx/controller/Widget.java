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
public class Widget extends Pane implements Initializable {
    
    @FXML
    private HBox hbHandle;
    @FXML
    private VBox vboxContainer;
    @FXML
    private VBox vbWidget;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (hbHandle != null) {
            hbHandle.setStyle("-fx-cursor: hand;");
        }

    }
    
    public Widget(){
        FXMLLoader fxmlLoader = new FXMLLoader(
            getClass().getResource("/cr/ac/una/flowfx/view/WidgetComponent.fxml")
        );
        fxmlLoader.setController(this);
        try {
            Pane root = fxmlLoader.load();
            this.getChildren().add(root);
        } catch (IOException exception) {
            throw new RuntimeException(
                "Failed to load Widget FXML. Check the file path and ensure it exists.",
                exception
            );
        }

        this.vbWidget.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE); // Allow widget to grow
    }

    public Widget(double width, double height) {
        this();
        this.vbWidget.setPrefSize(width, height);
    }

    public HBox getHbHandle() {
        return hbHandle;
    }

    public void setHbHandle(HBox hbHandle) {
        this.hbHandle = hbHandle;
    }

    public VBox getVboxContainer() {
        return vboxContainer;
    }

    public void setVboxContainer(VBox vboxContainer) {
        this.vboxContainer = vboxContainer;
    }

    public void setContainerChildren(Node child) {
        this.vboxContainer.getChildren().clear();
        this.vboxContainer.getChildren().add(child);
    }
}
