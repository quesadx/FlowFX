package cr.ac.una.flowfx.controller;

import cr.ac.una.flowfx.util.FlowController;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * Dock overlay component that provides quick navigation buttons.
 *
 * <p>This control loads its FXML on construction and wires a small set of
 * actions. It is defensive about FXML injection and logs failures instead of
 * silently failing.</p>
 */
public class DockComponent extends StackPane {

    private static final Logger LOGGER = Logger.getLogger(
        DockComponent.class.getName()
    );

    private static final double HOVER_SCALE = 1.3;
    private static final Duration HOVER_IN_DURATION = Duration.millis(190);
    private static final Duration HOVER_OUT_DURATION = Duration.millis(140);
    private static final Interpolator HOVER_IN_INTERPOLATOR =
        Interpolator.SPLINE(0.17, 0.67, 0.35, 1);
    private static final Interpolator HOVER_OUT_INTERPOLATOR =
        Interpolator.EASE_BOTH;

    @FXML
    private StackPane root;

    @FXML
    private HBox bar;

    @FXML
    private MFXButton btnHome;

    @FXML
    private MFXButton btnProjects;

    @FXML
    private MFXButton btnAdmin;

    /**
     * Loads FXML and configures behavior. Any failure to load the FXML is
     * logged and rethrown as RuntimeException to keep failure modes explicit.
     */
    public DockComponent() {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/cr/ac/una/flowfx/view/DockComponent.fxml")
        );
        loader.setController(this);
        try {
            StackPane content = loader.load();
            getChildren().add(content);
        } catch (Exception e) {
            LOGGER.log(
                Level.SEVERE,
                "Failed to load DockComponent FXML: /cr/ac/una/flowfx/view/DockComponent.fxml",
                e
            );
            throw new RuntimeException("Failed to load DockComponent FXML", e);
        }

        // Wire actions defensively (buttons may be null if FXML changed)
        wireActions();
        if (btnHome != null) addHoverMagnification(btnHome);
        if (btnProjects != null) addHoverMagnification(btnProjects);
        if (btnAdmin != null) addHoverMagnification(btnAdmin);
    }

    private void wireActions() {
        if (btnHome != null) {
            btnHome.setOnAction(e ->
                FlowController.getInstance().goView("MainView")
            );
        }
        if (btnProjects != null) {
            btnProjects.setOnAction(e ->
                FlowController.getInstance().goView("ProjectManagementView")
            );
        }
        if (btnAdmin != null) {
            btnAdmin.setOnAction(e ->
                FlowController.getInstance().goView("PersonSignUpView")
            );
        }
    }

    /**
     * Adds a gentle magnification effect on hover to the provided node.
     *
     * @param node target node (ignored if null)
     */
    private void addHoverMagnification(Node node) {
        if (node == null) return;

        ScaleTransition scaleIn = new ScaleTransition(HOVER_IN_DURATION, node);
        scaleIn.setToX(HOVER_SCALE);
        scaleIn.setToY(HOVER_SCALE);
        scaleIn.setInterpolator(HOVER_IN_INTERPOLATOR);

        ScaleTransition scaleOut = new ScaleTransition(
            HOVER_OUT_DURATION,
            node
        );
        scaleOut.setToX(1);
        scaleOut.setToY(1);
        scaleOut.setInterpolator(HOVER_OUT_INTERPOLATOR);

        node.setOnMouseEntered(e -> {
            try {
                scaleOut.stop();
                scaleIn.playFromStart();
            } catch (Exception ex) {
                LOGGER.log(Level.FINER, "Hover-in animation failed", ex);
            }
        });
        node.setOnMouseExited(e -> {
            try {
                scaleIn.stop();
                scaleOut.playFromStart();
            } catch (Exception ex) {
                LOGGER.log(Level.FINER, "Hover-out animation failed", ex);
            }
        });
    }
}
