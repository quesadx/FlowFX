package cr.ac.una.flowfx.util;

import cr.ac.una.flowfx.model.ProjectTrackingDTO;
import cr.ac.una.flowfx.service.ProjectTrackingService;
import io.github.palexdev.materialfx.controls.MFXProgressBar;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for managing project progress display based on latest tracking observations.
 * 
 * <p>This utility provides functionality to:
 * <ul>
 *   <li>Fetch the latest project tracking/observation for a given project</li>
 *   <li>Update progress bar and percentage label UI components</li>
 *   <li>Handle asynchronous loading with proper thread safety</li>
 *   <li>Provide fallback behavior when no observations exist</li>
 * </ul>
 * 
 * <p><strong>Usage Pattern:</strong><br>
 * Controllers should call {@link #updateProjectProgress(long, MFXProgressBar, Label)}
 * to load and display the latest project progress percentage. The method handles
 * all background loading and UI updates safely.
 * 
 * @author FlowFX Development Team
 * @version 1.0
 * @since 3.0
 */
public final class ProjectProgressUtil {
    
    private static final Logger LOGGER = Logger.getLogger(ProjectProgressUtil.class.getName());
    
    // Private constructor to prevent instantiation
    private ProjectProgressUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    /**
     * Updates project progress bar and percentage label based on the latest tracking observation.
     * 
     * <p>This method:
     * <ul>
     *   <li>Asynchronously loads project tracking data via ProjectTrackingService</li>
     *   <li>Finds the latest observation by tracking date</li>
     *   <li>Updates the progress bar (0.0 to 1.0 range) and label (0% to 100% format)</li>
     *   <li>Handles cases where no observations exist (displays 0%)</li>
     * </ul>
     * 
     * @param projectId the ID of the project to load progress for
     * @param progressBar the MFXProgressBar component to update (nullable)
     * @param percentageLabel the Label component to update with percentage text (nullable)
     */
    public static void updateProjectProgress(long projectId, MFXProgressBar progressBar, Label percentageLabel) {
        if (projectId <= 0) {
            LOGGER.warning("Invalid project ID provided for progress update: " + projectId);
            setProgressDisplay(0, progressBar, percentageLabel);
            return;
        }
        
        LOGGER.fine("Loading project progress for project ID: " + projectId);
        
        Task<Integer> progressTask = createProgressLoadTask(projectId);
        configureProgressTaskHandlers(progressTask, progressBar, percentageLabel);
        
        Thread taskThread = new Thread(progressTask, "project-progress-loader");
        taskThread.setDaemon(true);
        taskThread.start();
    }
    
    /**
     * Creates a background task to load the latest project progress percentage.
     * 
     * @param projectId the project ID to load progress for
     * @return Task that returns the latest progress percentage (0-100)
     */
    private static Task<Integer> createProgressLoadTask(long projectId) {
        return new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                LOGGER.fine("Starting progress load task for project ID: " + projectId);
                
                try {
                    ProjectTrackingService service = new ProjectTrackingService();
                    Respuesta response = service.findByProject(projectId);
                    
                    if (Boolean.TRUE.equals(response.getEstado())) {
                        @SuppressWarnings("unchecked")
                        List<ProjectTrackingDTO> trackings = (List<ProjectTrackingDTO>) response.getResultado("ProjectTrackings");
                        
                        if (trackings != null && !trackings.isEmpty()) {
                            // Find the latest tracking by date
                            ProjectTrackingDTO latestTracking = trackings.stream()
                                .filter(t -> t.getTrackingDate() != null)
                                .max((t1, t2) -> t1.getTrackingDate().compareTo(t2.getTrackingDate()))
                                .orElse(null);
                            
                            if (latestTracking != null && latestTracking.getProgressPercentage() != null) {
                                int percentage = latestTracking.getProgressPercentage();
                                LOGGER.info("Found latest progress: " + percentage + "% for project ID: " + projectId);
                                return Math.max(0, Math.min(100, percentage)); // Ensure valid range
                            }
                        }
                        
                        LOGGER.fine("No tracking observations found for project ID: " + projectId);
                    } else {
                        LOGGER.warning("Failed to load tracking data for project ID: " + projectId + 
                                     " - " + (response != null ? response.getMensaje() : "null response"));
                    }
                } catch (Exception ex) {
                    LOGGER.log(Level.WARNING, "Error loading project progress for ID: " + projectId, ex);
                    throw ex;
                }
                
                return 0; // Default to 0% if no data found
            }
        };
    }
    
    /**
     * Configures success and failure handlers for the progress loading task.
     * 
     * @param progressTask the task to configure
     * @param progressBar the progress bar to update
     * @param percentageLabel the label to update
     */
    private static void configureProgressTaskHandlers(Task<Integer> progressTask, 
                                                     MFXProgressBar progressBar, 
                                                     Label percentageLabel) {
        progressTask.setOnSucceeded(event -> {
            Integer percentage = (Integer) event.getSource().getValue();
            Platform.runLater(() -> {
                setProgressDisplay(percentage != null ? percentage : 0, progressBar, percentageLabel);
                LOGGER.fine("Successfully updated project progress display to: " + percentage + "%");
            });
        });
        
        progressTask.setOnFailed(event -> {
            Throwable exception = event.getSource().getException();
            LOGGER.log(Level.WARNING, "Failed to load project progress", exception);
            Platform.runLater(() -> {
                setProgressDisplay(0, progressBar, percentageLabel);
            });
        });
    }
    
    /**
     * Sets the progress bar and label display values.
     * 
     * @param percentage the percentage value (0-100)
     * @param progressBar the progress bar component (nullable)
     * @param percentageLabel the percentage label component (nullable)
     */
    private static void setProgressDisplay(int percentage, MFXProgressBar progressBar, Label percentageLabel) {
        // Ensure percentage is in valid range
        int validPercentage = Math.max(0, Math.min(100, percentage));
        
        // Update progress bar (expects 0.0 to 1.0 range)
        if (progressBar != null) {
            double progressValue = validPercentage / 100.0;
            progressBar.setProgress(progressValue);
            LOGGER.fine("Set progress bar to: " + progressValue + " (" + validPercentage + "%)");
        }
        
        // Update percentage label (format as "X%")
        if (percentageLabel != null) {
            String labelText = validPercentage + "%";
            percentageLabel.setText(labelText);
            LOGGER.fine("Set percentage label to: " + labelText);
        }
    }
    
    /**
     * Immediately sets progress display to a specific percentage without loading from server.
     * Useful for immediate UI updates after creating new observations.
     * 
     * @param percentage the percentage value (0-100)
     * @param progressBar the progress bar component (nullable)
     * @param percentageLabel the percentage label component (nullable)
     */
    public static void setProgressDisplayImmediate(int percentage, MFXProgressBar progressBar, Label percentageLabel) {
        Platform.runLater(() -> {
            setProgressDisplay(percentage, progressBar, percentageLabel);
            LOGGER.fine("Immediately set progress display to: " + percentage + "%");
        });
    }
    
    /**
     * Refreshes project progress display by reloading from the server.
     * This is useful when the project progress might have changed due to new observations.
     * 
     * @param projectId the ID of the project to refresh progress for
     * @param progressBar the MFXProgressBar component to update (nullable)
     * @param percentageLabel the Label component to update with percentage text (nullable)
     */
    public static void refreshProjectProgress(long projectId, MFXProgressBar progressBar, Label percentageLabel) {
        LOGGER.info("Refreshing project progress for project ID: " + projectId);
        updateProjectProgress(projectId, progressBar, percentageLabel);
    }
}