package cr.ac.una.flowfx.service;

import cr.ac.una.flowfx.model.NotificationDTO;
import cr.ac.una.flowfx.util.Respuesta;
import cr.ac.una.flowfx.ws.FlowFXWS;
import cr.ac.una.flowfx.ws.FlowFXWS_Service;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import jakarta.xml.ws.BindingProvider;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Client helper for Notification-related operations.
 *
 * <p>
 * This class prepares a FlowFX web service port and provides parameter-validated
 * public method shells for CRUD/query operations. Remote invocations are left
 * as clearly marked placeholders to avoid tight coupling to generated stub
 * method names; replace the placeholder responses with actual {@code port}
 * calls and map the returned web service {@code Respuesta} using
 * {@link #mapRespuesta(cr.ac.una.flowfx.ws.Respuesta)}.
 * </p>
 *
 * <p>
 * It also includes robust JSON parsing utilities to convert JSON payloads
 * contained in {@link Respuesta#getMensajeInterno()} into {@link NotificationDTO}
 * instances (both single and list variants). The parsing logic is tolerant to
 * common wrapper keys and field aliases.
 * </p>
 */
public class NotificationService {

    private static final Logger LOG = Logger.getLogger(
        NotificationService.class.getName()
    );
    private static final String ENTITY_KEY = "Notification";
    private static final String LIST_KEY = "Notifications";

    private FlowFXWS port;

    /**
     * Constructs the service client and configures the WS endpoint to the local
     * development address. Adjust the endpoint string if a different address is
     * required.
     */
    public NotificationService() {
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
     * Finds a notification by its identifier.
     *
     * <p>
     * This method validates parameters and returns a descriptive {@link Respuesta}.
     * Replace the not-implemented placeholder with the appropriate {@code port}
     * invocation (for example {@code port.getNotification(id)}) and use
     * {@link #mapRespuesta(cr.ac.una.flowfx.ws.Respuesta)} to map the result.
     * </p>
     *
     * @param id the notification identifier (required)
     * @return a {@link Respuesta} describing the outcome
     */
    public Respuesta find(Long id) {
        try {
            if (id == null) {
                return new Respuesta(
                    false,
                    "The parameter 'id' is required.",
                    "notification.find.id.null"
                );
            }
            if (port == null) {
                LOG.log(
                    Level.WARNING,
                    "Web service port is not available for Notification.find"
                );
                return new Respuesta(
                    false,
                    "Web service port is not available.",
                    "ws.port.null"
                );
            }

            LOG.log(
                Level.FINE,
                "Notification.find called for id={0} - remote invocation not implemented",
                id
            );
            return new Respuesta(
                false,
                "Operation not implemented in client. Implement the WS call in NotificationService.find.",
                "find.not.implemented"
            );
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error executing find for Notification", ex);
            return new Respuesta(
                false,
                "Error executing find operation.",
                "find " + ex.getMessage()
            );
        }
    }

    /**
     * Finds notifications for a specific project.
     *
     * @param projectId the project identifier (required)
     * @return a {@link Respuesta} describing the outcome
     */
    public Respuesta findByProject(Long projectId) {
        try {
            if (projectId == null) {
                return new Respuesta(
                    false,
                    "The parameter 'projectId' is required.",
                    "notification.findByProject.projectId.null"
                );
            }
            if (port == null) {
                LOG.log(
                    Level.WARNING,
                    "Web service port is not available for Notification.findByProject"
                );
                return new Respuesta(
                    false,
                    "Web service port is not available.",
                    "ws.port.null"
                );
            }

            LOG.log(
                Level.FINE,
                "Notification.findByProject called for projectId={0} - remote invocation not implemented",
                projectId
            );
            return new Respuesta(
                false,
                "Operation not implemented in client. Implement the WS call in NotificationService.findByProject.",
                "findByProject.not.implemented"
            );
        } catch (Exception ex) {
            LOG.log(
                Level.SEVERE,
                "Error executing findByProject for Notification",
                ex
            );
            return new Respuesta(
                false,
                "Error executing findByProject operation.",
                "findByProject " + ex.getMessage()
            );
        }
    }

    /**
     * Retrieves all notifications.
     *
     * @return a {@link Respuesta} describing the outcome
     */
    public Respuesta findAll() {
        try {
            if (port == null) {
                LOG.log(
                    Level.WARNING,
                    "Web service port is not available for Notification.findAll"
                );
                return new Respuesta(
                    false,
                    "Web service port is not available.",
                    "ws.port.null"
                );
            }

            LOG.log(
                Level.FINE,
                "Notification.findAll called - remote invocation not implemented"
            );
            return new Respuesta(
                false,
                "Operation not implemented in client. Implement the WS call in NotificationService.findAll.",
                "findAll.not.implemented"
            );
        } catch (Exception ex) {
            LOG.log(
                Level.SEVERE,
                "Error executing findAll for Notification",
                ex
            );
            return new Respuesta(
                false,
                "Error executing findAll operation.",
                "findAll " + ex.getMessage()
            );
        }
    }

    /**
     * Creates a notification.
     *
     * @param notification the DTO to create (required)
     * @return a {@link Respuesta} describing the outcome
     */
    public Respuesta create(NotificationDTO notification) {
        try {
            if (notification == null) {
                return new Respuesta(
                    false,
                    "The parameter 'notification' is required.",
                    "notification.create.null"
                );
            }
            if (port == null) {
                LOG.log(
                    Level.WARNING,
                    "Web service port is not available for Notification.create"
                );
                return new Respuesta(
                    false,
                    "Web service port is not available.",
                    "ws.port.null"
                );
            }

            LOG.log(
                Level.FINE,
                "Notification.create called - remote invocation not implemented"
            );
            return new Respuesta(
                false,
                "Operation not implemented in client. Implement the WS call in NotificationService.create.",
                "create.not.implemented"
            );
        } catch (Exception ex) {
            LOG.log(
                Level.SEVERE,
                "Error executing create for Notification",
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
     * Updates a notification.
     *
     * @param notification the DTO to update (required, must contain id)
     * @return a {@link Respuesta} describing the outcome
     */
    public Respuesta update(NotificationDTO notification) {
        try {
            if (notification == null || notification.getId() == null) {
                return new Respuesta(
                    false,
                    "The notification and its 'id' are required.",
                    "notification.update.null"
                );
            }
            if (port == null) {
                LOG.log(
                    Level.WARNING,
                    "Web service port is not available for Notification.update"
                );
                return new Respuesta(
                    false,
                    "Web service port is not available.",
                    "ws.port.null"
                );
            }

            LOG.log(
                Level.FINE,
                "Notification.update called for id={0} - remote invocation not implemented",
                notification.getId()
            );
            return new Respuesta(
                false,
                "Operation not implemented in client. Implement the WS call in NotificationService.update.",
                "update.not.implemented"
            );
        } catch (Exception ex) {
            LOG.log(
                Level.SEVERE,
                "Error executing update for Notification",
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
     * Deletes a notification by id.
     *
     * @param id the notification identifier (required)
     * @return a {@link Respuesta} describing the outcome
     */
    public Respuesta delete(Long id) {
        try {
            if (id == null) {
                return new Respuesta(
                    false,
                    "The parameter 'id' is required.",
                    "notification.delete.id.null"
                );
            }
            if (port == null) {
                LOG.log(
                    Level.WARNING,
                    "Web service port is not available for Notification.delete"
                );
                return new Respuesta(
                    false,
                    "Web service port is not available.",
                    "ws.port.null"
                );
            }

            LOG.log(
                Level.FINE,
                "Notification.delete called for id={0} - remote invocation not implemented",
                id
            );
            return new Respuesta(
                false,
                "Operation not implemented in client. Implement the WS call in NotificationService.delete.",
                "delete.not.implemented"
            );
        } catch (Exception ex) {
            LOG.log(
                Level.SEVERE,
                "Error executing delete for Notification",
                ex
            );
            return new Respuesta(
                false,
                "Error executing delete operation.",
                "delete " + ex.getMessage()
            );
        }
    }

    // --------------------- Mapping and JSON parsing utilities ---------------------

    /**
     * Maps the generated web service response into the application {@link Respuesta}.
     *
     * @param ws the web service response
     * @return a mapped application {@link Respuesta}
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
     * Parses {@link Respuesta#getMensajeInterno()} expecting a single notification
     * and sets it under the {@link #ENTITY_KEY} result key when successful.
     *
     * @param r the response whose mensajeInterno will be parsed
     */
    public void fillSingleFromMensajeInterno(Respuesta r) {
        String mi = r.getMensajeInterno();
        if (mi == null || mi.isBlank()) return;

        try (JsonReader jr = Json.createReader(new StringReader(mi))) {
            JsonValue value = jr.readValue();
            switch (value.getValueType()) {
                case OBJECT: {
                    JsonObject obj = (JsonObject) value;
                    obj = unwrapObject(
                        obj,
                        "notification",
                        "Notification",
                        "NOTIFICATION"
                    );
                    NotificationDTO dto = fromJsonNotification(obj);
                    if (dto != null) r.setResultado(ENTITY_KEY, dto);
                    break;
                }
                case ARRAY: {
                    JsonArray arr = (JsonArray) value;
                    if (
                        !arr.isEmpty() &&
                        arr.get(0).getValueType() == JsonValue.ValueType.OBJECT
                    ) {
                        NotificationDTO dto = fromJsonNotification(
                            arr.getJsonObject(0)
                        );
                        if (dto != null) r.setResultado(ENTITY_KEY, dto);
                    }
                    break;
                }
                default:
                    break;
            }
        } catch (Exception ex) {
            // Fallback: attempt to parse strictly as object
            try (JsonReader jr = Json.createReader(new StringReader(mi))) {
                JsonObject obj = jr.readObject();
                obj = unwrapObject(
                    obj,
                    "notification",
                    "Notification",
                    "NOTIFICATION"
                );
                NotificationDTO dto = fromJsonNotification(obj);
                if (dto != null) r.setResultado(ENTITY_KEY, dto);
            } catch (Exception e) {
                LOG.log(
                    Level.FINE,
                    "Unable to parse mensajeInterno to single NotificationDTO",
                    e
                );
            }
        }
    }

    /**
     * Parses {@link Respuesta#getMensajeInterno()} expecting an array or wrapper
     * containing notifications and sets the resulting list under {@link #LIST_KEY}.
     *
     * @param r the response whose mensajeInterno will be parsed
     */
    public void fillListFromMensajeInterno(Respuesta r) {
        String mi = r.getMensajeInterno();
        if (mi == null || mi.isBlank()) return;

        List<NotificationDTO> list = new ArrayList<>();

        // 1) Try direct array
        try (JsonReader jr = Json.createReader(new StringReader(mi))) {
            JsonArray arr = jr.readArray();
            for (int i = 0; i < arr.size(); i++) {
                if (arr.get(i).getValueType() == JsonValue.ValueType.OBJECT) {
                    NotificationDTO dto = fromJsonNotification(
                        arr.getJsonObject(i)
                    );
                    if (dto != null) list.add(dto);
                }
            }
            r.setResultado(LIST_KEY, list);
            return;
        } catch (Exception ignore) {
            // continue to other strategies
        }

        // 2) Try object with array inside under common keys
        try (JsonReader jr = Json.createReader(new StringReader(mi))) {
            JsonObject root = jr.readObject();
            JsonArray arr = unwrapArray(
                root,
                "notifications",
                "Notifications",
                "NOTIFICATIONS",
                "data",
                "list"
            );
            if (arr != null) {
                for (int i = 0; i < arr.size(); i++) {
                    if (
                        arr.get(i).getValueType() == JsonValue.ValueType.OBJECT
                    ) {
                        NotificationDTO dto = fromJsonNotification(
                            arr.getJsonObject(i)
                        );
                        if (dto != null) list.add(dto);
                    }
                }
            }
        } catch (Exception ex) {
            LOG.log(
                Level.WARNING,
                "Unable to parse mensajeInterno to List<NotificationDTO>",
                ex
            );
        }

        r.setResultado(LIST_KEY, list);
    }

    /**
     * If the provided object contains a nested object under any of the supplied
     * keys, returns that nested object; otherwise returns the original object.
     */
    private JsonObject unwrapObject(JsonObject obj, String... keys) {
        if (obj == null) return null;
        for (String k : keys) {
            if (
                obj.containsKey(k) &&
                obj.get(k).getValueType() == JsonValue.ValueType.OBJECT
            ) {
                return obj.getJsonObject(k);
            }
        }
        return obj;
    }

    /**
     * If the provided object contains an array under any of the supplied keys,
     * returns that array; otherwise returns null.
     */
    private JsonArray unwrapArray(JsonObject obj, String... keys) {
        if (obj == null) return null;
        for (String k : keys) {
            if (
                obj.containsKey(k) &&
                obj.get(k).getValueType() == JsonValue.ValueType.ARRAY
            ) {
                return obj.getJsonArray(k);
            }
        }
        return null;
    }

    /**
     * Converts a JSON object representing a notification into a {@link NotificationDTO}.
     * The method is tolerant to common field aliases and formats.
     *
     * @param o the JSON object to transform
     * @return the constructed NotificationDTO or {@code null} when input is null
     */
    private NotificationDTO fromJsonNotification(JsonObject o) {
        if (o == null) return null;
        NotificationDTO d = new NotificationDTO();

        d.setId(
            getLong(
                o,
                "id",
                "notification_id",
                "notificationId",
                "NOTIFICATION_ID"
            )
        );

        d.setSubject(getString(o, "subject", "SUBJECT"));
        d.setMessage(getString(o, "message", "MESSAGE", "body", "BODY"));

        d.setStatus(getChar(o, "status", "STATUS"));
        // eventType in NotificationDTO is a String; map using getString to preserve full value
        d.setEventType(getString(o, "event_type", "eventType", "EVENT_TYPE"));

        d.setSentAt(getDate(o, "sent_at", "sentAt", "SENT_AT", "sentAtMillis"));

        return d;
    }

    // ------------------------ JSON helper utilities ------------------------

    private String getString(JsonObject obj, String... keys) {
        if (obj == null) return null;
        for (String k : keys) {
            if (obj.containsKey(k) && !obj.isNull(k)) {
                try {
                    JsonValue.ValueType vt = obj.get(k).getValueType();
                    switch (vt) {
                        case STRING:
                            return ((JsonString) obj.get(k)).getString();
                        case NUMBER:
                            return ((JsonNumber) obj.get(k)).toString();
                        case TRUE:
                            return "true";
                        case FALSE:
                            return "false";
                        default:
                            return obj.get(k).toString();
                    }
                } catch (Exception ignore) {
                    // continue to next candidate
                }
            }
        }
        return null;
    }

    private Long getLong(JsonObject obj, String... keys) {
        if (obj == null) return null;
        for (String k : keys) {
            if (obj.containsKey(k) && !obj.isNull(k)) {
                try {
                    JsonValue v = obj.get(k);
                    switch (v.getValueType()) {
                        case NUMBER:
                            return obj.getJsonNumber(k).longValue();
                        case STRING:
                            String s = obj.getString(k);
                            if (
                                s != null && !s.isBlank()
                            ) return Long.parseLong(s.trim());
                            break;
                        default:
                            String raw = v.toString();
                            if (raw != null && !raw.isBlank()) {
                                raw = raw.replace("\"", "").trim();
                                return Long.parseLong(raw);
                            }
                    }
                } catch (Exception ignore) {
                    // continue to next candidate
                }
            }
        }
        return null;
    }

    private Character getChar(JsonObject obj, String... keys) {
        String s = getString(obj, keys);
        if (s != null) {
            s = s.trim();
            if (!s.isEmpty()) return s.charAt(0);
        }
        return null;
    }

    /**
     * Attempts to parse a date value from various formats:
     * - ISO-8601 instant strings
     * - common date/time formats ("yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd")
     * - numeric epoch seconds/milliseconds
     */
    private Date getDate(JsonObject obj, String... keys) {
        if (obj == null) return null;
        // 1) Try textual representations first
        String s = getString(obj, keys);
        if (s != null && !s.isBlank()) {
            s = s.trim();
            // ISO-8601
            try {
                Instant inst = Instant.parse(s);
                return Date.from(inst);
            } catch (DateTimeParseException ignore) {
                // try other common formats
            }
            try {
                return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(s);
            } catch (ParseException ignore) {
                // try next
            }
            try {
                return new SimpleDateFormat("yyyy-MM-dd").parse(s);
            } catch (ParseException ignore) {
                // ignore and try numeric below
            }
        }

        // 2) Try numeric epoch values (seconds or milliseconds)
        for (String k : keys) {
            if (
                obj.containsKey(k) &&
                obj.get(k).getValueType() == JsonValue.ValueType.NUMBER
            ) {
                try {
                    long epoch = obj.getJsonNumber(k).longValue();
                    // Heuristic: if length <= 10 treat as seconds
                    if (String.valueOf(Math.abs(epoch)).length() <= 10) {
                        epoch = epoch * 1000L;
                    }
                    return new Date(epoch);
                } catch (Exception ignore) {
                    // try next candidate
                }
            }
            // Also consider numeric encoded as string
            if (
                obj.containsKey(k) &&
                obj.get(k).getValueType() == JsonValue.ValueType.STRING
            ) {
                try {
                    String v = obj.getString(k);
                    if (v != null && !v.isBlank()) {
                        long epoch = Long.parseLong(v.trim());
                        if (String.valueOf(Math.abs(epoch)).length() <= 10) {
                            epoch = epoch * 1000L;
                        }
                        return new Date(epoch);
                    }
                } catch (Exception ignore) {
                    // continue
                }
            }
        }

        return null;
    }
}
