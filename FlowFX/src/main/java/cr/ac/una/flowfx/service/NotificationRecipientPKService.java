package cr.ac.una.flowfx.service;

import cr.ac.una.flowfx.model.NotificationRecipientPK;
import cr.ac.una.flowfx.util.Respuesta;
import cr.ac.una.flowfx.ws.FlowFXWS;
import cr.ac.una.flowfx.ws.FlowFXWS_Service;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.xml.ws.BindingProvider;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Client service for working with NotificationRecipientPK via the FlowFX web service.
 *
 * <p>This class is a lightweight client wrapper that prepares the WS port and provides
 * helper methods to map WS responses and to parse a NotificationRecipientPK from
 * a JSON payload contained in the web service's internal message.</p>
 *
 * <p>Notes:
 * - The actual remote operation to retrieve a NotificationRecipientPK must be available
 *   in the generated web service stub. This client currently performs parameter
 *   validation and exposes a stable API. If the generated stub exposes a specific
 *   operation (for example {@code getNotificationRecipientPK}), you can add the call
 *   inside {@link #find(Long, String)} without changing the method signature.</p>
 */
public class NotificationRecipientPKService {

    private static final Logger LOG = Logger.getLogger(
        NotificationRecipientPKService.class.getName()
    );

    private FlowFXWS port;

    /**
     * Initializes the web service port and configures the endpoint address.
     * The endpoint address is set to the local development URL by default.
     */
    public NotificationRecipientPKService() {
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
     * Finds a NotificationRecipientPK by its id and email.
     *
     * <p>This method validates the input parameters and returns a {@link Respuesta}
     * describing the outcome. If the generated web service stub exposes a concrete
     * operation to retrieve the entity (for example {@code getNotificationRecipientPK}),
     * that call should be implemented here. At present this method only performs
     * validation and returns a not-implemented Respuesta when the port is available.</p>
     *
     * @param id    the notification id (required)
     * @param email the recipient email (required)
     * @return a {@link Respuesta} with the result or an error state
     */
    public Respuesta find(Long id, String email) {
        try {
            if (id == null) {
                return new Respuesta(
                    false,
                    "The parameter 'id' is required.",
                    "find.id.null"
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
                    "Web service port is not available when attempting to find NotificationRecipientPK [{0}, {1}]",
                    new Object[] { id, email }
                );
                return new Respuesta(
                    false,
                    "Web service port is not available.",
                    "ws.port.null"
                );
            }

            // NOTE: Do not call any WS operation here unless the generated stub defines it.
            // The following is intentionally a stable, non-breaking behavior: return a Respuesta
            // indicating the client-side call is not implemented. If your generated stub
            // provides a method such as getNotificationRecipientPK(Long, String), replace
            // the block below with the actual call and mapping to Respuesta.
            LOG.log(
                Level.FINE,
                "find called for NotificationRecipientPK [{0}, {1}] - remote invocation not implemented in client",
                new Object[] { id, email }
            );
            return new Respuesta(
                false,
                "Operation not implemented in client. Implement the WS call in NotificationRecipientPKService.find.",
                "find.not.implemented"
            );
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error in find(NotificationRecipientPK)", ex);
            return new Respuesta(
                false,
                "Error executing find operation.",
                "find " + ex.getMessage()
            );
        }
    }

    /**
     * Maps a web-service {@code cr.ac.una.flowfx.ws.Respuesta} into the local {@link Respuesta}.
     *
     * @param ws the web service response instance
     * @return a mapped {@link Respuesta} instance
     */
    private Respuesta mapRespuesta(cr.ac.una.flowfx.ws.Respuesta ws) {
        if (ws == null) {
            return new Respuesta(
                false,
                "Null response received from web service",
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
     * Parses a JSON payload (mensajeInterno) into a {@link NotificationRecipientPK}.
     *
     * <p>Expected JSON example: {"id":1,"email":"abc@xyz.com"}</p>
     *
     * @param mensajeInterno the JSON string to parse
     * @return the parsed {@link NotificationRecipientPK} or {@code null} on parse error
     */
    private NotificationRecipientPK parsePK(String mensajeInterno) {
        if (mensajeInterno == null || mensajeInterno.isBlank()) return null;
        try (
            JsonReader jr = Json.createReader(new StringReader(mensajeInterno))
        ) {
            JsonObject obj = jr.readObject();
            Long id = obj.containsKey("id") && !obj.isNull("id")
                ? obj.getJsonNumber("id").longValue()
                : null;
            String email = obj.containsKey("email") && !obj.isNull("email")
                ? obj.getString("email")
                : null;
            return new NotificationRecipientPK(id, email);
        } catch (Exception e) {
            LOG.log(
                Level.WARNING,
                "Failed to parse mensajeInterno into NotificationRecipientPK",
                e
            );
            return null;
        }
    }
}
