package cr.ac.una.flowfx.service;

import cr.ac.una.flowfx.model.ProjectActivityDTO;
import cr.ac.una.flowfx.util.Respuesta;
import cr.ac.una.flowfx.ws.FlowFXWS;
import cr.ac.una.flowfx.ws.FlowFXWS_Service;
import jakarta.xml.ws.BindingProvider;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProjectActivityService {
//
//    private static final Logger LOG = Logger.getLogger(ProjectActivityService.class.getName());
//    private FlowFXWS port;
//
//    public ProjectActivityService() {
//        try {
//            FlowFXWS_Service service = new FlowFXWS_Service();
//            port = service.getFlowFXWSPort();
//
//            if (port instanceof BindingProvider) {
//                ((BindingProvider) port).getRequestContext().put(
//                        BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
//                        "http://localhost:8080/FlowFXWS/FlowFXWS"
//                );
//            }
//        } catch (Exception e) {
//            LOG.log(Level.SEVERE, "Error inicializando port del WS", e);
//        }
//    }
//
////    public Respuesta find(Long id) {
////        try {
////            if (id == null) return new Respuesta(false, "El parámetro 'id' es requerido.", "find.id.null");
////
////            cr.ac.una.flowfx.ws.Respuesta wsResp = port.getProjectActivity(id);
////            Respuesta r = mapRespuesta(wsResp);
////
////            if (Boolean.TRUE.equals(r.getEstado()) && wsResp.getMensajeInterno() != null) {
////                ProjectActivityDTO dto = parseActivity(wsResp.getMensajeInterno());
////                r.setResultado("Activity", dto);
////            }
////
////            return r;
////
////        } catch (Exception ex) {
////            LOG.log(Level.SEVERE, "Error obteniendo actividad [" + id + "]", ex);
////            return new Respuesta(false, "Error obteniendo actividad.", "find " + ex.getMessage());
////        }
////    }
//
//    public Respuesta create(ProjectActivityDTO activity) {
//        try {
//            if (activity == null) return new Respuesta(false, "El parámetro 'activity' es requerido.", "create.activity.null");
//
//            cr.ac.una.flowfx.ws.Respuesta wsResp = port.createProjectActivity(toWs(activity));
//            Respuesta r = mapRespuesta(wsResp);
//
//            if (Boolean.TRUE.equals(r.getEstado()) && wsResp.getMensajeInterno() != null) {
//                ProjectActivityDTO dto = parseActivity(wsResp.getMensajeInterno());
//                r.setResultado("Activity", dto);
//            }
//
//            return r;
//
//        } catch (Exception ex) {
//            LOG.log(Level.SEVERE, "Error creando actividad", ex);
//            return new Respuesta(false, "Error creando actividad.", "create " + ex.getMessage());
//        }
//    }
//
//    public Respuesta update(ProjectActivityDTO activity) {
//        try {
//            if (activity == null || activity.getId() == null)
//                return new Respuesta(false, "La actividad y su 'id' son requeridos.", "update.activity.null");
//
//            cr.ac.una.flowfx.ws.Respuesta wsResp = port.updateProjectActivity(toWs(activity));
//            Respuesta r = mapRespuesta(wsResp);
//
//            if (Boolean.TRUE.equals(r.getEstado()) && wsResp.getMensajeInterno() != null) {
//                ProjectActivityDTO dto = parseActivity(wsResp.getMensajeInterno());
//                r.setResultado("Activity", dto);
//            }
//
//            return r;
//
//        } catch (Exception ex) {
//            LOG.log(Level.SEVERE, "Error actualizando actividad [" + activity.getId() + "]", ex);
//            return new Respuesta(false, "Error actualizando actividad.", "update " + ex.getMessage());
//        }
//    }
//
//    public Respuesta delete(Long id) {
//        try {
//            if (id == null) return new Respuesta(false, "El parámetro 'id' es requerido.", "delete.id.null");
//
//            cr.ac.una.flowfx.ws.Respuesta wsResp = port.deleteProjectActivity(id);
//            return mapRespuesta(wsResp);
//
//        } catch (Exception ex) {
//            LOG.log(Level.SEVERE, "Error eliminando actividad [" + id + "]", ex);
//            return new Respuesta(false, "Error eliminando actividad.", "delete " + ex.getMessage());
//        }
//    }
//
    public Respuesta findRecentForUser(Long userId, int maxResults) {
//        try {
//            if (userId == null) return new Respuesta(false, "El parámetro 'userId' es requerido.", "findRecentForUser.userId.null");
//            if (maxResults <= 0) return new Respuesta(false, "El parámetro 'maxResults' debe ser > 0.", "findRecentForUser.maxResults.invalid");
//
//            cr.ac.una.flowfx.ws.Respuesta wsResp = port.getRecentActivitiesForUser(userId, maxResults);
//            Respuesta r = mapRespuesta(wsResp);
//
//            if (Boolean.TRUE.equals(r.getEstado()) && wsResp.getMensajeInterno() != null) {
//                ProjectActivityDTO[] arr = parseActivityArray(wsResp.getMensajeInterno());
//                List<ProjectActivityDTO> dtos = Arrays.asList(arr != null ? arr : new ProjectActivityDTO[0]);
//                r.setResultado("Activities", dtos);
//            }
//
//            return r;
//
//        } catch (Exception ex) {
//            LOG.log(Level.SEVERE, "Error obteniendo actividades recientes del usuario [" + userId + "]", ex);
//            return new Respuesta(false, "Error obteniendo actividades", "findRecentForUser " + ex.getMessage());
//        }
return null;
    }
//
    public Respuesta countByProjectIds(List<Long> projectIds) {
//        try {
//            if (projectIds == null || projectIds.isEmpty()) return new Respuesta(true, "Sin proyectos", "countByProjectIds empty", "Counts", new HashMap<Long, Long>());
//
//            cr.ac.una.flowfx.ws.Respuesta wsResp = port.countActivitiesByProjectIds(projectIds);
//            Respuesta r = mapRespuesta(wsResp);
//
//            if (Boolean.TRUE.equals(r.getEstado()) && wsResp.getMensajeInterno() != null) {
//                @SuppressWarnings("unchecked")
//                Map<Object, Object> raw = (Map<Object, Object>) new com.fasterxml.jackson.databind.ObjectMapper().readValue(wsResp.getMensajeInterno(), Map.class);
//                Map<Long, Long> counts = new HashMap<>();
//                for (Map.Entry<Object, Object> e : raw.entrySet()) {
//                    Long pid = e.getKey() instanceof Number ? ((Number) e.getKey()).longValue() : Long.valueOf(e.getKey().toString());
//                    Long cnt = e.getValue() instanceof Number ? ((Number) e.getValue()).longValue() : Long.valueOf(e.getValue().toString());
//                    counts.put(pid, cnt);
//                }
//                r.setResultado("Counts", counts);
//            }
//
//            return r;
//
//        } catch (Exception ex) {
//            LOG.log(Level.SEVERE, "Error contando actividades por proyecto", ex);
//            return new Respuesta(false, "Error contando actividades por proyecto", "countByProjectIds " + ex.getMessage());
//        }
return null;
    }
//
//    // ----------------- Métodos auxiliares -----------------
//
//    private Respuesta mapRespuesta(cr.ac.una.flowfx.ws.Respuesta ws) {
//        if (ws == null) return new Respuesta(false, "Respuesta nula del WS", "ws.response.null");
//        return new Respuesta(ws.isEstado(), ws.getMensaje(), ws.getMensajeInterno());
//    }
//
//    private cr.ac.una.flowfx.ws.ProjectActivityDTO toWs(ProjectActivityDTO dto) {
//        cr.ac.una.flowfx.ws.ProjectActivityDTO w = new cr.ac.una.flowfx.ws.ProjectActivityDTO();
//        w.setId(dto.getId());
//        w.setName(dto.getName());
//        w.setDescription(dto.getDescription());
//        w.setStartDate(dto.getStartDate());
//        w.setEndDate(dto.getEndDate());
//        return w;
//    }
//
//    private ProjectActivityDTO parseActivity(String json) {
//        try (jakarta.json.JsonReader jr = jakarta.json.Json.createReader(new java.io.StringReader(json))) {
//            jakarta.json.JsonObject obj = jr.readObject();
//            ProjectActivityDTO dto = new ProjectActivityDTO();
//            dto.setId(obj.containsKey("id") ? obj.getJsonNumber("id").longValue() : null);
//            dto.setName(obj.containsKey("name") ? obj.getString("name") : null);
//            dto.setDescription(obj.containsKey("description") ? obj.getString("description") : null);
//            dto.setStartDate(obj.containsKey("startDate") ? obj.getString("startDate") : null);
//            dto.setEndDate(obj.containsKey("endDate") ? obj.getString("endDate") : null);
//            return dto;
//        } catch (Exception e) {
//            LOG.log(Level.WARNING, "No se pudo parsear mensajeInterno a ProjectActivityDTO", e);
//            return null;
//        }
//    }
//
//    private ProjectActivityDTO[] parseActivityArray(String json) {
//        try (jakarta.json.JsonReader jr = jakarta.json.Json.createReader(new java.io.StringReader(json))) {
//            jakarta.json.JsonArray arr = jr.readArray();
//            ProjectActivityDTO[] dtos = new ProjectActivityDTO[arr.size()];
//            for (int i = 0; i < arr.size(); i++) {
//                dtos[i] = parseActivity(arr.getJsonObject(i).toString());
//            }
//            return dtos;
//        } catch (Exception e) {
//            LOG.log(Level.WARNING, "No se pudo parsear mensajeInterno a ProjectActivityDTO[]", e);
//            return new ProjectActivityDTO[0];
//        }
//    }
}
