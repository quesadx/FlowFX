/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cr.ac.una.flowfx.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.security.auth.callback.Callback;

import cr.ac.una.flowfx.util.AnimationManager;
import cr.ac.una.flowfx.util.AppContext;
import cr.ac.una.flowfx.util.FlowController;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckListView;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author quesadx
 */
public class MainController extends Controller implements Initializable {

    @FXML
    private AnchorPane root;
    @FXML
    private VBox vbCover, vbLogInDisplay;
    @FXML
    private MFXButton btnLogIn;
    private ScrollPane scrollPaneProjects;
    private VBox vboxProjects;
    @FXML
    private VBox vBoxCentral;
    @FXML
    private MFXCheckListView<?> checkListDashboard;
    @FXML
    private DatePicker datePKDashboard;
    @FXML
    private PieChart pieChartDashboard;
    @FXML
    private MFXTextField txfUsername, txfUserPassword;

    Boolean userLoggedIn = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("Inicializando MainController");
        vbCover.setEffect(new javafx.scene.effect.GaussianBlur());
        AnimationManager.showPopup(vbLogInDisplay, vbCover);
        // Disable navigation when user is NOT logged in
        Object nav = AppContext.getInstance().get("NavigationBar");
        if (nav instanceof VBox) ((VBox) nav).setDisable(!userLoggedIn);
    }

    @Override
    public void initialize() {
        
    }

    public void addDefaultWidgetContent(Widget widget) {
        
    }

    @FXML
    private void onActionBtnLogIn(ActionEvent event) {
        AnimationManager.hidePopup(vbLogInDisplay, vbCover);
        // TODO: Login logic
        userLoggedIn = true;
        Object nav = AppContext.getInstance().get("NavigationBar");
        if (nav instanceof VBox) {
            ((VBox) nav).setDisable(!userLoggedIn);
        }
    }

    @FXML
    private void onMouseClickedLblSignUp(MouseEvent event) {
    }

    @FXML
    private void onMouseClickedLblPasswordRecovery(MouseEvent event) {
    }

}
