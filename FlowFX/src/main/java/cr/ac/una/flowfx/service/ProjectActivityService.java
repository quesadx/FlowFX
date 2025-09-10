package cr.ac.una.flowfx.service;

import cr.ac.una.flowfx.model.ProjectActivityDTO;
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
import java.lang.reflect.Method;
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
 * Client service for ProjectActivity operations against the FlowFX web service.
 *
 * <p>
 * This class prepares the web service port and provides CRUD and query helper
 * methods. It also contains robust JSON parsing utilities for converting the
 * web service's internal {@code mensajeInterno} payload into
 * {@link ProjectActivityDTO} instances.
 * </p>
 *
 * <p>
 * The public methods perform parameter validation and map web service responses
 * (of type {@code cr.ac.una.flowfx.ws.Respuesta}) into application {@link Respuesta}.
 * If the generated WS stub uses different operation names, adapt the port calls
 * accordingly.
 * </p>
 */
public class ProjectActivityService {

    private static final Logger LOG = Logger.getLogger(
        ProjectActivityService.class.getName()
    );
    private static final String ENTITY_KEY = "ProjectActivity";
    private static final String LIST_KEY = "ProjectActivities";

    private FlowFXWS port;

    /**
     * Constructs the client and configures the default endpoint address.
     * Adjust the endpoint if your deployment uses a different URL.
     */
    public ProjectActivityService() {
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
     * Finds a ProjectActivity by its id.
     *
     * @param id activity identifier (required)
     * @return a Respuesta containing the result or an error state
     */
    public Respuesta find(Long id) {
        try {
            if (id == null) {
                return new Respuesta(
                    false,
                    "The parameter 'id' is required.",
                    "activity.find.id.null"
                );
            }
            cr.ac.una.flowfx.ws.Respuesta wsResp = port.getProjectActivity(id);
            Respuesta r = mapRespuesta(wsResp);
            if (
                Boolean.TRUE.equals(r.getEstado())
            ) fillSingleFromMensajeInterno(r);
            return r;
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error retrieving activity [" + id + "]", ex);
            return new Respuesta(
                false,
                "Error retrieving the activity.",
                "activity.find " + ex.getMessage()
            );
        }
    }

    /**
     * Retrieves all project activities.
     *
     * @return a Respuesta with a list of activities or an error
     */
    public Respuesta findAll() {
        try {
            cr.ac.una.flowfx.ws.Respuesta wsResp =
                port.getAllProjectActivities();
            Respuesta r = mapRespuesta(wsResp);
            if (Boolean.TRUE.equals(r.getEstado())) fillListFromMensajeInterno(
                r
            );
            return r;
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error retrieving activities", ex);
            return new Respuesta(
                false,
                "Error retrieving activities.",
                "activity.findAll " + ex.getMessage()
            );
        }
    }

    /**
     * Deprecated create method. Use {@link #create(ProjectActivityDTO, Long, Long)}.
     *
     * @param activity ignored
     * @return an error Respuesta indicating required parameters
     */
    @Deprecated
    public Respuesta create(ProjectActivityDTO activity) {
        return new Respuesta(
            false,
            "projectId and responsibleId are required to create an activity. Use create(activity, projectId, responsibleId).",
            "activity.create.missing-ids"
        );
    }

    /**
     * Creates a project activity associated with a project and responsible person.
     *
     * @param activity      the activity DTO to create (required)
     * @param projectId     the project id (required)
     * @param responsibleId the responsible person id (required)
     * @return a Respuesta with the created activity or an error
     */
    public Respuesta create(
        ProjectActivityDTO activity,
        Long projectId,
        Long responsibleId
    ) {
        try {
            if (activity == null) {
                return new Respuesta(
                    false,
                    "The parameter 'activity' is required.",
                    "activity.create.null"
                );
            }
            if (projectId == null || responsibleId == null) {
                return new Respuesta(
                    false,
                    "The parameters 'projectId' and 'responsibleId' are required.",
                    "activity.create.ids.null"
                );
            }

            cr.ac.una.flowfx.ws.ProjectActivityDTO wsDto = toWs(activity);
            cr.ac.una.flowfx.ws.Respuesta wsResp = port.createProjectActivity(
                wsDto,
                projectId,
                responsibleId
            );

            Respuesta r = mapRespuesta(wsResp);
            if (Boolean.TRUE.equals(r.getEstado())) {
                fillSingleFromMensajeInterno(r);
            }
            return r;
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error creating activity", ex);
            return new Respuesta(
                false,
                "Error creating activity.",
                "activity.create " + ex.getMessage()
            );
        }
    }

    /**
     * Updates an existing project activity.
     *
     * @param activity the activity DTO to update (required, must contain id)
     * @return a Respuesta with the updated activity or an error
     */
    public Respuesta update(ProjectActivityDTO activity) {
        try {
            if (activity == null || activity.getId() == null) {
                return new Respuesta(
                    false,
                    "The activity and its 'id' are required.",
                    "activity.update.null"
                );
            }
            cr.ac.una.flowfx.ws.ProjectActivityDTO wsDto = toWs(activity);
            cr.ac.una.flowfx.ws.Respuesta wsResp = port.updateProjectActivity(
                wsDto
            );
            Respuesta r = mapRespuesta(wsResp);
            if (
                Boolean.TRUE.equals(r.getEstado())
            ) fillSingleFromMensajeInterno(r);
            return r;
        } catch (Exception ex) {
            LOG.log(
                Level.SEVERE,
                "Error updating activity [" +
                (activity != null ? activity.getId() : null) +
                "]",
                ex
            );
            return new Respuesta(
                false,
                "Error updating activity.",
                "activity.update " + ex.getMessage()
            );
        }
    }

    /**
     * Deletes a project activity by id.
     *
     * @param id the activity id (required)
     * @return a Respuesta describing the outcome
     */
    public Respuesta delete(Long id) {
        try {
            if (id == null) {
                return new Respuesta(
                    false,
                    "The parameter 'id' is required.",
                    "activity.delete.id.null"
                );
            }
            cr.ac.una.flowfx.ws.Respuesta wsResp = port.deleteProjectActivity(
                id
            );
            return mapRespuesta(wsResp);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error deleting activity [" + id + "]", ex);
            return new Respuesta(
                false,
                "Error deleting activity.",
                "activity.delete " + ex.getMessage()
            );
        }
    }

    /**
     * Retrieves recent activities for a user.
     *
     * @param userId     the user id (required)
     * @param maxResults maximum number of results (if <= 0 defaults to 10)
     * @return a Respuesta with a list of recent activities or an error
     */
    public Respuesta findRecentForUser(Long userId, int maxResults) {
        try {
            if (userId == null) {
                return new Respuesta(
                    false,
                    "The parameter 'userId' is required.",
                    "activity.recent.userId.null"
                );
            }
            if (maxResults <= 0) maxResults = 10;

            cr.ac.una.flowfx.ws.Respuesta wsResp =
                port.getRecentActivitiesForUser(userId, maxResults);
            Respuesta r = mapRespuesta(wsResp);
            if (Boolean.TRUE.equals(r.getEstado())) fillListFromMensajeInterno(
                r
            );
            return r;
        } catch (Exception ex) {
            LOG.log(
                Level.SEVERE,
                "Error retrieving recent activities for userId=" + userId,
                ex
            );
            return new Respuesta(
                false,
                "Error retrieving recent activities.",
                "activity.recent " + ex.getMessage()
            );
        }
    }

    /**
     * Counts activities grouped by the provided project ids.
     *
     * @param projectIds list of project ids (required)
     * @return a Respuesta containing a count under key 'count' or an error
     */
    public Respuesta countByProjectIds(List<Long> projectIds) {
        try {
            if (projectIds == null || projectIds.isEmpty()) {
                return new Respuesta(
                    false,
                    "The list 'projectIds' is required.",
                    "activity.count.projectIds.null"
                );
            }
            cr.ac.una.flowfx.ws.Respuesta wsResp =
                port.countActivitiesByProjectIds(projectIds);
            Respuesta r = mapRespuesta(wsResp);
            if (Boolean.TRUE.equals(r.getEstado())) {
                Long count = tryParseCount(r.getMensajeInterno());
                if (count != null) r.setResultado("count", count);
            }
            return r;
        } catch (Exception ex) {
            LOG.log(
                Level.SEVERE,
                "Error counting activities by projects " + projectIds,
                ex
            );
            return new Respuesta(
                false,
                "Error obtaining activity count.",
                "activity.count " + ex.getMessage()
            );
        }
    }

    /**
     * Converts an application DTO to the generated WS DTO.
     * Uses reflection for optional setters (for instance projectId) so the code
     * remains resilient to stub variations.
     *
     * @param dto source DTO
     * @return generated WS DTO
     */
    private cr.ac.una.flowfx.ws.ProjectActivityDTO toWs(
        ProjectActivityDTO dto
    ) {
        cr.ac.una.flowfx.ws.ProjectActivityDTO w =
            new cr.ac.una.flowfx.ws.ProjectActivityDTO();
        w.setId(dto.getId());
        // Use reflection so code compiles even if WS stub lacks setProjectId(Long)
        trySetLong(w, "setProjectId", dto.getProjectId());

        w.setDescription(dto.getDescription());
        if (dto.getStatus() != null) w.setStatus(dto.getStatus());
        if (dto.getExecutionOrder() != null) w.setExecutionOrder(
            dto.getExecutionOrder()
        );

        // If your WS stub includes date setters, enable them here:
        // w.setPlannedStartDate(dto.getPlannedStartDate());
        // w.setPlannedEndDate(dto.getPlannedEndDate());
        // w.setActualStartDate(dto.getActualStartDate());
        // w.setActualEndDate(dto.getActualEndDate());
        // w.setCreatedAt(dto.getCreatedAt());
        // w.setUpdatedAt(dto.getUpdatedAt());
        return w;
    }

    /**
     * Maps the generated web service response to the application Respuesta.
     *
     * @param ws the web service response
     * @return mapped Respuesta
     */
    private Respuesta mapRespuesta(cr.ac.una.flowfx.ws.Respuesta ws) {
        if (ws == null) return new Respuesta(
            false,
            "Null response from WS",
            "ws.response.null"
        );
        return new Respuesta(
            ws.isEstado(),
            ws.getMensaje(),
            ws.getMensajeInterno()
        );
    }

    /**
     * Parses mensajeInterno and populates a single ProjectActivityDTO in the Respuesta.
     * Handles value as object or array; recognizes common wrappers.
     *
     * @param r response container to populate
     */
    private void fillSingleFromMensajeInterno(Respuesta r) {
        String mi = r.getMensajeInterno();
        if (mi == null || mi.isBlank()) return;

        try (JsonReader jr = Json.createReader(new StringReader(mi))) {
            JsonValue val = jr.readValue();
            switch (val.getValueType()) {
                case OBJECT: {
                    JsonObject obj = (JsonObject) val;
                    obj = unwrapObject(
                        obj,
                        "activity",
                        "Activity",
                        "projectActivity",
                        "ProjectActivity",
                        "PROJECT_ACTIVITY",
                        "PROJECTACTIVITY"
                    );
                    ProjectActivityDTO dto = fromJsonActivity(obj);
                    if (dto != null) {
                        r.setResultado(ENTITY_KEY, dto);
                        return;
                    }
                    break;
                }
                case ARRAY: {
                    JsonArray arr = (JsonArray) val;
                    if (
                        !arr.isEmpty() &&
                        arr.get(0).getValueType() == JsonValue.ValueType.OBJECT
                    ) {
                        ProjectActivityDTO dto = fromJsonActivity(
                            arr.getJsonObject(0)
                        );
                        if (dto != null) {
                            r.setResultado(ENTITY_KEY, dto);
                            return;
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        } catch (Exception ignore) {}

        // Fallback: try strict object parse
        try (JsonReader jr = Json.createReader(new StringReader(mi))) {
            JsonObject obj = jr.readObject();
            obj = unwrapObject(
                obj,
                "activity",
                "Activity",
                "projectActivity",
                "ProjectActivity",
                "PROJECT_ACTIVITY",
                "PROJECTACTIVITY"
            );
            ProjectActivityDTO dto = fromJsonActivity(obj);
            if (dto != null) {
                r.setResultado(ENTITY_KEY, dto);
            }
        } catch (Exception ignore) {}
    }

    /**
     * Parses mensajeInterno and populates a list of ProjectActivityDTO in the Respuesta.
     *
     * @param r response container to populate
     */
    private void fillListFromMensajeInterno(Respuesta r) {
        String mi = r.getMensajeInterno();
        if (mi == null || mi.isBlank()) return;

        List<ProjectActivityDTO> list = new ArrayList<>();

        // 1) Try direct array
        try (JsonReader jr = Json.createReader(new StringReader(mi))) {
            JsonArray arr = jr.readArray();
            for (int i = 0; i < arr.size(); i++) {
                if (arr.get(i).getValueType() == JsonValue.ValueType.OBJECT) {
                    ProjectActivityDTO dto = fromJsonActivity(
                        arr.getJsonObject(i)
                    );
                    if (dto != null) list.add(dto);
                }
            }
            r.setResultado(LIST_KEY, list);
            return;
        } catch (Exception ignore) {}

        // 2) Try object with array inside under common wrapper keys
        try (JsonReader jr = Json.createReader(new StringReader(mi))) {
            JsonObject root = jr.readObject();
            JsonArray arr = unwrapArray(
                root,
                "activities",
                "Activities",
                "ACTIVITIES",
                "projectActivities",
                "ProjectActivities",
                "PROJECT_ACTIVITIES",
                "PROJECTACTIVITIES",
                "data",
                "list"
            );
            if (arr != null) {
                for (int i = 0; i < arr.size(); i++) {
                    if (
                        arr.get(i).getValueType() == JsonValue.ValueType.OBJECT
                    ) {
                        ProjectActivityDTO dto = fromJsonActivity(
                            arr.getJsonObject(i)
                        );
                        if (dto != null) list.add(dto);
                    }
                }
            }
        } catch (Exception ex) {
            LOG.log(
                Level.WARNING,
                "Unable to parse mensajeInterno into List<ProjectActivityDTO>",
                ex
            );
        }

        r.setResultado(LIST_KEY, list);
    }

    private JsonObject unwrapObject(JsonObject obj, String... keys) {
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

    private JsonArray unwrapArray(JsonObject obj, String... keys) {
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

    private ProjectActivityDTO fromJsonActivity(JsonObject obj) {
        if (obj == null) return null;

        ProjectActivityDTO dto = new ProjectActivityDTO();
        dto.setId(getLong(obj, "id", "activity_id", "activityId"));
        dto.setProjectId(getLong(obj, "project_id", "projectId"));
        dto.setDescription(getString(obj, "description", "desc"));

        String st = getString(obj, "status", "STATUS");
        dto.setStatus(st);

        Integer order = getInteger(obj, "execution_order", "executionOrder");
        if (order != null) dto.setExecutionOrder(order);

        Date psd = getDate(obj, "planned_start_date", "plannedStartDate");
        Date ped = getDate(obj, "planned_end_date", "plannedEndDate");
        Date asd = getDate(obj, "actual_start_date", "actualStartDate");
        Date aed = getDate(obj, "actual_end_date", "actualEndDate");
        Date cat = getDate(obj, "created_at", "createdAt");
        Date uat = getDate(obj, "updated_at", "updatedAt");

        dto.setPlannedStartDate(psd);
        dto.setPlannedEndDate(ped);
        dto.setActualStartDate(asd);
        dto.setActualEndDate(aed);
        dto.setCreatedAt(cat);
        dto.setUpdatedAt(uat);

        return dto;
    }

    /**
     * Tries to parse a count value from a mensajeInterno that may be a number,
     * a string with the numeric, or an object containing a count/total/value key.
     *
     * @param mi the mensajeInterno string
     * @return parsed count as Long or null if not parseable
     */
    private Long tryParseCount(String mi) {
        if (mi == null || mi.isBlank()) return null;

        try (JsonReader jr = Json.createReader(new StringReader(mi))) {
            JsonValue val = jr.readValue();
            switch (val.getValueType()) {
                case NUMBER:
                    return ((JsonNumber) val).longValue();
                case STRING:
                    try {
                        return Long.parseLong(((JsonString) val).getString());
                    } catch (NumberFormatException ignore) {}
                    break;
                case OBJECT:
                    JsonObject obj = (JsonObject) val;
                    Long c = getLong(obj, "count", "total", "value");
                    if (c != null) return c;
                    break;
                default:
                    break;
            }
        } catch (Exception ignore) {}

        try (JsonReader jr = Json.createReader(new StringReader(mi))) {
            JsonObject obj = jr.readObject();
            return getLong(obj, "count", "total", "value");
        } catch (Exception ignore) {}
        return null;
    }

    private String getString(JsonObject obj, String... keys) {
        for (String k : keys) {
            if (obj.containsKey(k) && !obj.isNull(k)) {
                try {
                    switch (obj.get(k).getValueType()) {
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
                } catch (Exception ignore) {}
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
                } catch (Exception ignore) {}
            }
        }
        return null;
    }

    private Integer getInteger(JsonObject obj, String... keys) {
        for (String k : keys) {
            if (obj.containsKey(k) && !obj.isNull(k)) {
                try {
                    JsonValue v = obj.get(k);
                    switch (v.getValueType()) {
                        case NUMBER:
                            return obj.getJsonNumber(k).intValue();
                        case STRING:
                            String s = obj.getString(k);
                            if (
                                s != null && !s.isBlank()
                            ) return Integer.parseInt(s.trim());
                            break;
                        default:
                            String raw = v.toString();
                            if (
                                raw != null && !raw.isBlank()
                            ) return Integer.parseInt(
                                raw.replace("\"", "").trim()
                            );
                    }
                } catch (Exception ignore) {}
            }
        }
        return null;
    }

    private Date getDate(JsonObject obj, String... keys) {
        String s = getString(obj, keys);
        if (s != null && !s.isBlank()) {
            try {
                Instant inst = Instant.parse(s);
                return Date.from(inst);
            } catch (DateTimeParseException ignore) {}
            try {
                return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(s);
            } catch (ParseException ignore) {}
            try {
                return new SimpleDateFormat("yyyy-MM-dd").parse(s);
            } catch (ParseException ignore) {}
        }
        for (String k : keys) {
            if (
                obj.containsKey(k) &&
                obj.get(k).getValueType() == JsonValue.ValueType.NUMBER
            ) {
                try {
                    long epoch = obj.getJsonNumber(k).longValue();
                    if (String.valueOf(epoch).length() <= 10) {
                        epoch = epoch * 1000L;
                    }
                    return new Date(epoch);
                } catch (Exception ignore) {}
            }
        }
        return null;
    }

    /**
     * Reflection helper to set optional Long fields on WS DTOs without compile-time dependency.
     *
     * @param target     the generated WS DTO
     * @param setterName setter name to try (e.g. "setProjectId")
     * @param value      Long value to set
     */
    private void trySetLong(Object target, String setterName, Long value) {
        if (target == null || value == null || setterName == null) return;
        Class<?> cls = target.getClass();
        try {
            Method m = cls.getMethod(setterName, Long.class);
            m.invoke(target, value);
            return;
        } catch (NoSuchMethodException e1) {
            try {
                Method m2 = cls.getMethod(setterName, long.class);
                m2.invoke(target, value.longValue());
                return;
            } catch (Exception ignore) {}
        } catch (Exception ignore) {}
        // If neither exists, just skip silently
    }
}
