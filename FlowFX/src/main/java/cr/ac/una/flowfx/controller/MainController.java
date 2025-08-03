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

import cr.ac.una.flowfx.controller.MainController.ProjectDto;
import cr.ac.una.flowfx.util.AnimationManager;
import cr.ac.una.flowfx.util.FlowController;
import io.github.palexdev.materialfx.controls.MFXButton;
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
    @FXML
    private HBox vBoxTop;
    private ListView<List<Widget>> lVProjectInfo; 
    @FXML
    private HBox hBoxButtonLeft;
    @FXML
    private PieChart PieChartProjects;
    @FXML
    private MFXButton btnProjects;
    @FXML
    private VBox hBoxCentral;
    private ScrollPane scrollPaneProjects;
    private VBox vboxProjects;
    @FXML
    private ScrollPane scrollProjects;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        vbCover.setEffect(new javafx.scene.effect.GaussianBlur());    
        AnimationManager.showPopup(vbLogInDisplay, vbCover);   
        //imgBackground.fitHeightProperty().bind(root.heightProperty());
        //imgBackground.fitWidthProperty().bind(root.widthProperty());
        setupProjectScrollPane();
        ArrayList<ProjectDto> projectList = new ArrayList<>();
        projectList.add(new ProjectDto("Proyecto Alpha", "2024-01-01", "2024-06-30", "Activo", "Sponsor A"));
        projectList.add(new ProjectDto("Proyecto Beta", "2023-05-15", "2023-12-31", "Finalizado", "Sponsor B"));
        projectList.add(new ProjectDto("Proyecto Gamma", "2024-03-10", "2024-09-15", "En progreso", "Sponsor C"));
        projectList.add(new ProjectDto("Proyecto Delta", "2024-04-01", "2024-10-01", "Activo", "Sponsor D"));
        projectList.add(new ProjectDto("Proyecto Epsilon", "2024-05-01", "2024-11-01", "Activo", "Sponsor E"));
        projectList.add(new ProjectDto("Proyecto Alpha", "2024-01-01", "2024-06-30", "Activo", "Sponsor A"));
        projectList.add(new ProjectDto("Proyecto Beta", "2023-05-15", "2023-12-31", "Finalizado", "Sponsor B"));
        projectList.add(new ProjectDto("Proyecto Gamma", "2024-03-10", "2024-09-15", "En progreso", "Sponsor C"));
        projectList.add(new ProjectDto("Proyecto Delta", "2024-04-01", "2024-10-01", "Activo", "Sponsor D"));
        projectList.add(new ProjectDto("Proyecto Epsilon", "2024-05-01", "2024-11-01", "Activo", "Sponsor E"));

        loadProjects(projectList);

    }    

    @Override
    public void initialize() {

    }


    public void addDefaultWidgetContent(Widget widget) {
        
    }

    @FXML
    private void onActionBtnLogIn(ActionEvent event) {
        AnimationManager.hidePopup(vbLogInDisplay, vbCover);
        //vbLogInDisplay.setVisible(false);
        //vbLogInDisplay.setManaged(false);
    }

    @FXML
    private void onActionBtnProjects(ActionEvent event) {
        
    }

    public void loadProjects(ArrayList<ProjectDto> projectList) {
        vboxProjects.getChildren().clear();
        for (ProjectDto project : projectList) {
            Node projectNode = projectContainer(project);
            vboxProjects.getChildren().add(projectNode);
            VBox.setVgrow(projectNode, Priority.NEVER);
        }
    }


    public Node projectContainer(ProjectDto project){
        HBox hBox = new HBox(40);
        hBox.setPadding(new Insets(10));
        hBox.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: #f9f9f9;");
        Label projectName = new Label(project.getName());
        Label projectStartDate = new Label("Inicio: " + project.getStartDate());
        Label projectEndDate = new Label("Fin: " + project.getEndDate());
        Label projectStatus = new Label("Estado: " + project.getStatus());
        Label projectSponsor = new Label("Sponsor: " + project.getSponsor());
        hBox.getChildren().addAll(projectName, projectStartDate, projectEndDate, projectStatus, projectSponsor);

        hBox.setPrefWidth(Double.MAX_VALUE);
        HBox.setHgrow(projectName, Priority.ALWAYS);
        HBox.setHgrow(projectStartDate, Priority.ALWAYS);
        HBox.setHgrow(projectEndDate, Priority.ALWAYS);
        HBox.setHgrow(projectStatus, Priority.ALWAYS);
        HBox.setHgrow(projectSponsor, Priority.ALWAYS);

        return hBox;
    }

    private void setupProjectScrollPane() {
        vboxProjects = new VBox(10);
        vboxProjects.setPadding(new Insets(10));
        scrollPaneProjects = new ScrollPane(vboxProjects);
        scrollPaneProjects.setFitToWidth(true);
        scrollPaneProjects.setStyle("-fx-background-color:transparent;");

        hBoxCentral.getChildren().clear();
        hBoxCentral.getChildren().add(scrollPaneProjects);
        HBox.setHgrow(scrollPaneProjects, Priority.ALWAYS);
    }

    @FXML
    private void OnMouseClickedScrollProjects(MouseEvent event) {
        if (event.getClickCount() == 2) {
            Node source = (Node) event.getSource();
            Bounds bounds = source.getBoundsInParent();
            Point2D point = new Point2D(bounds.getMinX(), bounds.getMinY());
            scrollProjects.setVvalue(point.getY() / scrollProjects.getContent().getBoundsInLocal().getHeight());
        }
    }

    @FXML
    private void onActionBtnManageProjects(ActionEvent event) {
        FlowController.getInstance().goView("ProjectManagementView");
    }



    public static class ProjectDto {
        private String name;
        private String startDate;
        private String endDate;
        private String status;
        private String sponsor;

        public ProjectDto(String name, String startDate, String endDate, String status, String sponsor) {
            this.name = name;
            this.startDate = startDate;
            this.endDate = endDate;
            this.status = status;
            this.sponsor = sponsor;
        }

        public String getName() { return name; }
        public String getStartDate() { return startDate; }
        public String getEndDate() { return endDate; }
        public String getStatus() { return status; }
        public String getSponsor() { return sponsor; }
    }
}
