package cr.ac.una.flowfx.service;

import cr.ac.una.flowfx.model.ProjectTrackingDTO;
import cr.ac.una.flowfx.util.Respuesta;
import cr.ac.una.flowfx.ws.FlowFXWS;
import cr.ac.una.flowfx.ws.FlowFXWS_Service;
//import cr.ac.una.flowfx.ws.ProjectTrackingDTO as WSProjectTrackingDTO;
import jakarta.xml.ws.BindingProvider;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ProjectTrackingService {
//
//    private static final Logger LOG = Logger.getLogger(ProjectTrackingService.class.getName());
//    private FlowFXWS port;
//
//    public ProjectTrackingService() {
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
//    public Respuesta find(Long id) {
//        try {
//            if (id == null) return new Respuesta(false, "El parámetro 'id' es requerido.", "tracking.find.id.null");
//
//            cr.ac.una.flowfx.ws.Respuesta wsResp = port.getProjectTracking(id);
//            Respuesta r = mapRespuesta(wsResp);
//
//            if (Boolean.TRUE.equals(r.getEstado()) && wsResp.getMensajeInterno() != null) {
//                ProjectTrackingDTO dto = parseTracking(wsResp.getMensajeInterno());
//                r.setResultado("ProjectTracking", dto);
//            }
//
//            return r;
//
//        } catch (Exception ex) {
//            LOG.log(Level.SEVERE, "Error obteniendo seguimiento [" + id + "]", ex);
//            return new Respuesta(false, "Error obteniendo seguimiento.", "tracking.find " + ex.getMessage());
//        }
//    }
//
//    public Respuesta create(ProjectTrackingDTO tracking) {
//        try {
//            if (tracking == null) return new Respuesta(false, "El parámetro 'tracking' es requerido.", "tracking.create.null");
//
//            cr.ac.una.flowfx.ws.Respuesta wsResp = port.createProjectTracking(toWS(tracking));
//            Respuesta r = mapRespuesta(wsResp);
//
//            if (Boolean.TRUE.equals(r.getEstado()) && wsResp.getMensajeInterno() != null) {
//                ProjectTrackingDTO dto = parseTracking(wsResp.getMensajeInterno());
//                r.setResultado("ProjectTracking", dto);
//            }
//
//            return r;
//
//        } catch (Exception ex) {
//            LOG.log(Level.SEVERE, "Error creando seguimiento", ex);
//            return new Respuesta(false, "Error creando seguimiento.", "tracking.create " + ex.getMessage());
//        }
//    }
//
//    public Respuesta update(ProjectTrackingDTO tracking) {
//        try {
//            if (tracking == null || tracking.getId() == null)
//                return new Respuesta(false, "El seguimiento y su 'id' son requeridos.", "tracking.update.null");
//
//            cr.ac.una.flowfx.ws.Respuesta wsResp = port.updateProjectTracking(toWS(tracking));
//            Respuesta r = mapRespuesta(wsResp);
//
//            if (Boolean.TRUE.equals(r.getEstado()) && wsResp.getMensajeInterno() != null) {
//                ProjectTrackingDTO dto = parseTracking(wsResp.getMensajeInterno());
//                r.setResultado("ProjectTracking", dto);
//            }
//
//            return r;
//
//        } catch (Exception ex) {
//            LOG.log(Level.SEVERE, "Error actualizando seguimiento [" + (tracking != null ? tracking.getId() : null) + "]", ex);
//            return new Respuesta(false, "Error actualizando seguimiento.", "tracking.update " + ex.getMessage());
//        }
//    }
//
//    public Respuesta delete(Long id) {
//        try {
//            if (id == null) return new Respuesta(false, "El parámetro 'id' es requerido.", "tracking.delete.id.null");
//
//            cr.ac.una.flowfx.ws.Respuesta wsResp = port.deleteProjectTracking(id);
//            return mapRespuesta(wsResp);
//
//        } catch (Exception ex) {
//            LOG.log(Level.SEVERE, "Error eliminando seguimiento [" + id + "]", ex);
//            return new Respuesta(false, "Error eliminando seguimiento.", "tracking.delete " + ex.getMessage());
//        }
//    }
//
//    public Respuesta findByProject(Long projectId) {
//        try {
//            if (projectId == null) return new Respuesta(false, "El parámetro 'projectId' es requerido.", "tracking.findByProject.projectId.null");
//
//            cr.ac.una.flowfx.ws.Respuesta wsResp = port.getProjectTrackingsByProject(projectId);
//            Respuesta r = mapRespuesta(wsResp);
//
//            if (Boolean.TRUE.equals(r.getEstado()) && wsResp.getMensajeInterno() != null) {
//                WSProjectTrackingDTO[] arr = port.parseProjectTrackingArray(wsResp.getMensajeInterno());
//                List<ProjectTrackingDTO> dtos = Arrays.stream(arr != null ? arr : new WSProjectTrackingDTO[0])
//                        .map(this::toLocal)
//                        .collect(Collectors.toList());
//                r.setResultado("ProjectTrackings", dtos);
//            }
//
//            return r;
//
//        } catch (Exception ex) {
//            LOG.log(Level.SEVERE, "Error obteniendo seguimientos del proyecto [" + projectId + "]", ex);
//            return new Respuesta(false, "Error obteniendo seguimientos del proyecto.", "tracking.findByProject " + ex.getMessage());
//        }
//    }
//
//    // ----------------- Métodos auxiliares -----------------
//
//    private Respuesta mapRespuesta(cr.ac.una.flowfx.ws.Respuesta ws) {
//        if (ws == null) return new Respuesta(false, "Respuesta nula del WS", "ws.response.null");
//        return new Respuesta(ws.isEstado(), ws.getMensaje(), ws.getMensajeInterno());
//    }
//
//    private ProjectTrackingDTO parseTracking(String json) {
//        try (jakarta.json.JsonReader jr = jakarta.json.Json.createReader(new java.io.StringReader(json))) {
//            jakarta.json.JsonObject obj = jr.readObject();
//            ProjectTrackingDTO dto = new ProjectTrackingDTO();
//            dto.setId(obj.containsKey("id") ? obj.getJsonNumber("id").longValue() : null);
//            dto.setProjectId(obj.containsKey("projectId") ? obj.getJsonNumber("projectId").longValue() : null);
//            dto.setDescription(obj.containsKey("description") ? obj.getString("description") : null);
//            dto.setDate(obj.containsKey("date") ? obj.getString("date") : null);
//            dto.setStatus(obj.containsKey("status") ? obj.getString("status") : null);
//            return dto;
//        } catch (Exception e) {
//            LOG.log(Level.WARNING, "No se pudo parsear mensajeInterno a ProjectTrackingDTO", e);
//            return null;
//        }
//    }
//
//    private WSProjectTrackingDTO toWS(ProjectTrackingDTO dto) {
//        WSProjectTrackingDTO w = new WSProjectTrackingDTO();
//        w.setId(dto.getId());
//        w.setProjectId(dto.getProjectId());
//        w.setDescription(dto.getDescription());
//        w.setDate(dto.getDate());
//        w.setStatus(dto.getStatus());
//        return w;
//    }
//
//    private ProjectTrackingDTO toLocal(WSProjectTrackingDTO ws) {
//        ProjectTrackingDTO dto = new ProjectTrackingDTO();
//        dto.setId(ws.getId());
//        dto.setProjectId(ws.getProjectId());
//        dto.setDescription(ws.getDescription());
//        dto.setDate(ws.getDate());
//        dto.setStatus(ws.getStatus());
//        return dto;
//    }
}
