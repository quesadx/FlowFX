package cr.ac.una.flowfx.service;

import cr.ac.una.flowfx.model.ProjectDTO;
import cr.ac.una.flowfx.util.Respuesta;
import cr.ac.una.flowfx.ws.FlowFXWS;
import cr.ac.una.flowfx.ws.FlowFXWS_Service;
//import cr.ac.una.flowfx.ws.ProjectDTO as WSProjectDTO;
import jakarta.xml.ws.BindingProvider;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ProjectService {
//
//    private static final Logger LOG = Logger.getLogger(ProjectService.class.getName());
//    private FlowFXWS port;
//
//    public ProjectService() {
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
//            if (id == null) return new Respuesta(false, "El parámetro 'id' es requerido.", "find.id.null");
//
//            cr.ac.una.flowfx.ws.Respuesta wsResp = port.getProject(id);
//            Respuesta r = mapRespuesta(wsResp);
//
//            if (Boolean.TRUE.equals(r.getEstado()) && wsResp.getMensajeInterno() != null) {
//                ProjectDTO dto = parseProject(wsResp.getMensajeInterno());
//                r.setResultado("Project", dto);
//            }
//
//            return r;
//
//        } catch (Exception ex) {
//            LOG.log(Level.SEVERE, "Error obteniendo proyecto [" + id + "]", ex);
//            return new Respuesta(false, "Error obteniendo proyecto.", "find " + ex.getMessage());
//        }
//    }
//
    public Respuesta findProjectsForUser(Long userId) {
//        try {
//            if (userId == null) return new Respuesta(false, "El parámetro 'userId' es requerido.", "findProjectsForUser.userId.null");
//
//            cr.ac.una.flowfx.ws.Respuesta wsResp = port.getProjectsForUser(userId);
//            Respuesta r = mapRespuesta(wsResp);
//
//            if (Boolean.TRUE.equals(r.getEstado()) && wsResp.getMensajeInterno() != null) {
//                WSProjectDTO[] arr = port.parseProjectArray(wsResp.getMensajeInterno());
//                List<ProjectDTO> proyectos = Arrays.stream(arr != null ? arr : new WSProjectDTO[0])
//                        .map(this::toLocal)
//                        .collect(Collectors.toList());
//                r.setResultado("Projects", proyectos);
//            }
//
//            return r;
//
//        } catch (Exception ex) {
//            LOG.log(Level.SEVERE, "Error obteniendo proyectos del usuario [" + userId + "]", ex);
//            return new Respuesta(false, "Error obteniendo proyectos del usuario.", "findProjectsForUser " + ex.getMessage());
//        }
return null;
    }
//
    public Respuesta create(ProjectDTO project, Long leaderId, Long techLeaderId, Long sponsorId) {
//        try {
//            if (project == null) return new Respuesta(false, "El parámetro 'project' es requerido.", "create.project.null");
//            if (leaderId == null || techLeaderId == null || sponsorId == null)
//                return new Respuesta(false, "Los parámetros 'leaderId', 'techLeaderId' y 'sponsorId' son requeridos.", "create.params.null");
//
//            cr.ac.una.flowfx.ws.Respuesta wsResp = port.createProject(toWS(project), leaderId, techLeaderId, sponsorId);
//            Respuesta r = mapRespuesta(wsResp);
//
//            if (Boolean.TRUE.equals(r.getEstado()) && wsResp.getMensajeInterno() != null) {
//                ProjectDTO dto = parseProject(wsResp.getMensajeInterno());
//                r.setResultado("Project", dto);
//            }
//
//            return r;
//
//        } catch (Exception ex) {
//            LOG.log(Level.SEVERE, "Error creando proyecto", ex);
//            return new Respuesta(false, "Error creando proyecto.", "create " + ex.getMessage());
//        }
return null;
    }
//
//    public Respuesta update(ProjectDTO project) {
//        try {
//            if (project == null || project.getId() == null)
//                return new Respuesta(false, "El proyecto y su 'id' son requeridos.", "update.project.null");
//
//            cr.ac.una.flowfx.ws.Respuesta wsResp = port.updateProject(toWS(project));
//            Respuesta r = mapRespuesta(wsResp);
//
//            if (Boolean.TRUE.equals(r.getEstado()) && wsResp.getMensajeInterno() != null) {
//                ProjectDTO dto = parseProject(wsResp.getMensajeInterno());
//                r.setResultado("Project", dto);
//            }
//
//            return r;
//
//        } catch (Exception ex) {
//            LOG.log(Level.SEVERE, "Error actualizando proyecto [" + (project != null ? project.getId() : null) + "]", ex);
//            return new Respuesta(false, "Error actualizando proyecto.", "update " + ex.getMessage());
//        }
//    }
//
//    public Respuesta delete(Long id) {
//        try {
//            if (id == null) return new Respuesta(false, "El parámetro 'id' es requerido.", "delete.id.null");
//
//            cr.ac.una.flowfx.ws.Respuesta wsResp = port.deleteProject(id);
//            return mapRespuesta(wsResp);
//
//        } catch (Exception ex) {
//            LOG.log(Level.SEVERE, "Error eliminando proyecto [" + id + "]", ex);
//            return new Respuesta(false, "Error eliminando proyecto.", "delete " + ex.getMessage());
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
//    private ProjectDTO parseProject(String json) {
//        try (jakarta.json.JsonReader jr = jakarta.json.Json.createReader(new java.io.StringReader(json))) {
//            jakarta.json.JsonObject obj = jr.readObject();
//            ProjectDTO dto = new ProjectDTO();
//            dto.setId(obj.containsKey("id") ? obj.getJsonNumber("id").longValue() : null);
//            dto.setName(obj.containsKey("name") ? obj.getString("name") : null);
//            dto.setDescription(obj.containsKey("description") ? obj.getString("description") : null);
//            dto.setStartDate(obj.containsKey("startDate") ? obj.getString("startDate") : null);
//            dto.setEndDate(obj.containsKey("endDate") ? obj.getString("endDate") : null);
//            return dto;
//        } catch (Exception e) {
//            LOG.log(Level.WARNING, "No se pudo parsear mensajeInterno a ProjectDTO", e);
//            return null;
//        }
//    }
//
//    private WSProjectDTO toWS(ProjectDTO dto) {
//        WSProjectDTO w = new WSProjectDTO();
//        w.setId(dto.getId());
//        w.setName(dto.getName());
//        w.setDescription(dto.getDescription());
//        w.setStartDate(dto.getStartDate());
//        w.setEndDate(dto.getEndDate());
//        return w;
//    }
//
//    private ProjectDTO toLocal(WSProjectDTO ws) {
//        ProjectDTO dto = new ProjectDTO();
//        dto.setId(ws.getId());
//        dto.setName(ws.getName());
//        dto.setDescription(ws.getDescription());
//        dto.setStartDate(ws.getStartDate());
//        dto.setEndDate(ws.getEndDate());
//        return dto;
//    }
}
