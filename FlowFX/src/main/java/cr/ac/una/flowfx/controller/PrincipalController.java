
package cr.ac.una.flowfx.controller;

import java.net.URL;
import java.util.ResourceBundle;
import io.github.palexdev.materialfx.controls.MFXButton;
import cr.ac.una.flowfx.util.AppContext;
import cr.ac.una.flowfx.util.FlowController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PrincipalController extends Controller implements Initializable {

    @FXML private BorderPane root;
    @FXML public MFXButton btnAdminSignUp;
    @FXML public MFXButton btnHome;
    @FXML public MFXButton btnProjects;
    @FXML private ImageView imgSettings;
    @FXML private ImageView imgHome;
    @FXML private ImageView imgProjects;
    @FXML private MFXButton btnClose;
    @FXML private MFXButton btnMaximize;
    @FXML private MFXButton btnMinimize;
    @FXML private VBox vbLateralHandlebar;
    @FXML private VBox vbNavigationBar;

    private static final int RESIZE_MARGIN = 8;
    private boolean resizingLeft;
    private boolean resizingRight;
    private boolean resizingTop;
    private boolean resizingBottom;
    private double dragOffsetX;
    private double dragOffsetY;
    private double mousePressedX;
    private double mousePressedY;
    private double windowPressedX;
    private double windowPressedY;
    private double windowPressedWidth;
    private double windowPressedHeight;
    private double xOffset;
    private double yOffset;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupWindowResizeHandlers();
        setupWindowDragHandlers();
        vbLateralHandlebar.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        vbLateralHandlebar.setOnMouseDragged(event -> {
            Stage stage = (Stage) root.getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
        btnClose.setFocusTraversable(false);
        btnMaximize.setFocusTraversable(false);
        btnMinimize.setFocusTraversable(false);
        btnHome.setStyle("-fx-background-color: #4c6afe;");
        btnAdminSignUp.setStyle("-fx-background-color: #ffffff;");
        btnProjects.setStyle("-fx-background-color: #ffffff;");
        imgHome.setEffect(new ColorAdjust(0, -1, 1, 0));
        javafx.application.Platform.runLater(() -> {
            if (root.getScene() != null) {
                FlowController.getInstance().goView("MainView");
                btnHome.requestFocus();
            }
        });
        AppContext.getInstance().set("navigationBar", vbNavigationBar);
        vbNavigationBar.setDisable(true);
    }

    @Override
    public void initialize() {
        // no-op
    }

    @FXML
    private void onActionBtnAdminSignUp(ActionEvent event) {
        btnAdminSignUp.setStyle("-fx-background-color: #4c6afe;");
        btnHome.setStyle("-fx-background-color: #ffffff;");
        btnProjects.setStyle("-fx-background-color: #ffffff;");
        imgSettings.setEffect(new ColorAdjust(0, -1, 1, 0));
        imgHome.setEffect(null);
        imgProjects.setEffect(null);
        FlowController.getInstance().goView("PersonSignUpView");
    }

    @FXML
    private void onActionBtnHome(ActionEvent event) {
        btnHome.setStyle("-fx-background-color: #4c6afe;");
        btnAdminSignUp.setStyle("-fx-background-color: #ffffff;");
        btnProjects.setStyle("-fx-background-color: #ffffff;");
        imgHome.setEffect(new ColorAdjust(0, -1, 1, 0));
        imgSettings.setEffect(null);
        imgProjects.setEffect(null);
        FlowController.getInstance().goView("MainView");
    }

    @FXML
    private void onActionBtnProjects(ActionEvent event) {
        btnProjects.setStyle("-fx-background-color: #4c6afe;");
        btnAdminSignUp.setStyle("-fx-background-color: #ffffff;");
        btnHome.setStyle("-fx-background-color: #ffffff;");
        imgProjects.setEffect(new ColorAdjust(0, -1, 1, 0));
        imgSettings.setEffect(null);
        imgHome.setEffect(null);
        FlowController.getInstance().goView("ProjectManagementView");
    }

    @FXML
    private void onActionBtnClose(ActionEvent event) {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onActionBtnMaximize(ActionEvent event) {
        ((Stage) root.getScene().getWindow()).setFullScreen(true);
    }

    @FXML
    private void onActionBtnMinimize(ActionEvent event) {
        ((Stage) root.getScene().getWindow()).setIconified(true);
    }

    private void setupWindowResizeHandlers() {
        root.setOnMouseMoved(this::handleMouseMovedForResize);
        root.setOnMousePressed(this::handleMousePressedForResize);
        root.setOnMouseDragged(this::handleMouseDraggedForResize);
        root.setOnMouseReleased(this::handleMouseReleasedForResize);
    }

    private void handleMouseMovedForResize(javafx.scene.input.MouseEvent event) {
        double mouseX = event.getX();
        double mouseY = event.getY();
        double width = root.getWidth();
        double height = root.getHeight();
        boolean onLeftEdge = mouseX >= 0 && mouseX < RESIZE_MARGIN;
        boolean onRightEdge = mouseX > width - RESIZE_MARGIN && mouseX <= width;
        boolean onTopEdge = mouseY >= 0 && mouseY < RESIZE_MARGIN;
        boolean onBottomEdge = mouseY > height - RESIZE_MARGIN && mouseY <= height;
        if (onLeftEdge && onTopEdge) {
            root.setCursor(javafx.scene.Cursor.NW_RESIZE);
        } else if (onRightEdge && onTopEdge) {
            root.setCursor(javafx.scene.Cursor.NE_RESIZE);
        } else if (onLeftEdge && onBottomEdge) {
            root.setCursor(javafx.scene.Cursor.SW_RESIZE);
        } else if (onRightEdge && onBottomEdge) {
            root.setCursor(javafx.scene.Cursor.SE_RESIZE);
        } else if (onLeftEdge) {
            root.setCursor(javafx.scene.Cursor.W_RESIZE);
        } else if (onRightEdge) {
            root.setCursor(javafx.scene.Cursor.E_RESIZE);
        } else if (onTopEdge) {
            root.setCursor(javafx.scene.Cursor.N_RESIZE);
        } else if (onBottomEdge) {
            root.setCursor(javafx.scene.Cursor.S_RESIZE);
        } else {
            root.setCursor(javafx.scene.Cursor.DEFAULT);
        }
    }

    private void handleMousePressedForResize(javafx.scene.input.MouseEvent event) {
        double mouseX = event.getX();
        double mouseY = event.getY();
        double width = root.getWidth();
        double height = root.getHeight();
        resizingLeft = mouseX >= 0 && mouseX < RESIZE_MARGIN;
        resizingRight = mouseX > width - RESIZE_MARGIN && mouseX <= width;
        resizingTop = mouseY >= 0 && mouseY < RESIZE_MARGIN;
        resizingBottom = mouseY > height - RESIZE_MARGIN && mouseY <= height;
        if (resizingLeft || resizingRight || resizingTop || resizingBottom) {
            mousePressedX = event.getScreenX();
            mousePressedY = event.getScreenY();
            Stage stage = (Stage) root.getScene().getWindow();
            windowPressedX = stage.getX();
            windowPressedY = stage.getY();
            windowPressedWidth = stage.getWidth();
            windowPressedHeight = stage.getHeight();
            event.consume();
        }
    }

    private void handleMouseDraggedForResize(javafx.scene.input.MouseEvent event) {
        Stage stage = (Stage) root.getScene().getWindow();
        boolean resized = false;
        double minWidth = stage.getMinWidth();
        double minHeight = stage.getMinHeight();
        double deltaX = event.getScreenX() - mousePressedX;
        double deltaY = event.getScreenY() - mousePressedY;
        double newX = windowPressedX;
        double newY = windowPressedY;
        double newWidth = windowPressedWidth;
        double newHeight = windowPressedHeight;
        if (resizingLeft) {
            newWidth = windowPressedWidth - deltaX;
            newX = windowPressedX + deltaX;
            if (newWidth < minWidth) {
                newX -= (minWidth - newWidth);
                newWidth = minWidth;
            }
            resized = true;
        }
        if (resizingRight) {
            newWidth = windowPressedWidth + deltaX;
            if (newWidth < minWidth) {
                newWidth = minWidth;
            }
            resized = true;
        }
        if (resizingTop) {
            newHeight = windowPressedHeight - deltaY;
            newY = windowPressedY + deltaY;
            if (newHeight < minHeight) {
                newY -= (minHeight - newHeight);
                newHeight = minHeight;
            }
            resized = true;
        }
        if (resizingBottom) {
            newHeight = windowPressedHeight + deltaY;
            if (newHeight < minHeight) {
                newHeight = minHeight;
            }
            resized = true;
        }
        if (resized) {
            stage.setX(newX);
            stage.setY(newY);
            stage.setWidth(newWidth);
            stage.setHeight(newHeight);
            if ((resizingLeft && resizingTop) || (resizingRight && resizingBottom)) {
                root.setCursor(javafx.scene.Cursor.NW_RESIZE);
            } else if ((resizingRight && resizingTop) || (resizingLeft && resizingBottom)) {
                root.setCursor(javafx.scene.Cursor.NE_RESIZE);
            } else if (resizingLeft || resizingRight) {
                root.setCursor(resizingLeft ? javafx.scene.Cursor.W_RESIZE : javafx.scene.Cursor.E_RESIZE);
            } else if (resizingTop || resizingBottom) {
                root.setCursor(resizingTop ? javafx.scene.Cursor.N_RESIZE : javafx.scene.Cursor.S_RESIZE);
            }
        }
        event.consume();
    }

    private void handleMouseReleasedForResize(javafx.scene.input.MouseEvent event) {
        resizingLeft = false;
        resizingRight = false;
        resizingTop = false;
        resizingBottom = false;
        root.setCursor(javafx.scene.Cursor.DEFAULT);
    }

    private void setupWindowDragHandlers() {
        vbLateralHandlebar.setOnMousePressed(event -> {
            if (!(resizingLeft || resizingRight || resizingTop || resizingBottom)) {
                Stage stage = (Stage) root.getScene().getWindow();
                dragOffsetX = event.getScreenX() - stage.getX();
                dragOffsetY = event.getScreenY() - stage.getY();
                vbLateralHandlebar.setCursor(javafx.scene.Cursor.MOVE);
            }
        });
        vbLateralHandlebar.setOnMouseDragged(event -> {
            if (!(resizingLeft || resizingRight || resizingTop || resizingBottom)) {
                Stage stage = (Stage) root.getScene().getWindow();
                stage.setX(event.getScreenX() - dragOffsetX);
                stage.setY(event.getScreenY() - dragOffsetY);
            }
        });
        vbLateralHandlebar.setOnMouseReleased(event -> {
            if (!(resizingLeft || resizingRight || resizingTop || resizingBottom)) {
                vbLateralHandlebar.setCursor(javafx.scene.Cursor.DEFAULT);
            }
        });
    }
}
