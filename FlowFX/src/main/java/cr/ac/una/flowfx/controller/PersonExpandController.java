package cr.ac.una.flowfx.controller;

import cr.ac.una.flowfx.model.PersonDTO;
import cr.ac.una.flowfx.model.PersonViewModel;
import cr.ac.una.flowfx.util.AppContext;
import cr.ac.una.flowfx.util.FlowController;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controller for the Person expand view.
 *
 * <p>This controller binds a {@link PersonViewModel} to the UI, providing
 * two-way conversions for numeric id fields and boolean/char mappings for
 * administrative/active flags.</p>
 *
 * <p>Minor improvements: consistent logging and defensive checks for the UI
 * bindings. No runtime behavior is changed.</p>
 */
public class PersonExpandController
    extends Controller
    implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(
        PersonExpandController.class.getName()
    );

    @FXML
    private AnchorPane root;

    @FXML
    private VBox vbCover;

    @FXML
    private MFXButton btnReturnPersonSignUp;

    @FXML
    private MFXTextField txfPersonName;

    @FXML
    private MFXTextField txfPersonLastName;

    @FXML
    private MFXTextField txfPersonMail;

    @FXML
    private MFXTextField txfPersonId;

    @FXML
    private MFXTextField txfPersonUsername;

    @FXML
    private MFXPasswordField psfPersonPassword;

    @FXML
    private MFXCheckbox cbIsAdmin;

    @FXML
    private MFXCheckbox cbIsActive;

    private final PersonViewModel vm = new PersonViewModel();
    private boolean syncingIdText = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // No-op for FXMLLoader-based initialization; kept for compatibility.
        LOGGER.fine(
            "FXML initialize(URL, ResourceBundle) invoked for PersonExpandController"
        );
    }

    @Override
    public void initialize() {
        Object p = AppContext.getInstance().get("selectedPerson");
        if (p instanceof PersonDTO) {
            PersonDTO dto = (PersonDTO) p;
            PersonViewModel initial = new PersonViewModel(dto);
            vm.setId(initial.getId());
            vm.setFirstName(initial.getFirstName());
            vm.setLastName(initial.getLastName());
            vm.setEmail(initial.getEmail());
            vm.setUsername(initial.getUsername());
            vm.setPassword(initial.getPassword());
            vm.setStatus(initial.getStatus());
            vm.setIsAdmin(initial.getIsAdmin());
        } else {
            vm.setStatus('I');
            vm.setIsAdmin('N');
        }
        bindFields();
    }

    private void bindFields() {
        // Simple text bindings
        txfPersonName.textProperty().bindBidirectional(vm.firstNameProperty());
        txfPersonLastName
            .textProperty()
            .bindBidirectional(vm.lastNameProperty());
        txfPersonMail.textProperty().bindBidirectional(vm.emailProperty());
        txfPersonUsername
            .textProperty()
            .bindBidirectional(vm.usernameProperty());
        psfPersonPassword
            .textProperty()
            .bindBidirectional(vm.passwordProperty());

        // ID numeric sync (manual two-way to convert long<->String)
        txfPersonId
            .textProperty()
            .addListener((obs, o, n) -> {
                if (syncingIdText) return;
                try {
                    long v = (n == null || n.trim().isEmpty())
                        ? 0L
                        : Long.parseLong(n.trim());
                    if (v != vm.getId()) vm.setId(v);
                } catch (NumberFormatException ignored) {}
            });
        vm
            .idProperty()
            .addListener((obs, o, n) -> {
                String nv = (n == null || n.longValue() == 0L)
                    ? ""
                    : String.valueOf(n.longValue());
                String cur = txfPersonId.getText();
                if (!nv.equals(cur)) {
                    syncingIdText = true;
                    txfPersonId.setText(nv);
                    syncingIdText = false;
                }
            });
        // Initialize ID text
        String initId = vm.getId() == 0L ? "" : String.valueOf(vm.getId());
        txfPersonId.setText(initId);

        // Checkboxes <-> char properties
        // Active status: 'A' selected, 'I' not selected
        cbIsActive.setSelected(
            vm.getStatus() != null &&
            Character.toUpperCase(vm.getStatus()) == 'A'
        );
        cbIsActive
            .selectedProperty()
            .addListener((obs, o, sel) -> vm.setStatus(sel ? 'A' : 'I'));
        vm
            .statusProperty()
            .addListener((obs, o, n) -> {
                boolean sel = n != null && Character.toUpperCase(n) == 'A';
                if (cbIsActive.isSelected() != sel) cbIsActive.setSelected(sel);
            });

        // Admin flag: 'Y' selected, 'N' not selected
        cbIsAdmin.setSelected(
            vm.getIsAdmin() != null &&
            Character.toUpperCase(vm.getIsAdmin()) == 'Y'
        );
        cbIsAdmin
            .selectedProperty()
            .addListener((obs, o, sel) -> vm.setIsAdmin(sel ? 'Y' : 'N'));
        vm
            .isAdminProperty()
            .addListener((obs, o, n) -> {
                boolean sel = n != null && Character.toUpperCase(n) == 'Y';
                if (cbIsAdmin.isSelected() != sel) cbIsAdmin.setSelected(sel);
            });
    }

    @FXML
    private void onActionReturnPersonSignUp(ActionEvent event) {
        FlowController.getInstance().goView("PersonSignUpView");
        Object nav = AppContext.getInstance().get("navigationBar");
        if (nav instanceof VBox) ((VBox) nav).setDisable(false);
    }
}
