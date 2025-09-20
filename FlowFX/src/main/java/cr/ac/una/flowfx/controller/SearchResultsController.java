package cr.ac.una.flowfx.controller;

import cr.ac.una.flowfx.model.SearchResultDTO;
import cr.ac.una.flowfx.util.AppContext;
import cr.ac.una.flowfx.util.FlowController;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * Controller for the search results popup window.
 * 
 * Displays search results in a ListView and handles navigation to selected projects.
 */
public class SearchResultsController extends Controller implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(
        SearchResultsController.class.getName()
    );

    @FXML private AnchorPane root;
    @FXML private MFXButton btnClose;
    @FXML private HBox hbLateralHandlebar;
    @FXML private Label lblSearchPrompt;
    @FXML private ListView<SearchResultDTO> lvSearchResults;

    private ObservableList<SearchResultDTO> searchResults;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupListView();
        loadSearchData();
    }

    @Override
    public void initialize() {
        setupListView();
        loadSearchData();
    }

    /**
     * Sets up the ListView with proper cell factory and event handlers.
     */
    private void setupListView() {
        searchResults = FXCollections.observableArrayList();
        lvSearchResults.setItems(searchResults);
        
        // Set custom cell factory to display search result text properly
        lvSearchResults.setCellFactory(listView -> new SearchResultListCell());
        
        // Handle double-click and Enter key events
        lvSearchResults.setOnMouseClicked(this::handleMouseClick);
        lvSearchResults.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) {
                handleResultSelection();
            }
        });
    }

    /**
     * Loads search data from AppContext and populates the ListView.
     */
    private void loadSearchData() {
        try {
            // Get search term
            Object searchTermObj = AppContext.getInstance().get("searchTerm");
            String searchTerm = searchTermObj instanceof String ? (String) searchTermObj : "";
            
            // Update search prompt label
            if (!searchTerm.isEmpty()) {
                lblSearchPrompt.setText("\"" + searchTerm + "\"");
            } else {
                lblSearchPrompt.setText("búsqueda");
            }
            
            // Get search results
            Object resultsObj = AppContext.getInstance().get("searchResults");
            if (resultsObj instanceof List<?>) {
                @SuppressWarnings("unchecked")
                List<SearchResultDTO> results = (List<SearchResultDTO>) resultsObj;
                
                searchResults.clear();
                searchResults.addAll(results);
                
                // Select first item if available
                if (!searchResults.isEmpty()) {
                    lvSearchResults.getSelectionModel().selectFirst();
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Error loading search data", ex);
        }
    }

    /**
     * Handles mouse click events on the ListView.
     */
    private void handleMouseClick(MouseEvent event) {
        if (event.getClickCount() == 2) { // Double-click
            handleResultSelection();
        }
    }

    /**
     * Handles selection of a search result and navigates to the corresponding project.
     */
    private void handleResultSelection() {
        SearchResultDTO selectedResult = lvSearchResults.getSelectionModel().getSelectedItem();
        if (selectedResult == null) {
            return;
        }

        try {
            // Set the selected project in context for ProjectExpandView
            if (selectedResult.getProject() != null) {
                AppContext.getInstance().set("currentProject", selectedResult.getProject());
                
                // Close this popup
                closeWindow();
                
                // Navigate to project expand view
                FlowController.getInstance().goView("ProjectExpandView");
                
                LOGGER.log(Level.INFO, "Navigated to project: " + selectedResult.getProject().getName());
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error handling result selection", ex);
        }
    }

    /**
     * Closes the search results window and cleans up context.
     */
    @FXML
    private void onActionBtnClose(ActionEvent event) {
        closeWindow();
    }

    /**
     * Closes the window and cleans up search data from context.
     */
    private void closeWindow() {
        try {
            // Clean up search data from context
            AppContext.getInstance().remove("searchResults");
            AppContext.getInstance().remove("searchTerm");
            
            // Close the window
            Stage stage = (Stage) root.getScene().getWindow();
            if (stage != null) {
                stage.close();
            }
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Error closing search results window", ex);
        }
    }

    /**
     * Custom ListCell implementation for displaying SearchResultDTO objects.
     */
    private static class SearchResultListCell extends javafx.scene.control.ListCell<SearchResultDTO> {
        
        @Override
        protected void updateItem(SearchResultDTO item, boolean empty) {
            super.updateItem(item, empty);
            
            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                setText(item.getDisplayText());
                
                // Add different styles based on result type
                getStyleClass().removeAll("search-result-project", "search-result-person");
                if (item.getPerson() == null) {
                    getStyleClass().add("search-result-project");
                } else {
                    getStyleClass().add("search-result-person");
                }
            }
        }
    }
}
