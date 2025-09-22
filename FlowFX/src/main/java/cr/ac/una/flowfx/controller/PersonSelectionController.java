/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cr.ac.una.flowfx.controller;

import cr.ac.una.flowfx.model.PersonDTO;
import cr.ac.una.flowfx.service.PersonService;
import cr.ac.una.flowfx.util.AppContext;
import cr.ac.una.flowfx.util.Respuesta;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author quesadx
 */
public class PersonSelectionController
    extends Controller
    implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(
        PersonSelectionController.class.getName()
    );

    @FXML private MFXTextField txfSearchBar;
    @FXML private MFXButton btnSearch;
    @FXML private TableView<PersonDTO> tvPersons;
    @FXML private TableColumn<PersonDTO, String> tbcPersonName;
    @FXML private TableColumn<PersonDTO, String> tbcPersonLastName;
    @FXML private TableColumn<PersonDTO, Long> tbcPersonId;
    @FXML private AnchorPane root;
    @FXML private HBox hbLateralHandlebar;
    @FXML private MFXButton btnClose;

    private ObservableList<PersonDTO> masterData =
        FXCollections.observableArrayList();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // reset selection result
        AppContext.getInstance().set("personSelectionResult", null);
        configureTable();
        loadPersons();
        configureRowDoubleClick();
    }

    @FXML
    private void onActionBtnSearch(ActionEvent event) {
        String filter = txfSearchBar.getText();
        if (filter == null || filter.isBlank()) {
            tvPersons.setItems(masterData);
            return;
        }
        String f = filter.trim().toLowerCase(Locale.ROOT);
        ObservableList<PersonDTO> filtered =
            FXCollections.observableArrayList();
        for (PersonDTO p : masterData) if (matchesFilter(p, f)) filtered.add(p);
        tvPersons.setItems(filtered);
    }

    @Override
    public void initialize() {
        /* secondary init unused */
    }

    @FXML
    private void onActionBtnClose(ActionEvent event) {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }

    private void configureTable() {
        if (
            tbcPersonName.getCellValueFactory() == null
        ) tbcPersonName.setCellValueFactory(
            new PropertyValueFactory<>("firstName")
        );
        if (
            tbcPersonLastName.getCellValueFactory() == null
        ) tbcPersonLastName.setCellValueFactory(
            new PropertyValueFactory<>("lastName")
        );
        if (
            tbcPersonId.getCellValueFactory() == null
        ) tbcPersonId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tvPersons.setItems(masterData);
    }

    private void loadPersons() {
        PersonService service = new PersonService();
        Respuesta r = service.findAll();
        if (!Boolean.TRUE.equals(r.getEstado())) {
            LOGGER.log(
                Level.WARNING,
                "Error al cargar personas desde el service. Respuesta estado={0}, mensaje={1}",
                new Object[] {
                    r == null ? null : r.getEstado(),
                    r == null ? null : r.getMensaje(),
                }
            );
            return;
        }
        @SuppressWarnings("unchecked")
        List<PersonDTO> list = (List<PersonDTO>) r.getResultado("Persons");
        if (list != null) masterData.setAll(list);
    }

    private void configureRowDoubleClick() {
        tvPersons.setRowFactory(tv -> {
            TableRow<PersonDTO> row = new TableRow<>();
            row.setOnMouseClicked(ev -> {
                if (ev.getClickCount() == 2 && !row.isEmpty()) {
                    PersonDTO selected = row.getItem();
                    AppContext.getInstance().set("personSelectionResult", selected);
                    // Cache full name label for global reuse
                    if (selected != null && selected.getId() != null) {
                        String first = selected.getFirstName() == null ? "" : selected.getFirstName().trim();
                        String last = selected.getLastName() == null ? "" : selected.getLastName().trim();
                        String nm = (first + " " + last).trim();
                        if (!nm.isBlank()) {
                            AppContext.getInstance().set("person." + selected.getId() + ".label", nm);
                        }
                    }
                    Stage stage = (Stage) root.getScene().getWindow();
                    stage.close();
                }
            });
            return row;
        });
    }

    private boolean matchesFilter(PersonDTO p, String filter) {
        if (p == null) return false;
        if (
            String.valueOf(p.getId()).toLowerCase(Locale.ROOT).contains(filter)
        ) return true;
        if (
            p.getFirstName() != null &&
            p.getFirstName().toLowerCase(Locale.ROOT).contains(filter)
        ) return true;
        if (
            p.getLastName() != null &&
            p.getLastName().toLowerCase(Locale.ROOT).contains(filter)
        ) return true;
        return false;
    }
}
