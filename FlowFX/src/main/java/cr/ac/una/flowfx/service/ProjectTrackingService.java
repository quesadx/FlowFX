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
import jakarta.xml.ws.BindingProvider;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service cliente para gestionar Project Tracking.
 * Sigue el mismo patrón de ProjectService:
 * - mapRespuesta
 * - parseo robusto de JSON a DTO
 *
 * No se tocan/añaden campos a tu DTO.
 */
public class ProjectTrackingService {

    private static final Logger LOG = Logger.getLogger(ProjectTrackingService.class.getName());
    private static final String ENTITY_KEY = "ProjectTracking";
    private static final String LIST_KEY = "ProjectTrackings";

    private FlowFXWS port;

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
            LOG.log(Level.SEVERE, "Error inicializando port del WS", e);
        }
    }

    /**
     * Obtiene el tracking de un proyecto.
     * Ajusta el nombre de la operación si tu stub lo generó distinto.
     */
    public Respuesta findByProject(Long projectId) {
        try {
            if (projectId == null) {
                return new Respuesta(false, "El parámetro 'projectId' es requerido.", "tracking.byProject.projectId.null");
            }
            cr.ac.una.flowfx.ws.Respuesta wsResp = port.getProjectTrackingByProject(projectId);
            Respuesta r = mapRespuesta(wsResp);
            if (Boolean.TRUE.equals(r.getEstado())) {
                fillListFromMensajeInterno(r);
            }
            return r;
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo tracking para projectId=" + projectId, ex);
            return new Respuesta(false, "Error obteniendo tracking del proyecto.", "tracking.byProject " + ex.getMessage());
        }
    }

    // ================= Utilitarios =================

    private Respuesta mapRespuesta(cr.ac.una.flowfx.ws.Respuesta ws) {
        if (ws == null) return new Respuesta(false, "Respuesta nula del WS", "ws.response.null");
        return new Respuesta(ws.isEstado(), ws.getMensaje(), ws.getMensajeInterno());
    }

    private void fillListFromMensajeInterno(Respuesta r) {
        String mi = r.getMensajeInterno();
        if (mi == null || mi.isBlank()) return;

        List<ProjectTrackingDTO> list = new ArrayList<>();

        // 1) arreglo directo
        try (JsonReader jr = Json.createReader(new StringReader(mi))) {
            var arr = jr.readArray();
            for (int i = 0; i < arr.size(); i++) {
                JsonObject obj = arr.getJsonObject(i);
                ProjectTrackingDTO dto = fromJsonTracking(obj);
                if (dto != null) list.add(dto);
            }
            r.setResultado(LIST_KEY, list);
            return;
        } catch (Exception ignore) {}

        // 2) objeto con arreglo dentro
        try (JsonReader jr = Json.createReader(new StringReader(mi))) {
            JsonObject root = jr.readObject();
            JsonArray arr = null;
            if (root.containsKey("trackings")) arr = root.getJsonArray("trackings");
            else if (root.containsKey("Trackings")) arr = root.getJsonArray("Trackings");
            else if (root.containsKey("TRACKINGS")) arr = root.getJsonArray("TRACKINGS");
            else if (root.containsKey("data") && root.get("data").getValueType() == jakarta.json.JsonValue.ValueType.ARRAY) arr = root.getJsonArray("data");
            else if (root.containsKey("list") && root.get("list").getValueType() == jakarta.json.JsonValue.ValueType.ARRAY) arr = root.getJsonArray("list");

            if (arr != null) {
                for (int i = 0; i < arr.size(); i++) {
                    JsonObject obj = arr.getJsonObject(i);
                    ProjectTrackingDTO dto = fromJsonTracking(obj);
                    if (dto != null) list.add(dto);
                }
            }
        } catch (Exception e) {
            LOG.log(Level.WARNING, "No se pudo parsear mensajeInterno a List<ProjectTrackingDTO>", e);
        }

        r.setResultado(LIST_KEY, list);
    }

    private ProjectTrackingDTO fromJsonTracking(JsonObject obj) {
        if (obj == null) return null;

        ProjectTrackingDTO dto = new ProjectTrackingDTO();
        dto.setId(getLong(obj, "id", "tracking_id", "trackingId"));
        dto.setProjectId(getLong(obj, "project_id", "projectId"));
        dto.setObservations(getString(obj, "observations", "obs"));
        // Fecha de tracking (no convierto a Date aquí sin conocer el formato)
        dto.setTrackingDate(null);
        // Porcentaje (NUMBER(5,2)) -> Double
        Double progress = getDouble(obj, "progress_percentage", "progressPercentage");
        if (progress != null) dto.setProgressPercentage(progress);
        // created_by / created_at
        dto.setCreatedBy(getLong(obj, "created_by", "createdBy"));
        dto.setCreatedAt(null);

        return dto;
    }

    // ======== Helpers de lectura JSON ========

    private String getString(JsonObject obj, String... keys) {
        for (String k : keys) {
            if (obj.containsKey(k) && !obj.isNull(k)) {
                try {
                    switch (obj.get(k).getValueType()) {
                        case STRING:
                            return ((JsonString) obj.get(k)).getString();
                        case NUMBER:
                            return ((JsonNumber) obj.get(k)).toString();
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
                    if (obj.get(k).getValueType() == jakarta.json.JsonValue.ValueType.NUMBER) {
                        return obj.getJsonNumber(k).longValue();
                    } else if (obj.get(k).getValueType() == jakarta.json.JsonValue.ValueType.STRING) {
                        String s = obj.getString(k);
                        if (s != null && !s.isBlank()) return Long.parseLong(s);
                    } else {
                        String s = obj.get(k).toString();
                        if (s != null && !s.isBlank()) return Long.parseLong(s.replace("\"", ""));
                    }
                } catch (Exception ignore) {}
            }
        }
        return null;
    }

    private Double getDouble(JsonObject obj, String... keys) {
        for (String k : keys) {
            if (obj.containsKey(k) && !obj.isNull(k)) {
                try {
                    if (obj.get(k).getValueType() == jakarta.json.JsonValue.ValueType.NUMBER) {
                        return obj.getJsonNumber(k).doubleValue();
                    } else if (obj.get(k).getValueType() == jakarta.json.JsonValue.ValueType.STRING) {
                        String s = obj.getString(k);
                        if (s != null && !s.isBlank()) return Double.valueOf(s);
                    } else {
                        String s = obj.get(k).toString();
                        if (s != null && !s.isBlank()) return Double.valueOf(s.replace("\"", ""));
                    }
                } catch (Exception ignore) {}
            }
        }
        return null;
    }
}