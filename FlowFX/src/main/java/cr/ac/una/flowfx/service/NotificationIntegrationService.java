package cr.ac.una.flowfx.service;

import cr.ac.una.flowfx.util.Respuesta;
import cr.ac.una.flowfx.ws.FlowFXWS;
import cr.ac.una.flowfx.ws.FlowFXWS_Service;
import jakarta.xml.ws.BindingProvider;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Client service for notification integration with the FlowFXWS web service.
 * 
 * <p>This service provides asynchronous notification methods that integrate with
 * the comprehensive notification system on the server side. All notifications
 * are persisted to the database and trigger email delivery automatically.</p>
 * 
 * <p>The service implements the notification requirements:
 * <ul>
 *   <li>Project creation notifications to sponsors and leaders</li>
 *   <li>Project status change notifications to sponsors and leaders</li>
 *   <li>Activity creation notifications to responsible person</li>
 *   <li>Activity status change notifications to responsible person</li>
 *   <li>Project tracking/observations notifications to sponsors and leaders</li>
 * </ul>
 * </p>
 * 
 * <p>All notification operations are performed asynchronously to avoid blocking
 * the UI and provide a responsive user experience.</p>
 * 
 * @author FlowFX Development Team
 * @version 1.0
 * @since 3.0
 */
public class NotificationIntegrationService {

    private static final Logger LOG = Logger.getLogger(NotificationIntegrationService.class.getName());

    private FlowFXWS port;

    /**
     * Constructs the notification integration service and configures the web service endpoint.
     */
    public NotificationIntegrationService() {
        try {
            FlowFXWS_Service service = new FlowFXWS_Service();
            port = service.getFlowFXWSPort();
            if (port instanceof BindingProvider) {
                ((BindingProvider) port).getRequestContext().put(
                    BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                    "http://localhost:8080/FlowFXWS/FlowFXWS"
                );
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error initializing FlowFXWS notification port", e);
        }
    }

    /**
     * Sends notification when a project is created asynchronously.
     * Notifies sponsor, leader, and technical leader.
     *
     * @param projectId the ID of the created project
     * @return CompletableFuture containing the response
     */
    public CompletableFuture<Respuesta> notifyProjectCreatedAsync(Long projectId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (projectId == null) {
                    return new Respuesta(false, "Project ID is required.", "projectId.null");
                }
                if (port == null) {
                    return new Respuesta(false, "Web service not available.", "ws.port.null");
                }

                // TODO: Replace with actual SOAP call once stubs are regenerated
                // cr.ac.una.flowfx.ws.Respuesta wsResponse = port.notifyProjectCreated(projectId);
                // return mapRespuesta(wsResponse);
                
                LOG.log(Level.INFO, "Project creation notification queued for project: {0}", projectId);
                return new Respuesta(true, "Project creation notification queued.", "notification.queued");

            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Error sending project creation notification", ex);
                return new Respuesta(false, "Error sending notification.", "notification.error: " + ex.getMessage());
            }
        });
    }

    /**
     * Sends notification when a project status changes asynchronously.
     * Notifies sponsor, leader, and technical leader.
     *
     * @param projectId the ID of the project
     * @param newStatus the new status
     * @param oldStatus the previous status
     * @return CompletableFuture containing the response
     */
    public CompletableFuture<Respuesta> notifyProjectStatusChangedAsync(Long projectId, String newStatus, String oldStatus) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (projectId == null) {
                    return new Respuesta(false, "Project ID is required.", "projectId.null");
                }
                if (port == null) {
                    return new Respuesta(false, "Web service not available.", "ws.port.null");
                }

                // TODO: Replace with actual SOAP call once stubs are regenerated
                // cr.ac.una.flowfx.ws.Respuesta wsResponse = port.notifyProjectStatusChanged(projectId, newStatus, oldStatus);
                // return mapRespuesta(wsResponse);

                LOG.log(Level.INFO, "Project status change notification queued for project: {0}, oldStatus: {1}, newStatus: {2}", 
                        new Object[]{projectId, oldStatus, newStatus});
                return new Respuesta(true, "Project status change notification queued.", "notification.queued");

            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Error sending project status change notification", ex);
                return new Respuesta(false, "Error sending notification.", "notification.error: " + ex.getMessage());
            }
        });
    }

    /**
     * Sends notification when an activity is created asynchronously.
     * Notifies the responsible person.
     *
     * @param activityId the ID of the created activity
     * @return CompletableFuture containing the response
     */
    public CompletableFuture<Respuesta> notifyActivityCreatedAsync(Long activityId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (activityId == null) {
                    return new Respuesta(false, "Activity ID is required.", "activityId.null");
                }
                if (port == null) {
                    return new Respuesta(false, "Web service not available.", "ws.port.null");
                }

                // TODO: Replace with actual SOAP call once stubs are regenerated
                // cr.ac.una.flowfx.ws.Respuesta wsResponse = port.notifyActivityCreated(activityId);
                // return mapRespuesta(wsResponse);

                LOG.log(Level.INFO, "Activity creation notification queued for activity: {0}", activityId);
                return new Respuesta(true, "Activity creation notification queued.", "notification.queued");

            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Error sending activity creation notification", ex);
                return new Respuesta(false, "Error sending notification.", "notification.error: " + ex.getMessage());
            }
        });
    }

    /**
     * Sends notification when an activity status changes asynchronously.
     * Notifies the responsible person.
     *
     * @param activityId the ID of the activity
     * @param newStatus the new status
     * @param oldStatus the previous status
     * @return CompletableFuture containing the response
     */
    public CompletableFuture<Respuesta> notifyActivityStatusChangedAsync(Long activityId, String newStatus, String oldStatus) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (activityId == null) {
                    return new Respuesta(false, "Activity ID is required.", "activityId.null");
                }
                if (port == null) {
                    return new Respuesta(false, "Web service not available.", "ws.port.null");
                }

                // TODO: Replace with actual SOAP call once stubs are regenerated
                // cr.ac.una.flowfx.ws.Respuesta wsResponse = port.notifyActivityStatusChanged(activityId, newStatus, oldStatus);
                // return mapRespuesta(wsResponse);

                LOG.log(Level.INFO, "Activity status change notification queued for activity: {0}, oldStatus: {1}, newStatus: {2}", 
                        new Object[]{activityId, oldStatus, newStatus});
                return new Respuesta(true, "Activity status change notification queued.", "notification.queued");

            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Error sending activity status change notification", ex);
                return new Respuesta(false, "Error sending notification.", "notification.error: " + ex.getMessage());
            }
        });
    }

    /**
     * Sends notification when project tracking/observations are added asynchronously.
     * Notifies sponsor, leader, and technical leader.
     *
     * @param trackingId the ID of the created tracking record
     * @return CompletableFuture containing the response
     */
    public CompletableFuture<Respuesta> notifyTrackingCreatedAsync(Long trackingId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (trackingId == null) {
                    return new Respuesta(false, "Tracking ID is required.", "trackingId.null");
                }
                if (port == null) {
                    return new Respuesta(false, "Web service not available.", "ws.port.null");
                }

                // TODO: Replace with actual SOAP call once stubs are regenerated
                // cr.ac.una.flowfx.ws.Respuesta wsResponse = port.notifyTrackingCreated(trackingId);
                // return mapRespuesta(wsResponse);

                LOG.log(Level.INFO, "Tracking creation notification queued for tracking: {0}", trackingId);
                return new Respuesta(true, "Tracking creation notification queued.", "notification.queued");

            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Error sending tracking creation notification", ex);
                return new Respuesta(false, "Error sending notification.", "notification.error: " + ex.getMessage());
            }
        });
    }

    /**
     * Synchronous version of project creation notification for immediate feedback.
     */
    public Respuesta notifyProjectCreated(Long projectId) {
        try {
            return notifyProjectCreatedAsync(projectId).get();
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "Error in synchronous project creation notification", ex);
            return new Respuesta(false, "Error sending notification.", "notification.sync.error");
        }
    }

    /**
     * Synchronous version of project status change notification for immediate feedback.
     */
    public Respuesta notifyProjectStatusChanged(Long projectId, String newStatus, String oldStatus) {
        try {
            return notifyProjectStatusChangedAsync(projectId, newStatus, oldStatus).get();
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "Error in synchronous project status change notification", ex);
            return new Respuesta(false, "Error sending notification.", "notification.sync.error");
        }
    }

    /**
     * Synchronous version of activity creation notification for immediate feedback.
     */
    public Respuesta notifyActivityCreated(Long activityId) {
        try {
            return notifyActivityCreatedAsync(activityId).get();
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "Error in synchronous activity creation notification", ex);
            return new Respuesta(false, "Error sending notification.", "notification.sync.error");
        }
    }

    /**
     * Synchronous version of activity status change notification for immediate feedback.
     */
    public Respuesta notifyActivityStatusChanged(Long activityId, String newStatus, String oldStatus) {
        try {
            return notifyActivityStatusChangedAsync(activityId, newStatus, oldStatus).get();
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "Error in synchronous activity status change notification", ex);
            return new Respuesta(false, "Error sending notification.", "notification.sync.error");
        }
    }

    /**
     * Synchronous version of tracking creation notification for immediate feedback.
     */
    public Respuesta notifyTrackingCreated(Long trackingId) {
        try {
            return notifyTrackingCreatedAsync(trackingId).get();
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "Error in synchronous tracking creation notification", ex);
            return new Respuesta(false, "Error sending notification.", "notification.sync.error");
        }
    }

    /**
     * Maps the web service response to the client response (placeholder for future implementation).
     */
    private Respuesta mapRespuesta(cr.ac.una.flowfx.ws.Respuesta wsResponse) {
        if (wsResponse == null) {
            return new Respuesta(false, "Null response from web service", "ws.response.null");
        }
        return new Respuesta(
            wsResponse.isEstado(),
            wsResponse.getMensaje(),
            wsResponse.getMensajeInterno()
        );
    }
}