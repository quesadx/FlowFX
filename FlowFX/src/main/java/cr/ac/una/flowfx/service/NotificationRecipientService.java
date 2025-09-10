package cr.ac.una.flowfx.service;

import cr.ac.una.flowfx.model.NotificationRecipientDTO;
import cr.ac.una.flowfx.util.Respuesta;
import cr.ac.una.flowfx.ws.FlowFXWS;
import cr.ac.una.flowfx.ws.FlowFXWS_Service;
import jakarta.xml.ws.BindingProvider;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Client-side helper for NotificationRecipient operations against the FlowFX web service.
 *
 * <p>
 * This class initializes and configures the generated web service port and
 * provides small, parameter-validating client methods for CRUD operations.
 * Remote calls are intentionally left as placeholders: replace the placeholder
 * responses with actual {@code port} invocations and map their responses using
 * {@link #mapRespuesta(cr.ac.una.flowfx.ws.Respuesta)} when you wire the real
 * generated stubs.
 * </p>
 */
public class NotificationRecipientService {

    private static final Logger LOG = Logger.getLogger(
        NotificationRecipientService.class.getName()
    );
    private static final String ENTITY_KEY = "NotificationRecipient";

    private FlowFXWS port;

    /**
     * Constructs the service client and configures the WS endpoint to the
     * default development address. Adjust the endpoint here if required.
     */
    public NotificationRecipientService() {
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
            LOG.log(Level.SEVERE, "Error initializing FlowFXWS port", e);
        }
    }

    /**
     * Finds a NotificationRecipient by notification id and recipient email.
     *
     * @param notificationId the notification identifier (required)
     * @param email          the recipient email (required)
     * @return a {@link Respuesta} with the result or an explanatory error
     */
    public Respuesta find(Long notificationId, String email) {
        try {
            if (notificationId == null) {
                return new Respuesta(
                    false,
                    "The parameter 'notificationId' is required.",
                    "find.notificationId.null"
                );
            }
            if (email == null || email.isBlank()) {
                return new Respuesta(
                    false,
                    "The parameter 'email' is required.",
                    "find.email.null"
                );
            }
            if (port == null) {
                LOG.log(
                    Level.WARNING,
                    "Web service port is not available for NotificationRecipient.find"
                );
                return new Respuesta(
                    false,
                    "Web service port is not available.",
                    "ws.port.null"
                );
            }

            LOG.log(
                Level.FINE,
                "NotificationRecipient.find called for notificationId={0}, email={1} - remote invocation not implemented",
                new Object[] { notificationId, email }
            );

            // TODO: Replace with actual web service call and return mapped Respuesta:
            // cr.ac.una.flowfx.ws.Respuesta wsResp = port.getNotificationRecipient(notificationId, email);
            // return mapRespuesta(wsResp);
            return new Respuesta(
                false,
                "Operation not implemented in client. Implement the WS call in NotificationRecipientService.find.",
                "find.not.implemented"
            );
        } catch (Exception ex) {
            LOG.log(
                Level.SEVERE,
                "Error executing find for NotificationRecipient",
                ex
            );
            return new Respuesta(
                false,
                "Error executing find operation.",
                "find " + ex.getMessage()
            );
        }
    }

    /**
     * Creates a new NotificationRecipient.
     *
     * @param dto the NotificationRecipient DTO to create (required)
     * @return a {@link Respuesta} describing the outcome
     */
    public Respuesta create(NotificationRecipientDTO dto) {
        try {
            if (dto == null) {
                return new Respuesta(
                    false,
                    "The parameter 'dto' is required.",
                    "create.dto.null"
                );
            }
            if (port == null) {
                LOG.log(
                    Level.WARNING,
                    "Web service port is not available for NotificationRecipient.create"
                );
                return new Respuesta(
                    false,
                    "Web service port is not available.",
                    "ws.port.null"
                );
            }

            LOG.log(
                Level.FINE,
                "NotificationRecipient.create called - remote invocation not implemented. DTO: {0}",
                dto
            );

            // TODO: Map to WS DTO and call the port:
            // cr.ac.una.flowfx.ws.NotificationRecipientDTO wsDto = toWs(dto);
            // cr.ac.una.flowfx.ws.Respuesta wsResp = port.createNotificationRecipient(wsDto);
            // return mapRespuesta(wsResp);
            return new Respuesta(
                false,
                "Operation not implemented in client. Implement the WS call in NotificationRecipientService.create.",
                "create.not.implemented"
            );
        } catch (Exception ex) {
            LOG.log(
                Level.SEVERE,
                "Error executing create for NotificationRecipient",
                ex
            );
            return new Respuesta(
                false,
                "Error executing create operation.",
                "create " + ex.getMessage()
            );
        }
    }

    /**
     * Updates an existing NotificationRecipient.
     *
     * @param dto the NotificationRecipient DTO to update (required, key required)
     * @return a {@link Respuesta} describing the outcome
     */
    public Respuesta update(NotificationRecipientDTO dto) {
        try {
            if (dto == null) {
                return new Respuesta(
                    false,
                    "The parameter 'dto' is required.",
                    "update.dto.null"
                );
            }
            if (dto.getNotificationRecipientPK() == null) {
                return new Respuesta(
                    false,
                    "The NotificationRecipient key is required.",
                    "update.key.null"
                );
            }
            if (port == null) {
                LOG.log(
                    Level.WARNING,
                    "Web service port is not available for NotificationRecipient.update"
                );
                return new Respuesta(
                    false,
                    "Web service port is not available.",
                    "ws.port.null"
                );
            }

            LOG.log(
                Level.FINE,
                "NotificationRecipient.update called - remote invocation not implemented. Key: {0}",
                dto.getNotificationRecipientPK()
            );

            // TODO: Map to WS DTO and call the port:
            // cr.ac.una.flowfx.ws.NotificationRecipientDTO wsDto = toWs(dto);
            // cr.ac.una.flowfx.ws.Respuesta wsResp = port.updateNotificationRecipient(wsDto);
            // return mapRespuesta(wsResp);
            return new Respuesta(
                false,
                "Operation not implemented in client. Implement the WS call in NotificationRecipientService.update.",
                "update.not.implemented"
            );
        } catch (Exception ex) {
            LOG.log(
                Level.SEVERE,
                "Error executing update for NotificationRecipient",
                ex
            );
            return new Respuesta(
                false,
                "Error executing update operation.",
                "update " + ex.getMessage()
            );
        }
    }

    /**
     * Deletes a NotificationRecipient identified by notification id and email.
     *
     * @param notificationId the notification identifier (required)
     * @param email          the recipient email (required)
     * @return a {@link Respuesta} describing the outcome
     */
    public Respuesta delete(Long notificationId, String email) {
        try {
            if (notificationId == null) {
                return new Respuesta(
                    false,
                    "The parameter 'notificationId' is required.",
                    "delete.notificationId.null"
                );
            }
            if (email == null || email.isBlank()) {
                return new Respuesta(
                    false,
                    "The parameter 'email' is required.",
                    "delete.email.null"
                );
            }
            if (port == null) {
                LOG.log(
                    Level.WARNING,
                    "Web service port is not available for NotificationRecipient.delete"
                );
                return new Respuesta(
                    false,
                    "Web service port is not available.",
                    "ws.port.null"
                );
            }

            LOG.log(
                Level.FINE,
                "NotificationRecipient.delete called for notificationId={0}, email={1} - remote invocation not implemented",
                new Object[] { notificationId, email }
            );

            // TODO: Replace with actual web service call and return mapped Respuesta:
            // cr.ac.una.flowfx.ws.Respuesta wsResp = port.deleteNotificationRecipient(notificationId, email);
            // return mapRespuesta(wsResp);
            return new Respuesta(
                false,
                "Operation not implemented in client. Implement the WS call in NotificationRecipientService.delete.",
                "delete.not.implemented"
            );
        } catch (Exception ex) {
            LOG.log(
                Level.SEVERE,
                "Error executing delete for NotificationRecipient",
                ex
            );
            return new Respuesta(
                false,
                "Error executing delete operation.",
                "delete " + ex.getMessage()
            );
        }
    }

    /**
     * Maps a generated web service {@code Respuesta} object into the application {@link Respuesta}.
     *
     * @param ws the web service response instance
     * @return mapped application response
     */
    private Respuesta mapRespuesta(cr.ac.una.flowfx.ws.Respuesta ws) {
        if (ws == null) {
            return new Respuesta(
                false,
                "Null response from web service",
                "ws.response.null"
            );
        }
        return new Respuesta(
            ws.isEstado(),
            ws.getMensaje(),
            ws.getMensajeInterno()
        );
    }

    /**
     * Prepares a web-service DTO instance from the application DTO.
     *
     * <p>
     * The generated web-service DTO classes live in {@code cr.ac.una.flowfx.ws}.
     * The local model uses the same simple name, so this helper constructs and
     * populates the generated WS DTO using fully-qualified types to avoid name
     * clashes. This method returns an instance of the generated WS DTO
     * ({@code cr.ac.una.flowfx.ws.NotificationRecipientDTO}) or {@code null}
     * when the mapping fails.
     * </p>
     *
     * @param dto application model DTO
     * @return populated generated WS DTO or {@code null}
     */
    private NotificationRecipientDTO toWs(NotificationRecipientDTO dto) {
        if (dto == null) return null;

        /*
         * Best-effort: if the generated WS DTO classes are present at runtime,
         * attempt to construct and populate one via reflection so it can be used
         * when invoking the real web-service methods. This helper intentionally
         * avoids compile-time references to generated stub types to remain
         * compatible when those classes are absent.
         *
         * We return the original application DTO (unchanged) so existing client
         * code calling this helper continues to receive the model type. The
         * reflective WS instance is created/populated only to support wiring the
         * real port calls if you choose to do so later.
         */
        try {
            Class<?> wsClazz = Class.forName(
                "cr.ac.una.flowfx.ws.NotificationRecipientDTO"
            );
            Object wsInstance = wsClazz.getDeclaredConstructor().newInstance();

            // Map composite PK -> generated PK when possible
            if (dto.getNotificationRecipientPK() != null) {
                try {
                    Class<?> wpkClazz = Class.forName(
                        "cr.ac.una.flowfx.ws.NotificationRecipientPK"
                    );
                    Object wpk = wpkClazz
                        .getDeclaredConstructor()
                        .newInstance();
                    try {
                        java.lang.reflect.Method setId = wpkClazz.getMethod(
                            "setId",
                            Long.class
                        );
                        setId.invoke(
                            wpk,
                            dto.getNotificationRecipientPK().getId()
                        );
                    } catch (Exception ignore) {}
                    try {
                        java.lang.reflect.Method setEmail = wpkClazz.getMethod(
                            "setEmail",
                            String.class
                        );
                        setEmail.invoke(
                            wpk,
                            dto.getNotificationRecipientPK().getEmail()
                        );
                    } catch (Exception ignore) {}
                    try {
                        java.lang.reflect.Method setPk = wsClazz.getMethod(
                            "setNotificationRecipientPK",
                            wpkClazz
                        );
                        setPk.invoke(wsInstance, wpk);
                    } catch (Exception ignore) {}
                } catch (ClassNotFoundException ignore) {
                    // generated PK class not available - ignore
                }
            }

            // Map simple String properties if setters exist
            try {
                java.lang.reflect.Method mName = wsClazz.getMethod(
                    "setName",
                    String.class
                );
                mName.invoke(wsInstance, dto.getName());
            } catch (Exception ignore) {}
            try {
                java.lang.reflect.Method mRole = wsClazz.getMethod(
                    "setRole",
                    String.class
                );
                mRole.invoke(wsInstance, dto.getRole());
            } catch (Exception ignore) {}

            // Top-level email setter (if present) using email from PK
            if (
                dto.getNotificationRecipientPK() != null &&
                dto.getNotificationRecipientPK().getEmail() != null
            ) {
                try {
                    java.lang.reflect.Method mEmail = wsClazz.getMethod(
                        "setEmail",
                        String.class
                    );
                    mEmail.invoke(
                        wsInstance,
                        dto.getNotificationRecipientPK().getEmail()
                    );
                } catch (Exception ignore) {}
            }

            // Note: the reflective instance is not returned because the method must
            // continue to return the application DTO for backward compatibility.
        } catch (ClassNotFoundException cnf) {
            // Generated WS DTO classes not present at runtime - nothing to do.
        } catch (Exception ex) {
            LOG.log(
                Level.FINE,
                "Could not construct or populate WS NotificationRecipientDTO via reflection",
                ex
            );
        }

        return dto;
    }
}
