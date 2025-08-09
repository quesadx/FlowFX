/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cr.ac.una.flowfx.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
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

     @FXML
     private VBox vbBoard;
     @FXML
     private MFXButton btnExpandProject;
     @FXML
     private Label lblTitle, lblDescription, lblStatus;


    @Override
    public void initialize(URL url, ResourceBundle rb) {

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

        this.vbBoard.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE); // Allow widget to grow
    }

    public Board(double width, double height) {
        this();
        this.vbBoard.setPrefSize(width, height);
    }

    public void setBtnExpandProject(MFXButton btnExpandProject) {
        this.btnExpandProject = btnExpandProject;
    }

    public Label getLblTitle() {
        return lblTitle;
    }

    public void setLblTitle(Label lblTitle) {
        this.lblTitle = lblTitle;
    }

    public Label getLblDescription() {
        return lblDescription;
    }

    public void setLblDescription(Label lblDescription) {
        this.lblDescription = lblDescription;
    }

    public Label getLblStatus() {
        return lblStatus;
    }

    public void setLblStatus(Label lblStatus) {
        this.lblStatus = lblStatus;
    }

    public MFXButton getBtnExpandProject() {
        return btnExpandProject;
    }

    public VBox getVbBoard() {
        return vbBoard;
    }

    public void setVbBoard(VBox vbBoard) {
        this.vbBoard = vbBoard;
    }

}
