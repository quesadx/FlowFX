package cr.ac.una.flowfx.controller;

import cr.ac.una.flowfx.util.FlowController;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class DockComponent extends StackPane {
    private static final double HOVER_SCALE = 1.3;
    private static final Duration HOVER_IN_DURATION = Duration.millis(190);
    private static final Duration HOVER_OUT_DURATION = Duration.millis(140);
    private static final Interpolator HOVER_IN_INTERPOLATOR = Interpolator.SPLINE(0.17, 0.67, 0.35, 1);
    private static final Interpolator HOVER_OUT_INTERPOLATOR = Interpolator.EASE_BOTH;

    @FXML private StackPane root;
    @FXML private HBox bar;
    @FXML private MFXButton btnHome;
    @FXML private MFXButton btnProjects;
    @FXML private MFXButton btnAdmin;

    public DockComponent() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/cr/ac/una/flowfx/view/DockComponent.fxml"));
        loader.setController(this);
        try {
            StackPane content = loader.load();
            getChildren().add(content);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load DockComponent FXML", e);
        }

        wireActions();
        addHoverMagnification(btnHome);
        addHoverMagnification(btnProjects);
        addHoverMagnification(btnAdmin);
    }

    private void wireActions() {
        btnHome.setOnAction(e -> FlowController.getInstance().goView("MainView"));
        btnProjects.setOnAction(e -> FlowController.getInstance().goView("ProjectManagementView"));
        btnAdmin.setOnAction(e -> FlowController.getInstance().goView("PersonSignUpView"));
    }

    private void addHoverMagnification(Node node) {
        ScaleTransition scaleIn = new ScaleTransition(HOVER_IN_DURATION, node);
        scaleIn.setToX(HOVER_SCALE);
        scaleIn.setToY(HOVER_SCALE);
        scaleIn.setInterpolator(HOVER_IN_INTERPOLATOR);

        ScaleTransition scaleOut = new ScaleTransition(HOVER_OUT_DURATION, node);
        scaleOut.setToX(1);
        scaleOut.setToY(1);
        scaleOut.setInterpolator(HOVER_OUT_INTERPOLATOR);

        node.setOnMouseEntered(e -> { scaleOut.stop(); scaleIn.playFromStart(); });
        node.setOnMouseExited(e -> { scaleIn.stop(); scaleOut.playFromStart(); });
    }
}