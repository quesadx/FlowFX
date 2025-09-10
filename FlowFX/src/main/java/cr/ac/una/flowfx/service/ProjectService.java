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
import jakarta.json.JsonValue;
import jakarta.xml.ws.BindingProvider;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Client helper for Project-related operations against the FlowFX web service.
 *
 * <p>This class prepares the web service port and exposes CRUD/query helpers.
 * It also contains robust JSON parsing utilities to convert the web service
 * {@code mensajeInterno} payload into {@link ProjectDTO} instances (single or list).</p>
 *
 * <p>Public methods perform parameter validation and return a {@link Respuesta}.
 * The client preserves the original behavior: it invokes the generated stub
 * operations on the {@link FlowFXWS} port and maps responses into application
 * {@link Respuesta} instances. JSON parsing is tolerant to common payload shapes
 * and field aliases.</p>
 */
public class ProjectService {

    private static final Logger LOG = Logger.getLogger(
        ProjectService.class.getName()
    );
    private static final String ENTITY_KEY = "Project";
    private static final String LIST_KEY = "Projects";
    private static final String DEFAULT_ENDPOINT =
        "http://localhost:8080/FlowFXWS/FlowFXWS";

    private FlowFXWS port;

    /**
     * Constructs the service client and configures the web service endpoint to
     * the default development address. Adjust the endpoint here if necessary.
     */
    public ProjectService() {
        try {
            FlowFXWS_Service service = new FlowFXWS_Service();
            port = service.getFlowFXWSPort();

            if (port instanceof BindingProvider) {
                ((BindingProvider) port).getRequestContext().put(
                    BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                    DEFAULT_ENDPOINT
                );
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error initializing FlowFXWS port", e);
        }
    }

    // ======= CRUD / QUERY METHODS =======

    /**
     * Retrieves a project by id.
     *
     * @param id project identifier (required)
     * @return Respuesta with the result; when successful, the {@code Project} key
     *         in the result contains a {@link ProjectDTO}.
     */
    public Respuesta find(Long id) {
        try {
            if (id == null) {
                return new Respuesta(
                    false,
                    "The parameter 'id' is required.",
                    "find.id.null"
                );
            }
            if (port == null) {
                LOG.log(
                    Level.WARNING,
                    "Web service port is not available for Project.find"
                );
                return new Respuesta(
                    false,
                    "Web service port is not available.",
                    "ws.port.null"
                );
            }

            cr.ac.una.flowfx.ws.Respuesta wsResp = port.getProject(id);
            Respuesta r = mapRespuesta(wsResp);

            if (Boolean.TRUE.equals(r.getEstado())) {
                fillSingleFromMensajeInterno(r);
            }
            return r;
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error fetching project [" + id + "]", ex);
            return new Respuesta(
                false,
                "Error fetching project.",
                "find " + ex.getMessage()
            );
        }
    }

    /**
     * Retrieves projects associated with a user.
     *
     * @param userId user identifier (required)
     * @return Respuesta with the result; when successful, the {@code Projects} key
     *         in the result contains a {@link List} of {@link ProjectDTO}.
     */
    public Respuesta findProjectsForUser(Long userId) {
        try {
            if (userId == null) {
                return new Respuesta(
                    false,
                    "The parameter 'userId' is required.",
                    "findProjectsForUser.userId.null"
                );
            }
            if (port == null) {
                LOG.log(
                    Level.WARNING,
                    "Web service port is not available for Project.findProjectsForUser"
                );
                return new Respuesta(
                    false,
                    "Web service port is not available.",
                    "ws.port.null"
                );
            }

            cr.ac.una.flowfx.ws.Respuesta wsResp = port.getProjectsForUser(
                userId
            );
            Respuesta r = mapRespuesta(wsResp);

            if (Boolean.TRUE.equals(r.getEstado())) {
                fillListFromMensajeInterno(r);
            }
            return r;
        } catch (Exception ex) {
            LOG.log(
                Level.SEVERE,
                "Error fetching projects for user [" + userId + "]",
                ex
            );
            return new Respuesta(
                false,
                "Error fetching projects for user.",
                "findProjectsForUser " + ex.getMessage()
            );
        }
    }

    /**
     * Retrieves all projects.
     *
     * @return Respuesta with the result; when successful, the {@code Projects} key
     *         in the result contains a {@link List} of {@link ProjectDTO}.
     */
    public Respuesta findAll() {
        try {
            if (port == null) {
                LOG.log(
                    Level.WARNING,
                    "Web service port is not available for Project.findAll"
                );
                return new Respuesta(
                    false,
                    "Web service port is not available.",
                    "ws.port.null"
                );
            }

            cr.ac.una.flowfx.ws.Respuesta wsResp = port.getAllProjects();
            Respuesta r = mapRespuesta(wsResp);

            if (Boolean.TRUE.equals(r.getEstado())) {
                fillListFromMensajeInterno(r);
            }
            return r;
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error fetching all projects", ex);
            return new Respuesta(
                false,
                "Error fetching projects.",
                "findAll " + ex.getMessage()
            );
        }
    }

    /**
     * Creates a project using the provided DTO and optional related person ids.
     *
     * @param project       project data (required)
     * @param leaderId      optional leader person id
     * @param techLeaderId  optional technical leader person id
     * @param sponsorId     optional sponsor person id
     * @return Respuesta with the result; when successful, the {@code Project} key
     *         contains the created {@link ProjectDTO}.
     */
    public Respuesta create(
        ProjectDTO project,
        Long leaderId,
        Long techLeaderId,
        Long sponsorId
    ) {
        try {
            if (project == null) {
                return new Respuesta(
                    false,
                    "The parameter 'project' is required.",
                    "create.project.null"
                );
            }
            if (port == null) {
                LOG.log(
                    Level.WARNING,
                    "Web service port is not available for Project.create"
                );
                return new Respuesta(
                    false,
                    "Web service port is not available.",
                    "ws.port.null"
                );
            }

            LOG.log(
                Level.FINE,
                "Creating project. plannedStart={0}, leaderId={1}, techLeaderId={2}, sponsorId={3}",
                new Object[] {
                    project.getPlannedStartDate(),
                    leaderId,
                    techLeaderId,
                    sponsorId,
                }
            );

            cr.ac.una.flowfx.ws.ProjectDTO wsProject = toWs(
                project,
                leaderId,
                techLeaderId,
                sponsorId
            );
            cr.ac.una.flowfx.ws.Respuesta wsResp = port.createProject(
                wsProject
            );
            Respuesta r = mapRespuesta(wsResp);

            if (Boolean.TRUE.equals(r.getEstado())) {
                fillSingleFromMensajeInterno(r);
            }
            return r;
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error creating project", ex);
            return new Respuesta(
                false,
                "Error creating project.",
                "create " + ex.getMessage()
            );
        }
    }

    /**
     * Updates an existing project.
     *
     * @param project project data (required, must contain id)
     * @return Respuesta with the result; when successful, the {@code Project} key
     *         contains the updated {@link ProjectDTO}.
     */
    public Respuesta update(ProjectDTO project) {
        try {
            if (project == null || project.getId() == null) {
                return new Respuesta(
                    false,
                    "The project and its 'id' are required.",
                    "update.project.null"
                );
            }
            if (port == null) {
                LOG.log(
                    Level.WARNING,
                    "Web service port is not available for Project.update"
                );
                return new Respuesta(
                    false,
                    "Web service port is not available.",
                    "ws.port.null"
                );
            }

            cr.ac.una.flowfx.ws.ProjectDTO wsProject = toWs(
                project,
                project.getLeaderUserId(),
                project.getTechLeaderId(),
                project.getSponsorId()
            );

            cr.ac.una.flowfx.ws.Respuesta wsResp = port.updateProject(
                wsProject
            );
            Respuesta r = mapRespuesta(wsResp);

            if (Boolean.TRUE.equals(r.getEstado())) {
                fillSingleFromMensajeInterno(r);
            }
            return r;
        } catch (Exception ex) {
            LOG.log(
                Level.SEVERE,
                "Error updating project [" +
                (project != null ? project.getId() : null) +
                "]",
                ex
            );
            return new Respuesta(
                false,
                "Error updating project.",
                "update " + ex.getMessage()
            );
        }
    }

    /**
     * Deletes a project by id.
     *
     * @param id project identifier (required)
     * @return Respuesta describing the outcome of the delete operation.
     */
    public Respuesta delete(Long id) {
        try {
            if (id == null) {
                return new Respuesta(
                    false,
                    "The parameter 'id' is required.",
                    "delete.id.null"
                );
            }
            if (port == null) {
                LOG.log(
                    Level.WARNING,
                    "Web service port is not available for Project.delete"
                );
                return new Respuesta(
                    false,
                    "Web service port is not available.",
                    "ws.port.null"
                );
            }

            cr.ac.una.flowfx.ws.Respuesta wsResp = port.deleteProject(id);
            return mapRespuesta(wsResp);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error deleting project [" + id + "]", ex);
            return new Respuesta(
                false,
                "Error deleting project.",
                "delete " + ex.getMessage()
            );
        }
    }

    // ======== Helpers ========

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

    private void fillSingleFromMensajeInterno(Respuesta r) {
        String mi = r.getMensajeInterno();
        if (mi == null || mi.isBlank()) return;

        if (!looksLikeJson(mi)) {
            LOG.log(
                Level.FINE,
                "mensajeInterno does not appear to be JSON: {0}",
                mi
            );
            return;
        }

        try (JsonReader jr = Json.createReader(new StringReader(mi))) {
            JsonValue v = jr.readValue();
            switch (v.getValueType()) {
                case OBJECT: {
                    JsonObject obj = (JsonObject) v;
                    obj = unwrapObject(obj, "project", "Project", "PROJECT");
                    ProjectDTO dto = fromJsonProject(obj);
                    if (dto != null) r.setResultado(ENTITY_KEY, dto);
                    break;
                }
                case ARRAY: {
                    JsonArray arr = (JsonArray) v;
                    if (
                        !arr.isEmpty() &&
                        arr.get(0).getValueType() == JsonValue.ValueType.OBJECT
                    ) {
                        ProjectDTO dto = fromJsonProject(arr.getJsonObject(0));
                        if (dto != null) r.setResultado(ENTITY_KEY, dto);
                    }
                    break;
                }
                default:
                    break;
            }
        } catch (Exception ex) {
            LOG.log(
                Level.FINE,
                "Unable to parse mensajeInterno to single ProjectDTO, attempting fallback",
                ex
            );
            // fallback: try strict object
            try (JsonReader jr = Json.createReader(new StringReader(mi))) {
                JsonObject obj = jr.readObject();
                obj = unwrapObject(obj, "project", "Project", "PROJECT");
                ProjectDTO dto = fromJsonProject(obj);
                if (dto != null) r.setResultado(ENTITY_KEY, dto);
            } catch (Exception e) {
                LOG.log(
                    Level.FINE,
                    "Fallback parsing failed for mensajeInterno",
                    e
                );
            }
        }
    }

    private void fillListFromMensajeInterno(Respuesta r) {
        String mi = r.getMensajeInterno();
        if (mi == null || mi.isBlank()) return;

        if (!looksLikeJson(mi)) {
            LOG.log(
                Level.FINE,
                "mensajeInterno does not appear to be JSON (list): {0}",
                mi
            );
            return;
        }

        List<ProjectDTO> projects = new ArrayList<>();

        // 1) Try direct array
        try (JsonReader jr = Json.createReader(new StringReader(mi))) {
            JsonArray arr = jr.readArray();
            for (int i = 0; i < arr.size(); i++) {
                if (arr.get(i).getValueType() == JsonValue.ValueType.OBJECT) {
                    ProjectDTO dto = fromJsonProject(arr.getJsonObject(i));
                    if (dto != null) projects.add(dto);
                }
            }
            r.setResultado(LIST_KEY, projects);
            return;
        } catch (Exception ignore) {
            // proceed to other shapes
        }

        // 2) Try object wrapper with common keys
        try (JsonReader jr = Json.createReader(new StringReader(mi))) {
            JsonObject root = jr.readObject();
            JsonArray arr = unwrapArray(
                root,
                "projects",
                "Projects",
                "PROJECTS",
                "data",
                "list"
            );
            if (arr != null) {
                for (int i = 0; i < arr.size(); i++) {
                    if (
                        arr.get(i).getValueType() == JsonValue.ValueType.OBJECT
                    ) {
                        ProjectDTO dto = fromJsonProject(arr.getJsonObject(i));
                        if (dto != null) projects.add(dto);
                    }
                }
            }
        } catch (Exception ex) {
            LOG.log(
                Level.WARNING,
                "Unable to parse mensajeInterno to List<ProjectDTO>",
                ex
            );
        }

        r.setResultado(LIST_KEY, projects);
    }

    private boolean looksLikeJson(String s) {
        if (s == null) return false;
        String t = s.trim();
        if (t.isEmpty()) return false;
        char c = t.charAt(0);
        return c == '{' || c == '[';
    }

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

    private ProjectDTO fromJsonProject(JsonObject obj) {
        if (obj == null) return null;

        ProjectDTO dto = new ProjectDTO();

        dto.setId(getLong(obj, "id", "project_id", "projectId"));
        dto.setName(getString(obj, "name", "project_name", "projectName"));

        // Dates as epoch millis or string tokens
        Long psd = getLong(
            obj,
            "plannedStartDate",
            "planned_start_date",
            "plannedStart"
        );
        Long ped = getLong(
            obj,
            "plannedEndDate",
            "planned_end_date",
            "plannedEnd"
        );
        Long asd = getLong(
            obj,
            "actualStartDate",
            "actual_start_date",
            "actualStart"
        );
        Long aed = getLong(
            obj,
            "actualEndDate",
            "actual_end_date",
            "actualEnd"
        );
        if (psd != null) dto.setPlannedStartDate(new Date(psd));
        if (ped != null) dto.setPlannedEndDate(new Date(ped));
        if (asd != null) dto.setActualStartDate(new Date(asd));
        if (aed != null) dto.setActualEndDate(new Date(aed));

        // Status token - take first character uppercase
        String st = getString(obj, "status", "statusCode");
        if (st != null && !st.isBlank()) {
            dto.setStatus(st.substring(0, 1).toUpperCase());
        }

        // Relations
        dto.setLeaderUserId(
            getLong(obj, "leaderUserId", "leaderId", "leader_user_id")
        );
        dto.setTechLeaderId(
            getLong(obj, "techLeaderId", "technicalLeaderId", "tech_leader_id")
        );
        dto.setSponsorId(getLong(obj, "sponsorId", "sponsor_id"));

        // Timestamps
        Long createdAt = getLong(obj, "createdAt", "created_at");
        Long updatedAt = getLong(obj, "updatedAt", "updated_at");
        if (createdAt != null) dto.setCreatedAt(new Date(createdAt));
        if (updatedAt != null) dto.setUpdatedAt(new Date(updatedAt));

        return dto;
    }

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
                    // continue to next key
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
                            if (s != null && !s.isBlank()) {
                                try {
                                    return Long.parseLong(s.trim());
                                } catch (NumberFormatException nfe) {
                                    // try parse as double then long
                                    try {
                                        return (long) Double.parseDouble(
                                            s.trim()
                                        );
                                    } catch (Exception ignore) {}
                                }
                            }
                            break;
                        default:
                            String raw = v.toString();
                            if (raw != null && !raw.isBlank()) {
                                String cleaned = raw.replaceAll("[^0-9-]", "");
                                if (!cleaned.isBlank()) {
                                    try {
                                        return Long.parseLong(cleaned);
                                    } catch (Exception ignore) {}
                                }
                            }
                    }
                } catch (Exception ignore) {
                    // continue to next key
                }
            }
        }
        return null;
    }

    private cr.ac.una.flowfx.ws.ProjectDTO toWs(
        ProjectDTO dto,
        Long leaderId,
        Long techLeaderId,
        Long sponsorId
    ) {
        cr.ac.una.flowfx.ws.ProjectDTO w = new cr.ac.una.flowfx.ws.ProjectDTO();
        if (dto == null) return w;

        w.setId(dto.getId());
        w.setName(dto.getName());

        if (dto.getPlannedStartDate() != null) {
            w.setPlannedStartDate(toXmlDate(dto.getPlannedStartDate()));
        }
        if (dto.getPlannedEndDate() != null) {
            w.setPlannedEndDate(toXmlDate(dto.getPlannedEndDate()));
        }

        setPersonRelation(
            w,
            leaderId,
            new String[] {
                "setLeaderUserId",
                "setLeaderId",
                "setLeaderPersonId",
            },
            new String[] { "setLeader", "setLeaderUser", "setLeaderPerson" }
        );

        setPersonRelation(
            w,
            techLeaderId,
            new String[] { "setTechLeaderId", "setTechnicalLeaderId" },
            new String[] { "setTechLeader", "setTechnicalLeader" }
        );

        setPersonRelation(
            w,
            sponsorId,
            new String[] { "setSponsorId" },
            new String[] { "setSponsor", "setProjectSponsor" }
        );

        return w;
    }

    private void setPersonRelation(
        Object target,
        Long personId,
        String[] idSetterCandidates,
        String[] dtoSetterCandidates
    ) {
        if (target == null || personId == null) return;

        // Try id setters first
        for (String method : idSetterCandidates) {
            if (invokeSetterIfExists(target, method, Long.class, personId)) {
                LOG.log(
                    Level.FINE,
                    "Assigned by Id with setter {0} = {1}",
                    new Object[] { method, personId }
                );
                return;
            }
        }

        // Then try DTO setters
        PersonDTO p = new PersonDTO();
        p.setId(personId);
        for (String method : dtoSetterCandidates) {
            if (invokeSetterIfExists(target, method, PersonDTO.class, p)) {
                LOG.log(
                    Level.FINE,
                    "Assigned by DTO with setter {0} -> PersonDTO.id={1}",
                    new Object[] { method, personId }
                );
                return;
            }
        }

        LOG.log(
            Level.FINE,
            "No setter found to assign person relation with id={0}",
            personId
        );
    }

    private XMLGregorianCalendar toXmlDate(Date date) {
        if (Objects.isNull(date)) return null;
        try {
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(date);
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
        } catch (DatatypeConfigurationException e) {
            LOG.log(
                Level.SEVERE,
                "Error converting Date to XMLGregorianCalendar",
                e
            );
            return null;
        }
    }

    private boolean invokeSetterIfExists(
        Object target,
        String method,
        Class<?> paramType,
        Object value
    ) {
        try {
            Method m = target.getClass().getMethod(method, paramType);
            m.invoke(target, value);
            return true;
        } catch (NoSuchMethodException nsme) {
            return false;
        } catch (Exception ex) {
            LOG.log(
                Level.FINE,
                "Unable to invoke " + method + " on stub: " + ex.getMessage(),
                ex
            );
            return false;
        }
    }
}
