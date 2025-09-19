package cr.ac.una.flowfx.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * Reusable Board component wrapper.
 *
 * <p>This class loads a small FXML fragment (BoardComponent.fxml) and exposes
 * a handful of controls for external wiring. It intentionally keeps behavior
 * minimal and defensive: failures to load the FXML are logged and rethrown as
 * a runtime exception so the caller is aware that the UI could not be created.</p>
 */
public class Board extends Pane implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(
        Board.class.getName()
    );

    @FXML
    private VBox vbBoard;

    @FXML
    private MFXButton btnExpandProject, btnDeleteProject;

    @FXML
    private Label lblTitle;

    @FXML
    private Label lblDescription;

    @FXML
    private Label lblStatus;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Intentionally left blank; FXML controller initialization happens via loader.
    }

    /**
     * Default constructor that loads the FXML fragment and attaches it as the
     * single child of this container.
     */
    public Board() {
        //btnDeleteProject.setVisible(false);
        FXMLLoader fxmlLoader = new FXMLLoader(
            getClass().getResource("/cr/ac/una/flowfx/view/BoardComponent.fxml")
        );
        fxmlLoader.setController(this);
        try {
            Pane root = fxmlLoader.load();
            this.getChildren().add(root);
        } catch (IOException e) {
            LOGGER.log(
                Level.SEVERE,
                "Failed to load Board FXML: /cr/ac/una/flowfx/view/BoardComponent.fxml",
                e
            );
            throw new RuntimeException(
                "Failed to load Board FXML. Check the file path and ensure it exists.",
                e
            );
        } catch (RuntimeException e) {
            // Re-throw after logging to preserve original behavior but provide context.
            LOGGER.log(Level.SEVERE, "Unexpected error loading Board FXML", e);
            throw e;
        }

        if (vbBoard != null) {
            vbBoard.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        } else {
            LOGGER.log(
                Level.WARNING,
                "vbBoard was not injected from FXML; layout sizing skipped."
            );
        }
    }

    /**
     * Convenience constructor that also sets a preferred size for the internal
     * board container.
     *
     * @param width preferred width
     * @param height preferred height
     */
    public Board(double width, double height) {
        this();
        if (vbBoard != null) {
            vbBoard.setPrefSize(width, height);
        }
    }

    public MFXButton getBtnExpandProject() {
        return btnExpandProject;
    }

    public MFXButton getBtnDeleteProject() {
        return btnDeleteProject;
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
