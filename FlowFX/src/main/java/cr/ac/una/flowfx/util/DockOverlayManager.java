package cr.ac.una.flowfx.util;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.layout.Region;
import javafx.util.Duration;
import cr.ac.una.flowfx.controller.DockComponent;

/**
 * Global, reusable overlay that renders a macOS-like dock on top of all views.
 *
 * Features:
 * - Renders in a StackPane layer above the app root; no changes to individual views.
 * - Auto-shows when the cursor nears the bottom edge (or side), auto-hides on leave.
 * - Frosted/blurred, translucent background with rounded corners and shadow.
 * - Subtle bounce-in/out animations and item hover magnification.
 */
public final class DockOverlayManager {
    private static final double EDGE_TRIGGER_PX = 25.0; // distance from screen border to trigger
    private static final double DOCK_HEIGHT = 64.0;

    private DockOverlayManager() {}

    public static void attach(Stage stage, StackPane rootStack) {
        if (rootStack == null || stage == null) return;

        // Container that holds the dock; kept invisible/collapsed until needed
        StackPane overlayRoot = new StackPane();
        overlayRoot.setPickOnBounds(false); // let events pass through when dock hidden
    overlayRoot.setMouseTransparent(false);
    // Managed so StackPane lays it out to full size
    overlayRoot.setManaged(true);
        overlayRoot.setVisible(false);
        overlayRoot.setOpacity(0);
        

        // Full-size sentinel pane to detect mouse near edges without blocking clicks
        Pane edgeSentinel = new Pane();
        edgeSentinel.setMouseTransparent(true);
        edgeSentinel.minWidthProperty().bind(rootStack.widthProperty());
        edgeSentinel.minHeightProperty().bind(rootStack.heightProperty());
        edgeSentinel.maxWidthProperty().bind(rootStack.widthProperty());
        edgeSentinel.maxHeightProperty().bind(rootStack.heightProperty());

    // The dock bar itself (FXML component)
    DockComponent dock = new DockComponent();
    // Avoid being stretched: keep to preferred size so alignment works
    dock.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
    StackPane.setAlignment(dock, Pos.BOTTOM_CENTER);
    StackPane.setMargin(dock, new Insets(0, 0, 16, 0));

    // Ensure overlay tracks the root size
    overlayRoot.minWidthProperty().bind(rootStack.widthProperty());
    overlayRoot.minHeightProperty().bind(rootStack.heightProperty());
    overlayRoot.prefWidthProperty().bind(rootStack.widthProperty());
    overlayRoot.prefHeightProperty().bind(rootStack.heightProperty());
    overlayRoot.maxWidthProperty().bind(rootStack.widthProperty());
    overlayRoot.maxHeightProperty().bind(rootStack.heightProperty());

    overlayRoot.getChildren().addAll(edgeSentinel, dock);
        rootStack.getChildren().add(overlayRoot);

    // Show/hide logic: reveal when mouse is near bottom edge; hide otherwise
        rootStack.addEventFilter(MouseEvent.MOUSE_MOVED, e -> {
            double y = e.getY();
            double height = rootStack.getHeight();
            boolean nearBottom = y > height - EDGE_TRIGGER_PX;
            if (nearBottom) {
                showOverlay(overlayRoot, dock);
            } else if (!dock.isHover()) {
                hideOverlay(overlayRoot, dock);
            }
        });

        // Keep overlay on top when visible
        overlayRoot.visibleProperty().addListener((obs, ov, nv) -> {
            if (nv) overlayRoot.toFront();
        });

        // Hide when focus lost (optional guard)
        stage.focusedProperty().addListener((obs, ov, nv) -> {
            if (!nv) hideOverlay(overlayRoot, dock);
        });
    }

    private static void showOverlay(StackPane overlayRoot, Node dockBar) {
        if (overlayRoot.isVisible()) return;
        overlayRoot.setVisible(true);
        overlayRoot.toFront();

        dockBar.setTranslateY(24);
        dockBar.setOpacity(0);
        Timeline inTl = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(overlayRoot.opacityProperty(), 0, Interpolator.EASE_OUT),
                new KeyValue(dockBar.opacityProperty(), 0, Interpolator.EASE_OUT),
                new KeyValue(dockBar.translateYProperty(), 24, Interpolator.EASE_OUT)
            ),
            new KeyFrame(Duration.millis(320),
                new KeyValue(overlayRoot.opacityProperty(), 1, Interpolator.EASE_BOTH),
                new KeyValue(dockBar.opacityProperty(), 1, Interpolator.SPLINE(0.2, 0.7, 0.2, 1)),
                new KeyValue(dockBar.translateYProperty(), 0, Interpolator.SPLINE(0.2, 0.7, 0.2, 1))
            )
        );
        inTl.play();
    }

    private static void hideOverlay(StackPane overlayRoot, Node dockBar) {
        if (!overlayRoot.isVisible()) return;
        Timeline outTl = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(dockBar.opacityProperty(), 1, Interpolator.EASE_OUT),
                new KeyValue(dockBar.translateYProperty(), 0, Interpolator.EASE_OUT)
            ),
            new KeyFrame(Duration.millis(220),
                new KeyValue(dockBar.opacityProperty(), 0, Interpolator.EASE_BOTH),
                new KeyValue(dockBar.translateYProperty(), 18, Interpolator.SPLINE(0.3, 0, 0.7, 1))
            )
        );
        outTl.setOnFinished(e -> {
            overlayRoot.setVisible(false);
            overlayRoot.setOpacity(0);
        });
        outTl.play();
    }
}
