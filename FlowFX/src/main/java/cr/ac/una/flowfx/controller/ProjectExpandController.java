package cr.ac.una.flowfx.controller;

import cr.ac.una.flowfx.model.PersonDTO;
import cr.ac.una.flowfx.model.ProjectActivityDTO;
import cr.ac.una.flowfx.model.ProjectActivityViewModel;
import cr.ac.una.flowfx.model.ProjectDTO;
import cr.ac.una.flowfx.model.ProjectViewModel;
import cr.ac.una.flowfx.service.PersonService;
import cr.ac.una.flowfx.service.ProjectActivityService;
import cr.ac.una.flowfx.service.ProjectService;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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
    private MFXDatePicker dpProjectActualStartDate; // planned end

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

    // Guard to avoid concurrent WS calls when rapidly toggling
    private boolean statusPersistInProgress = false;

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
        // Proactively refresh project from server to reflect any out-of-band DB changes
        refreshProjectFromServer();
    }

    /**
     * Fetches the latest project from the server and updates the ViewModel + UI.
     * Runs asynchronously to keep UI responsive.
     */
    private void refreshProjectFromServer() {
        long id = vm.getId();
        if (id <= 0) return;
        Task<Respuesta> task = new Task<>() {
            @Override
            protected Respuesta call() {
                ProjectService svc = new ProjectService();
                return svc.find(id);
            }
        };
        task.setOnSucceeded(e -> {
            Respuesta r = task.getValue();
            if (r != null && Boolean.TRUE.equals(r.getEstado())) {
                Object obj = r.getResultado("Project");
                if (obj instanceof ProjectDTO p) {
                    AppContext.getInstance().set("currentProject", p);
                    // Update fields and ensure toggle reflects refreshed status
                    vm.setName(p.getName());
                    vm.setPlannedStartDate(p.getPlannedStartDate());
                    vm.setPlannedEndDate(p.getPlannedEndDate());
                    vm.setActualStartDate(p.getActualStartDate());
                    vm.setActualEndDate(p.getActualEndDate());
                    vm.setStatus(p.getStatus());
                    vm.setCreatedAt(p.getCreatedAt());
                    vm.setUpdatedAt(p.getUpdatedAt());
                    vm.setLeaderUserId(p.getLeaderUserId() == null ? 0L : p.getLeaderUserId());
                    vm.setTechLeaderId(p.getTechLeaderId() == null ? 0L : p.getTechLeaderId());
                    vm.setSponsorId(p.getSponsorId() == null ? 0L : p.getSponsorId());
                    selectToggleForStatus(p.getStatus());
                    refreshLeaderLabel();
                    refreshTechLeaderLabel();
                    refreshSponsorLabel();
                }
            } else {
                LOGGER.fine("[Project Refresh] No update from server. estado=" + (r != null ? r.getEstado() : null));
            }
        });
        task.setOnFailed(e -> LOGGER.fine("[Project Refresh] Failed: " + task.getException()));
        Thread t = new Thread(task, "project-refresh");
        t.setDaemon(true);
        t.start();
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
        bindDatePicker(dpProjectActualStartDate, false);

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
        } else {
            // Ensure toggle reflects current status on load
            selectToggleForStatus(vm.getStatus());
        }

        // Note: persistence is handled by each toggle's onAction handler, ensuring
        // only user-initiated changes perform WS updates.

        // Configure person display fields as non-editable and show resolved names
        txfLeaderId.setEditable(false);
        txfTechLeaderId.setEditable(false);
        txfSponsorId.setEditable(false);
        refreshLeaderLabel();
        refreshTechLeaderLabel();
        refreshSponsorLabel();
        // Keep labels in sync if any id changes later (e.g., after a reload)
        vm.leaderUserIdProperty().addListener((obs, o, n) -> refreshLeaderLabel());
        vm.techLeaderIdProperty().addListener((obs, o, n) -> refreshTechLeaderLabel());
        vm.sponsorIdProperty().addListener((obs, o, n) -> refreshSponsorLabel());
        // Info popup is handled via @FXML onAction methods
    }

    private void refreshLeaderLabel() {
        updatePersonLabelIntoField(vm.getLeaderUserId(), txfLeaderId);
    }

    private void refreshTechLeaderLabel() {
        updatePersonLabelIntoField(vm.getTechLeaderId(), txfTechLeaderId);
    }

    private void refreshSponsorLabel() {
        updatePersonLabelIntoField(vm.getSponsorId(), txfSponsorId);
    }

    private void updatePersonLabelIntoField(long personId, MFXTextField target) {
        if (target == null) return;
        if (personId <= 0) {
            target.setText("");
            return;
        }
        String cacheKey = "person." + personId + ".label";
        Object lbl = AppContext.getInstance().get(cacheKey);
        if (lbl instanceof String s && !s.isBlank()) {
            target.setText(s);
            return;
        }
        // Not in cache: fetch asynchronously and update. Avoid showing raw id.
        target.setText("");
        new Thread(() -> {
            try {
                PersonService ps = new PersonService();
                Respuesta r = ps.find(personId);
                if (Boolean.TRUE.equals(r.getEstado())) {
                    Object po = r.getResultado("Person");
                    if (po instanceof PersonDTO pp) {
                        String nm = ((pp.getFirstName() == null ? "" : pp.getFirstName().trim()) +
                                     " " +
                                     (pp.getLastName() == null ? "" : pp.getLastName().trim())).trim();
                        if (!nm.isBlank()) {
                            AppContext.getInstance().set(cacheKey, nm);
                            Platform.runLater(() -> target.setText(nm));
                        }
                    }
                }
            } catch (Exception ex) {
                LOGGER.fine("Async person label fetch failed for id=" + personId + ": " + ex.getMessage());
            }
        }, "person-label-fetch-" + personId).start();
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

    @SuppressWarnings("unused")
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

    @FXML
    private void onActionBtnPrintReport(ActionEvent event){
        exportScheduleToExcel();
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
            // store entire DTO for optional later use and cache label for later lookups
            AppContext.getInstance().set(contextKey, p);
            if (p.getId() != null) {
                AppContext.getInstance().set(
                    "person." + p.getId() + ".label",
                    label.trim()
                );
            }
            target.setEditable(false);
        }
    }

    /**
     * Exports the simplified project schedule to an .xlsx Excel file using Apache POI.
     * Opens a FileChooser for the user to pick the destination file.
     */
    private void exportScheduleToExcel() {
        try {
            // Ensure this named module can read classes from the unnamed module (classpath),
            // so Apache POI (which is not modularized) can be accessed without JVM flags.
            try {
                Module myModule = ProjectExpandController.class.getModule();
                Module unnamed = ProjectExpandController.class.getClassLoader().getUnnamedModule();
                if (myModule != null && unnamed != null) {
                    myModule.addReads(unnamed);
                }
            } catch (Throwable ignore) {
                // Best-effort; if this fails, the build/run should be configured with --add-reads
            }
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Save Project Schedule (.xlsx)");
            chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel Workbook (*.xlsx)", "*.xlsx")
            );
            String defaultName = sanitizeFileName(vm.getName());
            if (defaultName == null || defaultName.isBlank()) defaultName = "project-schedule";
            chooser.setInitialFileName(defaultName + "-schedule.xlsx");
            Stage stage = (Stage) root.getScene().getWindow();
            java.io.File file = chooser.showSaveDialog(stage);
            if (file == null) return; // user cancelled

            // Ensure .xlsx extension
            if (!file.getName().toLowerCase().endsWith(".xlsx")) {
                file = new java.io.File(file.getParentFile(), file.getName() + ".xlsx");
            }

            // Build workbook
            try (XSSFWorkbook wb = new XSSFWorkbook();
                 java.io.FileOutputStream fos = new java.io.FileOutputStream(file)) {

                Sheet sheet = wb.createSheet("Schedule");

                // Styles
                Font titleFont = wb.createFont();
                if (titleFont instanceof XSSFFont tf) {
                    tf.setBold(true);
                    tf.setFontHeightInPoints((short) 14);
                } else {
                    titleFont.setBold(true);
                }
                CellStyle titleStyle = wb.createCellStyle();
                titleStyle.setFont(titleFont);
                titleStyle.setAlignment(HorizontalAlignment.LEFT);
                titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

                Font headerFont = wb.createFont();
                headerFont.setBold(true);
                CellStyle headerStyle = wb.createCellStyle();
                headerStyle.setFont(headerFont);
                headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                headerStyle.setAlignment(HorizontalAlignment.CENTER);
                headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                headerStyle.setBorderTop(BorderStyle.THIN);
                headerStyle.setBorderBottom(BorderStyle.THIN);
                headerStyle.setBorderLeft(BorderStyle.THIN);
                headerStyle.setBorderRight(BorderStyle.THIN);

                CellStyle textCell = wb.createCellStyle();
                textCell.setBorderTop(BorderStyle.THIN);
                textCell.setBorderBottom(BorderStyle.THIN);
                textCell.setBorderLeft(BorderStyle.THIN);
                textCell.setBorderRight(BorderStyle.THIN);
                textCell.setVerticalAlignment(VerticalAlignment.TOP);

                // Bold label for metadata rows
                Font metaFont = wb.createFont();
                metaFont.setBold(true);
                CellStyle metaLabel = wb.createCellStyle();
                metaLabel.setFont(metaFont);
                metaLabel.setVerticalAlignment(VerticalAlignment.CENTER);

                CellStyle dateCell = wb.createCellStyle();
                dateCell.cloneStyleFrom(textCell);
                short df = wb.getCreationHelper().createDataFormat().getFormat("yyyy-mm-dd");
                dateCell.setDataFormat(df);

                int r = 0;
                // Title row
                Row titleRow = sheet.createRow(r++);
                String projectName = vm.getName() == null ? "" : vm.getName().trim();
                Cell tCell = titleRow.createCell(0);
                tCell.setCellValue("Project Schedule: " + projectName);
                tCell.setCellStyle(titleStyle);

                // Generation date row
                Row genRow = sheet.createRow(r++);
                Cell gl = genRow.createCell(0);
                gl.setCellValue("Generated:");
                gl.setCellStyle(metaLabel);
                Cell gv = genRow.createCell(1);
                String now = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(LocalDateTime.now());
                gv.setCellValue(now);

                // Stakeholders (Leader, Tech Leader, Sponsor)
                String leaderName = resolvePersonNameSync(vm.getLeaderUserId());
                String techLeaderName = resolvePersonNameSync(vm.getTechLeaderId());
                String sponsorName = resolvePersonNameSync(vm.getSponsorId());

                Row lr = sheet.createRow(r++);
                Cell ll = lr.createCell(0);
                ll.setCellValue("Project Leader:");
                ll.setCellStyle(metaLabel);
                lr.createCell(1).setCellValue(leaderName == null ? "" : leaderName);

                Row tr = sheet.createRow(r++);
                Cell tl = tr.createCell(0);
                tl.setCellValue("Technical Leader:");
                tl.setCellStyle(metaLabel);
                tr.createCell(1).setCellValue(techLeaderName == null ? "" : techLeaderName);

                Row sr = sheet.createRow(r++);
                Cell sl = sr.createCell(0);
                sl.setCellValue("Sponsor:");
                sl.setCellStyle(metaLabel);
                sr.createCell(1).setCellValue(sponsorName == null ? "" : sponsorName);

                r++; // empty spacer row

                // Header row
                Row header = sheet.createRow(r++);
                String[] headers = new String[] {
                    "Activity",
                    "Responsible (ID)",
                    "Responsible Name",
                    "Status",
                    "Planned Start",
                    "Planned End",
                    "Actual Start",
                    "Actual End"
                };
                for (int c = 0; c < headers.length; c++) {
                    Cell hc = header.createCell(c);
                    hc.setCellValue(headers[c]);
                    hc.setCellStyle(headerStyle);
                }

                // First row for the project itself
                Row prow = sheet.createRow(r++);
                int c = 0;
                Cell pc0 = prow.createCell(c++);
                pc0.setCellValue(projectName);
                pc0.setCellStyle(textCell);

                // Responsible ID and Name: use project leader
                long leaderId = vm.getLeaderUserId();
                String leaderLabel = resolvePersonNameSync(leaderId);
                Cell pc1 = prow.createCell(c++);
                pc1.setCellValue(leaderId > 0 ? String.valueOf(leaderId) : "");
                pc1.setCellStyle(textCell);
                Cell pc1n = prow.createCell(c++);
                pc1n.setCellValue(leaderLabel == null ? "" : leaderLabel);
                pc1n.setCellStyle(textCell);

                String prjStatus = mapStatusForExport(vm.getStatus());
                Cell pc2 = prow.createCell(c++);
                pc2.setCellValue(prjStatus);
                pc2.setCellStyle(textCell);

                // Dates
                writeDateCell(prow.createCell(c++), vm.getPlannedStartDate(), dateCell);
                writeDateCell(prow.createCell(c++), vm.getPlannedEndDate(), dateCell);
                writeDateCell(prow.createCell(c++), vm.getActualStartDate(), dateCell);
                writeDateCell(prow.createCell(c++), vm.getActualEndDate(), dateCell);

                // Activity rows, keep current execution order
                List<ProjectActivityViewModel> list = activities.stream()
                    .sorted(Comparator.comparingInt(ProjectActivityViewModel::getExecutionOrder))
                    .toList();
                for (ProjectActivityViewModel a : list) {
                    Row row = sheet.createRow(r++);
                    int cc = 0;
                    Cell c0 = row.createCell(cc++);
                    c0.setCellValue(a.getDescription() == null ? "" : a.getDescription());
                    c0.setCellStyle(textCell);

                    long rid = a.getResponsibleId();
                    String respName = resolvePersonNameSync(rid);
                    Cell c1 = row.createCell(cc++);
                    c1.setCellValue(rid > 0 ? String.valueOf(rid) : "");
                    c1.setCellStyle(textCell);
                    Cell c1n = row.createCell(cc++);
                    c1n.setCellValue(respName == null ? "" : respName);
                    c1n.setCellStyle(textCell);

                    Cell c2 = row.createCell(cc++);
                    c2.setCellValue(mapStatusForExport(a.getStatus()));
                    c2.setCellStyle(textCell);

                    writeDateCell(row.createCell(cc++), a.getPlannedStartDate(), dateCell);
                    writeDateCell(row.createCell(cc++), a.getPlannedEndDate(), dateCell);
                    writeDateCell(row.createCell(cc++), a.getActualStartDate(), dateCell);
                    writeDateCell(row.createCell(cc++), a.getActualEndDate(), dateCell);
                }

                // Freeze the header row and autosize columns (cover metadata and data columns)
                sheet.createFreezePane(0, header.getRowNum() + 1);
                int cols = Math.max(3, headers.length);
                for (int i = 0; i < cols; i++) sheet.autoSizeColumn(i);

                wb.write(fos);
                LOGGER.info("[Excel Export] Schedule exported to: " + file.getAbsolutePath());
            }
        } catch (Exception ex) {
            LOGGER.warning("[Excel Export] Failed: " + ex.getMessage());
        }
    }

    private void writeDateCell(Cell cell, Date value, CellStyle dateStyle) {
        if (value == null) {
            cell.setBlank();
        } else {
            cell.setCellValue(value);
            cell.setCellStyle(dateStyle);
        }
    }

    private String sanitizeFileName(String name) {
        if (name == null) return null;
        String s = name.trim();
        if (s.isEmpty()) return s;
        // Remove illegal filename chars
        return s.replaceAll("[\\\\/:*?\"<>|]", "_");
    }

    /**
     * Resolves a person's display name synchronously, returning "First Last" when available.
     * Falls back to cached label or ID string if the service is unavailable.
     */
    private String resolvePersonNameSync(long personId) {
        if (personId <= 0) return null;
        String cacheKey = "person." + personId + ".label";
        Object lbl = AppContext.getInstance().get(cacheKey);
        if (lbl instanceof String s && !s.isBlank()) return s;
        try {
            PersonService ps = new PersonService();
            Respuesta r = ps.find(personId);
            if (Boolean.TRUE.equals(r.getEstado())) {
                Object po = r.getResultado("Person");
                if (po instanceof PersonDTO pp) {
                    String nm = ((pp.getFirstName() == null ? "" : pp.getFirstName().trim()) +
                                 " " +
                                 (pp.getLastName() == null ? "" : pp.getLastName().trim())).trim();
                    if (!nm.isBlank()) {
                        AppContext.getInstance().set(cacheKey, nm);
                        return nm;
                    }
                }
            }
        } catch (Exception ignore) {
            // service not reachable; will fallback to id
        }
        return String.valueOf(personId);
    }

    private String mapStatusForExport(String code) {
        if (code == null || code.isBlank()) return "";
        return switch (code.trim().toUpperCase()) {
            case "P" -> "Planificada";
            case "R" -> "En curso";
            case "S" -> "Postergada";
            case "C" -> "Finalizada";
            default -> code.trim();
        };
    }

    private void openPersonInformation(long personId, String roleLabel) {
        if (personId <= 0) {
            LOGGER.warning("[Person Info] Invalid person id: " + personId);
            return;
        }
        PersonService service = new PersonService();
        Respuesta r = service.find(personId);
        if (!Boolean.TRUE.equals(r.getEstado())) {
            LOGGER.warning(
                "[Person Info] Could not retrieve person id=" +
                    personId +
                    " | " +
                    (r != null ? r.getMensaje() : "null response")
            );
            return;
        }
        Object personObj = r.getResultado("Person");
        if (!(personObj instanceof PersonDTO p)) {
            LOGGER.warning(
                "[Person Info] Service did not return a valid PersonDTO for id=" +
                    personId
            );
            return;
        }
        AppContext.getInstance().set("personInformation.person", p);
        AppContext.getInstance().set("personInformation.role", roleLabel);
        try {
            FlowController.getInstance().goViewInWindowModal(
                "PersonInformationView",
                (javafx.stage.Stage) root.getScene().getWindow(),
                false
            );
        } finally {
            AppContext.getInstance().delete("personInformation.person");
            AppContext.getInstance().delete("personInformation.role");
        }
    }

    /**
     * Updates the project status locally and persists it via the web service.
     *
     * @param statusCode one of "P","R","S","C"
     */
    private void updateProjectStatus(String statusCode) {
        if (statusPersistInProgress) {
            LOGGER.fine("[Project Status] Update already in progress; ignoring click.");
            return;
        }
        if (statusCode == null || statusCode.isBlank() || statusCode.equalsIgnoreCase(vm.getStatus())) {
            LOGGER.fine("[Project Status] No-op status change to " + statusCode);
            return;
        }
        LOGGER.info("[Project Status] Toggling to status=" + statusCode + " for project id=" + vm.getId());
        statusPersistInProgress = true;
        try {
            String old = vm.getStatus();
            vm.setStatus(statusCode);
            ProjectService svc = new ProjectService();
            Respuesta r = svc.update(vm.toDTO());
            if (!Boolean.TRUE.equals(r.getEstado())) {
                LOGGER.warning(
                    "[Project Status] Update failed: " +
                        (r != null ? r.getMensaje() : "null") +
                        " | " +
                        (r != null ? r.getMensajeInterno() : "null")
                );
                // Revert client VM to previous status since persistence failed
                vm.setStatus(old);
                selectToggleForStatus(old);
            } else {
                LOGGER.info("[Project Status] Update OK. mensaje=" + r.getMensaje() + ", mensajeInterno=" + r.getMensajeInterno());
                Object updated = r.getResultado("Project");
                if (updated instanceof ProjectDTO p) {
                    // Refresh VM from server-confirmed DTO to keep client in sync
                    AppContext.getInstance().set("currentProject", p);
                    vm.setName(p.getName());
                    vm.setPlannedStartDate(p.getPlannedStartDate());
                    vm.setPlannedEndDate(p.getPlannedEndDate());
                    vm.setActualStartDate(p.getActualStartDate());
                    vm.setActualEndDate(p.getActualEndDate());
                    vm.setStatus(p.getStatus());
                    vm.setCreatedAt(p.getCreatedAt());
                    vm.setUpdatedAt(p.getUpdatedAt());
                    vm.setLeaderUserId(p.getLeaderUserId() == null ? 0L : p.getLeaderUserId());
                    vm.setTechLeaderId(p.getTechLeaderId() == null ? 0L : p.getTechLeaderId());
                    vm.setSponsorId(p.getSponsorId() == null ? 0L : p.getSponsorId());
                    selectToggleForStatus(p.getStatus());
                }
            }
        } finally {
            statusPersistInProgress = false;
        }
    }

    private void selectToggleForStatus(String status) {
        if (ProjectStatus == null) return;
        if (status == null || status.isBlank()) {
            // fallback to Pending if undefined
            ProjectStatus.selectToggle(tgProjectStatusPending);
            return;
        }
        String code = status.trim().toUpperCase();
        ProjectStatus.getToggles().stream()
            .filter(t -> t.getUserData() != null && code.equalsIgnoreCase(t.getUserData().toString()))
            .findFirst()
            .ifPresent(ProjectStatus::selectToggle);
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

            // Persist via WS
            try {
                ProjectActivityService svc = new ProjectActivityService();
                Respuesta r = svc.update(selectedActivity.toDTO());
                if (!Boolean.TRUE.equals(r.getEstado())) {
                    LOGGER.warning(
                        "[Activity Update] Persist failed: " +
                        (r != null ? r.getMensaje() : "null") +
                        " | " +
                        (r != null ? r.getMensajeInterno() : "null")
                    );
                }
            } catch (Exception ex) {
                LOGGER.warning(
                    "[Activity Update] Exception while persisting: " +
                    ex.getMessage()
                );
            }
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
        // Responsible column: show full name; if not cached yet, trigger async fetch and temporarily show "-"
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
                // Kick off async fetch so next refresh shows the name
                new Thread(() -> {
                    try {
                        PersonService ps = new PersonService();
                        Respuesta r = ps.find(rid);
                        if (Boolean.TRUE.equals(r.getEstado())) {
                            Object po = r.getResultado("Person");
                            if (po instanceof PersonDTO pp) {
                                String nm = ((pp.getFirstName() == null ? "" : pp.getFirstName().trim()) +
                                             " " +
                                             (pp.getLastName() == null ? "" : pp.getLastName().trim())).trim();
                                if (!nm.isBlank()) {
                                    AppContext.getInstance().set("person." + rid + ".label", nm);
                                    Platform.runLater(() -> tbvActivities.refresh());
                                }
                            }
                        }
                    } catch (Exception ignore) {}
                }, "resp-label-fetch-" + rid).start();
                return "-";
            });
        });

        // Items and default sort by executionOrder
        tbvActivities.setItems(activities);
        tbvActivities.getSortOrder().clear();
        tbvActivities.setSortPolicy(tv -> false);
        // Enforce execution-order display and disable column-based sorting
        tbcActivityName.setSortable(false);
        tbcActivityStatus.setSortable(false);
        tbcActivityResponsible.setSortable(false);
        activities.sort(
            Comparator.comparingInt(ProjectActivityViewModel::getExecutionOrder)
        );

        // Status change handling is wired in bindFields()

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
        boolean anyChanged = false;
        for (int i = 0; i < activities.size(); i++) {
            ProjectActivityViewModel a = activities.get(i);
            int newOrder = i + 1; // 1-based ordering
            if (a.getExecutionOrder() != newOrder) {
                a.setExecutionOrder(newOrder);
                anyChanged = true;
            }
        }
        activities.sort(
            Comparator.comparingInt(ProjectActivityViewModel::getExecutionOrder)
        );
        tbvActivities.refresh();

        if (anyChanged) {
            ProjectActivityService svc = new ProjectActivityService();
            for (ProjectActivityViewModel a : activities) {
                try {
                    Respuesta r = svc.update(a.toDTO());
                    if (!Boolean.TRUE.equals(r.getEstado())) {
                        LOGGER.fine(
                            "[Activity Reorder] Persist failed id=" +
                            a.getId() +
                            ": " +
                            (r != null ? r.getMensaje() : "null")
                        );
                    }
                } catch (Exception ex) {
                    LOGGER.fine(
                        "[Activity Reorder] Exception persisting id=" +
                        a.getId() +
                        ": " +
                        ex.getMessage()
                    );
                }
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
            activities.sort(
                Comparator.comparingInt(
                    ProjectActivityViewModel::getExecutionOrder
                )
            );
            tbvActivities.refresh();
            LOGGER.fine(
                "[Activities Load] After filter size=" + activities.size()
            );
            // Prefetch person labels for a clean UI without ID fallbacks
            prefetchResponsibleLabels();
        });
        task.setOnFailed(e -> {
            activities.clear();
        });
        Thread t = new Thread(task, "load-activities");
        t.setDaemon(true);
        t.start();
    }

    private void prefetchResponsibleLabels() {
        List<Long> ids = activities.stream()
            .map(ProjectActivityViewModel::getResponsibleId)
            .filter(id -> id > 0)
            .distinct()
            .toList();
        if (ids.isEmpty()) return;
        new Thread(() -> {
            PersonService ps = new PersonService();
            boolean any = false;
            for (Long id : ids) {
                String cacheKey = "person." + id + ".label";
                Object lbl = AppContext.getInstance().get(cacheKey);
                if (lbl instanceof String s && !s.isBlank()) continue;
                try {
                    Respuesta r = ps.find(id);
                    if (Boolean.TRUE.equals(r.getEstado())) {
                        Object po = r.getResultado("Person");
                        if (po instanceof PersonDTO pp) {
                            String nm = ((pp.getFirstName() == null ? "" : pp.getFirstName().trim()) +
                                         " " +
                                         (pp.getLastName() == null ? "" : pp.getLastName().trim())).trim();
                            if (!nm.isBlank()) {
                                AppContext.getInstance().set(cacheKey, nm);
                                any = true;
                            }
                        }
                    }
                } catch (Exception ignore) {}
            }
            if (any) Platform.runLater(() -> tbvActivities.refresh());
        }, "prefetch-person-labels").start();
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

        // Resolve responsible label; synchronous lookup to avoid showing raw IDs
        long rid = item.getResponsibleId();
        String responsibleLabel = rid > 0 ? resolvePersonNameSync(rid) : "-";
        txfResponsible.setText(responsibleLabel);
        // Resolve createdBy label synchronously
        long cbid = item.getCreatedById();
        String createdByLabel = cbid > 0 ? resolvePersonNameSync(cbid) : "-";
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

    @FXML
    private void onActionTxfSponsorId(ActionEvent event) {
        openPersonInformation(vm.getSponsorId(), "Sponsor");
    }

    @FXML
    private void onActionTxfLeaderId(ActionEvent event) {
        openPersonInformation(vm.getLeaderUserId(), "Project Leader");
    }

    @FXML
    private void onActionTechLeaderId(ActionEvent event) {
        openPersonInformation(vm.getTechLeaderId(), "Technical Leader");
    }

    @FXML
    private void onActionTgProjectStatusPending(ActionEvent event) {
        updateProjectStatus("P");
    }

    @FXML
    private void onActionTgProjectStatusRunning(ActionEvent event) {
        updateProjectStatus("R");
    }

    @FXML
    private void onActionTgProjectStatusSuspended(ActionEvent event) {
        updateProjectStatus("S");
    }

    @FXML
    private void onActionTgProjectStatusCompleted(ActionEvent event) {
        updateProjectStatus("C");
    }
}
