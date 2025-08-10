package cr.ac.una.flowfx.controller;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;

import cr.ac.una.flowfx.model.ProjectDTO;
import cr.ac.una.flowfx.model.ProjectViewModel;
import cr.ac.una.flowfx.util.AppContext;
import cr.ac.una.flowfx.util.BindingUtils;
import cr.ac.una.flowfx.util.FlowController;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCircleToggleNode;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author quesadx
 */
public class ProjectExpandController extends Controller implements Initializable {

    @FXML private AnchorPane root;
    @FXML private VBox vbCover;
    @FXML private MFXButton btnReturnManagement;
    @FXML private MFXTextField txfProjectName;
    @FXML private MFXTextField txfSponsorId;
    @FXML private MFXTextField txfLeaderId;
    @FXML private MFXTextField txfTechLeaderId;
    @FXML private MFXDatePicker dpProjectStartDate;
    @FXML private MFXDatePicker dpProjectStartDate1;
    @FXML private MFXCircleToggleNode tgProjectStatusPending;
    @FXML private ToggleGroup ProjectStatus;
    @FXML private MFXCircleToggleNode tgProjectStatusRunning;
    @FXML private MFXCircleToggleNode tgProjectStatusSuspended;
    @FXML private MFXCircleToggleNode tgProjectStatusCompleted;
    
    private final ProjectViewModel vm = new ProjectViewModel();

    @Override
    public void initialize(URL url, ResourceBundle rb) { }

    @Override
    public void initialize() {
        Object p = AppContext.getInstance().get("currentProject");
        if (p instanceof ProjectDTO) {
            ProjectDTO dto = (ProjectDTO) p;
            ProjectViewModel initial = new ProjectViewModel(dto);
            vm.setId(initial.getId());
            vm.setName(initial.getName());
            vm.setPlannedStartDate(initial.getPlannedStartDate());
            vm.setPlannedEndDate(initial.getPlannedEndDate());
            vm.setActualStartDate(initial.getActualStartDate());
            vm.setActualEndDate(initial.getActualEndDate());
            vm.setStatus(initial.getStatus());
            vm.setCreatedAt(initial.getCreatedAt());
            vm.setUpdatedAt(initial.getUpdatedAt());
            vm.setLeaderUserId(initial.getLeaderUserId());
            vm.setTechLeaderId(initial.getTechLeaderId());
            vm.setSponsorId(initial.getSponsorId());
        } else {
            vm.setStatus('P');
        }
        bindFields();
    }

    @FXML
    private void onActionBtnReturnToManagement(ActionEvent event) {
        FlowController.getInstance().goView("ProjectManagementView");
    Object nav = AppContext.getInstance().get("navigationBar");
        if (nav instanceof VBox) ((VBox) nav).setDisable(false);
    }

    private void bindFields() {
        txfProjectName.textProperty().bindBidirectional(vm.nameProperty());
        bindDatePicker(dpProjectStartDate, true);
        bindDatePicker(dpProjectStartDate1, false);
    tgProjectStatusPending.setUserData('P');
        tgProjectStatusRunning.setUserData('R');
        tgProjectStatusSuspended.setUserData('S');
        tgProjectStatusCompleted.setUserData('C');
    BindingUtils.bindToggleGroupToProperty(ProjectStatus, vm.statusProperty());
    if (vm.getStatus() == null) ProjectStatus.selectToggle(tgProjectStatusPending);

    bindNumericText(txfLeaderId, true, false);
    bindNumericText(txfTechLeaderId, false, false);
    bindNumericText(txfSponsorId, false, true);
    }

    private void bindDatePicker(MFXDatePicker picker, boolean isStart) {
        if (isStart) {
            vm.plannedStartDateProperty().addListener((obs, o, n) -> {
                LocalDate ld = n == null ? null : Instant.ofEpochMilli(n.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
                if (ld != picker.getValue()) picker.setValue(ld);
            });
            picker.valueProperty().addListener((obs, o, n) -> {
                Date d = n == null ? null : Date.from(n.atStartOfDay(ZoneId.systemDefault()).toInstant());
                if (d == null && vm.getPlannedStartDate() != null) vm.setPlannedStartDate(null);
                else if (d != null && !d.equals(vm.getPlannedStartDate())) vm.setPlannedStartDate(d);
            });
            if (vm.getPlannedStartDate() != null) {
                LocalDate ld = Instant.ofEpochMilli(vm.getPlannedStartDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
                picker.setValue(ld);
            }
        } else {
            vm.plannedEndDateProperty().addListener((obs, o, n) -> {
                LocalDate ld = n == null ? null : Instant.ofEpochMilli(n.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
                if (ld != picker.getValue()) picker.setValue(ld);
            });
            picker.valueProperty().addListener((obs, o, n) -> {
                Date d = n == null ? null : Date.from(n.atStartOfDay(ZoneId.systemDefault()).toInstant());
                if (d == null && vm.getPlannedEndDate() != null) vm.setPlannedEndDate(null);
                else if (d != null && !d.equals(vm.getPlannedEndDate())) vm.setPlannedEndDate(d);
            });
            if (vm.getPlannedEndDate() != null) {
                LocalDate ld = Instant.ofEpochMilli(vm.getPlannedEndDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
                picker.setValue(ld);
            }
        }
    }

    private void bindNumericText(MFXTextField field, boolean isLeader, boolean isSponsor) {
        field.textProperty().addListener((obs, o, n) -> {
            try {
                long v = (n == null || n.trim().isEmpty()) ? 0L : Long.parseLong(n.trim());
                if (isLeader) vm.setLeaderUserId(v);
                else if (isSponsor) vm.setSponsorId(v);
                else vm.setTechLeaderId(v);
            } catch (NumberFormatException ignored) { }
        });
        long init = isLeader ? vm.getLeaderUserId() : isSponsor ? vm.getSponsorId() : vm.getTechLeaderId();
        field.setText(init == 0L ? "" : String.valueOf(init));
    }

}
