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
 * Controller for the Project Expand view.
 * Ensures status is handled as String ("P","R","S","C") and binds UI to ViewModel.
 */
public class ProjectExpandController extends Controller implements Initializable {

    @FXML private AnchorPane root;
    @FXML private VBox vbCover;
    @FXML private MFXButton btnReturnManagement;

    @FXML private MFXTextField txfProjectName;
    @FXML private MFXTextField txfSponsorId;
    @FXML private MFXTextField txfLeaderId;
    @FXML private MFXTextField txfTechLeaderId;

    @FXML private MFXDatePicker dpProjectStartDate;   // planned start
    @FXML private MFXDatePicker dpProjectStartDate1;  // planned end

    @FXML private MFXCircleToggleNode tgProjectStatusPending;
    @FXML private MFXCircleToggleNode tgProjectStatusRunning;
    @FXML private MFXCircleToggleNode tgProjectStatusSuspended;
    @FXML private MFXCircleToggleNode tgProjectStatusCompleted;
    @FXML private ToggleGroup ProjectStatus;

    private final ProjectViewModel vm = new ProjectViewModel();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("Inicialize con parametros!");
    }

    @Override
    public void initialize() {
        System.out.println("Inicialize sin parametros!");
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
            // Status must be String (e.g., "P","R","S","C")
            vm.setStatus(initial.getStatus());
            vm.setCreatedAt(initial.getCreatedAt());
            vm.setUpdatedAt(initial.getUpdatedAt());
            vm.setLeaderUserId(initial.getLeaderUserId());
            vm.setTechLeaderId(initial.getTechLeaderId());
            vm.setSponsorId(initial.getSponsorId());
        } else {
            // Default to Pending as String
            vm.setStatus("P");
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
        // Name
        txfProjectName.textProperty().bindBidirectional(vm.nameProperty());

        // Dates
        bindDatePicker(dpProjectStartDate, true);
        bindDatePicker(dpProjectStartDate1, false);

        // Status as String tokens
        tgProjectStatusPending.setUserData("P");
        tgProjectStatusRunning.setUserData("R");
        tgProjectStatusSuspended.setUserData("S");
        tgProjectStatusCompleted.setUserData("C");

        // Bind ToggleGroup to ViewModel status (String)
        BindingUtils.bindToggleGroupToProperty(ProjectStatus, vm.statusProperty());

        // Default selection if empty
        if (vm.getStatus() == null || vm.getStatus().isBlank()) {
            ProjectStatus.selectToggle(tgProjectStatusPending);
        }

        // Numeric bindings for related IDs
        bindNumericText(txfLeaderId, true, false);
        bindNumericText(txfTechLeaderId, false, false);
        bindNumericText(txfSponsorId, false, true);
    }

    private void bindDatePicker(MFXDatePicker picker, boolean isStart) {
        if (isStart) {
            vm.plannedStartDateProperty().addListener((obs, o, n) -> {
                LocalDate ld = n == null ? null
                        : Instant.ofEpochMilli(n.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
                if (ld != picker.getValue()) picker.setValue(ld);
            });
            picker.valueProperty().addListener((obs, o, n) -> {
                Date d = n == null ? null
                        : Date.from(n.atStartOfDay(ZoneId.systemDefault()).toInstant());
                if (d == null && vm.getPlannedStartDate() != null) vm.setPlannedStartDate(null);
                else if (d != null && !d.equals(vm.getPlannedStartDate())) vm.setPlannedStartDate(d);
            });
            if (vm.getPlannedStartDate() != null) {
                LocalDate ld = Instant.ofEpochMilli(vm.getPlannedStartDate().getTime())
                        .atZone(ZoneId.systemDefault()).toLocalDate();
                picker.setValue(ld);
            }
        } else {
            vm.plannedEndDateProperty().addListener((obs, o, n) -> {
                LocalDate ld = n == null ? null
                        : Instant.ofEpochMilli(n.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
                if (ld != picker.getValue()) picker.setValue(ld);
            });
            picker.valueProperty().addListener((obs, o, n) -> {
                Date d = n == null ? null
                        : Date.from(n.atStartOfDay(ZoneId.systemDefault()).toInstant());
                if (d == null && vm.getPlannedEndDate() != null) vm.setPlannedEndDate(null);
                else if (d != null && !d.equals(vm.getPlannedEndDate())) vm.setPlannedEndDate(d);
            });
            if (vm.getPlannedEndDate() != null) {
                LocalDate ld = Instant.ofEpochMilli(vm.getPlannedEndDate().getTime())
                        .atZone(ZoneId.systemDefault()).toLocalDate();
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
            } catch (NumberFormatException ignored) {
            }
        });
        long init = isLeader ? vm.getLeaderUserId() : (isSponsor ? vm.getSponsorId() : vm.getTechLeaderId());
        field.setText(init == 0L ? "" : String.valueOf(init));
    }

    @FXML
    private void onActionBtnSelectSponsor(ActionEvent event) {
        openPersonSelector(txfSponsorId, "ProjectExpand.sponsor.selected");
    }

    @FXML
    private void onActionBtnSelectLeader(ActionEvent event) {
        openPersonSelector(txfLeaderId, "ProjectExpand.leader.selected");
    }

    @FXML
    private void onActionBtnSelectTechLeader(ActionEvent event) {
        openPersonSelector(txfTechLeaderId, "ProjectExpand.techleader.selected");
    }

    private void openPersonSelector(MFXTextField target, String contextKey) {
        target.setEditable(true);
        FlowController.getInstance().goViewInWindowModal("PersonSelectionView", (javafx.stage.Stage) root.getScene().getWindow(), false);
        Object sel = AppContext.getInstance().get("personSelectionResult");
        if (sel instanceof cr.ac.una.flowfx.model.PersonDTO p) {
            String label = (p.getFirstName() == null ? "" : p.getFirstName().trim()) + " " + (p.getLastName() == null ? "" : p.getLastName().trim());
            target.setText(label.trim());
            if (target == txfLeaderId) vm.setLeaderUserId(p.getId() == null ? 0L : p.getId());
            else if (target == txfTechLeaderId) vm.setTechLeaderId(p.getId() == null ? 0L : p.getId());
            else if (target == txfSponsorId) vm.setSponsorId(p.getId() == null ? 0L : p.getId());
            // store entire DTO for optional later use
            AppContext.getInstance().set(contextKey, p);
            target.setEditable(false);
        }
    }
}