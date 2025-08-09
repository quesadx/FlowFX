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
import cr.ac.una.flowfx.util.FlowController;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckListView;
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
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        vbCover.setEffect(new javafx.scene.effect.GaussianBlur());    
        AnimationManager.showPopup(vbLogInDisplay, vbCover);   
        //imgBackground.fitHeightProperty().bind(root.heightProperty());
        //imgBackground.fitWidthProperty().bind(root.widthProperty());
        setupProjectScrollPane();
        // ArrayList<ProjectDto> projectList = new ArrayList<>();
        // loadProjects(projectList);

    }    

    @Override
    public void initialize() {

    }

    public void addDefaultWidgetContent(Widget widget) {
        
    }

    @FXML
    private void onActionBtnLogIn(ActionEvent event) {
        AnimationManager.hidePopup(vbLogInDisplay, vbCover);
    }

    // public void loadProjects(ArrayList<ProjectDto> projectList) {
    //     vboxProjects.getChildren().clear();
    //     for (ProjectDto project : projectList) {
    //         Node projectNode = projectContainer(project);
    //         vboxProjects.getChildren().add(projectNode);
    //         VBox.setVgrow(projectNode, Priority.NEVER);
    //     }
    // }

    // public Node projectContainer(ProjectDto project){
    //     VBox vBox = new VBox(10); 
    //     vBox.setPadding(new Insets(10));
    //     vBox.getStyleClass().add("container-sub");

    //     HBox hBoxTop = new HBox(10);
    //     Label projectName = new Label(project.getName());
    //     Label projectStatus = new Label("Estado: " + project.getStatus());
    //     hBoxTop.getChildren().addAll(projectName, projectStatus);

    //     HBox hBoxBottom = new HBox(10);
    //     Label projectStartDate = new Label("Inicio: " + project.getStartDate());
    //     Label projectEndDate = new Label("Fin: " + project.getEndDate());
    //     hBoxBottom.getChildren().addAll(projectStartDate, projectEndDate);

    //     vBox.getChildren().addAll(hBoxTop, hBoxBottom);
    //     vBox.setPrefWidth(Double.MAX_VALUE);

    //     vBox.setOnMouseClicked(event->{
    //         System.out.println("Clickeando " + project.getName());
    //     });
    //     return vBox;
    // }

    private void setupProjectScrollPane() {
        vboxProjects = new VBox(10);
        vboxProjects.setPadding(new Insets(10));
        scrollPaneProjects = new ScrollPane(vboxProjects);
        scrollPaneProjects.setFitToWidth(true);
        scrollPaneProjects.getStyleClass().add("container-sub");

        vBoxCentral.getChildren().clear();
        vBoxCentral.getChildren().add(scrollPaneProjects);
        HBox.setHgrow(scrollPaneProjects, Priority.ALWAYS);
    }

}
