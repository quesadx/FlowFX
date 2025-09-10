package cr.ac.una.flowfx.controller;

import cr.ac.una.flowfx.model.ProjectActivityDTO;
import cr.ac.una.flowfx.model.ProjectActivityViewModel;
import cr.ac.una.flowfx.model.ProjectDTO;
import cr.ac.una.flowfx.model.ProjectViewModel;
import cr.ac.una.flowfx.service.ProjectActivityService;
import cr.ac.una.flowfx.util.AnimationManager;
import cr.ac.una.flowfx.util.AppContext;
import cr.ac.una.flowfx.util.BindingUtils;
import cr.ac.una.flowfx.util.FlowController;
import cr.ac.una.flowfx.util.Respuesta;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCircleToggleNode;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controller for the Project Expand view.
 * Ensures status is handled as String ("P","R","S","C") and binds UI to ViewModel.
 */
public class ProjectExpandController
    extends Controller
    implements Initializable {

    private static final java.util.logging.Logger LOGGER =
        java.util.logging.Logger.getLogger(
            ProjectExpandController.class.getName()
        );

    @FXML
    private AnchorPane root;

    @FXML
    private VBox vbCover, vbDisplayActivityExpand;

    @FXML
    private MFXButton btnReturnManagement;

    @FXML
    private MFXTextField txfProjectName;

    @FXML
    private MFXTextField txfSponsorId;

    @FXML
    private MFXTextField txfLeaderId;

    @FXML
    private MFXTextField txfTechLeaderId;

    @FXML
    private MFXDatePicker dpProjectStartDate; // planned start

    @FXML
    private MFXDatePicker dpProjectStartDate1; // planned end

    @FXML
    private MFXCircleToggleNode tgProjectStatusPending;

    @FXML
    private MFXCircleToggleNode tgProjectStatusRunning;

    @FXML
    private MFXCircleToggleNode tgProjectStatusSuspended;

    @FXML
    private MFXCircleToggleNode tgProjectStatusCompleted;

    @FXML
    private ToggleGroup ProjectStatus;

    private final ProjectViewModel vm = new ProjectViewModel();
    private final ObservableList<ProjectActivityViewModel> activities =
        FXCollections.observableArrayList();
    private ProjectActivityViewModel selectedActivity;

    @FXML
    private TableView<ProjectActivityViewModel> tbvActivities;

    @FXML
    private TableColumn<ProjectActivityViewModel, String> tbcActivityName;

    @FXML
    private TableColumn<ProjectActivityViewModel, String> tbcActivityStatus;

    @FXML
    private TableColumn<
        ProjectActivityViewModel,
        String
    > tbcActivityResponsible;

    @FXML
    private MFXTextField txfResponsible;

    @FXML
    private MFXTextField txfCreatedBy;

    @FXML
    private MFXDatePicker dpLastUpdate;

    @FXML
    private MFXDatePicker dpCreationDate;

    @FXML
    private MFXDatePicker dpPlannedStartDate;

    @FXML
    private MFXDatePicker dpActualStartDate;

    @FXML
    private MFXDatePicker dpPlannedEndDate;

    @FXML
    private MFXDatePicker dpActualEndDate;

    private static final DataFormat ACTIVITY_INDEX = new DataFormat(
        "application/x-flowfx-activity-index"
    );

    @FXML
    private TextArea txaDescription;

    @FXML
    private VBox vbDisplayActivityCreation;

    @FXML
    private MFXTextField txfResponsableCreation;

    @FXML
    private TextArea txaDescriptionCreation;

    @FXML
    private MFXDatePicker dpPlannedStartDateCreation;

    @FXML
    private MFXDatePicker dpPlannedEndDateCreation;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LOGGER.fine(
            "initialize(URL, ResourceBundle) invoked for ProjectExpandController"
        );
    }

    @Override
    public void initialize() {
        LOGGER.fine("initialize() invoked for ProjectExpandController");
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
        setupActivitiesTable();
        loadActivitiesForProject();
    }

    @FXML
    private void onActionBtnReturnToManagement(ActionEvent event) {
        FlowController.getInstance().goView("ProjectManagementView");
        Object nav = AppContext.getInstance().get("navigationBar");
        if (nav instanceof VBox) ((VBox) nav).setDisable(false);
    }

    @FXML
    private void onActionBtnSelectActivityResponsible(ActionEvent event) {
        // Allow editing while selecting
        txfResponsableCreation.setEditable(true);
        FlowController.getInstance().goViewInWindowModal(
            "PersonSelectionView",
            (javafx.stage.Stage) root.getScene().getWindow(),
            false
        );
        Object sel = AppContext.getInstance().get("personSelectionResult");
        if (sel instanceof cr.ac.una.flowfx.model.PersonDTO p) {
            String label =
                (p.getFirstName() == null ? "" : p.getFirstName().trim()) +
                " " +
                (p.getLastName() == null ? "" : p.getLastName().trim());
            txfResponsableCreation.setText(label.trim());
            // Store the actual PersonDTO for later use on creation
            AppContext.getInstance().set(
                "ProjectExpand.activityResponsible",
                p
            );
            txfResponsableCreation.setEditable(false);
        }
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
        BindingUtils.bindToggleGroupToProperty(
            ProjectStatus,
            vm.statusProperty()
        );

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
            vm
                .plannedStartDateProperty()
                .addListener((obs, o, n) -> {
                    LocalDate ld = n == null
                        ? null
                        : Instant.ofEpochMilli(n.getTime())
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                    if (ld != picker.getValue()) picker.setValue(ld);
                });
            picker
                .valueProperty()
                .addListener((obs, o, n) -> {
                    Date d = n == null
                        ? null
                        : Date.from(
                            n.atStartOfDay(ZoneId.systemDefault()).toInstant()
                        );
                    if (
                        d == null && vm.getPlannedStartDate() != null
                    ) vm.setPlannedStartDate(null);
                    else if (
                        d != null && !d.equals(vm.getPlannedStartDate())
                    ) vm.setPlannedStartDate(d);
                });
            if (vm.getPlannedStartDate() != null) {
                LocalDate ld = Instant.ofEpochMilli(
                    vm.getPlannedStartDate().getTime()
                )
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
                picker.setValue(ld);
            }
        } else {
            vm
                .plannedEndDateProperty()
                .addListener((obs, o, n) -> {
                    LocalDate ld = n == null
                        ? null
                        : Instant.ofEpochMilli(n.getTime())
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                    if (ld != picker.getValue()) picker.setValue(ld);
                });
            picker
                .valueProperty()
                .addListener((obs, o, n) -> {
                    Date d = n == null
                        ? null
                        : Date.from(
                            n.atStartOfDay(ZoneId.systemDefault()).toInstant()
                        );
                    if (
                        d == null && vm.getPlannedEndDate() != null
                    ) vm.setPlannedEndDate(null);
                    else if (
                        d != null && !d.equals(vm.getPlannedEndDate())
                    ) vm.setPlannedEndDate(d);
                });
            if (vm.getPlannedEndDate() != null) {
                LocalDate ld = Instant.ofEpochMilli(
                    vm.getPlannedEndDate().getTime()
                )
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
                picker.setValue(ld);
            }
        }
    }

    private void bindNumericText(
        MFXTextField field,
        boolean isLeader,
        boolean isSponsor
    ) {
        field
            .textProperty()
            .addListener((obs, o, n) -> {
                try {
                    long v = (n == null || n.trim().isEmpty())
                        ? 0L
                        : Long.parseLong(n.trim());
                    if (isLeader) vm.setLeaderUserId(v);
                    else if (isSponsor) vm.setSponsorId(v);
                    else vm.setTechLeaderId(v);
                } catch (NumberFormatException ignored) {}
            });
        long init = isLeader
            ? vm.getLeaderUserId()
            : (isSponsor ? vm.getSponsorId() : vm.getTechLeaderId());
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
        openPersonSelector(
            txfTechLeaderId,
            "ProjectExpand.techleader.selected"
        );
    }

    private void openPersonSelector(MFXTextField target, String contextKey) {
        target.setEditable(true);
        FlowController.getInstance().goViewInWindowModal(
            "PersonSelectionView",
            (javafx.stage.Stage) root.getScene().getWindow(),
            false
        );
        Object sel = AppContext.getInstance().get("personSelectionResult");
        if (sel instanceof cr.ac.una.flowfx.model.PersonDTO p) {
            String label =
                (p.getFirstName() == null ? "" : p.getFirstName().trim()) +
                " " +
                (p.getLastName() == null ? "" : p.getLastName().trim());
            target.setText(label.trim());
            if (target == txfLeaderId) vm.setLeaderUserId(
                p.getId() == null ? 0L : p.getId()
            );
            else if (target == txfTechLeaderId) vm.setTechLeaderId(
                p.getId() == null ? 0L : p.getId()
            );
            else if (target == txfSponsorId) vm.setSponsorId(
                p.getId() == null ? 0L : p.getId()
            );
            // store entire DTO for optional later use
            AppContext.getInstance().set(contextKey, p);
            target.setEditable(false);
        }
    }

    @FXML
    private void onActionBtnReturnActivityExpand(ActionEvent event) {
        AnimationManager.hidePopup(vbDisplayActivityExpand, vbCover);
    }

    @FXML
    private void onActionCreateActivity(ActionEvent event) {
        clearActivityCreationForm();
        AnimationManager.showPopup(vbDisplayActivityCreation, vbCover);
    }

    @FXML
    private void onActionCancelCreateActivity(ActionEvent event) {
        clearActivityCreationForm();
        AnimationManager.hidePopup(vbDisplayActivityCreation, vbCover);
    }

    @FXML
    private void onActionConfirmCreateActivity(ActionEvent event) {
        // Build DTO for creation
        ProjectActivityDTO dto = new ProjectActivityDTO();
        long projectId = vm.getId();
        if (projectId <= 0) {
            LOGGER.warning("[Activity Create] Project id missing; aborting.");
            return;
        }
        dto.setProjectId(projectId);
        String desc = txaDescriptionCreation.getText();
        dto.setDescription(desc == null ? null : desc.trim());
        dto.setStatus("P"); // default status
        dto.setPlannedStartDate(fromPicker(dpPlannedStartDateCreation));
        dto.setPlannedEndDate(fromPicker(dpPlannedEndDateCreation));
        dto.setExecutionOrder(activities.size() + 1);

        // Resolve createdBy/responsible
        Long responsibleId = null;
        Object respSel = AppContext.getInstance().get(
            "ProjectExpand.activityResponsible"
        );
        if (respSel instanceof cr.ac.una.flowfx.model.PersonDTO psel) {
            responsibleId = psel.getId();
        }
        if (responsibleId == null) {
            Object u = AppContext.getInstance().get("user");
            if (u instanceof cr.ac.una.flowfx.model.PersonDTO user) {
                responsibleId = user.getId();
            }
        }

        ProjectActivityService svc = new ProjectActivityService();
        Respuesta r = svc.create(dto, projectId, responsibleId);
        if (Boolean.TRUE.equals(r.getEstado())) {
            LOGGER.info("[Activity Create] Created successfully.");
            // Optimistic UI update (use server-returned entity if available)
            Object createdObj = r.getResultado("ProjectActivity");
            if (createdObj instanceof ProjectActivityDTO createdDto) {
                // Ensure projectId is set (some WS variants may omit it in payload)
                if (createdDto.getProjectId() == null) {
                    createdDto.setProjectId(projectId);
                }
                ProjectActivityViewModel vmAct = new ProjectActivityViewModel(
                    createdDto
                );
                if (vmAct.getExecutionOrder() == 0) {
                    vmAct.setExecutionOrder(activities.size() + 1);
                }
                activities.add(vmAct);
                renumberExecutionOrder();
                Platform.runLater(() -> {
                    tbvActivities.refresh();
                });
            }
            clearActivityCreationForm();
            AnimationManager.hidePopup(vbDisplayActivityCreation, vbCover);
            // Defensive full reload to stay in sync with backend ordering / data
            loadActivitiesForProject();
        } else {
            LOGGER.warning(
                "[Activity Create] Failed: " +
                    r.getMensaje() +
                    " | " +
                    r.getMensajeInterno()
            );
        }
    }

    private void clearActivityCreationForm() {
        txfResponsableCreation.clear();
        txaDescriptionCreation.clear();
        dpPlannedStartDateCreation.setValue(null);
        dpPlannedEndDateCreation.setValue(null);
        AppContext.getInstance().delete("ProjectExpand.activityResponsible");
    }

    @FXML
    private void onActionConfirmUpdates(ActionEvent event) {
        if (selectedActivity != null) {
            // Pull dates back from pickers into the VM
            selectedActivity.setDescription(txaDescription.getText());
            selectedActivity.setCreatedAt(fromPicker(dpCreationDate));
            selectedActivity.setUpdatedAt(fromPicker(dpLastUpdate));
            selectedActivity.setPlannedStartDate(
                fromPicker(dpPlannedStartDate)
            );
            selectedActivity.setActualStartDate(fromPicker(dpActualStartDate));
            selectedActivity.setPlannedEndDate(fromPicker(dpPlannedEndDate));
            selectedActivity.setActualEndDate(fromPicker(dpActualEndDate));

            // Debug print
            LOGGER.fine(
                "[Activity Confirm] id=" +
                    selectedActivity.getId() +
                    ", order=" +
                    selectedActivity.getExecutionOrder() +
                    ", desc=" +
                    selectedActivity.getDescription() +
                    ", plannedStart=" +
                    selectedActivity.getPlannedStartDate() +
                    ", plannedEnd=" +
                    selectedActivity.getPlannedEndDate() +
                    ", actualStart=" +
                    selectedActivity.getActualStartDate() +
                    ", actualEnd=" +
                    selectedActivity.getActualEndDate()
            );

            // Future: persist via WS
            // ProjectActivityService svc = new ProjectActivityService();
            // svc.update(selectedActivity.toDTO());
        }
        AnimationManager.hidePopup(vbDisplayActivityExpand, vbCover);
    }

    // ================= Activities table wiring =================

    private void setupActivitiesTable() {
        // Columns
        if (tbcActivityName.getCellValueFactory() == null) {
            tbcActivityName.setCellValueFactory(
                new PropertyValueFactory<>("description")
            );
        }
        tbcActivityStatus.setCellValueFactory(cd -> {
            String code = cd.getValue() != null
                ? cd.getValue().getStatus()
                : null;
            String text = mapStatus(code);
            return Bindings.createStringBinding(() -> text);
        });
        // Responsible column now shows resolved name (if cached) or the responsibleId; fallback "-"
        tbcActivityResponsible.setCellValueFactory(cd -> {
            ProjectActivityViewModel vmAct = cd.getValue();
            return Bindings.createStringBinding(() -> {
                if (vmAct == null) return "-";
                long rid = vmAct.getResponsibleId();
                if (rid <= 0) return "-";
                Object label = AppContext.getInstance().get(
                    "person." + rid + ".label"
                );
                if (label instanceof String s && !s.isBlank()) {
                    return s;
                }
                return String.valueOf(rid);
            });
        });

        // Items and default sort by executionOrder
        tbvActivities.setItems(activities);
        tbvActivities.getSortOrder().clear();

        // Row factory for double click, DnD and pastel coloring
        tbvActivities.setRowFactory(tv -> {
            TableRow<ProjectActivityViewModel> row = new TableRow<>() {
                @Override
                protected void updateItem(
                    ProjectActivityViewModel item,
                    boolean empty
                ) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setStyle("");
                        setCursor(Cursor.DEFAULT);
                    } else {
                        // Alternate subtle surfaces from theme tokens
                        int idx = getIndex();
                        String bg;
                        switch (idx % 3) {
                            case 0 -> bg = "-fx-surface"; // base card surface
                            case 1 -> bg = "-fx-surface-variant"; // slightly different
                            default -> bg = "#f6f8ff"; // light pastel fallback
                        }
                        setStyle(
                            "-fx-background-color: " +
                                bg +
                                "; -fx-background-radius: 12;"
                        );
                        setCursor(Cursor.OPEN_HAND);
                    }
                }
            };

            // Double click -> open detail
            row.setOnMouseClicked((MouseEvent e) -> {
                if (e.getClickCount() == 2 && !row.isEmpty()) {
                    ProjectActivityViewModel item = row.getItem();
                    showActivityDetail(item);
                }
            });

            // Drag support
            row.setOnDragDetected(ev -> {
                if (!row.isEmpty()) {
                    Integer index = row.getIndex();
                    Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
                    ClipboardContent cc = new ClipboardContent();
                    cc.put(ACTIVITY_INDEX, index);
                    db.setContent(cc);
                    // Drag view snapshot for smooth UX
                    WritableImage snapshot = row.snapshot(
                        new SnapshotParameters(),
                        null
                    );
                    db.setDragView(
                        snapshot,
                        snapshot.getWidth() / 2,
                        snapshot.getHeight() / 2
                    );
                    ev.consume();
                }
            });

            row.setOnDragOver((DragEvent ev) -> {
                Dragboard db = ev.getDragboard();
                if (db.hasContent(ACTIVITY_INDEX)) {
                    if (
                        row.getIndex() !=
                        ((Integer) db.getContent(ACTIVITY_INDEX)).intValue()
                    ) {
                        ev.acceptTransferModes(TransferMode.MOVE);
                        ev.consume();
                    }
                }
            });

            row.setOnDragDropped((DragEvent ev) -> {
                Dragboard db = ev.getDragboard();
                boolean success = false;
                if (db.hasContent(ACTIVITY_INDEX)) {
                    int draggedIndex = (Integer) db.getContent(ACTIVITY_INDEX);
                    ProjectActivityViewModel draggedItem = tbvActivities
                        .getItems()
                        .remove(draggedIndex);

                    int dropIndex;
                    if (row.isEmpty()) {
                        dropIndex = tbvActivities.getItems().size();
                    } else {
                        dropIndex = row.getIndex();
                    }

                    tbvActivities.getItems().add(dropIndex, draggedItem);
                    renumberExecutionOrder();
                    tbvActivities.getSelectionModel().select(dropIndex);
                    tbvActivities.refresh();

                    // Debug print of current order
                    LOGGER.fine(
                        "=== Activities new order (index -> id:order) ==="
                    );
                    for (int i = 0; i < activities.size(); i++) {
                        ProjectActivityViewModel a = activities.get(i);
                        LOGGER.fine(
                            i + " -> " + a.getId() + ":" + a.getExecutionOrder()
                        );
                    }

                    success = true;
                }
                ev.setDropCompleted(success);
                ev.consume();
            });

            return row;
        });
    }

    private void renumberExecutionOrder() {
        for (int i = 0; i < activities.size(); i++) {
            ProjectActivityViewModel a = activities.get(i);
            int newOrder = i + 1; // 1-based ordering
            if (a.getExecutionOrder() != newOrder) {
                a.setExecutionOrder(newOrder);
            }
        }
    }

    private void loadActivitiesForProject() {
        long projectId = vm.getId();
        if (projectId <= 0) {
            activities.clear();
            return;
        }

        Task<List<ProjectActivityDTO>> task = new Task<>() {
            @Override
            protected List<ProjectActivityDTO> call() {
                ProjectActivityService svc = new ProjectActivityService();
                Respuesta r = svc.findAll();
                if (r != null && Boolean.TRUE.equals(r.getEstado())) {
                    Object listObj = r.getResultado("ProjectActivities");
                    if (listObj instanceof List<?>) {
                        @SuppressWarnings("unchecked")
                        List<ProjectActivityDTO> dtos = (List<
                            ProjectActivityDTO
                        >) listObj;
                        return dtos;
                    }
                }
                return List.of();
            }
        };
        task.setOnSucceeded(e -> {
            List<ProjectActivityDTO> dtos = task.getValue();
            LOGGER.fine(
                "[Activities Load] WS returned list size=" +
                    (dtos != null ? dtos.size() : 0) +
                    ", filtering by projectId=" +
                    projectId
            );
            activities.clear();
            dtos
                .stream()
                .filter(
                    d ->
                        d.getProjectId() != null &&
                        d.getProjectId().longValue() == projectId
                )
                .map(ProjectActivityViewModel::new)
                .sorted(
                    Comparator.comparingInt(
                        ProjectActivityViewModel::getExecutionOrder
                    )
                )
                .forEach(activities::add);
            LOGGER.fine(
                "[Activities Load] After filter size=" + activities.size()
            );
        });
        task.setOnFailed(e -> {
            activities.clear();
        });
        Thread t = new Thread(task, "load-activities");
        t.setDaemon(true);
        t.start();
    }

    private String mapStatus(String code) {
        if (code == null || code.isBlank()) return "-";
        return switch (code.trim().toUpperCase()) {
            case "P" -> "Pendiente";
            case "R" -> "En proceso";
            case "S" -> "Suspendida";
            case "C" -> "Completada";
            case "D" -> "Detenida";
            default -> code.trim();
        };
    }

    private void showActivityDetail(ProjectActivityViewModel item) {
        this.selectedActivity = item;
        if (item == null) return;

        // Resolve responsible label from cache if present, otherwise show ID or "-"
        long rid = item.getResponsibleId();
        String responsibleLabel = "-";
        if (rid > 0) {
            Object lbl = AppContext.getInstance().get(
                "person." + rid + ".label"
            );
            if (lbl instanceof String s && !s.isBlank()) {
                responsibleLabel = s;
            } else {
                responsibleLabel = String.valueOf(rid);
            }
        }
        txfResponsible.setText(responsibleLabel);
        // Resolve createdBy label from cache if present, otherwise show ID or "-"
        long cbid = item.getCreatedById();
        String createdByLabel = "-";
        if (cbid > 0) {
            Object cbl = AppContext.getInstance().get(
                "person." + cbid + ".label"
            );
            if (cbl instanceof String s2 && !s2.isBlank()) {
                createdByLabel = s2;
            } else {
                createdByLabel = String.valueOf(cbid);
            }
        }
        txfCreatedBy.setText(createdByLabel);
        txaDescription.setText(item.getDescription());

        // Dates
        setPickerFromDate(dpCreationDate, item.getCreatedAt());
        setPickerFromDate(dpLastUpdate, item.getUpdatedAt());
        setPickerFromDate(dpPlannedStartDate, item.getPlannedStartDate());
        setPickerFromDate(dpActualStartDate, item.getActualStartDate());
        setPickerFromDate(dpPlannedEndDate, item.getPlannedEndDate());
        setPickerFromDate(dpActualEndDate, item.getActualEndDate());

        AnimationManager.showPopup(vbDisplayActivityExpand, vbCover);
    }

    private void setPickerFromDate(MFXDatePicker picker, Date d) {
        if (picker == null) return;
        LocalDate ld = null;
        if (d != null) {
            ld = Instant.ofEpochMilli(d.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        }
        final LocalDate v = ld;
        Platform.runLater(() -> picker.setValue(v));
    }

    private Date fromPicker(MFXDatePicker picker) {
        if (picker == null) return null;
        LocalDate ld = picker.getValue();
        if (ld == null) return null;
        return Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
