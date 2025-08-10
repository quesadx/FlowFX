package cr.ac.una.flowfx.util;
import javafx.animation.*;
import java.net.URL;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

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
    private static final double DOCK_HEIGHT = 72.0;
    private static final double DOCK_PADDING = 12.0;
    private static final double ICON_SIZE = 36.0;
    private static final double ICON_HOVER_SCALE = 1.25;

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
        overlayRoot.setCache(true);
        overlayRoot.setCacheHint(CacheHint.SPEED);

        // Full-size sentinel pane to detect mouse near edges without blocking clicks
        Pane edgeSentinel = new Pane();
        edgeSentinel.setMouseTransparent(true);
        edgeSentinel.minWidthProperty().bind(rootStack.widthProperty());
        edgeSentinel.minHeightProperty().bind(rootStack.heightProperty());
        edgeSentinel.maxWidthProperty().bind(rootStack.widthProperty());
        edgeSentinel.maxHeightProperty().bind(rootStack.heightProperty());

        // The dock bar itself
        HBox dockBar = buildDockBar();
        StackPane.setAlignment(dockBar, Pos.BOTTOM_CENTER);
        StackPane.setMargin(dockBar, new Insets(0, 0, 16, 0));

    // Ensure overlay tracks the root size
    overlayRoot.minWidthProperty().bind(rootStack.widthProperty());
    overlayRoot.minHeightProperty().bind(rootStack.heightProperty());
    overlayRoot.prefWidthProperty().bind(rootStack.widthProperty());
    overlayRoot.prefHeightProperty().bind(rootStack.heightProperty());
    overlayRoot.maxWidthProperty().bind(rootStack.widthProperty());
    overlayRoot.maxHeightProperty().bind(rootStack.heightProperty());

    overlayRoot.getChildren().addAll(edgeSentinel, dockBar);
        rootStack.getChildren().add(overlayRoot);

    // Show/hide logic: reveal when mouse is near bottom edge; hide otherwise
        rootStack.addEventFilter(MouseEvent.MOUSE_MOVED, e -> {
            double y = e.getY();
            double height = rootStack.getHeight();
            boolean nearBottom = y > height - EDGE_TRIGGER_PX;
            if (nearBottom) {
                showOverlay(overlayRoot, dockBar);
            } else if (!dockBar.isHover()) {
                hideOverlay(overlayRoot, dockBar);
            }
        });

        // Keep overlay on top when visible
        overlayRoot.visibleProperty().addListener((obs, ov, nv) -> {
            if (nv) overlayRoot.toFront();
        });

        // Hide when focus lost (optional guard)
        stage.focusedProperty().addListener((obs, ov, nv) -> {
            if (!nv) hideOverlay(overlayRoot, dockBar);
        });
    }

    private static HBox buildDockBar() {
        HBox bar = new HBox(12);
        bar.getStyleClass().add("fx-dock");
        bar.setPadding(new Insets(DOCK_PADDING));
        bar.setAlignment(Pos.CENTER);
        bar.setFillHeight(false);

        // Frosted rounded background using a StackPane with clip and blur
        StackPane bg = new StackPane();
        bg.setPickOnBounds(false);
        Rectangle clip = new Rectangle();
        clip.arcWidthProperty().set(24);
        clip.arcHeightProperty().set(24);
        clip.widthProperty().bind(Bindings.createDoubleBinding(
            () -> bar.getWidth() + DOCK_PADDING * 2,
            bar.widthProperty()
        ));
        clip.heightProperty().bind(Bindings.createDoubleBinding(
            () -> DOCK_HEIGHT + DOCK_PADDING * 2,
            bar.heightProperty()
        ));
        bg.setClip(clip);
        bg.setBackground(new Background(new BackgroundFill(Color.web("#ffffff", 0.35), new CornerRadii(16), Insets.EMPTY)));
        bg.setEffect(new BoxBlur(12, 12, 2));
        bg.setMouseTransparent(true);

        // Container to layer background behind icons
        StackPane dockContainer = new StackPane(bg, bar);
        StackPane.setAlignment(bg, Pos.CENTER);

        HBox root = new HBox(dockContainer);
        root.setAlignment(Pos.CENTER);
        root.setPickOnBounds(false);

        // Populate with nav items migrated from PrincipalView left bar
        bar.getChildren().addAll(
            dockItem("Home", "/cr/ac/una/flowfx/resources/icons/lucide--home.png", () -> FlowController.getInstance().goView("MainView")),
            dockItem("Projects", "/cr/ac/una/flowfx/resources/icons/lucide--folder-cog.png", () -> FlowController.getInstance().goView("ProjectManagementView")),
            dockItem("Admin", "/cr/ac/una/flowfx/resources/icons/lucide--book-user.png", () -> FlowController.getInstance().goView("PersonSignUpView"))
        );

        // Wrap in container with margins
        HBox outer = new HBox(root);
        outer.setAlignment(Pos.CENTER);
        outer.setMouseTransparent(false);
        outer.setPickOnBounds(false);
        outer.setPadding(new Insets(0));
        outer.setMinHeight(DOCK_HEIGHT + DOCK_PADDING * 2);
        outer.setMaxHeight(DOCK_HEIGHT + DOCK_PADDING * 2);
        outer.setPrefHeight(DOCK_HEIGHT + DOCK_PADDING * 2);
        return outer;
    }

    private static Node dockItem(String title, String iconPath, Runnable action) {
        StackPane item = new StackPane();
        item.getStyleClass().add("fx-dock-item");
        item.setCursor(Cursor.HAND);

        ImageView icon = new ImageView();
        URL iconUrl = DockOverlayManager.class.getResource(iconPath);
        if (iconUrl != null) {
            icon.setImage(new Image(iconUrl.toExternalForm(), false));
        } else {
            // Soft fallback if resource not found; small rounded rectangle as placeholder
            icon.setImage(new Image("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8Xw8AAh0B9Qf0bA0AAAAASUVORK5CYII=", false));
        }
        icon.setFitWidth(ICON_SIZE);
        icon.setFitHeight(ICON_SIZE);
        icon.setPreserveRatio(true);
        icon.setSmooth(true);
        icon.setCache(true);
        icon.setCacheHint(CacheHint.SPEED);

        Label label = new Label(title);
        label.getStyleClass().add("fx-dock-label");
        label.setTextFill(Color.WHITE);
        label.setMouseTransparent(true);
        label.setOpacity(0);
        StackPane.setAlignment(label, Pos.TOP_CENTER);
        StackPane.setMargin(label, new Insets(-18, 0, 0, 0));

        item.getChildren().addAll(icon, label);

        // Hover magnification and label fade
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(160), item);
        scaleIn.setToX(ICON_HOVER_SCALE);
        scaleIn.setToY(ICON_HOVER_SCALE);
        scaleIn.setInterpolator(Interpolator.SPLINE(0.17, 0.67, 0.35, 1));

        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(150), item);
        scaleOut.setToX(1);
        scaleOut.setToY(1);
        scaleOut.setInterpolator(Interpolator.EASE_BOTH);

        FadeTransition labelIn = new FadeTransition(Duration.millis(160), label);
        labelIn.setToValue(1);

        FadeTransition labelOut = new FadeTransition(Duration.millis(120), label);
        labelOut.setToValue(0);

        item.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            scaleOut.stop();
            labelOut.stop();
            labelIn.playFromStart();
            scaleIn.playFromStart();
        });
        item.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            scaleIn.stop();
            labelIn.stop();
            labelOut.playFromStart();
            scaleOut.playFromStart();
        });
        item.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            // Click bounce
            ScaleTransition bounceDown = new ScaleTransition(Duration.millis(90), item);
            bounceDown.setToX(0.9);
            bounceDown.setToY(0.9);
            ScaleTransition bounceUp = new ScaleTransition(Duration.millis(160), item);
            bounceUp.setToX(1.08);
            bounceUp.setToY(1.08);
            bounceUp.setInterpolator(Interpolator.SPLINE(0.2, 0.7, 0.2, 1));
            SequentialTransition seq = new SequentialTransition(bounceDown, bounceUp);
            seq.setOnFinished(ev -> action.run());
            seq.play();
        });

        return item;
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
