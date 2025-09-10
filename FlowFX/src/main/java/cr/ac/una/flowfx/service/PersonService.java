package cr.ac.una.flowfx.service;

import cr.ac.una.flowfx.model.PersonDTO;
import cr.ac.una.flowfx.util.Respuesta;
import cr.ac.una.flowfx.ws.FlowFXWS;
import cr.ac.una.flowfx.ws.FlowFXWS_Service;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import jakarta.xml.ws.BindingProvider;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Client service for Person operations against the FlowFX web service.
 *
 * <p>
 * This service prepares the web service port and provides CRUD helpers plus
 * robust parsing utilities to convert the web service's internal JSON payloads
 * into {@link PersonDTO} instances. Public methods perform parameter validation
 * and map web service responses into {@link Respuesta}.
 * </p>
 *
 * <p>
 * Note: If the generated web service stub uses different method names than the
 * ones assumed here, adapt the remote invocation accordingly when enabling the
 * actual calls. This class focuses on stability, clear validation and robust
 * JSON parsing.
 * </p>
 */
public class PersonService {

    private static final Logger LOG = Logger.getLogger(
        PersonService.class.getName()
    );

    private static final String ENTITY_KEY = "Person";
    private static final String LIST_KEY = "Persons";

    private FlowFXWS port;

    /**
     * Initializes the FlowFX web service port and configures a default endpoint.
     * Adjust the endpoint address here if your deployment uses a different URL.
     */
    public PersonService() {
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

    // ======= CRUD =======

    /**
     * Finds a Person by id.
     *
     * @param id the person identifier (required)
     * @return Respuesta with the PersonDTO in the result under key {@code Person}
     */
    public Respuesta find(Long id) {
        try {
            if (id == null) return new Respuesta(
                false,
                "Parameter 'id' is required.",
                "find.id.null"
            );

            cr.ac.una.flowfx.ws.Respuesta wsResp = port.getPerson(id);
            Respuesta r = mapRespuesta(wsResp);
            if (
                Boolean.TRUE.equals(r.getEstado())
            ) fillSingleFromMensajeInterno(r);
            return r;
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error retrieving person [" + id + "]", ex);
            return new Respuesta(
                false,
                "Error retrieving person",
                "find " + ex.getMessage()
            );
        }
    }

    /**
     * Creates a new Person.
     *
     * @param person a PersonDTO instance to create (required)
     * @return Respuesta with the created PersonDTO in the result under key {@code Person}
     */
    public Respuesta create(PersonDTO person) {
        try {
            if (person == null) return new Respuesta(
                false,
                "Parameter 'person' is required.",
                "create.person.null"
            );

            cr.ac.una.flowfx.ws.PersonDTO wsPerson = toWsPerson(person);
            cr.ac.una.flowfx.ws.Respuesta wsResp = port.createPerson(wsPerson);
            Respuesta r = mapRespuesta(wsResp);
            if (
                Boolean.TRUE.equals(r.getEstado())
            ) fillSingleFromMensajeInterno(r);
            return r;
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error creating person", ex);
            return new Respuesta(
                false,
                "Error creating person",
                "create " + ex.getMessage()
            );
        }
    }

    /**
     * Updates an existing Person.
     *
     * @param person the PersonDTO to update (must include id)
     * @return Respuesta with the updated PersonDTO in the result under key {@code Person}
     */
    public Respuesta update(PersonDTO person) {
        try {
            if (person == null || person.getId() == null) return new Respuesta(
                false,
                "Person and its 'id' are required.",
                "update.person.null"
            );

            cr.ac.una.flowfx.ws.PersonDTO wsPerson = toWsPerson(person);
            cr.ac.una.flowfx.ws.Respuesta wsResp = port.updatePerson(wsPerson);
            Respuesta r = mapRespuesta(wsResp);
            if (
                Boolean.TRUE.equals(r.getEstado())
            ) fillSingleFromMensajeInterno(r);
            return r;
        } catch (Exception ex) {
            LOG.log(
                Level.SEVERE,
                "Error updating person [" +
                (person != null ? person.getId() : null) +
                "]",
                ex
            );
            return new Respuesta(
                false,
                "Error updating person",
                "update " + ex.getMessage()
            );
        }
    }

    /**
     * Deletes a Person by id.
     *
     * @param id the person identifier (required)
     * @return Respuesta describing the outcome
     */
    public Respuesta delete(Long id) {
        try {
            if (id == null) return new Respuesta(
                false,
                "Parameter 'id' is required.",
                "delete.id.null"
            );

            cr.ac.una.flowfx.ws.Respuesta wsResp = port.deletePerson(id);
            return mapRespuesta(wsResp);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error deleting person [" + id + "]", ex);
            return new Respuesta(
                false,
                "Error deleting person",
                "delete " + ex.getMessage()
            );
        }
    }

    /**
     * Retrieves all Person entries.
     *
     * @return Respuesta with a List<PersonDTO> under key {@code Persons}
     */
    public Respuesta findAll() {
        try {
            cr.ac.una.flowfx.ws.Respuesta wsResp = port.getAllPeople();
            Respuesta r = mapRespuesta(wsResp);
            if (Boolean.TRUE.equals(r.getEstado())) fillListFromMensajeInterno(
                r
            );
            return r;
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error retrieving persons", ex);
            return new Respuesta(
                false,
                "Error retrieving persons",
                "findAll " + ex.getMessage()
            );
        }
    }

    /**
     * Validates credentials against the remote service.
     *
     * @param username user name (required)
     * @param password password (required)
     * @return Respuesta with PersonDTO under key {@code Person} when validation succeeds
     */
    public Respuesta validateCredentials(String username, String password) {
        try {
            if (
                username == null || username.isBlank() || password == null
            ) return new Respuesta(
                false,
                "Username and password are required.",
                "validateCredentials.params.null"
            );

            cr.ac.una.flowfx.ws.Respuesta wsResp = port.validateCredentials(
                username,
                password
            );
            Respuesta r = mapRespuesta(wsResp);
            if (
                Boolean.TRUE.equals(r.getEstado())
            ) fillSingleFromMensajeInterno(r);
            return r;
        } catch (Exception ex) {
            LOG.log(
                Level.SEVERE,
                "Error validating credentials for [" + username + "]",
                ex
            );
            return new Respuesta(
                false,
                "Error validating credentials",
                "validateCredentials " + ex.getMessage()
            );
        }
    }

    // ======== Utilities ========

    private Respuesta mapRespuesta(cr.ac.una.flowfx.ws.Respuesta ws) {
        if (ws == null) return new Respuesta(
            false,
            "Null response from web service",
            "ws.response.null"
        );
        return new Respuesta(
            ws.isEstado(),
            ws.getMensaje(),
            ws.getMensajeInterno()
        );
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
        w.setStatus(
            p.getStatus() != null ? String.valueOf(p.getStatus()) : null
        );
        w.setIsAdmin(
            p.getIsAdmin() != null ? String.valueOf(p.getIsAdmin()) : null
        );
        return w;
    }

    /**
     * Fills a single PersonDTO from the internal message contained in Respuesta.
     * Supports:
     * - direct object
     * - wrapped under keys "person"/"Person"/"PERSON"
     * - array (takes the first element)
     *
     * @param r the Respuesta whose mensajeInterno will be parsed
     */
    private void fillSingleFromMensajeInterno(Respuesta r) {
        String mi = r.getMensajeInterno();
        if (mi == null || mi.isBlank()) return;

        // 1) Attempt: direct object (or wrapped)
        try (JsonReader jr = Json.createReader(new StringReader(mi))) {
            JsonObject obj = jr.readObject();

            if (
                obj.containsKey("person") &&
                obj.get("person") instanceof JsonObject
            ) {
                obj = obj.getJsonObject("person");
            } else if (
                obj.containsKey("Person") &&
                obj.get("Person") instanceof JsonObject
            ) {
                obj = obj.getJsonObject("Person");
            } else if (
                obj.containsKey("PERSON") &&
                obj.get("PERSON") instanceof JsonObject
            ) {
                obj = obj.getJsonObject("PERSON");
            }
            PersonDTO dto = fromJsonPerson(obj);
            if (dto != null) {
                r.setResultado(ENTITY_KEY, dto);
                return;
            }
        } catch (Exception ignore) {
            // Might not be an object, try next approach.
        }

        // 2) Attempt: array (take first)
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
            // Leave mensajeInterno as-is for diagnostics
        }
    }

    /**
     * Fills a list of PersonDTO from Respuesta.mensajeInterno.
     * Supports:
     * - direct array
     * - object with array under keys typical names: persons/Persons/PERSONS/data/list
     *
     * @param r the Respuesta whose mensajeInterno will be parsed
     */
    private void fillListFromMensajeInterno(Respuesta r) {
        String mi = r.getMensajeInterno();
        if (mi == null || mi.isBlank()) return;

        List<PersonDTO> list = new ArrayList<>();

        // 1) Attempt: direct array
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
            // proceed to object-with-array attempt
        }

        // 2) Attempt: object with internal array
        try (JsonReader jr = Json.createReader(new StringReader(mi))) {
            JsonObject root = jr.readObject();
            JsonArray arr = null;
            if (
                root.containsKey("persons") &&
                root.get("persons") instanceof JsonArray
            ) {
                arr = root.getJsonArray("persons");
            } else if (
                root.containsKey("Persons") &&
                root.get("Persons") instanceof JsonArray
            ) {
                arr = root.getJsonArray("Persons");
            } else if (
                root.containsKey("PERSONS") &&
                root.get("PERSONS") instanceof JsonArray
            ) {
                arr = root.getJsonArray("PERSONS");
            } else if (
                root.containsKey("data") &&
                root.get("data") instanceof JsonArray
            ) {
                arr = root.getJsonArray("data");
            } else if (
                root.containsKey("list") &&
                root.get("list") instanceof JsonArray
            ) {
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
            LOG.log(
                Level.FINE,
                "Unable to parse list of persons from mensajeInterno",
                ex
            );
        }

        r.setResultado(LIST_KEY, list);
    }

    /**
     * Converts a JsonObject to PersonDTO accepting aliases for keys and tolerant parsing.
     *
     * @param o the JsonObject
     * @return a populated PersonDTO or null if input is null
     */
    private PersonDTO fromJsonPerson(JsonObject o) {
        if (o == null) return null;
        PersonDTO p = new PersonDTO();

        // id: id | per_id | person_id
        Long id = getLong(o, "id", "per_id", "person_id", "PER_ID");
        p.setId(id);

        // firstName: firstName | per_first_name | first_name | FIRST_NAME
        p.setFirstName(
            getString(
                o,
                "firstName",
                "per_first_name",
                "first_name",
                "FIRST_NAME"
            )
        );

        // lastName: lastName | per_last_name | last_name | LAST_NAME
        p.setLastName(
            getString(o, "lastName", "per_last_name", "last_name", "LAST_NAME")
        );

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

    // ======== Safe JSON readers ========

    private String getString(JsonObject obj, String... keys) {
        for (String k : keys) {
            if (obj.containsKey(k) && !obj.isNull(k)) {
                try {
                    switch (obj.get(k).getValueType()) {
                        case STRING:
                            return ((JsonString) obj.get(k)).getString();
                        case NUMBER:
                            return obj.getJsonNumber(k).toString();
                        case TRUE:
                            return "true";
                        case FALSE:
                            return "false";
                        default:
                            // fallback: textual representation
                            return obj.get(k).toString();
                    }
                } catch (Exception ignore) {
                    // continue with other keys
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
                            if (
                                s != null && !s.isBlank()
                            ) return Long.parseLong(s.trim());
                            break;
                        default:
                            String raw = obj.get(k).toString();
                            if (
                                raw != null && !raw.isBlank()
                            ) return Long.parseLong(
                                raw.replace("\"", "").trim()
                            );
                    }
                } catch (Exception ignore) {
                    // continue with other keys
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
