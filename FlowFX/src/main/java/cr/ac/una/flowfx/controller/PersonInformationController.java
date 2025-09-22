/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cr.ac.una.flowfx.controller;

import cr.ac.una.flowfx.model.PersonDTO;
import cr.ac.una.flowfx.util.AppContext;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author quesadx
 */
public class PersonInformationController
    extends Controller
    implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(
        PersonInformationController.class.getName()
    );

    @FXML private AnchorPane root;
    @FXML private HBox hbLateralHandlebar;
    @FXML private MFXButton btnClose;
    @FXML private Label lblPersonName;
    @FXML private Label lblPersonMail;
    @FXML private Label lblPersonId;
    @FXML private Label lblPersonProjectRole;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("PersonInformationController initialized");
        initialize();
    }

    @Override
    public void initialize() {
        Object pObj = AppContext.getInstance().get("personInformation.person");
        Object roleObj = AppContext.getInstance().get("personInformation.role");

        String role = roleObj instanceof String ? (String) roleObj : "-";
        if (pObj instanceof PersonDTO p) {
            String fullName = ((p.getFirstName() == null
                        ? ""
                        : p.getFirstName().trim()) +
                " " +
                (p.getLastName() == null ? "" : p.getLastName().trim())).trim();
            lblPersonName.setText(fullName.isEmpty() ? "-" : fullName);
            lblPersonMail.setText(p.getEmail() == null ? "-" : p.getEmail());
            lblPersonId.setText(
                p.getId() == null ? "-" : String.valueOf(p.getId())
            );
            lblPersonProjectRole.setText(
                role == null || role.isBlank() ? "-" : role
            );
        } else {
            lblPersonName.setText("-");
            lblPersonMail.setText("-");
            lblPersonId.setText("-");
            lblPersonProjectRole.setText(
                role == null || role.isBlank() ? "-" : role
            );
        }
    }

    @FXML
    private void onActionBtnClose(ActionEvent event) {
        // Reset labels and clear context entries
        lblPersonName.setText("");
        lblPersonMail.setText("");
        lblPersonId.setText("");
        lblPersonProjectRole.setText("");
        AppContext.getInstance().delete("personInformation.person");
        AppContext.getInstance().delete("personInformation.role");
        Stage stage = (Stage) root.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }
}
