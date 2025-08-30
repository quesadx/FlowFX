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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * PersonService
 * - Preparado para recibir lo que mande el WS:
 *   - Soporta mensajeInterno como objeto o arreglo.
 *   - Reconoce envolturas típicas (person/Person/PERSON, persons/Persons/PERSONS, data, list).
 *   - Acepta alias de campos (id/per_id, firstName/per_first_name, etc.).
 *   - Tolerante a números enviados como string.
 */
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

    /**
     * Llena un único PersonDTO desde mensajeInterno:
     * - Si viene como objeto directo.
     * - Si viene envuelto bajo "person"/"Person"/"PERSON".
     * - Si viene como arreglo (toma el primero).
     */
    private void fillSingleFromMensajeInterno(Respuesta r) {
        String mi = r.getMensajeInterno();
        if (mi == null || mi.isBlank()) return;

        // 1) Intento: objeto directo (con posible envoltura)
        try (JsonReader jr = Json.createReader(new StringReader(mi))) {
            JsonObject obj = jr.readObject();

            if (obj.containsKey("person") && obj.get("person") instanceof JsonObject) {
                obj = obj.getJsonObject("person");
            } else if (obj.containsKey("Person") && obj.get("Person") instanceof JsonObject) {
                obj = obj.getJsonObject("Person");
            } else if (obj.containsKey("PERSON") && obj.get("PERSON") instanceof JsonObject) {
                obj = obj.getJsonObject("PERSON");
            }
            PersonDTO dto = fromJsonPerson(obj);
            if (dto != null) {
                r.setResultado(ENTITY_KEY, dto);
                return;
            }
        } catch (Exception ignore) {
            // Puede no ser un objeto JSON, seguimos al siguiente intento
        }

        // 2) Intento: arreglo (tomar el primero)
        try (JsonReader jr = Json.createReader(new StringReader(mi))) {
            JsonArray arr = jr.readArray();
            if (!arr.isEmpty()) {
                JsonObject obj = arr.getJsonObject(0);
                PersonDTO dto = fromJsonPerson(obj);
                if (dto != null) {
                    r.setResultado(ENTITY_KEY, dto);
                }
            }
        } catch (Exception ignore) {
            // Dejar mensajeInterno para diagnóstico
        }
    }

    /**
     * Llena la lista de PersonDTO desde mensajeInterno:
     * - Arreglo directo.
     * - Objeto con arreglo bajo "persons"/"Persons"/"PERSONS"/"data"/"list".
     */
    private void fillListFromMensajeInterno(Respuesta r) {
        String mi = r.getMensajeInterno();
        if (mi == null || mi.isBlank()) return;

        List<PersonDTO> list = new ArrayList<>();

        // 1) Intento: arreglo directo
        try (JsonReader jr = Json.createReader(new StringReader(mi))) {
            JsonArray arr = jr.readArray();
            for (int i = 0; i < arr.size(); i++) {
                JsonObject obj = arr.getJsonObject(i);
                PersonDTO dto = fromJsonPerson(obj);
                if (dto != null) list.add(dto);
            }
            r.setResultado(LIST_KEY, list);
            return;
        } catch (Exception ignore) {
            // Pasar a objeto con arreglo interno
        }

        // 2) Intento: objeto con arreglo interno
        try (JsonReader jr = Json.createReader(new StringReader(mi))) {
            JsonObject root = jr.readObject();
            JsonArray arr = null;
            if (root.containsKey("persons") && root.get("persons") instanceof JsonArray) {
                arr = root.getJsonArray("persons");
            } else if (root.containsKey("Persons") && root.get("Persons") instanceof JsonArray) {
                arr = root.getJsonArray("Persons");
            } else if (root.containsKey("PERSONS") && root.get("PERSONS") instanceof JsonArray) {
                arr = root.getJsonArray("PERSONS");
            } else if (root.containsKey("data") && root.get("data") instanceof JsonArray) {
                arr = root.getJsonArray("data");
            } else if (root.containsKey("list") && root.get("list") instanceof JsonArray) {
                arr = root.getJsonArray("list");
            }

            if (arr != null) {
                for (int i = 0; i < arr.size(); i++) {
                    JsonObject obj = arr.getJsonObject(i);
                    PersonDTO dto = fromJsonPerson(obj);
                    if (dto != null) list.add(dto);
                }
            }
        } catch (Exception ex) {
            LOG.log(Level.FINE, "No se pudo parsear lista de personas desde mensajeInterno", ex);
        }

        r.setResultado(LIST_KEY, list);
    }

    /**
     * Convierte un JsonObject a PersonDTO aceptando alias de nombres y tipos.
     */
    private PersonDTO fromJsonPerson(JsonObject o) {
        if (o == null) return null;
        PersonDTO p = new PersonDTO();

        // id: id | per_id | person_id
        Long id = getLong(o, "id", "per_id", "person_id", "PER_ID");
        p.setId(id);

        // firstName: firstName | per_first_name | first_name | FIRST_NAME
        p.setFirstName(getString(o, "firstName", "per_first_name", "first_name", "FIRST_NAME"));

        // lastName: lastName | per_last_name | last_name | LAST_NAME
        p.setLastName(getString(o, "lastName", "per_last_name", "last_name", "LAST_NAME"));

        // email: email | EMAIL
        p.setEmail(getString(o, "email", "EMAIL"));

        // username: username | USERNAME
        p.setUsername(getString(o, "username", "USERNAME"));

        // password: password | PASSWORD
        p.setPassword(getString(o, "password", "PASSWORD"));

        // status: status | STATUS (string -> first char)
        Character status = getChar(o, "status", "STATUS");
        p.setStatus(status);

        // isAdmin: isAdmin | is_admin | IS_ADMIN (string -> first char)
        Character isAdmin = getChar(o, "isAdmin", "is_admin", "IS_ADMIN");
        p.setIsAdmin(isAdmin);

        return p;
    }

    // ======== Helpers de lectura segura JSON ========

    private String getString(JsonObject obj, String... keys) {
        for (String k : keys) {
            if (obj.containsKey(k) && !obj.isNull(k)) {
                try {
                    switch (obj.get(k).getValueType()) {
                        case STRING:
                            return obj.getString(k);
                        case NUMBER:
                            return obj.getJsonNumber(k).toString();
                        case TRUE:
                            return "true";
                        case FALSE:
                            return "false";
                        default:
                            // Como fallback, devolver representación textual
                            return obj.get(k).toString();
                    }
                } catch (Exception ignore) {
                }
            }
        }
        return null;
    }

    private Long getLong(JsonObject obj, String... keys) {
        for (String k : keys) {
            if (obj.containsKey(k) && !obj.isNull(k)) {
                try {
                    switch (obj.get(k).getValueType()) {
                        case NUMBER:
                            return obj.getJsonNumber(k).longValue();
                        case STRING:
                            String s = obj.getString(k);
                            if (s != null && !s.isBlank()) return Long.parseLong(s.trim());
                            break;
                        default:
                            String raw = obj.get(k).toString();
                            if (raw != null && !raw.isBlank()) return Long.parseLong(raw.replace("\"", "").trim());
                    }
                } catch (Exception ignore) {
                }
            }
        }
        return null;
    }

    private Character getChar(JsonObject obj, String... keys) {
        String s = getString(obj, keys);
        if (s != null) {
            s = s.trim();
            if (!s.isEmpty()) return s.charAt(0);
        }
        return null;
    }
}