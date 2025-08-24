package cr.ac.una.flowfx.service;

import cr.ac.una.flowfx.model.PersonDTO;
import cr.ac.una.flowfx.util.Respuesta;
import cr.ac.una.flowfx.ws.FlowFXWS;
import cr.ac.una.flowfx.ws.FlowFXWS_Service;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

import jakarta.xml.ws.BindingProvider;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PersonService {

    private static final Logger LOG = Logger.getLogger(PersonService.class.getName());

    private static final String ENTITY_KEY = "Person";
    private static final String LIST_KEY = "Persons";

    private FlowFXWS port;

    public PersonService() {
        try {
            FlowFXWS_Service service = new FlowFXWS_Service();
            port = service.getFlowFXWSPort();

            // Opcional: cambiar endpoint si usas otra URL
            if (port instanceof BindingProvider) {
                ((BindingProvider) port).getRequestContext().put(
                        BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                        "http://localhost:8080/FlowFXWS/FlowFXWS"
                );
            }

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error inicializando el port del WS", e);
        }
    }

    // ======= MÉTODOS CRUD =======

    public Respuesta find(Long id) {
        try {
            if (id == null) return new Respuesta(false, "El parámetro 'id' es requerido.", "find.id.null");

            cr.ac.una.flowfx.ws.Respuesta wsResp = port.getPerson(id);
            Respuesta r = mapRespuesta(wsResp);
            if (Boolean.TRUE.equals(r.getEstado())) fillSingleFromMensajeInterno(r);
            return r;

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo la persona [" + id + "]", ex);
            return new Respuesta(false, "Error obteniendo la persona", "find " + ex.getMessage());
        }
    }

    public Respuesta create(PersonDTO person) {
        try {
            if (person == null) return new Respuesta(false, "El parámetro 'person' es requerido.", "create.person.null");

            cr.ac.una.flowfx.ws.PersonDTO wsPerson = toWsPerson(person);
            cr.ac.una.flowfx.ws.Respuesta wsResp = port.createPerson(wsPerson);
            Respuesta r = mapRespuesta(wsResp);
            if (Boolean.TRUE.equals(r.getEstado())) fillSingleFromMensajeInterno(r);
            return r;

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error creando la persona", ex);
            return new Respuesta(false, "Error creando la persona", "create " + ex.getMessage());
        }
    }

    public Respuesta update(PersonDTO person) {
        try {
            if (person == null || person.getId() == null)
                return new Respuesta(false, "La persona y su 'id' son requeridos.", "update.person.null");

            cr.ac.una.flowfx.ws.PersonDTO wsPerson = toWsPerson(person);
            cr.ac.una.flowfx.ws.Respuesta wsResp = port.updatePerson(wsPerson);
            Respuesta r = mapRespuesta(wsResp);
            if (Boolean.TRUE.equals(r.getEstado())) fillSingleFromMensajeInterno(r);
            return r;

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error actualizando la persona [" + (person != null ? person.getId() : null) + "]", ex);
            return new Respuesta(false, "Error actualizando la persona", "update " + ex.getMessage());
        }
    }

    public Respuesta delete(Long id) {
        try {
            if (id == null) return new Respuesta(false, "El parámetro 'id' es requerido.", "delete.id.null");

            cr.ac.una.flowfx.ws.Respuesta wsResp = port.deletePerson(id);
            return mapRespuesta(wsResp);

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error eliminando la persona [" + id + "]", ex);
            return new Respuesta(false, "Error eliminando la persona", "delete " + ex.getMessage());
        }
    }

    public Respuesta findAll() {
        try {
            cr.ac.una.flowfx.ws.Respuesta wsResp = port.getAllPeople();
            Respuesta r = mapRespuesta(wsResp);
            if (Boolean.TRUE.equals(r.getEstado())) fillListFromMensajeInterno(r);
            return r;

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo las personas", ex);
            return new Respuesta(false, "Error obteniendo las personas", "findAll " + ex.getMessage());
        }
    }

    public Respuesta validateCredentials(String username, String password) {
        try {
            if (username == null || username.isBlank() || password == null)
                return new Respuesta(false, "Usuario y contraseña son requeridos.", "validateCredentials.params.null");

            cr.ac.una.flowfx.ws.Respuesta wsResp = port.validateCredentials(username, password);
            Respuesta r = mapRespuesta(wsResp);
            if (Boolean.TRUE.equals(r.getEstado())) fillSingleFromMensajeInterno(r);
            return r;

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error validando credenciales de [" + username + "]", ex);
            return new Respuesta(false, "Error validando credenciales", "validateCredentials " + ex.getMessage());
        }
    }

    // ======== UTILITARIOS ========

    private Respuesta mapRespuesta(cr.ac.una.flowfx.ws.Respuesta ws) {
        if (ws == null) return new Respuesta(false, "Respuesta nula del WS", "ws.response.null");
        return new Respuesta(ws.isEstado(), ws.getMensaje(), ws.getMensajeInterno());
    }

    private cr.ac.una.flowfx.ws.PersonDTO toWsPerson(PersonDTO p) {
        if (p == null) return null;
        cr.ac.una.flowfx.ws.PersonDTO w = new cr.ac.una.flowfx.ws.PersonDTO();
        w.setId(p.getId());
        w.setFirstName(p.getFirstName());
        w.setLastName(p.getLastName());
        w.setEmail(p.getEmail());
        w.setUsername(p.getUsername());
        w.setPassword(p.getPassword());
        // Map Character flags to WS String fields
        w.setStatus(p.getStatus() != null ? String.valueOf(p.getStatus()) : null);
        w.setIsAdmin(p.getIsAdmin() != null ? String.valueOf(p.getIsAdmin()) : null);
        return w;
    }

    private void fillSingleFromMensajeInterno(Respuesta r) {
        String mi = r.getMensajeInterno();
        if (mi == null || mi.isBlank()) return;

        try (JsonReader jr = Json.createReader(new StringReader(mi))) {
            JsonObject obj = jr.readObject();
            if (obj.containsKey("person")) obj = obj.getJsonObject("person");
            PersonDTO dto = fromJsonPerson(obj);
            if (dto != null) r.setResultado(ENTITY_KEY, dto);
        } catch (Exception e) {
            // si no es JSON válido, se puede dejar mensajeInterno para debug
        }
    }

    private void fillListFromMensajeInterno(Respuesta r) {
        String mi = r.getMensajeInterno();
        if (mi == null || mi.isBlank()) return;
        List<PersonDTO> list = new ArrayList<>();
        try (JsonReader jr = Json.createReader(new StringReader(mi))) {
            JsonArray arr = jr.readArray();
            for (int i = 0; i < arr.size(); i++) {
                JsonObject obj = arr.getJsonObject(i);
                PersonDTO dto = fromJsonPerson(obj);
                if (dto != null) list.add(dto);
            }
        } catch (Exception e) {
            // fallback: intentar parsear como objeto con lista interna
        }
        r.setResultado(LIST_KEY, list);
    }

    private PersonDTO fromJsonPerson(JsonObject o) {
        if (o == null) return null;
        PersonDTO p = new PersonDTO();
        if (o.containsKey("id") && !o.isNull("id")) p.setId(o.getJsonNumber("id").longValue());
        p.setFirstName(o.containsKey("firstName") && !o.isNull("firstName") ? o.getString("firstName") : null);
        p.setLastName(o.containsKey("lastName") && !o.isNull("lastName") ? o.getString("lastName") : null);
        p.setEmail(o.containsKey("email") && !o.isNull("email") ? o.getString("email") : null);
        p.setUsername(o.containsKey("username") && !o.isNull("username") ? o.getString("username") : null);
        p.setPassword(o.containsKey("password") && !o.isNull("password") ? o.getString("password") : null);
        p.setStatus(o.containsKey("status") && !o.isNull("status") && !o.getString("status").isBlank() ? o.getString("status").charAt(0) : null);
        p.setIsAdmin(o.containsKey("isAdmin") && !o.isNull("isAdmin") && !o.getString("isAdmin").isBlank() ? o.getString("isAdmin").charAt(0) : null);
        return p;
    }
}
