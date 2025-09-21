package cr.ac.una.flowfx.controller;

import cr.ac.una.flowfx.util.AppContext;
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

    private static final double HOVER_SCALE = 1.1;
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

    @FXML
    private MFXButton btnUserConfig;

    @FXML
    private MFXButton btnCreativeZone;

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
        if (btnUserConfig != null) addHoverMagnification(btnUserConfig);

        // Expose the dock bar for global enable/disable and set initial state
        if (bar != null) {
            try {
                AppContext.getInstance().set("dockBar", bar);
                Object user = AppContext.getInstance().get("user");
                bar.setDisable(user == null);
                
                // Configure admin button visibility based on user role
                configureAdminButtonVisibility(user);
            } catch (Exception ex) {
                LOGGER.log(Level.FINER, "Failed to goofy ahh enviar dockBar to AppContext, arreglen esto!!!!", ex);
            }
        }
    }

    /**
     * Configures the admin button visibility based on current user's admin status.
     */
    private void configureAdminButtonVisibility(Object user) {
        if (btnAdmin == null) {
            LOGGER.warning("btnAdmin is null - cannot configure admin button visibility");
            return;
        }
        
        LOGGER.info("Configuring admin button visibility. User object: " + 
            (user != null ? user.getClass().getSimpleName() : "null"));
        
        boolean isAdmin = false;
        if (user instanceof cr.ac.una.flowfx.model.PersonDTO person) {
            Character adminFlag = person.getIsAdmin();
            isAdmin = adminFlag != null && Character.toUpperCase(adminFlag) == 'Y';
            LOGGER.info("User admin status: " + isAdmin + " (flag: " + adminFlag + ") for user ID: " + person.getId());
            LOGGER.info("User details: " + person.getFirstName() + " " + person.getLastName());
        } else {
            LOGGER.warning("User object is not PersonDTO: " + (user != null ? user.toString() : "null"));
        }
        
        btnAdmin.setVisible(isAdmin);
        btnAdmin.setManaged(isAdmin); // Also control layout participation
        
        LOGGER.info("Admin button configured: visible=" + isAdmin + ", managed=" + isAdmin);
    }

    /**
     * Updates the dock component state based on current user context.
     * Call this method when user context changes (login/logout/role change).
     */
    public void updateDockState() {
        Object user = AppContext.getInstance().get("user");
        if (bar != null) {
            bar.setDisable(user == null);
        }
        configureAdminButtonVisibility(user);
        LOGGER.fine("Dock state updated");
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
        if (btnUserConfig != null) {
            btnUserConfig.setOnAction(e ->
                FlowController.getInstance().goView("PersonConfigView")
            );
        }
        if (btnCreativeZone != null) {
            btnCreativeZone.setOnAction(e ->
                FlowController.getInstance().goView("CreativeZoneView")
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
