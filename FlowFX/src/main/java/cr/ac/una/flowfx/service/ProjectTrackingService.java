package cr.ac.una.flowfx.service;

import cr.ac.una.flowfx.model.ProjectTrackingDTO;
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
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Client service for ProjectTracking operations against the FlowFX web service.
 *
 * <p>This class prepares the WS port and exposes a simple API to retrieve
 * project tracking entries. It contains robust parsing utilities that accept
 * a variety of JSON shapes returned inside the web service's
 * {@code mensajeInterno} field.</p>
 *
 * <p>The public API preserves behavior: {@link #findByProject(Long)} calls the
 * remote operation (assumed to be {@code getProjectTrackingByProject}) and
 * maps the response into {@link Respuesta}, populating parsed DTOs when the
 * response indicates success.</p>
 */
public class ProjectTrackingService {

    private static final Logger LOG = Logger.getLogger(
        ProjectTrackingService.class.getName()
    );
    private static final String ENTITY_KEY = "ProjectTracking";
    private static final String LIST_KEY = "ProjectTrackings";

    private FlowFXWS port;

    /**
     * Constructs the client and configures the default endpoint address.
     * Adjust the endpoint constant below if your deployment uses a different URL.
     */
    public ProjectTrackingService() {
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
     * Retrieves tracking entries for a project.
     *
     * @param projectId the project identifier (required)
     * @return a Respuesta containing a List<ProjectTrackingDTO> under key {@code ProjectTrackings}
     *         when the operation succeeds; otherwise a Respuesta with error details.
     */
    public Respuesta findByProject(Long projectId) {
        try {
            if (projectId == null) {
                return new Respuesta(
                    false,
                    "The parameter 'projectId' is required.",
                    "tracking.byProject.projectId.null"
                );
            }
            cr.ac.una.flowfx.ws.Respuesta wsResp =
                port.getProjectTrackingByProject(projectId);
            Respuesta r = mapRespuesta(wsResp);
            if (Boolean.TRUE.equals(r.getEstado())) {
                fillListFromMensajeInterno(r);
            }
            return r;
        } catch (Exception ex) {
            LOG.log(
                Level.SEVERE,
                "Error retrieving tracking for projectId=" + projectId,
                ex
            );
            return new Respuesta(
                false,
                "Error retrieving project tracking.",
                "tracking.byProject " + ex.getMessage()
            );
        }
    }

    // --------------------
    // Utilities and parsing
    // --------------------

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
     * Parses the mensajeInterno field of a successful Respuesta and fills a list
     * of {@link ProjectTrackingDTO} under the key {@link #LIST_KEY}.
     *
     * Supported shapes:
     * - direct JSON array of objects
     * - object containing an array under keys: trackings / Trackings / TRACKINGS
     * - object containing an array under keys: data / list
     */
    private void fillListFromMensajeInterno(Respuesta r) {
        String mi = r.getMensajeInterno();
        if (mi == null || mi.isBlank()) return;

        List<ProjectTrackingDTO> list = new ArrayList<>();

        // 1) Direct array
        try (JsonReader jr = Json.createReader(new StringReader(mi))) {
            JsonArray arr = jr.readArray();
            for (int i = 0; i < arr.size(); i++) {
                if (arr.get(i).getValueType() == JsonValue.ValueType.OBJECT) {
                    JsonObject obj = arr.getJsonObject(i);
                    ProjectTrackingDTO dto = fromJsonTracking(obj);
                    if (dto != null) list.add(dto);
                }
            }
            r.setResultado(LIST_KEY, list);
            return;
        } catch (Exception ignore) {
            // not an array, continue to object-with-array attempts
        }

        // 2) Object with array inside
        try (JsonReader jr = Json.createReader(new StringReader(mi))) {
            JsonObject root = jr.readObject();
            JsonArray arr = null;
            if (
                root.containsKey("trackings") &&
                root.get("trackings").getValueType() ==
                JsonValue.ValueType.ARRAY
            ) {
                arr = root.getJsonArray("trackings");
            } else if (
                root.containsKey("Trackings") &&
                root.get("Trackings").getValueType() ==
                JsonValue.ValueType.ARRAY
            ) {
                arr = root.getJsonArray("Trackings");
            } else if (
                root.containsKey("TRACKINGS") &&
                root.get("TRACKINGS").getValueType() ==
                JsonValue.ValueType.ARRAY
            ) {
                arr = root.getJsonArray("TRACKINGS");
            } else if (
                root.containsKey("data") &&
                root.get("data").getValueType() == JsonValue.ValueType.ARRAY
            ) {
                arr = root.getJsonArray("data");
            } else if (
                root.containsKey("list") &&
                root.get("list").getValueType() == JsonValue.ValueType.ARRAY
            ) {
                arr = root.getJsonArray("list");
            }

            if (arr != null) {
                for (int i = 0; i < arr.size(); i++) {
                    if (
                        arr.get(i).getValueType() == JsonValue.ValueType.OBJECT
                    ) {
                        ProjectTrackingDTO dto = fromJsonTracking(
                            arr.getJsonObject(i)
                        );
                        if (dto != null) list.add(dto);
                    }
                }
            }
        } catch (Exception ex) {
            LOG.log(
                Level.WARNING,
                "Unable to parse mensajeInterno into List<ProjectTrackingDTO>",
                ex
            );
        }

        r.setResultado(LIST_KEY, list);
    }

    /**
     * Convert a JsonObject into ProjectTrackingDTO. Accepts common key aliases
     * and tolerates numbers as strings.
     */
    private ProjectTrackingDTO fromJsonTracking(JsonObject obj) {
        if (obj == null) return null;

        ProjectTrackingDTO dto = new ProjectTrackingDTO();

        dto.setId(getLong(obj, "id", "tracking_id", "trackingId"));
        dto.setProjectId(getLong(obj, "project_id", "projectId"));
        dto.setObservations(getString(obj, "observations", "obs", "note"));

        // trackingDate: try ISO string or epoch numeric
        Date tdate = getDate(obj, "tracking_date", "trackingDate", "date");
        dto.setTrackingDate(tdate);

        // progress percentage (NUMBER(5,2)) -> Double
        Double progress = getDouble(
            obj,
            "progress_percentage",
            "progressPercentage",
            "progress"
        );
        if (progress != null) dto.setProgressPercentage(progress);

        dto.setCreatedBy(getLong(obj, "created_by", "createdBy"));
        Date createdAt = getDate(obj, "created_at", "createdAt");
        dto.setCreatedAt(createdAt);

        return dto;
    }

    // ------- JSON helpers -------

    private String getString(JsonObject obj, String... keys) {
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
                    // try next key
                }
            }
        }
        return null;
    }

    private Long getLong(JsonObject obj, String... keys) {
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
                            if (
                                raw != null && !raw.isBlank()
                            ) return Long.parseLong(
                                raw.replace("\"", "").trim()
                            );
                    }
                } catch (Exception ignore) {
                    // try next key
                }
            }
        }
        return null;
    }

    private Double getDouble(JsonObject obj, String... keys) {
        for (String k : keys) {
            if (obj.containsKey(k) && !obj.isNull(k)) {
                try {
                    JsonValue v = obj.get(k);
                    switch (v.getValueType()) {
                        case NUMBER:
                            return obj.getJsonNumber(k).doubleValue();
                        case STRING:
                            String s = obj.getString(k);
                            if (
                                s != null && !s.isBlank()
                            ) return Double.valueOf(s.trim());
                            break;
                        default:
                            String raw = v.toString();
                            if (
                                raw != null && !raw.isBlank()
                            ) return Double.valueOf(
                                raw.replace("\"", "").trim()
                            );
                    }
                } catch (Exception ignore) {
                    // try next key
                }
            }
        }
        return null;
    }

    /**
     * Attempts to parse a date from a json object key. Supports:
     * - ISO-8601 strings (parsed by Instant.parse)
     * - common formats (yyyy-MM-dd HH:mm:ss, yyyy-MM-dd)
     * - numeric epoch values (seconds or milliseconds)
     */
    private Date getDate(JsonObject obj, String... keys) {
        String s = getString(obj, keys);
        if (s != null && !s.isBlank()) {
            try {
                Instant inst = Instant.parse(s);
                return Date.from(inst);
            } catch (DateTimeParseException ignore) {
                // continue to other formats
            }
            try {
                return new java.text.SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss"
                ).parse(s);
            } catch (Exception ignore) {
                // try next
            }
            try {
                return new java.text.SimpleDateFormat("yyyy-MM-dd").parse(s);
            } catch (Exception ignore) {
                // fallback to numeric attempts
            }
        }

        // Numeric epoch fallback
        for (String k : keys) {
            if (
                obj.containsKey(k) &&
                obj.get(k).getValueType() == JsonValue.ValueType.NUMBER
            ) {
                try {
                    long epoch = obj.getJsonNumber(k).longValue();
                    if (String.valueOf(epoch).length() <= 10) {
                        epoch = epoch * 1000L; // seconds -> millis
                    }
                    return new Date(epoch);
                } catch (Exception ignore) {
                    // continue
                }
            }
        }

        return null;
    }
}
