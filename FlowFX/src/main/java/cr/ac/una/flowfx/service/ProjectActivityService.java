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
import java.lang.reflect.Method; // added
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProjectActivityService {

    private static final Logger LOG = Logger.getLogger(ProjectActivityService.class.getName());
    private static final String ENTITY_KEY = "ProjectActivity";
    private static final String LIST_KEY = "ProjectActivities";

    private FlowFXWS port;

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
            LOG.log(Level.SEVERE, "Error inicializando port del WS", e);
        }
    }

    public Respuesta find(Long id) {
        try {
            if (id == null) {
                return new Respuesta(false, "El parámetro 'id' es requerido.", "activity.find.id.null");
            }
            cr.ac.una.flowfx.ws.Respuesta wsResp = port.getProjectActivity(id);
            Respuesta r = mapRespuesta(wsResp);
            if (Boolean.TRUE.equals(r.getEstado())) fillSingleFromMensajeInterno(r);
            return r;
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo actividad [" + id + "]", ex);
            return new Respuesta(false, "Error obteniendo la actividad.", "activity.find " + ex.getMessage());
        }
    }

    public Respuesta findAll() {
        try {
            cr.ac.una.flowfx.ws.Respuesta wsResp = port.getAllProjectActivities();
            Respuesta r = mapRespuesta(wsResp);
            if (Boolean.TRUE.equals(r.getEstado())) fillListFromMensajeInterno(r);
            return r;
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo actividades", ex);
            return new Respuesta(false, "Error obteniendo actividades.", "activity.findAll " + ex.getMessage());
        }
    }

    @Deprecated
    public Respuesta create(ProjectActivityDTO activity) {
        return new Respuesta(false,
                "Se requieren 'createdBy' y 'responsibleId' para crear la actividad. Usa create(activity, createdBy, responsibleId).",
                "activity.create.missing-ids");
    }

    public Respuesta create(ProjectActivityDTO activity, Long createdBy, Long responsibleId) {
        try {
            if (activity == null) {
                return new Respuesta(false, "El parámetro 'activity' es requerido.", "activity.create.null");
            }
            if (createdBy == null || responsibleId == null) {
                return new Respuesta(false, "Los parámetros 'createdBy' y 'responsibleId' son requeridos.", "activity.create.ids.null");
            }

            cr.ac.una.flowfx.ws.ProjectActivityDTO wsDto = toWs(activity);
            cr.ac.una.flowfx.ws.Respuesta wsResp = port.createProjectActivity(wsDto, createdBy, responsibleId);

            Respuesta r = mapRespuesta(wsResp);
            if (Boolean.TRUE.equals(r.getEstado())) {
                fillSingleFromMensajeInterno(r);
            }
            return r;
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error creando actividad", ex);
            return new Respuesta(false, "Error creando actividad.", "activity.create " + ex.getMessage());
        }
    }

    public Respuesta update(ProjectActivityDTO activity) {
        try {
            if (activity == null || activity.getId() == null) {
                return new Respuesta(false, "La actividad y su 'id' son requeridos.", "activity.update.null");
            }
            cr.ac.una.flowfx.ws.ProjectActivityDTO wsDto = toWs(activity);
            cr.ac.una.flowfx.ws.Respuesta wsResp = port.updateProjectActivity(wsDto);
            Respuesta r = mapRespuesta(wsResp);
            if (Boolean.TRUE.equals(r.getEstado())) fillSingleFromMensajeInterno(r);
            return r;
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error actualizando actividad [" + (activity != null ? activity.getId() : null) + "]", ex);
            return new Respuesta(false, "Error actualizando actividad.", "activity.update " + ex.getMessage());
        }
    }

    public Respuesta delete(Long id) {
        try {
            if (id == null) {
                return new Respuesta(false, "El parámetro 'id' es requerido.", "activity.delete.id.null");
            }
            cr.ac.una.flowfx.ws.Respuesta wsResp = port.deleteProjectActivity(id);
            return mapRespuesta(wsResp);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error eliminando actividad [" + id + "]", ex);
            return new Respuesta(false, "Error eliminando actividad.", "activity.delete " + ex.getMessage());
        }
    }

    public Respuesta findRecentForUser(Long userId, int maxResults) {
        try {
            if (userId == null) {
                return new Respuesta(false, "El parámetro 'userId' es requerido.", "activity.recent.userId.null");
            }
            if (maxResults <= 0) maxResults = 10;

            cr.ac.una.flowfx.ws.Respuesta wsResp = port.getRecentActivitiesForUser(userId, maxResults);
            Respuesta r = mapRespuesta(wsResp);
            if (Boolean.TRUE.equals(r.getEstado())) fillListFromMensajeInterno(r);
            return r;
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo actividades recientes para userId=" + userId, ex);
            return new Respuesta(false, "Error obteniendo actividades recientes.", "activity.recent " + ex.getMessage());
        }
    }

    public Respuesta countByProjectIds(List<Long> projectIds) {
        try {
            if (projectIds == null || projectIds.isEmpty()) {
                return new Respuesta(false, "La lista 'projectIds' es requerida.", "activity.count.projectIds.null");
            }
            cr.ac.una.flowfx.ws.Respuesta wsResp = port.countActivitiesByProjectIds(projectIds);
            Respuesta r = mapRespuesta(wsResp);
            if (Boolean.TRUE.equals(r.getEstado())) {
                Long count = tryParseCount(r.getMensajeInterno());
                if (count != null) r.setResultado("count", count);
            }
            return r;
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error contando actividades por proyectos " + projectIds, ex);
            return new Respuesta(false, "Error obteniendo conteo de actividades.", "activity.count " + ex.getMessage());
        }
    }

    private cr.ac.una.flowfx.ws.ProjectActivityDTO toWs(ProjectActivityDTO dto) {
        cr.ac.una.flowfx.ws.ProjectActivityDTO w = new cr.ac.una.flowfx.ws.ProjectActivityDTO();
        w.setId(dto.getId());
        // Was: w.setProjectId(dto.getProjectId());
        // Use reflection so code compiles even if WS stub lacks setProjectId(Long)
        trySetLong(w, "setProjectId", dto.getProjectId());

        w.setDescription(dto.getDescription());
        if (dto.getStatus() != null) w.setStatus(dto.getStatus());
        if (dto.getExecutionOrder() != null) w.setExecutionOrder(dto.getExecutionOrder());

        // If the WS DTO defines date setters, you can enable them:
        // w.setPlannedStartDate(dto.getPlannedStartDate());
        // w.setPlannedEndDate(dto.getPlannedEndDate());
        // w.setActualStartDate(dto.getActualStartDate());
        // w.setActualEndDate(dto.getActualEndDate());
        // w.setCreatedAt(dto.getCreatedAt());
        // w.setUpdatedAt(dto.getUpdatedAt());
        return w;
    }

    private Respuesta mapRespuesta(cr.ac.una.flowfx.ws.Respuesta ws) {
        if (ws == null) return new Respuesta(false, "Respuesta nula del WS", "ws.response.null");
        return new Respuesta(ws.isEstado(), ws.getMensaje(), ws.getMensajeInterno());
    }

    private void fillSingleFromMensajeInterno(Respuesta r) {
        String mi = r.getMensajeInterno();
        if (mi == null || mi.isBlank()) return;

        try (JsonReader jr = Json.createReader(new StringReader(mi))) {
            JsonValue val = jr.readValue();
            switch (val.getValueType()) {
                case OBJECT: {
                    JsonObject obj = (JsonObject) val;
                    obj = unwrapObject(obj,
                            "activity", "Activity",
                            "projectActivity", "ProjectActivity",
                            "PROJECT_ACTIVITY", "PROJECTACTIVITY"
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
                    if (!arr.isEmpty() && arr.get(0).getValueType() == JsonValue.ValueType.OBJECT) {
                        ProjectActivityDTO dto = fromJsonActivity(arr.getJsonObject(0));
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
        } catch (Exception ignore) {
        }

        try (JsonReader jr = Json.createReader(new StringReader(mi))) {
            JsonObject obj = jr.readObject();
            obj = unwrapObject(obj,
                    "activity", "Activity",
                    "projectActivity", "ProjectActivity",
                    "PROJECT_ACTIVITY", "PROJECTACTIVITY"
            );
            ProjectActivityDTO dto = fromJsonActivity(obj);
            if (dto != null) {
                r.setResultado(ENTITY_KEY, dto);
            }
        } catch (Exception ignore) {
        }
    }

    private void fillListFromMensajeInterno(Respuesta r) {
        String mi = r.getMensajeInterno();
        if (mi == null || mi.isBlank()) return;

        List<ProjectActivityDTO> list = new ArrayList<>();

        try (JsonReader jr = Json.createReader(new StringReader(mi))) {
            JsonArray arr = jr.readArray();
            for (int i = 0; i < arr.size(); i++) {
                if (arr.get(i).getValueType() == JsonValue.ValueType.OBJECT) {
                    ProjectActivityDTO dto = fromJsonActivity(arr.getJsonObject(i));
                    if (dto != null) list.add(dto);
                }
            }
            r.setResultado(LIST_KEY, list);
            return;
        } catch (Exception ignore) {
        }

        try (JsonReader jr = Json.createReader(new StringReader(mi))) {
            JsonObject root = jr.readObject();
            JsonArray arr = unwrapArray(root,
                    "activities", "Activities", "ACTIVITIES",
                    "projectActivities", "ProjectActivities", "PROJECT_ACTIVITIES", "PROJECTACTIVITIES",
                    "data", "list"
            );
            if (arr != null) {
                for (int i = 0; i < arr.size(); i++) {
                    if (arr.get(i).getValueType() == JsonValue.ValueType.OBJECT) {
                        ProjectActivityDTO dto = fromJsonActivity(arr.getJsonObject(i));
                        if (dto != null) list.add(dto);
                    }
                }
            }
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "No se pudo parsear mensajeInterno a List<ProjectActivityDTO>", ex);
        }

        r.setResultado(LIST_KEY, list);
    }

    private JsonObject unwrapObject(JsonObject obj, String... keys) {
        for (String k : keys) {
            if (obj.containsKey(k) && obj.get(k).getValueType() == JsonValue.ValueType.OBJECT) {
                return obj.getJsonObject(k);
            }
        }
        return obj;
    }

    private JsonArray unwrapArray(JsonObject obj, String... keys) {
        for (String k : keys) {
            if (obj.containsKey(k) && obj.get(k).getValueType() == JsonValue.ValueType.ARRAY) {
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
        } catch (Exception ignore) {
        }

        try (JsonReader jr = Json.createReader(new StringReader(mi))) {
            JsonObject obj = jr.readObject();
            return getLong(obj, "count", "total", "value");
        } catch (Exception ignore) {
        }
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
                            if (s != null && !s.isBlank()) return Long.parseLong(s.trim());
                            break;
                        default:
                            String raw = v.toString();
                            if (raw != null && !raw.isBlank()) return Long.parseLong(raw.replace("\"", "").trim());
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
                            if (s != null && !s.isBlank()) return Integer.parseInt(s.trim());
                            break;
                        default:
                            String raw = v.toString();
                            if (raw != null && !raw.isBlank()) return Integer.parseInt(raw.replace("\"", "").trim());
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
            } catch (DateTimeParseException ignore) {
            }
            try {
                return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(s);
            } catch (ParseException ignore) {
            }
            try {
                return new SimpleDateFormat("yyyy-MM-dd").parse(s);
            } catch (ParseException ignore) {
            }
        }
        for (String k : keys) {
            if (obj.containsKey(k) && obj.get(k).getValueType() == JsonValue.ValueType.NUMBER) {
                try {
                    long epoch = obj.getJsonNumber(k).longValue();
                    if (String.valueOf(epoch).length() <= 10) {
                        epoch = epoch * 1000L;
                    }
                    return new Date(epoch);
                } catch (Exception ignore) {
                }
            }
        }
        return null;
    }

    // Reflection helper to set optional Long fields on WS DTOs without compile-time dependency
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
            } catch (Exception ignore) {
            }
        } catch (Exception ignore) {
        }
        // If neither exists, just skip silently
    }
}