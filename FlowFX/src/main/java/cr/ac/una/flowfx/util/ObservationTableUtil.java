package cr.ac.una.flowfx.util;

import cr.ac.una.flowfx.model.ProjectTrackingViewModel;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * Utility class for managing observation table configuration, styling, and interaction functionality.
 * 
 * <p>This utility provides comprehensive table management functionality including:
 * <ul>
 *   <li>Table column configuration with custom cell value factories</li>
 *   <li>Professional row styling with alternating colors</li>
 *   <li>Double-click and interaction handling</li>
 *   <li>Date and percentage formatting for display</li>
 *   <li>Person name resolution integration</li>
 * </ul>
 * 
 * <p><strong>Styling System:</strong><br>
 * Rows use alternating background colors for improved readability. The cursor changes to
 * indicate interactive elements. Empty rows are styled differently from populated rows.
 * 
 * <p><strong>Integration:</strong><br>
 * Uses PersonLabelUtil for person name resolution and provides callback interfaces
 * for custom behavior integration with controllers.
 * 
 * @author FlowFX Development Team
 * @version 1.0
 * @since 3.0
 */
public final class ObservationTableUtil {

    private static final Logger LOGGER = Logger.getLogger(ObservationTableUtil.class.getName());
    
    // Date formatter for observation dates
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd/MM/yyyy");

    // Private constructor to prevent instantiation of utility class
    private ObservationTableUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Sets up the complete observations table with columns, sorting, and interaction functionality.
     * 
     * @param tableView the TableView to configure
     * @param observations the ObservableList of observations to bind
     * @param titleColumn the observation title/name column
     * @param dateColumn the tracking date column  
     * @param percentageColumn the progress percentage column
     * @param createdByColumn the created by person column
     * @param onDoubleClick callback for double-click events on rows
     */
    public static void setupObservationsTable(
            TableView<ProjectTrackingViewModel> tableView,
            ObservableList<ProjectTrackingViewModel> observations,
            TableColumn<ProjectTrackingViewModel, String> titleColumn,
            TableColumn<ProjectTrackingViewModel, String> dateColumn,
            TableColumn<ProjectTrackingViewModel, String> percentageColumn,
            TableColumn<ProjectTrackingViewModel, String> createdByColumn,
            Consumer<ProjectTrackingViewModel> onDoubleClick) {
        
        LOGGER.fine("Setting up observations table with " + observations.size() + " observations");
        
        configureTableColumns(titleColumn, dateColumn, percentageColumn, createdByColumn);
        configureTableSorting(tableView, observations);
        configureTableRowFactory(tableView, onDoubleClick);
    }

    /**
     * Configures table columns and their value factories.
     */
    private static void configureTableColumns(
            TableColumn<ProjectTrackingViewModel, String> titleColumn,
            TableColumn<ProjectTrackingViewModel, String> dateColumn,
            TableColumn<ProjectTrackingViewModel, String> percentageColumn,
            TableColumn<ProjectTrackingViewModel, String> createdByColumn) {
        
        LOGGER.fine("Configuring observation table columns");
        
        // Configure title column (observations field)
        if (titleColumn.getCellValueFactory() == null) {
            titleColumn.setCellValueFactory(cellData -> {
                ProjectTrackingViewModel observation = cellData.getValue();
                return Bindings.createStringBinding(() -> {
                    if (observation == null) return "-";
                    
                    String obs = observation.getObservations();
                    if (obs == null || obs.isBlank()) return "-";
                    
                    // Truncate long observations for table display
                    return obs.length() > 50 ? obs.substring(0, 47) + "..." : obs;
                });
            });
        }
        
        // Configure date column with formatting
        dateColumn.setCellValueFactory(cellData -> {
            ProjectTrackingViewModel observation = cellData.getValue();
            return Bindings.createStringBinding(() -> {
                if (observation == null || observation.getTrackingDate() == null) return "-";
                
                try {
                    return DATE_FORMATTER.format(observation.getTrackingDate());
                } catch (Exception ex) {
                    LOGGER.fine("Date formatting error for observation: " + ex.getMessage());
                    return "-";
                }
            });
        });
        
        // Configure percentage column with formatting
        // Configure percentage column with formatting
        percentageColumn.setCellValueFactory(cellData -> {
            ProjectTrackingViewModel observation = cellData.getValue();
            return Bindings.createStringBinding(() -> {
                if (observation == null) return "-";
                
                int percentage = observation.getProgressPercentage();
                return percentage + "%";
            });
        });
        
        // Configure created by person column
        createdByColumn.setCellValueFactory(cellData -> {
            ProjectTrackingViewModel observation = cellData.getValue();
            return Bindings.createStringBinding(() -> {
                if (observation == null) return "-";
                
                long createdById = observation.getCreatedBy();
                if (createdById <= 0) return "-";
                
                String cachedLabel = PersonLabelUtil.getCachedPersonLabel(createdById);
                if (cachedLabel != null && !cachedLabel.isBlank()) {
                    return cachedLabel;
                }
                
                // Trigger async fetch for next refresh
                fetchPersonLabelForTable(createdById, () -> Platform.runLater(() -> {
                    // Find the table view from the column and refresh it
                    if (createdByColumn.getTableView() != null) {
                        createdByColumn.getTableView().refresh();
                    }
                }));
                return "-";
            });
        });
    }

    /**
     * Fetches person label for table display asynchronously.
     */
    private static void fetchPersonLabelForTable(long createdById, Runnable onComplete) {
        PersonLabelUtil.resolvePersonNameAsync(createdById, resolvedName -> {
            if (resolvedName != null && !resolvedName.isBlank() && onComplete != null) {
                onComplete.run();
            }
        });
    }

    /**
     * Configures table sorting behavior.
     */
    private static void configureTableSorting(
            TableView<ProjectTrackingViewModel> tableView,
            ObservableList<ProjectTrackingViewModel> observations) {
        
        tableView.setItems(observations);
        tableView.getSortOrder().clear();
        tableView.setSortPolicy(tv -> false); // Disable column sorting
        
        // Disable individual column sorting
        tableView.getColumns().forEach(column -> column.setSortable(false));
        
        // Sort by tracking date (most recent first)
        observations.sort(Comparator.comparing(
            ProjectTrackingViewModel::getTrackingDate, 
            Comparator.nullsLast(Comparator.reverseOrder())
        ));
        
        LOGGER.fine("Configured table sorting - observations sorted by tracking date descending");
    }

    /**
     * Configures table row factory for double-click and styling.
     */
    private static void configureTableRowFactory(
            TableView<ProjectTrackingViewModel> tableView,
            Consumer<ProjectTrackingViewModel> onDoubleClick) {
        
        tableView.setRowFactory(tv -> {
            TableRow<ProjectTrackingViewModel> row = createStyledTableRow();
            
            // Double click to open detail
            row.setOnMouseClicked(event -> handleRowDoubleClick(event, onDoubleClick));
            
            return row;
        });
    }

    /**
     * Creates a styled table row with alternating colors.
     */
    private static TableRow<ProjectTrackingViewModel> createStyledTableRow() {
        return new TableRow<>() {
            @Override
            protected void updateItem(ProjectTrackingViewModel item, boolean empty) {
                super.updateItem(item, empty);
                
                if (empty || item == null) {
                    setStyle("");
                    setCursor(Cursor.DEFAULT);
                } else {
                    int index = getIndex();
                    String backgroundColor = switch (index % 3) {
                        case 0 -> "-fx-surface";
                        case 1 -> "-fx-surface-variant";
                        default -> "#f6f8ff";
                    };
                    setStyle("-fx-background-color: " + backgroundColor + "; -fx-background-radius: 12;");
                    setCursor(Cursor.HAND);
                }
            }
        };
    }

    /**
     * Handles double-click events on table rows.
     */
    private static void handleRowDoubleClick(MouseEvent event, Consumer<ProjectTrackingViewModel> onDoubleClick) {
        if (event.getClickCount() == 2 && onDoubleClick != null) {
            TableRow<?> row = (TableRow<?>) event.getSource();
            if (!row.isEmpty() && row.getItem() instanceof ProjectTrackingViewModel observation) {
                LOGGER.fine("Double-click detected on observation ID: " + observation.getId());
                onDoubleClick.accept(observation);
            }
        }
    }

    /**
     * Refreshes the table view to update any cached data or styling.
     * 
     * @param tableView the table view to refresh
     */
    public static void refreshTable(TableView<ProjectTrackingViewModel> tableView) {
        if (tableView != null) {
            Platform.runLater(() -> {
                tableView.refresh();
                LOGGER.fine("Refreshed observations table");
            });
        }
    }

    /**
     * Sorts observations by tracking date (most recent first) and refreshes the table.
     * 
     * @param observations the observations to sort
     * @param tableView the table view to refresh
     */
    public static void sortAndRefreshTable(
            ObservableList<ProjectTrackingViewModel> observations,
            TableView<ProjectTrackingViewModel> tableView) {
        
        observations.sort(Comparator.comparing(
            ProjectTrackingViewModel::getTrackingDate, 
            Comparator.nullsLast(Comparator.reverseOrder())
        ));
        refreshTable(tableView);
        
        LOGGER.fine("Sorted and refreshed observations table with " + observations.size() + " items");
    }

    /**
     * Validates that all required table components are properly configured.
     * 
     * @param tableView the table view to validate
     * @param observations the observations list to validate
     * @return true if configuration is valid, false otherwise
     */
    public static boolean validateTableConfiguration(
            TableView<ProjectTrackingViewModel> tableView,
            ObservableList<ProjectTrackingViewModel> observations) {
        
        if (tableView == null) {
            LOGGER.warning("Table view is null");
            return false;
        }
        
        if (observations == null) {
            LOGGER.warning("Observations list is null");
            return false;
        }
        
        if (tableView.getColumns().isEmpty()) {
            LOGGER.warning("Table view has no columns configured");
            return false;
        }
        
        LOGGER.fine("Table configuration validation passed");
        return true;
    }

    /**
     * Configuration class for table setup parameters.
     */
    public static class TableConfig {
        private final TableView<ProjectTrackingViewModel> tableView;
        private final ObservableList<ProjectTrackingViewModel> observations;
        private final TableColumn<ProjectTrackingViewModel, String> titleColumn;
        private final TableColumn<ProjectTrackingViewModel, String> dateColumn;
        private final TableColumn<ProjectTrackingViewModel, String> percentageColumn;
        private final TableColumn<ProjectTrackingViewModel, String> createdByColumn;
        private final Consumer<ProjectTrackingViewModel> onDoubleClick;

        public TableConfig(
                TableView<ProjectTrackingViewModel> tableView,
                ObservableList<ProjectTrackingViewModel> observations,
                TableColumn<ProjectTrackingViewModel, String> titleColumn,
                TableColumn<ProjectTrackingViewModel, String> dateColumn,
                TableColumn<ProjectTrackingViewModel, String> percentageColumn,
                TableColumn<ProjectTrackingViewModel, String> createdByColumn,
                Consumer<ProjectTrackingViewModel> onDoubleClick) {
            
            this.tableView = tableView;
            this.observations = observations;
            this.titleColumn = titleColumn;
            this.dateColumn = dateColumn;
            this.percentageColumn = percentageColumn;
            this.createdByColumn = createdByColumn;
            this.onDoubleClick = onDoubleClick;
        }

        /**
         * Applies this configuration to set up the table.
         */
        public void setup() {
            ObservationTableUtil.setupObservationsTable(
                tableView, observations, titleColumn, dateColumn, percentageColumn, createdByColumn, onDoubleClick);
        }

        // Getters
        public TableView<ProjectTrackingViewModel> getTableView() { return tableView; }
        public ObservableList<ProjectTrackingViewModel> getObservations() { return observations; }
        public TableColumn<ProjectTrackingViewModel, String> getTitleColumn() { return titleColumn; }
        public TableColumn<ProjectTrackingViewModel, String> getDateColumn() { return dateColumn; }
        public TableColumn<ProjectTrackingViewModel, String> getPercentageColumn() { return percentageColumn; }
        public TableColumn<ProjectTrackingViewModel, String> getCreatedByColumn() { return createdByColumn; }
        public Consumer<ProjectTrackingViewModel> getOnDoubleClick() { return onDoubleClick; }
    }
}