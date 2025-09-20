package cr.ac.una.flowfx.util;

import cr.ac.una.flowfx.model.ProjectActivityViewModel;
import cr.ac.una.flowfx.service.ProjectActivityService;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

import java.util.Comparator;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * Utility class for managing activity table configuration, styling, and drag-and-drop functionality.
 * 
 * <p>This utility provides comprehensive table management functionality including:
 * <ul>
 *   <li>Table column configuration with custom cell value factories</li>
 *   <li>Drag-and-drop reordering with automatic execution order updates</li>
 *   <li>Professional row styling with alternating colors</li>
 *   <li>Double-click and interaction handling</li>
 *   <li>Automatic persistence of order changes</li>
 * </ul>
 * 
 * <p><strong>Drag-and-Drop Behavior:</strong><br>
 * Activities can be reordered by dragging table rows. The execution order is automatically
 * updated and persisted to the backend service. Visual feedback is provided during drag operations.
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
public final class ActivityTableUtil {

    private static final Logger LOGGER = Logger.getLogger(ActivityTableUtil.class.getName());
    
    // Constants for drag-and-drop
    private static final DataFormat ACTIVITY_INDEX = new DataFormat("application/x-flowfx-activity-index");

    // Private constructor to prevent instantiation of utility class
    private ActivityTableUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Sets up the complete activities table with columns, sorting, and drag-and-drop functionality.
     * 
     * @param tableView the TableView to configure
     * @param activities the ObservableList of activities to bind
     * @param nameColumn the name/description column
     * @param statusColumn the status column  
     * @param responsibleColumn the responsible person column
     * @param statusMapper function to map status codes to display text
     * @param onDoubleClick callback for double-click events on rows
     */
    public static void setupActivitiesTable(
            TableView<ProjectActivityViewModel> tableView,
            ObservableList<ProjectActivityViewModel> activities,
            TableColumn<ProjectActivityViewModel, String> nameColumn,
            TableColumn<ProjectActivityViewModel, String> statusColumn,
            TableColumn<ProjectActivityViewModel, String> responsibleColumn,
            StatusMapper statusMapper,
            Consumer<ProjectActivityViewModel> onDoubleClick) {
        
        configureTableColumns(nameColumn, statusColumn, responsibleColumn, statusMapper);
        configureTableSorting(tableView, activities);
        configureTableRowFactory(tableView, activities, onDoubleClick);
    }

    /**
     * Configures table columns and their value factories.
     */
    private static void configureTableColumns(
            TableColumn<ProjectActivityViewModel, String> nameColumn,
            TableColumn<ProjectActivityViewModel, String> statusColumn,
            TableColumn<ProjectActivityViewModel, String> responsibleColumn,
            StatusMapper statusMapper) {
        
        // Configure name column
        if (nameColumn.getCellValueFactory() == null) {
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        }
        
        // Configure status column with mapping
        statusColumn.setCellValueFactory(cellData -> {
            String statusCode = cellData.getValue() != null ? cellData.getValue().getStatus() : null;
            String statusText = statusMapper.mapStatus(statusCode);
            return Bindings.createStringBinding(() -> statusText);
        });
        
        // Configure responsible person column
        responsibleColumn.setCellValueFactory(cellData -> {
            ProjectActivityViewModel activity = cellData.getValue();
            return Bindings.createStringBinding(() -> {
                if (activity == null) return "-";
                
                long responsibleId = activity.getResponsibleId();
                if (responsibleId <= 0) return "-";
                
                String cachedLabel = PersonLabelUtil.getCachedPersonLabel(responsibleId);
                if (cachedLabel != null && !cachedLabel.isBlank()) {
                    return cachedLabel;
                }
                
                // Trigger async fetch for next refresh
                fetchPersonLabelForTable(responsibleId, () -> Platform.runLater(() -> {
                    // Find the table view from the column and refresh it
                    if (responsibleColumn.getTableView() != null) {
                        responsibleColumn.getTableView().refresh();
                    }
                }));
                return "-";
            });
        });
    }

    /**
     * Fetches person label for table display asynchronously.
     */
    private static void fetchPersonLabelForTable(long responsibleId, Runnable onComplete) {
        PersonLabelUtil.resolvePersonNameAsync(responsibleId, resolvedName -> {
            if (resolvedName != null && !resolvedName.isBlank() && onComplete != null) {
                onComplete.run();
            }
        });
    }

    /**
     * Configures table sorting behavior.
     */
    private static void configureTableSorting(
            TableView<ProjectActivityViewModel> tableView,
            ObservableList<ProjectActivityViewModel> activities) {
        
        tableView.setItems(activities);
        tableView.getSortOrder().clear();
        tableView.setSortPolicy(tv -> false); // Disable column sorting
        
        // Disable individual column sorting
        tableView.getColumns().forEach(column -> column.setSortable(false));
        
        // Sort by execution order
        activities.sort(Comparator.comparingInt(ProjectActivityViewModel::getExecutionOrder));
    }

    /**
     * Configures table row factory for double-click and drag-and-drop.
     */
    private static void configureTableRowFactory(
            TableView<ProjectActivityViewModel> tableView,
            ObservableList<ProjectActivityViewModel> activities,
            Consumer<ProjectActivityViewModel> onDoubleClick) {
        
        tableView.setRowFactory(tv -> {
            TableRow<ProjectActivityViewModel> row = createStyledTableRow();
            
            // Double click to open detail
            row.setOnMouseClicked(event -> handleRowDoubleClick(event, onDoubleClick));
            
            // Drag and drop support
            row.setOnDragDetected(event -> handleRowDragDetected(row, event));
            row.setOnDragOver(event -> handleRowDragOver(row, event));
            row.setOnDragDropped(event -> handleRowDragDropped(row, event, activities));
            
            return row;
        });
    }

    /**
     * Creates a styled table row with alternating colors.
     */
    private static TableRow<ProjectActivityViewModel> createStyledTableRow() {
        return new TableRow<>() {
            @Override
            protected void updateItem(ProjectActivityViewModel item, boolean empty) {
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
                    setCursor(Cursor.OPEN_HAND);
                }
            }
        };
    }

    /**
     * Handles double-click events on table rows.
     */
    private static void handleRowDoubleClick(MouseEvent event, Consumer<ProjectActivityViewModel> onDoubleClick) {
        if (event.getClickCount() == 2 && onDoubleClick != null) {
            TableRow<?> row = (TableRow<?>) event.getSource();
            if (!row.isEmpty() && row.getItem() instanceof ProjectActivityViewModel activity) {
                onDoubleClick.accept(activity);
            }
        }
    }

    /**
     * Handles drag detection on table rows.
     */
    private static void handleRowDragDetected(TableRow<ProjectActivityViewModel> row, MouseEvent event) {
        if (!row.isEmpty()) {
            Integer index = row.getIndex();
            Dragboard dragboard = row.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.put(ACTIVITY_INDEX, index);
            dragboard.setContent(content);
            
            // Create drag view
            WritableImage snapshot = row.snapshot(new SnapshotParameters(), null);
            dragboard.setDragView(snapshot, snapshot.getWidth() / 2, snapshot.getHeight() / 2);
            
            event.consume();
        }
    }

    /**
     * Handles drag over events on table rows.
     */
    private static void handleRowDragOver(TableRow<ProjectActivityViewModel> row, DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        if (dragboard.hasContent(ACTIVITY_INDEX)) {
            Integer draggedIndex = (Integer) dragboard.getContent(ACTIVITY_INDEX);
            if (row.getIndex() != draggedIndex.intValue()) {
                event.acceptTransferModes(TransferMode.MOVE);
                event.consume();
            }
        }
    }

    /**
     * Handles drag drop events on table rows.
     */
    private static void handleRowDragDropped(
            TableRow<ProjectActivityViewModel> row, 
            DragEvent event, 
            ObservableList<ProjectActivityViewModel> activities) {
        
        Dragboard dragboard = event.getDragboard();
        boolean success = false;
        
        if (dragboard.hasContent(ACTIVITY_INDEX)) {
            int draggedIndex = (Integer) dragboard.getContent(ACTIVITY_INDEX);
            ProjectActivityViewModel draggedItem = activities.remove(draggedIndex);
            
            int dropIndex = row.isEmpty() ? activities.size() : row.getIndex();
            
            activities.add(dropIndex, draggedItem);
            renumberExecutionOrder(activities);
            
            // Select the dropped item and refresh the table
            TableView<ProjectActivityViewModel> tableView = row.getTableView();
            if (tableView != null) {
                tableView.getSelectionModel().select(dropIndex);
                tableView.refresh();
            }
            
            logActivityOrder(activities);
            success = true;
        }
        
        event.setDropCompleted(success);
        event.consume();
    }

    /**
     * Renumbers execution order for activities and persists changes.
     */
    public static void renumberExecutionOrder(ObservableList<ProjectActivityViewModel> activities) {
        boolean hasChanges = false;
        
        for (int i = 0; i < activities.size(); i++) {
            ProjectActivityViewModel activity = activities.get(i);
            int newOrder = i + 1;
            
            if (activity.getExecutionOrder() != newOrder) {
                activity.setExecutionOrder(newOrder);
                hasChanges = true;
            }
        }
        
        activities.sort(Comparator.comparingInt(ProjectActivityViewModel::getExecutionOrder));
        
        if (hasChanges) {
            persistActivityOrderChanges(activities);
        }
    }

    /**
     * Persists activity order changes to the web service.
     */
    private static void persistActivityOrderChanges(ObservableList<ProjectActivityViewModel> activities) {
        ProjectActivityService service = new ProjectActivityService();
        
        for (ProjectActivityViewModel activity : activities) {
            try {
                Respuesta response = service.update(activity.toDTO());
                if (!Boolean.TRUE.equals(response.getEstado())) {
                    LOGGER.fine("Activity reorder persist failed for ID " + activity.getId() + ": " +
                        (response != null ? response.getMensaje() : "null"));
                }
            } catch (Exception ex) {
                LOGGER.fine("Exception persisting activity reorder for ID " + activity.getId() + ": " +
                    ex.getMessage());
            }
        }
    }

    /**
     * Logs the current activity order for debugging.
     */
    public static void logActivityOrder(ObservableList<ProjectActivityViewModel> activities) {
        LOGGER.fine("=== Activities new order (index -> id:order) ===");
        for (int i = 0; i < activities.size(); i++) {
            ProjectActivityViewModel activity = activities.get(i);
            LOGGER.fine(i + " -> " + activity.getId() + ":" + activity.getExecutionOrder());
        }
    }

    /**
     * Refreshes the table view to update any cached data or styling.
     * 
     * @param tableView the table view to refresh
     */
    public static void refreshTable(TableView<ProjectActivityViewModel> tableView) {
        if (tableView != null) {
            Platform.runLater(() -> tableView.refresh());
        }
    }

    /**
     * Sorts activities by execution order and refreshes the table.
     * 
     * @param activities the activities to sort
     * @param tableView the table view to refresh
     */
    public static void sortAndRefreshTable(
            ObservableList<ProjectActivityViewModel> activities,
            TableView<ProjectActivityViewModel> tableView) {
        
        activities.sort(Comparator.comparingInt(ProjectActivityViewModel::getExecutionOrder));
        refreshTable(tableView);
    }

    /**
     * Functional interface for mapping status codes to display text.
     */
    @FunctionalInterface
    public interface StatusMapper {
        /**
         * Maps a status code to display text.
         * 
         * @param statusCode the status code to map
         * @return the mapped status display text
         */
        String mapStatus(String statusCode);
    }

    /**
     * Configuration class for table setup parameters.
     */
    public static class TableConfig {
        private final TableView<ProjectActivityViewModel> tableView;
        private final ObservableList<ProjectActivityViewModel> activities;
        private final TableColumn<ProjectActivityViewModel, String> nameColumn;
        private final TableColumn<ProjectActivityViewModel, String> statusColumn;
        private final TableColumn<ProjectActivityViewModel, String> responsibleColumn;
        private final StatusMapper statusMapper;
        private final Consumer<ProjectActivityViewModel> onDoubleClick;

        public TableConfig(
                TableView<ProjectActivityViewModel> tableView,
                ObservableList<ProjectActivityViewModel> activities,
                TableColumn<ProjectActivityViewModel, String> nameColumn,
                TableColumn<ProjectActivityViewModel, String> statusColumn,
                TableColumn<ProjectActivityViewModel, String> responsibleColumn,
                StatusMapper statusMapper,
                Consumer<ProjectActivityViewModel> onDoubleClick) {
            
            this.tableView = tableView;
            this.activities = activities;
            this.nameColumn = nameColumn;
            this.statusColumn = statusColumn;
            this.responsibleColumn = responsibleColumn;
            this.statusMapper = statusMapper;
            this.onDoubleClick = onDoubleClick;
        }

        /**
         * Applies this configuration to set up the table.
         */
        public void setup() {
            ActivityTableUtil.setupActivitiesTable(
                tableView, activities, nameColumn, statusColumn, responsibleColumn, statusMapper, onDoubleClick);
        }

        // Getters
        public TableView<ProjectActivityViewModel> getTableView() { return tableView; }
        public ObservableList<ProjectActivityViewModel> getActivities() { return activities; }
        public TableColumn<ProjectActivityViewModel, String> getNameColumn() { return nameColumn; }
        public TableColumn<ProjectActivityViewModel, String> getStatusColumn() { return statusColumn; }
        public TableColumn<ProjectActivityViewModel, String> getResponsibleColumn() { return responsibleColumn; }
        public StatusMapper getStatusMapper() { return statusMapper; }
        public Consumer<ProjectActivityViewModel> getOnDoubleClick() { return onDoubleClick; }
    }
}