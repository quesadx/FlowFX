package cr.ac.una.flowfx.util;

import cr.ac.una.flowfx.controller.DockComponent;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public final class DockOverlayManager {
    private static final double EDGE_TRIGGER_PX = 25.0;
    private static final double DOCK_BOTTOM_MARGIN = 16.0;
    private static final double SHOW_START_TRANSLATE_Y = 24.0;
    private static final double HIDE_END_TRANSLATE_Y = 18.0;
    private static final Duration SHOW_DURATION = Duration.millis(500);
    private static final Duration HIDE_DURATION = Duration.millis(300);
    private static final Interpolator OVERLAY_INTERPOLATOR = Interpolator.EASE_BOTH;
    private static final Interpolator DOCK_IN_INTERPOLATOR = Interpolator.SPLINE(0.2, 0.7, 0.2, 1.0);
    private static final Interpolator DOCK_OUT_INTERPOLATOR = Interpolator.SPLINE(0.3, 0.0, 0.7, 1.0);

    private DockOverlayManager() { }

    public static void attach(Stage stage, StackPane rootStack) {
        if (rootStack == null || stage == null) return;

        StackPane overlayRoot = new StackPane();
        overlayRoot.setPickOnBounds(false);
        overlayRoot.setMouseTransparent(false);
        overlayRoot.setManaged(true);
        overlayRoot.setVisible(false);
        overlayRoot.setOpacity(0);

        Pane edgeSentinel = new Pane();
        edgeSentinel.setMouseTransparent(true);
        edgeSentinel.minWidthProperty().bind(rootStack.widthProperty());
        edgeSentinel.minHeightProperty().bind(rootStack.heightProperty());
        edgeSentinel.maxWidthProperty().bind(rootStack.widthProperty());
        edgeSentinel.maxHeightProperty().bind(rootStack.heightProperty());

        DockComponent dock = new DockComponent();
        dock.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        StackPane.setAlignment(dock, Pos.BOTTOM_CENTER);
        StackPane.setMargin(dock, new Insets(0, 0, DOCK_BOTTOM_MARGIN, 0));

        overlayRoot.minWidthProperty().bind(rootStack.widthProperty());
        overlayRoot.minHeightProperty().bind(rootStack.heightProperty());
        overlayRoot.prefWidthProperty().bind(rootStack.widthProperty());
        overlayRoot.prefHeightProperty().bind(rootStack.heightProperty());
        overlayRoot.maxWidthProperty().bind(rootStack.widthProperty());
        overlayRoot.maxHeightProperty().bind(rootStack.heightProperty());

        overlayRoot.getChildren().addAll(edgeSentinel, dock);
        rootStack.getChildren().add(overlayRoot);

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
        rootStack.addEventFilter(MouseEvent.MOUSE_EXITED, e -> {
            if (!dock.isHover()) hideOverlay(overlayRoot, dock);
        });

        overlayRoot.visibleProperty().addListener((obs, ov, nv) -> {
            if (nv) overlayRoot.toFront();
        });

        stage.focusedProperty().addListener((obs, ov, nv) -> {
            if (!nv) hideOverlay(overlayRoot, dock);
        });
    }

    private static void showOverlay(StackPane overlayRoot, Node dockBar) {
        if (overlayRoot.isVisible()) return;
        overlayRoot.setVisible(true);
        overlayRoot.toFront();

        dockBar.setTranslateY(SHOW_START_TRANSLATE_Y);
        dockBar.setOpacity(0);

        Timeline inTl = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(overlayRoot.opacityProperty(), 0, Interpolator.EASE_OUT),
                new KeyValue(dockBar.opacityProperty(), 0, Interpolator.EASE_OUT),
                new KeyValue(dockBar.translateYProperty(), SHOW_START_TRANSLATE_Y, Interpolator.EASE_OUT)
            ),
            new KeyFrame(SHOW_DURATION,
                new KeyValue(overlayRoot.opacityProperty(), 1, OVERLAY_INTERPOLATOR),
                new KeyValue(dockBar.opacityProperty(), 1, DOCK_IN_INTERPOLATOR),
                new KeyValue(dockBar.translateYProperty(), 0, DOCK_IN_INTERPOLATOR)
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
            new KeyFrame(HIDE_DURATION,
                new KeyValue(dockBar.opacityProperty(), 0, OVERLAY_INTERPOLATOR),
                new KeyValue(dockBar.translateYProperty(), HIDE_END_TRANSLATE_Y, DOCK_OUT_INTERPOLATOR)
            )
        );
        outTl.setOnFinished(e -> {
            overlayRoot.setVisible(false);
            overlayRoot.setOpacity(0);
        });
        outTl.play();
    }
}