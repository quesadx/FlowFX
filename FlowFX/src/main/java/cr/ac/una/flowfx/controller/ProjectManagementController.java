package cr.ac.una.flowfx.controller;

import java.net.URL;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import cr.ac.una.flowfx.util.AnimationManager;
import cr.ac.una.flowfx.util.AppContext;
import cr.ac.una.flowfx.util.FlowController;
import cr.ac.una.flowfx.model.PersonDTO;
import cr.ac.una.flowfx.model.ProjectDTO;
import cr.ac.una.flowfx.service.ProjectService;
import cr.ac.una.flowfx.util.Mensaje;
import cr.ac.una.flowfx.util.Respuesta;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author quesadx
 */

public class ProjectManagementController extends Controller implements Initializable {

    @FXML private AnchorPane root;
    @FXML private TilePane tpProjects;
    @FXML private VBox vbCover;
    @FXML private VBox vbProjectCreationDisplay;
    @FXML private MFXTextField txfProjectName;
    @FXML private TextArea txaProjectDescription;
    @FXML private MFXDatePicker dpProjectStartDate;
    @FXML private MFXDatePicker dpProjectEndDate;
    @FXML private MFXTextField txfSponsorId;
    @FXML private MFXTextField txfLeaderId;
    @FXML private MFXTextField txfTechLeaderId;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadUserProjects();
    }

    @Override
    public void initialize() {
        // no-op
    }

    @FXML
    private void onActionBtnReturnProjectCreation(ActionEvent event) {
        AnimationManager.hidePopup(vbProjectCreationDisplay, vbCover);
        Object nav = AppContext.getInstance().get("navigationBar");
        if (nav instanceof VBox) {
            ((VBox) nav).setDisable(false);
        }
    }

    @FXML
    private void onActionCreateProject(ActionEvent event) {
        AnimationManager.showPopup(vbProjectCreationDisplay, vbCover);
        Object nav = AppContext.getInstance().get("navigationBar");
        if (nav instanceof VBox) {
            ((VBox) nav).setDisable(true);
        }
    }

    @FXML
    private void onActionConfirmProjectCreation(ActionEvent event) {
        ProjectDTO dto = new ProjectDTO();
        dto.setName(getTrimmedText(txfProjectName));
        dto.setPlannedStartDate(getDateFromPicker(dpProjectStartDate));
        dto.setPlannedEndDate(getDateFromPicker(dpProjectEndDate));
        dto.setStatus('P');

        Long leaderId = parseLongSafe(txfLeaderId.getText());
        Long techId = parseLongSafe(txfTechLeaderId.getText());
        Long sponsorId = parseLongSafe(txfSponsorId.getText());

        PersonDTO user = (PersonDTO) AppContext.getInstance().get("user");
        if (leaderId == null && user != null) leaderId = user.getId();
        if (techId == null && user != null) techId = user.getId();
        if (sponsorId == null && user != null) sponsorId = user.getId();

        ProjectService service = new ProjectService();
        Respuesta response = service.create(dto, leaderId, techId, sponsorId);
        if (Boolean.TRUE.equals(response.getEstado())) {
            new Mensaje().showModal(javafx.scene.control.Alert.AlertType.INFORMATION, "Proyecto", root.getScene().getWindow(), "Proyecto creado correctamente.");
            AnimationManager.hidePopup(vbProjectCreationDisplay, vbCover);
            clearForm();
            loadUserProjects();
        } else {
            String detail = response.getMensajeInterno() != null && !response.getMensajeInterno().isBlank() ? ("\nDetalle: " + response.getMensajeInterno()) : "";
            new Mensaje().showModal(javafx.scene.control.Alert.AlertType.ERROR, "Proyecto", root.getScene().getWindow(), response.getMensaje() + detail);
            System.out.println(response.getMensajeInterno());
            System.out.println(response.getMensaje());
        }
    }

    private void loadUserProjects() {
        tpProjects.getChildren().clear();
        Object userObj = AppContext.getInstance().get("user");
        if (!(userObj instanceof PersonDTO)) return;
        PersonDTO user = (PersonDTO) userObj;
        ProjectService service = new ProjectService();
        Respuesta response = service.findProjectsForUser(user.getId());
        if (!Boolean.TRUE.equals(response.getEstado())) return;
        @SuppressWarnings("unchecked")
        List<ProjectDTO> projects = (List<ProjectDTO>) response.getResultado("Projects");
        if (projects == null) return;
        for (ProjectDTO project : projects) {
            Board board = new Board();
            board.getLblTitle().setText(project.getName());
            board.getLblStatus().setText(String.valueOf(project.getStatus()));
            board.getBtnExpandProject().setOnAction(e -> {
                FlowController.getInstance().goView("ProjectExpandView");
                Object nav = AppContext.getInstance().get("navigationBar");
                if (nav instanceof VBox) ((VBox) nav).setDisable(true);
                AppContext.getInstance().set("currentProject", project);
            });
            tpProjects.getChildren().add(board);
        }
    }

    private Long parseLongSafe(String text) {
        if (text == null) return null;
        String trimmed = text.trim();
        if (trimmed.isEmpty()) return null;
        try {
            return Long.parseLong(trimmed);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void clearForm() {
        txfProjectName.clear();
        txaProjectDescription.clear();
        dpProjectStartDate.setValue(null);
        dpProjectEndDate.setValue(null);
        txfSponsorId.clear();
        txfLeaderId.clear();
        txfTechLeaderId.clear();
    }

    private String getTrimmedText(MFXTextField field) {
        String text = field.getText();
        return text != null ? text.trim() : "";
    }

    private Date getDateFromPicker(MFXDatePicker picker) {
        return picker.getValue() != null ? Date.from(picker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;
    }
}
