
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

public class Board extends Pane implements Initializable {

    @FXML private VBox vbBoard;
    @FXML private MFXButton btnExpandProject;
    @FXML private Label lblTitle;
    @FXML private Label lblDescription;
    @FXML private Label lblStatus;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // no-op
    }

    public Board() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/cr/ac/una/flowfx/view/BoardComponent.fxml"));
        fxmlLoader.setController(this);
        try {
            Pane root = fxmlLoader.load();
            this.getChildren().add(root);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load Board FXML. Check the file path and ensure it exists.", e);
        }
        vbBoard.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    }

    public Board(double width, double height) {
        this();
        vbBoard.setPrefSize(width, height);
    }

    public MFXButton getBtnExpandProject() {
        return btnExpandProject;
    }

    public Label getLblTitle() {
        return lblTitle;
    }

    public Label getLblDescription() {
        return lblDescription;
    }

    public Label getLblStatus() {
        return lblStatus;
    }

    public VBox getVbBoard() {
        return vbBoard;
    }
}
