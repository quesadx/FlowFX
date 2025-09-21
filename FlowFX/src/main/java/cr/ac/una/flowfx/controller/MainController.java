package cr.ac.una.flowfx.controller;

import cr.ac.una.flowfx.model.PersonDTO;
import cr.ac.una.flowfx.model.ProjectActivityDTO;
import cr.ac.una.flowfx.model.ProjectDTO;
import cr.ac.una.flowfx.service.PersonService;
import cr.ac.una.flowfx.service.ProjectActivityService;
import cr.ac.una.flowfx.service.ProjectService;
import cr.ac.una.flowfx.util.AnimationManager;
import cr.ac.una.flowfx.util.AppContext;
import cr.ac.una.flowfx.util.FlowController;
import cr.ac.una.flowfx.util.Mensaje;
import cr.ac.una.flowfx.util.Respuesta;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Main controller handling login and dashboard.
 * Updated to treat project/activity status as String (e.g., "P","R","S","C").
 */
public class MainController extends Controller implements Initializable {

    private static final java.util.logging.Logger LOGGER =
        java.util.logging.Logger.getLogger(MainController.class.getName());

    @FXML
    private AnchorPane root;

    @FXML
    private VBox vbCover;

    @FXML
    private VBox vbLogInDisplay;

    @FXML
    private VBox vbSignUpDisplay;

    @FXML
    private MFXTextField txfUsername;

    @FXML
    private MFXPasswordField psfUserPassword;

    @FXML
    private MFXButton btnLogIn;

    @FXML
    private MFXTextField txfPersonId;

    @FXML
    private MFXTextField txfPersonFirstName;

    @FXML
    private MFXTextField txfPersonLastName;

    @FXML
    private MFXTextField txfPersonEmail;

    @FXML
    private MFXTextField txfPersonUsername;

    @FXML
    private MFXPasswordField pswPersonPassword;

    @FXML
    private MFXCheckbox cbIsAdmin;

    @FXML
    private MFXCheckbox cbIsActive;

    @FXML
    private PieChart pcPersonActivities;

    private boolean userLoggedIn = false;
    private PersonDTO user;

    @FXML
    private StackedBarChart<?, ?> sbcActivitiesPerProjects;

    @FXML
    private TableView<ProjectDTO> tvProjects;

    @FXML
    private TableColumn<ProjectDTO, String> tbcProjectName;

    @FXML
    private TableColumn<ProjectDTO, String> tbcProjectStatus;

    @FXML
    private ListView<String> lvActivities;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Show login popup initially
        AnimationManager.showPopup(vbLogInDisplay, vbCover);

        Object nav = AppContext.getInstance().get("navigationBar");
        if (nav instanceof VBox) ((VBox) nav).setDisable(!userLoggedIn);
        // Disable dock while logged out
        Object dockBarInit = AppContext.getInstance().get("dockBar");
        if (dockBarInit instanceof javafx.scene.layout.HBox) {
            ((javafx.scene.layout.HBox) dockBarInit).setDisable(!userLoggedIn);
        }

        // Make it so all the text inputs have a limit of N characters
        setTextFieldLimit(txfUsername, 20);
        setTextFieldLimit(psfUserPassword, 20);
        setTextFieldLimit(txfPersonId, 9);
        setTextFieldLimit(txfPersonFirstName, 20);
        setTextFieldLimit(txfPersonLastName, 20);
        setTextFieldLimit(txfPersonEmail, 20);
        setTextFieldLimit(txfPersonUsername, 20);
        setTextFieldLimit(pswPersonPassword, 20);

        // Configure projects table columns and double-click navigation
        tbcProjectName.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(
                data.getValue().getName() == null
                    ? "-"
                    : data.getValue().getName()
            )
        );
        // Status is String; show Spanish names instead of raw codes
        tbcProjectStatus.setCellValueFactory(data -> {
            String st = data.getValue().getStatus();
            return new javafx.beans.property.SimpleStringProperty(
                mapStatusToSpanish(st)
            );
        });

        tvProjects.setRowFactory(tv -> {
            TableRow<ProjectDTO> row = new TableRow<>();
            row.setOnMouseClicked(evt -> {
                if (!row.isEmpty() && evt.getClickCount() == 2) {
                    ProjectDTO selected = row.getItem();
                    AppContext.getInstance().set("currentProject", selected);
                    Object navBar = AppContext.getInstance().get(
                        "navigationBar"
                    );
                    if (navBar instanceof VBox) ((VBox) navBar).setDisable(
                        true
                    );
                    FlowController.getInstance().goView("ProjectExpandView");
                }
            });
            return row;
        });
    }

    private void setTextFieldLimit(MFXTextField txf, int i) {
        txf.addEventFilter(KeyEvent.KEY_TYPED, e -> {
            if (txf.getText().length() >= i) {
                e.consume();
            }
        });
    }

    @Override
    public void initialize() {
        refreshDashboard();
    }

    @FXML
    private void onActionObtainInfo(ActionEvent event) {
        Stage stage = (Stage) root.getScene().getWindow();
        FlowController.getInstance().goViewInWindowModal(
            "AboutView",
            stage,
            false
        );
    }

    @FXML
    private void onActionLogOut(ActionEvent event) {
        userLoggedIn = false;
        user = null;
        AppContext.getInstance().set("user", null);

        Object nav = AppContext.getInstance().get("navigationBar");
        if (nav instanceof VBox) ((VBox) nav).setDisable(true);
        // Disable dock on logout
        Object dockBarLogout = AppContext.getInstance().get("dockBar");
        if (dockBarLogout instanceof javafx.scene.layout.HBox) {
            ((javafx.scene.layout.HBox) dockBarLogout).setDisable(true);
        }
        
        // Update dock component state (hide admin button)
        Object dockComponent = AppContext.getInstance().get("dockComponent");
        if (dockComponent instanceof cr.ac.una.flowfx.controller.DockComponent) {
            ((cr.ac.una.flowfx.controller.DockComponent) dockComponent).updateDockState();
        }

        AnimationManager.showPopup(vbLogInDisplay, vbCover);
        refreshDashboard();
    }

    @FXML
    private void onActionObtainHelp(ActionEvent event) {
        Stage stage = (Stage) root.getScene().getWindow();
        FlowController.getInstance().goViewInWindowModal(
            "HelpView",
            stage,
            false
        );
    }
    
    @FXML
    private void onActionBtnLogIn(ActionEvent event) {
        String username = getTrimmedText(txfUsername);
        String password = psfUserPassword.getText();
        if (username.isEmpty() || password == null || password.isEmpty()) {
            new Mensaje().showModal(
                Alert.AlertType.ERROR,
                "Login",
                root.getScene().getWindow(),
                "Complete todos los campos requeridos."
            );
            return;
        }
        PersonService personService = new PersonService();
        Respuesta response = personService.validateCredentials(
            username,
            password
        );
        if (Boolean.TRUE.equals(response.getEstado())) {
            user = (PersonDTO) response.getResultado("Person");
            if (user == null) {
                // Defensive: server reported success but did not include a Person payload.
                // Likely cause: WS did not set mensajeInterno with a Person JSON.
                LOGGER.warning(
                    "Login success without Person payload. mensajeInterno=" +
                    response.getMensajeInterno()
                );
                new Mensaje().showModal(
                    Alert.AlertType.ERROR,
                    "Login",
                    root.getScene().getWindow(),
                    "No se recibió información del usuario. Intente de nuevo o contacte al administrador."
                );
                return;
            }
            AppContext.getInstance().set("user", user);
            userLoggedIn = true;

            // Enable navigation bar after successful login
            Object nav = AppContext.getInstance().get("navigationBar");
            if (nav instanceof VBox) ((VBox) nav).setDisable(false);
            // Enable dock on successful login
            Object dockBarLogin = AppContext.getInstance().get("dockBar");
            if (dockBarLogin instanceof javafx.scene.layout.HBox) {
                ((javafx.scene.layout.HBox) dockBarLogin).setDisable(false);
            }
            
            // Update dock component state (including admin button visibility)
            Object dockComponent = AppContext.getInstance().get("dockComponent");
            if (dockComponent instanceof cr.ac.una.flowfx.controller.DockComponent) {
                ((cr.ac.una.flowfx.controller.DockComponent) dockComponent).updateDockState();
            }

            AnimationManager.hidePopup(vbLogInDisplay, vbCover);
            clearLogInFields();
            refreshDashboard();
        } else {
            new Mensaje().showModal(
                Alert.AlertType.ERROR,
                "Login",
                root.getScene().getWindow(),
                response.getMensaje()
            );
        }
    }

    @FXML
    private void onMouseClickedLblSignUp(MouseEvent event) {
        AnimationManager.hidePopup(vbLogInDisplay);
        AnimationManager.showPopup(vbSignUpDisplay, vbCover);
    }

    @FXML
    private void onMouseClickedLblPasswordRecovery(MouseEvent event) {
        // Open person selection as a temporary password recovery flow
        Stage stage = (Stage) root.getScene().getWindow();
        FlowController.getInstance().goViewInWindowModal(
            "PersonSelectionView",
            stage,
            false
        );
    }

    @FXML
    private void onActionBtnCancelPersonSignUp(ActionEvent event) {
        AnimationManager.hidePopup(vbSignUpDisplay);
        AnimationManager.showPopup(vbLogInDisplay, vbCover);
        clearSignUpFields();
    }

    @FXML
    private void onActionBtnPersonSignUp(ActionEvent event) {
        PersonDTO newUser = extractPersonFromSignUp();
        if (newUser == null) return;
        PersonService service = new PersonService();
        Respuesta response = service.create(newUser);
        if (Boolean.TRUE.equals(response.getEstado())) {
            new Mensaje().showModal(
                Alert.AlertType.INFORMATION,
                "Registro",
                root.getScene().getWindow(),
                "Usuario registrado correctamente."
            );
            AnimationManager.hidePopup(vbSignUpDisplay);
            AnimationManager.showPopup(vbLogInDisplay, vbCover);
            clearSignUpFields();
        } else {
            new Mensaje().showModal(
                Alert.AlertType.ERROR,
                "Registro",
                root.getScene().getWindow(),
                response.getMensaje()
            );
        }
    }

    private void refreshDashboard() {
        // Require logged user
        Object u = AppContext.getInstance().get("user");
        if (!(u instanceof PersonDTO)) {
            tvProjects.setItems(FXCollections.observableArrayList());
            lvActivities.setItems(FXCollections.observableArrayList());
            pcPersonActivities.setData(FXCollections.observableArrayList());
            sbcActivitiesPerProjects.getData().clear();
            return;
        }

        PersonDTO userDto = (PersonDTO) u;

        // Load projects for user
        ProjectService projectService = new ProjectService();
        Respuesta r = projectService.findProjectsForUser(userDto.getId());
        List<ProjectDTO> projects = new ArrayList<>();
        if (Boolean.TRUE.equals(r.getEstado())) {
            @SuppressWarnings("unchecked")
            List<ProjectDTO> pr = (List<ProjectDTO>) r.getResultado("Projects");
            if (pr != null) projects.addAll(pr);
        }
        tvProjects.setItems(FXCollections.observableArrayList(projects));

        // Activities list: latest N activities for user with project names
        ProjectActivityService actService = new ProjectActivityService();
        Respuesta ar = actService.findRecentForUser(userDto.getId(), 20);
        List<String> items = new ArrayList<>();
        if (Boolean.TRUE.equals(ar.getEstado())) {
            // Try both possible keys for activities data
            @SuppressWarnings("unchecked")
            List<ProjectActivityDTO> acts = (List<ProjectActivityDTO>) ar.getResultado("ProjectActivities");
            if (acts == null) {
                @SuppressWarnings("unchecked")
                List<ProjectActivityDTO> activitiesFallback = (List<ProjectActivityDTO>) ar.getResultado("Activities");
                acts = activitiesFallback;
            }
            
            LOGGER.info("Dashboard: Found " + (acts != null ? acts.size() : 0) + " activities for user " + userDto.getId());
            
            if (acts != null && !acts.isEmpty()) {
                // Create a map of projectId -> projectName for efficiency
                java.util.Map<Long, String> projectNames = new java.util.HashMap<>();
                for (ProjectDTO p : projects) {
                    if (p.getId() != null && p.getName() != null) {
                        projectNames.put(p.getId(), p.getName());
                    }
                }
                
                // Collect missing project IDs and fetch their names
                java.util.Set<Long> missingProjectIds = new java.util.HashSet<>();
                for (ProjectActivityDTO a : acts) {
                    if (a.getProjectId() != null && !projectNames.containsKey(a.getProjectId())) {
                        missingProjectIds.add(a.getProjectId());
                    }
                }
                
                LOGGER.info("Dashboard: Need to fetch " + missingProjectIds.size() + " additional project names");
                
                // Fetch missing project names
                for (Long projectId : missingProjectIds) {
                    Respuesta projectResp = projectService.find(projectId);
                    if (Boolean.TRUE.equals(projectResp.getEstado())) {
                        Object projObj = projectResp.getResultado("Project");
                        if (projObj instanceof ProjectDTO proj && proj.getName() != null) {
                            projectNames.put(projectId, proj.getName());
                            LOGGER.fine("Dashboard: Resolved project name for ID " + projectId + ": " + proj.getName());
                        }
                    }
                }
                
                for (ProjectActivityDTO a : acts) {
                    String activityName = a.getDescription() == null
                            ? "Actividad"
                            : a.getDescription();
                    String projectName = "Proyecto desconocido";
                    
                    // Resolve project name if projectId is available
                    if (a.getProjectId() != null) {
                        String resolvedName = projectNames.get(a.getProjectId());
                        if (resolvedName != null) {
                            projectName = resolvedName;
                        } else {
                            LOGGER.warning("Dashboard: No se encontró el nombre del proyecto para activity projectId=" + a.getProjectId());
                        }
                    } else {
                        LOGGER.warning("Dashboard: La actividad tiene null projectId: " + a.getId());
                    }
                    
                    String status = mapStatusToSpanish(a.getStatus());
                    String label = activityName + ": " + projectName + " [" + status + "]";
                    items.add(label);
                    LOGGER.fine("Dashboard: Se agregó: " + label);
                }
            } else {
                LOGGER.warning("Dashboard: No se encontraron actividades para el usuario " + userDto.getId());
            }
        } else {
            LOGGER.warning("Dashboard: findRecentForUser fallido: " + (ar != null ? ar.getMensaje() : "respuesta null"));
            // Fallback: If the service is failing due to backend issues, try to get activities from project activities
            LOGGER.info("Dashboard: Intentando método alternativo para obtener actividades...");
            try {
                Respuesta allActivitiesResp = actService.findAll();
                if (Boolean.TRUE.equals(allActivitiesResp.getEstado())) {
                    @SuppressWarnings("unchecked")
                    List<ProjectActivityDTO> allActs = (List<ProjectActivityDTO>) allActivitiesResp.getResultado("ProjectActivities");
                    if (allActs == null) {
                        @SuppressWarnings("unchecked")
                        List<ProjectActivityDTO> fallbackActs = (List<ProjectActivityDTO>) allActivitiesResp.getResultado("Activities");
                        allActs = fallbackActs;
                    }
                    
                    if (allActs != null) {
                        // Filter activities where user is responsible or creator
                        List<ProjectActivityDTO> userActivities = allActs.stream()
                            .filter(a -> {
                                Long responsibleId = a.getResponsibleId();
                                Long createdById = a.getCreatedById();
                                return (responsibleId != null && responsibleId.equals(userDto.getId())) ||
                                       (createdById != null && createdById.equals(userDto.getId()));
                            })
                            .limit(20)
                            .toList();
                        
                        LOGGER.info("Dashboard: Método alternativo encontró " + userActivities.size() + " actividades");
                        
                        if (!userActivities.isEmpty()) {
                            // Create project name map
                            java.util.Map<Long, String> projectNames = new java.util.HashMap<>();
                            for (ProjectDTO p : projects) {
                                if (p.getId() != null && p.getName() != null) {
                                    projectNames.put(p.getId(), p.getName());
                                }
                            }
                            
                            // Process user activities
                            for (ProjectActivityDTO a : userActivities) {
                                String activityName = a.getDescription() == null ? "Actividad" : a.getDescription();
                                String projectName = "Proyecto desconocido";
                                
                                if (a.getProjectId() != null) {
                                    String resolvedName = projectNames.get(a.getProjectId());
                                    if (resolvedName != null) {
                                        projectName = resolvedName;
                                    } else {
                                        // Try to fetch project name
                                        Respuesta projectResp = projectService.find(a.getProjectId());
                                        if (Boolean.TRUE.equals(projectResp.getEstado())) {
                                            Object projObj = projectResp.getResultado("Project");
                                            if (projObj instanceof ProjectDTO proj && proj.getName() != null) {
                                                projectName = proj.getName();
                                                projectNames.put(a.getProjectId(), proj.getName());
                                            }
                                        }
                                    }
                                }
                                
                                String status = mapStatusToSpanish(a.getStatus());
                                String label = activityName + ": " + projectName + " [" + status + "]";
                                items.add(label);
                            }
                        }
                    }
                }
            } catch (Exception fallbackEx) {
                LOGGER.warning("Dashboard: Método alternativo también falló: " + fallbackEx.getMessage());
            }
        }
        lvActivities.setItems(FXCollections.observableArrayList(items));

        // Pie chart: status distribution across user's projects (based on first char of String)
        int cntP = 0,
            cntR = 0,
            cntS = 0,
            cntC = 0,
            cntU = 0;
        for (ProjectDTO p : projects) {
            char sc = firstStatusChar(p.getStatus());
            switch (sc) {
                case 'P':
                    cntP++;
                    break;
                case 'R':
                    cntR++;
                    break;
                case 'S':
                    cntS++;
                    break;
                case 'C':
                    cntC++;
                    break;
                default:
                    cntU++;
                    break;
            }
        }
        var pieData = FXCollections.observableArrayList(
            new PieChart.Data("Planificada", cntP),
            new PieChart.Data("En curso", cntR),
            new PieChart.Data("Suspendida", cntS),
            new PieChart.Data("Finalizada", cntC)
        );
        if (cntU > 0) pieData.add(new PieChart.Data("Desconocido", cntU));
        pcPersonActivities.setData(pieData);

        // Stacked bar: individual activity counts per project
        // Safely cast to a typed chart to add typed series
        @SuppressWarnings("unchecked")
        StackedBarChart<String, Number> chart = (StackedBarChart<
            String,
            Number
        >) (StackedBarChart<?, ?>) sbcActivitiesPerProjects;
        chart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Actividades");
        
        // Count activities per project individually for accurate display
        for (ProjectDTO p : projects) {
            if (p.getId() != null && p.getName() != null) {
                List<Long> singleProjectList = List.of(p.getId());
                Respuesta countResponse = actService.countByProjectIds(singleProjectList);
                long activityCount = 0L;
                
                if (Boolean.TRUE.equals(countResponse.getEstado())) {
                    Object countResult = countResponse.getResultado("count");
                    if (countResult instanceof Number num) {
                        activityCount = num.longValue();
                    }
                }
                
                series.getData().add(new XYChart.Data<>(p.getName(), activityCount));
            }
        }
        
        chart.getData().add(series);
    }

    private PersonDTO extractPersonFromSignUp() {
        try {
            Long id = parseLongSafe(txfPersonId.getText());
            String firstName = getTrimmedText(txfPersonFirstName);
            String lastName = getTrimmedText(txfPersonLastName);
            String email = getTrimmedText(txfPersonEmail);
            String username = getTrimmedText(txfPersonUsername);
            String password = pswPersonPassword.getText();
            char status = cbIsActive.isSelected() ? 'A' : 'I';
            char isAdmin = cbIsAdmin.isSelected() ? 'Y' : 'N';
            if (
                firstName.isEmpty() ||
                lastName.isEmpty() ||
                email.isEmpty() ||
                id == null
            ) {
                new Mensaje().showModal(
                    Alert.AlertType.ERROR,
                    "Login",
                    root.getScene().getWindow(),
                    "Complete todos los campos requeridos."
                );
                return null;
            }
            boolean usernameEmpty = username.isEmpty();
            boolean passwordEmpty = password == null || password.isEmpty();
            if (usernameEmpty && passwordEmpty) {
                // Both empty: allowed, set as null
                return new PersonDTO(
                    id,
                    firstName,
                    lastName,
                    email,
                    null,
                    null,
                    status,
                    isAdmin
                );
            } else if (!usernameEmpty && !passwordEmpty) {
                // Both filled: allowed
                return new PersonDTO(
                    id,
                    firstName,
                    lastName,
                    email,
                    username,
                    password,
                    status,
                    isAdmin
                );
            } else {
                // Only one filled: error
                new Mensaje().showModal(
                    Alert.AlertType.ERROR,
                    "SignUp",
                    root.getScene().getWindow(),
                    "Debe completar usuario y clave o dejar ambos vacíos."
                );
                return null;
            }
        } catch (NumberFormatException nfe) {
            new Mensaje().showModal(
                Alert.AlertType.ERROR,
                "SignUp",
                root.getScene().getWindow(),
                "La cédula debe ser numérica."
            );
            return null;
        } catch (Exception e) {
            LOGGER.warning(
                "Error al crear usuario por falta de datos: " + e.getMessage()
            );
            return null;
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

    private String getTrimmedText(MFXTextField field) {
        String text = field.getText();
        return text != null ? text.trim() : "";
    }

    private void clearLogInFields() {
        txfUsername.clear();
        psfUserPassword.clear();
    }

    private void clearSignUpFields() {
        txfPersonId.clear();
        txfPersonFirstName.clear();
        txfPersonLastName.clear();
        txfPersonEmail.clear();
        txfPersonUsername.clear();
        pswPersonPassword.clear();
        cbIsAdmin.setSelected(false);
        cbIsActive.setSelected(false);
    }

    @FXML
    private void onKeyPressedTxfPersonId(KeyEvent event) {
        // Only allow numeric input, ignore letters and other non-digit keys
        String text = event.getText();
        if (!text.matches("[0-9]")) {
            event.consume();
        }
    }

    // =========================
    // Helpers for status String
    // =========================

    /**
     * Returns the first character of the status String in upper-case,
     * or '?' if null/blank.
     */
    private char firstStatusChar(String status) {
        if (status == null || status.isBlank()) return '?';
        return Character.toUpperCase(status.charAt(0));
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
}
