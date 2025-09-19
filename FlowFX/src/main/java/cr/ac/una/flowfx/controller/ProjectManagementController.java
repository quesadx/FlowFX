package cr.ac.una.flowfx.controller;

import cr.ac.una.flowfx.model.PersonDTO;
import cr.ac.una.flowfx.model.ProjectDTO;
import cr.ac.una.flowfx.service.ProjectService;
import cr.ac.una.flowfx.util.AnimationManager;
import cr.ac.una.flowfx.util.AppContext;
import cr.ac.una.flowfx.util.FlowController;
import cr.ac.una.flowfx.util.Mensaje;
import cr.ac.una.flowfx.util.Respuesta;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.net.URL;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author quesadx
 */

public class ProjectManagementController
    extends Controller
    implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(
        ProjectManagementController.class.getName()
    );

    // Delete mode and animation constants
    private boolean deleteMode = false;
    private final Map<MFXButton, RotateTransition> wiggleAnimations = new HashMap<>();
    
    private static final double WIGGLE_ANGLE = 2.0;
    private static final Duration WIGGLE_DURATION = Duration.millis(150);
    private static final Interpolator WIGGLE_INTERPOLATOR = Interpolator.EASE_BOTH;

    @FXML
    private AnchorPane root;

    @FXML
    private TilePane tpProjects;

    @FXML
    private VBox vbCover;

    @FXML
    private VBox vbProjectCreationDisplay;

    @FXML
    private MFXTextField txfProjectName;

    @FXML
    private TextArea txaProjectDescription;

    @FXML
    private MFXDatePicker dpProjectStartDate;

    @FXML
    private MFXDatePicker dpProjectEndDate;

    @FXML
    private MFXTextField txfSponsorId;

    @FXML
    private MFXTextField txfLeaderId;

    @FXML
    private MFXTextField txfTechLeaderId;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadUserProjects();
    }

    @Override
    public void initialize() {
        clearForm();
        loadUserProjects();
        //FlowController.getInstance().limpiarLoader("ProjectExpandView");
    }

    @FXML
    private void onActionBtnReturnProjectCreation(ActionEvent event) {
        AnimationManager.hidePopup(vbProjectCreationDisplay, vbCover);
        clearForm();
    }

    @FXML
    private void onActionCreateProject(ActionEvent event) {
        AnimationManager.showPopup(vbProjectCreationDisplay, vbCover);
    }

    @FXML
    private void onActionConfirmProjectCreation(ActionEvent event) {
        ProjectDTO dto = new ProjectDTO();
        dto.setName(getTrimmedText(txfProjectName));
        dto.setPlannedStartDate(getDateFromPicker(dpProjectStartDate));
        dto.setPlannedEndDate(getDateFromPicker(dpProjectEndDate));
        dto.setStatus("P");

        // Prefer selected PersonDTOs stored in context (set by selector dialog)
        PersonDTO leaderSel = (PersonDTO) AppContext.getInstance().get(
            "txfLeaderIdSelectedPerson"
        );
        PersonDTO techSel = (PersonDTO) AppContext.getInstance().get(
            "txfTechLeaderIdSelectedPerson"
        );
        PersonDTO sponsorSel = (PersonDTO) AppContext.getInstance().get(
            "txfSponsorIdSelectedPerson"
        );

        Long leaderId = leaderSel != null
            ? leaderSel.getId()
            : parseLongSafe(txfLeaderId.getText());
        Long techId = techSel != null
            ? techSel.getId()
            : parseLongSafe(txfTechLeaderId.getText());
        Long sponsorId = sponsorSel != null
            ? sponsorSel.getId()
            : parseLongSafe(txfSponsorId.getText());

        PersonDTO user = (PersonDTO) AppContext.getInstance().get("user");
        if (leaderId == null && user != null) leaderId = user.getId();
        if (techId == null && user != null) techId = user.getId();
        if (sponsorId == null && user != null) sponsorId = user.getId();

        ProjectService service = new ProjectService();
        Respuesta response = service.create(dto, leaderId, techId, sponsorId);
        if (Boolean.TRUE.equals(response.getEstado())) {
            new Mensaje().showModal(
                javafx.scene.control.Alert.AlertType.INFORMATION,
                "Proyecto",
                root.getScene().getWindow(),
                "Proyecto creado correctamente."
            );
            AnimationManager.hidePopup(vbProjectCreationDisplay, vbCover);
            clearForm();
            loadUserProjects();
        } else {
            String detail = response.getMensajeInterno() != null &&
                !response.getMensajeInterno().isBlank()
                ? ("\nDetalle: " + response.getMensajeInterno())
                : "";
            new Mensaje().showModal(
                javafx.scene.control.Alert.AlertType.ERROR,
                "Proyecto",
                root.getScene().getWindow(),
                response.getMensaje() + detail
            );
            LOGGER.log(
                Level.WARNING,
                "Project creation failed. mensaje={0}, mensajeInterno={1}",
                new Object[] {
                    response.getMensaje(),
                    response.getMensajeInterno(),
                }
            );
        }
    }

    private void loadUserProjects() {
        tpProjects.getChildren().clear();
        Object userObj = AppContext.getInstance().get("user");
        if (!(userObj instanceof PersonDTO)) return;
        PersonDTO user = (PersonDTO) userObj;
        ProjectService s = new ProjectService();
        Respuesta r = s.findProjectsForUser(user.getId());
        if (!Boolean.TRUE.equals(r.getEstado())) return;
        @SuppressWarnings("unchecked")
        List<ProjectDTO> projects = (List<ProjectDTO>) r.getResultado(
            "Projects"
        );
        if (projects == null) return;
        for (ProjectDTO p : projects) {
            Board b = new Board();
            b.getLblTitle().setText(p.getName());
            // Show Spanish status names instead of raw codes
            b.getLblStatus().setText(mapStatusToSpanish(p.getStatus()));
            
            // Set up expand button
            b.getBtnExpandProject().setOnAction(e -> {
                AppContext.getInstance().set("currentProject", p);
                FlowController.getInstance().goView("ProjectExpandView");
            });
            
            // Set up delete button
            setupDeleteButton(b, p);
            
            tpProjects.getChildren().add(b);
        }
    }

    private Long parseLongSafe(String text) {
        if (text == null) return null;
        String trimmed = text.trim();
        if (trimmed.isEmpty()) return null;
        // Extract first sequence of digits if mixed content
        Matcher m = Pattern.compile("(\\d+)").matcher(trimmed);
        if (m.find()) {
            try {
                return Long.parseLong(m.group(1));
            } catch (NumberFormatException ignored) {}
        }
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
        return picker.getValue() != null
            ? Date.from(
                picker
                    .getValue()
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
            )
            : null;
    }

    @FXML
    private void onActionBtnSelectSponsor(ActionEvent event) {
        selectPersonForField(txfSponsorId);
    }

    @FXML
    private void onActionBtnSelectLeader(ActionEvent event) {
        selectPersonForField(txfLeaderId);
    }

    @FXML
    private void onActionBtnSelectTechLeader(ActionEvent event) {
        selectPersonForField(txfTechLeaderId);
    }

    private void selectPersonForField(MFXTextField targetField) {
        // Allow editing while selecting
        targetField.setEditable(true);
        Stage current = (Stage) root.getScene().getWindow();
        FlowController.getInstance().goViewInWindowModal(
            "PersonSelectionView",
            current,
            false
        );
        Object sel = AppContext.getInstance().get("personSelectionResult");
        if (sel instanceof PersonDTO) {
            PersonDTO p = (PersonDTO) sel;
            targetField.setText(
                safe(p.getFirstName()) + " " + safe(p.getLastName())
            );
            // store plain id numeric value (if needed elsewhere) into context keyed by field
            AppContext.getInstance().set(
                targetField.getId() + "SelectedPerson",
                p
            );
            // Make field read-only after selection
            targetField.setEditable(false);
        }
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }

        /**
     * Maps status codes to Spanish display names.
     * P = Planned / In Planning
     * R = Running / In Progress
     * S = Suspended
     * C = Closed / Completed
     */
    private String mapStatusToSpanish(String code) {
        if (code == null || code.isBlank()) return "-";
        return switch (code.trim().toUpperCase()) {
            case "P" -> "Planificada";
            case "R" -> "En curso";
            case "S" -> "Suspendida";
            case "C" -> "Finalizada";
            default -> code.trim();
        };
    }

    /**
     * Sets up the delete button for a Board component.
     */
    private void setupDeleteButton(Board board, ProjectDTO project) {
        // Initially hide delete button
        board.getBtnDeleteProject().setVisible(deleteMode);
        board.getBtnDeleteProject().setManaged(deleteMode);
        
        // Add wiggle animation if in delete mode
        if (deleteMode) {
            startWiggleAnimation(board.getBtnDeleteProject());
        }
        
        // Set up delete action
        board.getBtnDeleteProject().setOnAction(e -> deleteProject(project));
    }

    /**
     * Starts a wiggle animation on the given button (iOS-style delete mode).
     */
    private void startWiggleAnimation(MFXButton button) {
        if (button == null) return;
        
        // Stop any existing animation
        stopWiggleAnimation(button);
        
        // Create new wiggle animation
        RotateTransition wiggle = new RotateTransition(WIGGLE_DURATION, button);
        wiggle.setFromAngle(-WIGGLE_ANGLE);
        wiggle.setToAngle(WIGGLE_ANGLE);
        wiggle.setAutoReverse(true);
        wiggle.setCycleCount(RotateTransition.INDEFINITE);
        wiggle.setInterpolator(WIGGLE_INTERPOLATOR);
        
        // Store animation for later cleanup
        wiggleAnimations.put(button, wiggle);
        
        // Start wiggling
        wiggle.play();
    }

    /**
     * Stops wiggle animation on the given button.
     */
    private void stopWiggleAnimation(MFXButton button) {
        if (button == null) return;
        
        RotateTransition existingAnimation = wiggleAnimations.remove(button);
        if (existingAnimation != null) {
            existingAnimation.stop();
            // Reset rotation to 0
            button.setRotate(0);
        }
    }

    /**
     * Deletes a project with Spanish confirmation dialog.
     */
    private void deleteProject(ProjectDTO project) {
        try {
            if (project == null || project.getId() == null) {
                LOGGER.log(Level.WARNING, "Cannot delete: invalid project");
                return;
            }

            // Show Spanish confirmation dialog
            boolean confirmed = new Mensaje().showConfirmation(
                "Eliminar Proyecto",
                root.getScene().getWindow(),
                "¿Está seguro que desea eliminar el proyecto '" + 
                safe(project.getName()) + "'?\n\nEsta acción no se puede deshacer."
            );

            if (confirmed) {
                ProjectService service = new ProjectService();
                Respuesta response = service.delete(project.getId());
                
                if (Boolean.TRUE.equals(response.getEstado())) {
                    new Mensaje().showModal(
                        javafx.scene.control.Alert.AlertType.INFORMATION,
                        "Eliminar Proyecto",
                        root.getScene().getWindow(),
                        "El proyecto ha sido eliminado correctamente."
                    );
                    
                    // Reload projects to refresh the view
                    loadUserProjects();
                    
                    LOGGER.log(Level.INFO, "Project deleted successfully: " + project.getId());
                } else {
                    new Mensaje().showModal(
                        javafx.scene.control.Alert.AlertType.ERROR,
                        "Eliminar Proyecto",
                        root.getScene().getWindow(),
                        "Error al eliminar el proyecto: " + response.getMensaje()
                    );
                    
                    LOGGER.log(Level.WARNING, 
                        "Project deletion failed: " + response.getMensaje());
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error deleting project", ex);
            new Mensaje().showModal(
                javafx.scene.control.Alert.AlertType.ERROR,
                "Eliminar Proyecto",
                root.getScene().getWindow(),
                "Error inesperado al eliminar el proyecto."
            );
        }
    }

    /**
     * Toggles delete mode and updates all existing Board components.
     */
    private void toggleDeleteMode() {
        deleteMode = !deleteMode;
        
        // Update all existing Board components
        tpProjects.getChildren().forEach(node -> {
            if (node instanceof Board) {
                Board board = (Board) node;
                MFXButton deleteBtn = board.getBtnDeleteProject();
                
                deleteBtn.setVisible(deleteMode);
                deleteBtn.setManaged(deleteMode);
                
                // Start/stop wiggle animation
                if (deleteMode) {
                    startWiggleAnimation(deleteBtn);
                } else {
                    stopWiggleAnimation(deleteBtn);
                }
            }
        });
        
        LOGGER.log(Level.INFO, "Delete mode " + (deleteMode ? "enabled" : "disabled"));
    }

    @FXML
    private void onActionDeleteMode(ActionEvent event) {
        toggleDeleteMode();
    }
}
