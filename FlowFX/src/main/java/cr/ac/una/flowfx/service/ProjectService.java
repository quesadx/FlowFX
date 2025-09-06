package cr.ac.una.flowfx.service;

import cr.ac.una.flowfx.model.ProjectDTO;
import cr.ac.una.flowfx.util.Respuesta;
import cr.ac.una.flowfx.ws.FlowFXWS;
import cr.ac.una.flowfx.ws.FlowFXWS_Service;
import cr.ac.una.flowfx.ws.PersonDTO; 
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonString;
import jakarta.xml.ws.BindingProvider;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProjectService {

    private static final Logger LOG = Logger.getLogger(ProjectService.class.getName());
    private static final String ENTITY_KEY = "Project";
    private static final String LIST_KEY = "Projects";

    private FlowFXWS port;

    public ProjectService() {
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

    // ======= METHODS CRUD/QUERY =======

    public Respuesta find(Long id) {
        try {
            if (id == null)
                return new Respuesta(false, "El parámetro 'id' es requerido.", "find.id.null");

            cr.ac.una.flowfx.ws.Respuesta wsResp = port.getProject(id);
            Respuesta r = mapRespuesta(wsResp);

            if (Boolean.TRUE.equals(r.getEstado())) {
                fillSingleFromMensajeInterno(r);
            }
            return r;
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo proyecto [" + id + "]", ex);
            return new Respuesta(false, "Error obteniendo proyecto.", "find " + ex.getMessage());
        }
    }

    public Respuesta findProjectsForUser(Long userId) {
        try {
            if (userId == null)
                return new Respuesta(false, "El parámetro 'userId' es requerido.", "findProjectsForUser.userId.null");

            cr.ac.una.flowfx.ws.Respuesta wsResp = port.getProjectsForUser(userId);
            Respuesta r = mapRespuesta(wsResp);

            if (Boolean.TRUE.equals(r.getEstado())) {
                fillListFromMensajeInterno(r);
            }
            return r;
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo proyectos del usuario [" + userId + "]", ex);
            return new Respuesta(false, "Error obteniendo proyectos del usuario.", "findProjectsForUser " + ex.getMessage());
        }
    }

    public Respuesta findAll() {
        try {
            cr.ac.una.flowfx.ws.Respuesta wsResp = port.getAllProjects();
            Respuesta r = mapRespuesta(wsResp);
            if (Boolean.TRUE.equals(r.getEstado())) {
                fillListFromMensajeInterno(r);
            }
            return r;
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo los proyectos", ex);
            return new Respuesta(false, "Error obteniendo los proyectos", "findAll " + ex.getMessage());
        }
    }
    public Respuesta create(ProjectDTO project, Long leaderId, Long techLeaderId, Long sponsorId) {
        try {
            if (project == null)
                return new Respuesta(false, "El parámetro 'project' es requerido.", "create.project.null");

            LOG.log(Level.FINE, "Creando proyecto. plannedStart={0}, leaderId={1}, techLeaderId={2}, sponsorId={3}",
                    new Object[]{project.getPlannedStartDate(), leaderId, techLeaderId, sponsorId});

            // Mapear al DTO del WS incluyendo fechas e IDs de responsables
            cr.ac.una.flowfx.ws.ProjectDTO wsProject = toWs(project, leaderId, techLeaderId, sponsorId);

            cr.ac.una.flowfx.ws.Respuesta wsResp = port.createProject(wsProject);
            Respuesta r = mapRespuesta(wsResp);

            if (Boolean.TRUE.equals(r.getEstado())) {
                fillSingleFromMensajeInterno(r);
            }
            return r;
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error creando proyecto", ex);
            return new Respuesta(false, "Error creando proyecto.", "create " + ex.getMessage());
        }
    }

    public Respuesta update(ProjectDTO project) {
        try {
            if (project == null || project.getId() == null)
                return new Respuesta(false, "El proyecto y su 'id' son requeridos.", "update.project.null");

            cr.ac.una.flowfx.ws.Respuesta wsResp = port.updateProject(
                    toWs(project, project.getLeaderUserId(), project.getTechLeaderId(), project.getSponsorId())
            );
            Respuesta r = mapRespuesta(wsResp);

            if (Boolean.TRUE.equals(r.getEstado())) {
                fillSingleFromMensajeInterno(r);
            }
            return r;
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error actualizando proyecto [" + (project != null ? project.getId() : null) + "]", ex);
            return new Respuesta(false, "Error actualizando proyecto.", "update " + ex.getMessage());
        }
    }

    public Respuesta delete(Long id) {
        try {
            if (id == null)
                return new Respuesta(false, "El parámetro 'id' es requerido.", "delete.id.null");

            cr.ac.una.flowfx.ws.Respuesta wsResp = port.deleteProject(id);
            return mapRespuesta(wsResp);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error eliminando proyecto [" + id + "]", ex);
            return new Respuesta(false, "Error eliminando proyecto.", "delete " + ex.getMessage());
        }
    }

    // ======== UTILS ========

    private Respuesta mapRespuesta(cr.ac.una.flowfx.ws.Respuesta ws) {
        if (ws == null) return new Respuesta(false, "Respuesta nula del WS", "ws.response.null");
        return new Respuesta(ws.isEstado(), ws.getMensaje(), ws.getMensajeInterno());
    }

    private void fillSingleFromMensajeInterno(Respuesta r) {
        String mi = r.getMensajeInterno();
        if (mi == null || mi.isBlank()) return;

        if (!looksLikeJson(mi)) {
            LOG.log(Level.FINE, "mensajeInterno no es JSON. Valor: {0}", mi);
            return;
        }

        try (JsonReader jr = Json.createReader(new StringReader(mi))) {
            JsonObject obj = jr.readObject();
            if (obj.containsKey("project")) obj = obj.getJsonObject("project");
            else if (obj.containsKey("Project")) obj = obj.getJsonObject("Project");
            else if (obj.containsKey("PROJECT")) obj = obj.getJsonObject("PROJECT");

            ProjectDTO dto = fromJsonProject(obj);
            if (dto != null) r.setResultado(ENTITY_KEY, dto);
        } catch (Exception e) {
            LOG.log(Level.WARNING, "No se pudo parsear mensajeInterno a ProjectDTO", e);
        }
    }

    private void fillListFromMensajeInterno(Respuesta r) {
        String mi = r.getMensajeInterno();
        if (mi == null || mi.isBlank()) return;

        if (!looksLikeJson(mi)) {
            LOG.log(Level.FINE, "mensajeInterno no es JSON (lista). Valor: {0}", mi);
            return;
        }

        List<ProjectDTO> projects = new ArrayList<>();
        try (JsonReader jr = Json.createReader(new StringReader(mi))) {
            JsonArray arr = jr.readArray();
            for (int i = 0; i < arr.size(); i++) {
                JsonObject obj = arr.getJsonObject(i);
                ProjectDTO dto = fromJsonProject(obj);
                if (dto != null) projects.add(dto);
            }
            r.setResultado(LIST_KEY, projects);
            return;
        } catch (Exception ignore) {}

        try (JsonReader jr = Json.createReader(new StringReader(mi))) {
            JsonObject root = jr.readObject();
            JsonArray arr = null;
            if (root.containsKey("projects")) arr = root.getJsonArray("projects");
            else if (root.containsKey("Projects")) arr = root.getJsonArray("Projects");
            else if (root.containsKey("PROJECTS")) arr = root.getJsonArray("PROJECTS");
            else if (root.containsKey("data") && root.get("data") instanceof JsonArray) arr = root.getJsonArray("data");
            else if (root.containsKey("list") && root.get("list") instanceof JsonArray) arr = root.getJsonArray("list");

            if (arr != null) {
                for (int i = 0; i < arr.size(); i++) {
                    JsonObject obj = arr.getJsonObject(i);
                    ProjectDTO dto = fromJsonProject(obj);
                    if (dto != null) projects.add(dto);
                }
            }
        } catch (Exception e) {
            LOG.log(Level.WARNING, "No se pudo parsear mensajeInterno a List<ProjectDTO>", e);
        }

        r.setResultado(LIST_KEY, projects);
    }

    private boolean looksLikeJson(String s) {
        char c = s.trim().isEmpty() ? 0 : s.trim().charAt(0);
        return c == '{' || c == '[';
    }

    private ProjectDTO fromJsonProject(JsonObject obj) {
        if (obj == null) return null;

        ProjectDTO dto = new ProjectDTO();
        dto.setId(getLong(obj, "id", "project_id", "projectId"));
        dto.setName(getString(obj, "name", "project_name", "projectName"));

        // Dates as epoch millis
        Long psd = getLong(obj, "plannedStartDate", "planned_start_date", "plannedStart");
        Long ped = getLong(obj, "plannedEndDate", "planned_end_date", "plannedEnd");
        Long asd = getLong(obj, "actualStartDate", "actual_start_date", "actualStart");
        Long aed = getLong(obj, "actualEndDate", "actual_end_date", "actualEnd");
        if (psd != null) dto.setPlannedStartDate(new java.util.Date(psd));
        if (ped != null) dto.setPlannedEndDate(new java.util.Date(ped));
        if (asd != null) dto.setActualStartDate(new java.util.Date(asd));
        if (aed != null) dto.setActualEndDate(new java.util.Date(aed));

        // Status token
        String st = getString(obj, "status", "statusCode");
        if (st != null && !st.isBlank()) dto.setStatus(st.substring(0, 1).toUpperCase());

        // Relations
        dto.setLeaderUserId(getLong(obj, "leaderUserId", "leaderId", "leader_user_id"));
        dto.setTechLeaderId(getLong(obj, "techLeaderId", "technicalLeaderId", "tech_leader_id"));
        dto.setSponsorId(getLong(obj, "sponsorId", "sponsor_id"));

        // Timestamps
        Long createdAt = getLong(obj, "createdAt", "created_at");
        Long updatedAt = getLong(obj, "updatedAt", "updated_at");
        if (createdAt != null) dto.setCreatedAt(new java.util.Date(createdAt));
        if (updatedAt != null) dto.setUpdatedAt(new java.util.Date(updatedAt));
        return dto;
    }

    private cr.ac.una.flowfx.ws.ProjectDTO toWs(ProjectDTO dto, Long leaderId, Long techLeaderId, Long sponsorId) {
        cr.ac.una.flowfx.ws.ProjectDTO w = new cr.ac.una.flowfx.ws.ProjectDTO();
        w.setId(dto.getId());
        w.setName(dto.getName());

        if (dto.getPlannedStartDate() != null) {
            w.setPlannedStartDate(toXmlDate(dto.getPlannedStartDate()));
        }
        if (dto.getPlannedEndDate() != null) {
            w.setPlannedEndDate(toXmlDate(dto.getPlannedEndDate()));
        }
        setPersonRelation(w,
                leaderId,
                new String[]{"setLeaderUserId", "setLeaderId", "setLeaderPersonId"},
                new String[]{"setLeader", "setLeaderUser", "setLeaderPerson"});

        setPersonRelation(w,
                techLeaderId,
                new String[]{"setTechLeaderId", "setTechnicalLeaderId"},
                new String[]{"setTechLeader", "setTechnicalLeader"});

        setPersonRelation(w,
                sponsorId,
                new String[]{"setSponsorId"},
                new String[]{"setSponsor", "setProjectSponsor"});

        return w;
    }

    private void setPersonRelation(Object target,
                                   Long personId,
                                   String[] idSetterCandidates,
                                   String[] dtoSetterCandidates) {
        if (personId == null) return;

        for (String method : idSetterCandidates) {
            if (invokeSetterIfExists(target, method, Long.class, personId)) {
                LOG.log(Level.FINE, "Asignado por Id con setter {0} = {1}", new Object[]{method, personId});
                return;
            }
        }

        PersonDTO p = new PersonDTO();
        p.setId(personId);
        for (String method : dtoSetterCandidates) {
            if (invokeSetterIfExists(target, method, PersonDTO.class, p)) {
                LOG.log(Level.FINE, "Asignado por DTO con setter {0} -> PersonDTO.id={1}", new Object[]{method, personId});
                return;
            }
        }

        LOG.log(Level.FINE, "No se encontró setter para asignar relación persona con id={0}", personId);
    }

    // ======== Helpers JSON ========

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
                        if (s != null && !s.isBlank()) return Long.parseLong(s.replaceAll("\"", ""));
                    }
                } catch (Exception ignore) {}
            }
        }
        return null;
    }
    private XMLGregorianCalendar toXmlDate(java.util.Date date) {
        if (Objects.isNull(date)) return null;
        try {
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(date);
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
        } catch (DatatypeConfigurationException e) {
            LOG.log(Level.SEVERE, "Error convirtiendo Date a XMLGregorianCalendar", e);
            return null;
        }
    }
    private boolean invokeSetterIfExists(Object target, String method, Class<?> paramType, Object value) {
        try {
            Method m = target.getClass().getMethod(method, paramType);
            m.invoke(target, value);
            return true;
        } catch (NoSuchMethodException nsme) {
            return false;
        } catch (Exception ex) {
            LOG.log(Level.FINE, "No se pudo invocar " + method + " en stub: " + ex.getMessage(), ex);
            return false;
        }
    }
}