package cr.ac.una.flowfx.service;

import cr.ac.una.flowfx.model.NotificationRecipientPK;
import cr.ac.una.flowfx.util.Respuesta;
import cr.ac.una.flowfx.ws.FlowFXWS;
import cr.ac.una.flowfx.ws.FlowFXWS_Service;
import jakarta.xml.ws.BindingProvider;

import java.util.logging.Level;
import java.util.logging.Logger;

public class NotificationRecipientPKService {

    private static final Logger LOG = Logger.getLogger(NotificationRecipientPKService.class.getName());

    private FlowFXWS port;

    public NotificationRecipientPKService() {
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
            LOG.log(Level.SEVERE, "Error inicializando el port del WS", e);
        }
    }

    /**
     * Busca un NotificationRecipientPK en el WS por ID y email.
     */
    public Respuesta find(Long id, String email) {
//        try {
//            if (id == null)
//                return new Respuesta(false, "El parámetro 'id' es requerido.", "find.id.null");
//            if (email == null || email.isBlank())
//                return new Respuesta(false, "El parámetro 'email' es requerido.", "find.email.null");
//
//            // Llamada al WS (asumiendo que existe un método getNotificationRecipientPK)
//            cr.ac.una.flowfx.ws.Respuesta wsResp = port.getNotificationRecipientPK(id, email);
//            Respuesta r = mapRespuesta(wsResp);
//
//            if (Boolean.TRUE.equals(r.getEstado()) && wsResp.getMensajeInterno() != null) {
//                // Parseamos el mensajeInterno si contiene los datos
//                NotificationRecipientPK pk = parsePK(wsResp.getMensajeInterno());
//                r.setResultado("NotificationRecipientPK", pk);
//            }
//
//            return r;
//
//        } catch (Exception ex) {
//            LOG.log(Level.SEVERE, "Error obteniendo NotificationRecipientPK [" + id + ", " + email + "]", ex);
//            return new Respuesta(false, "Error obteniendo NotificationRecipientPK.", "find " + ex.getMessage());
//        }
return null;
    }

    /**
     * Mapea Respuesta generada por el WS a nuestra Respuesta.
     */
    private Respuesta mapRespuesta(cr.ac.una.flowfx.ws.Respuesta ws) {
        if (ws == null) return new Respuesta(false, "Respuesta nula del WS", "ws.response.null");
        return new Respuesta(ws.isEstado(), ws.getMensaje(), ws.getMensajeInterno());
    }

    /**
     * Convierte el mensajeInterno (JSON) en NotificationRecipientPK.
     * Ejemplo de mensajeInterno: {"id":1,"email":"abc@xyz.com"}
     */
    private NotificationRecipientPK parsePK(String mensajeInterno) {
        try (jakarta.json.JsonReader jr = jakarta.json.Json.createReader(new java.io.StringReader(mensajeInterno))) {
            jakarta.json.JsonObject obj = jr.readObject();
            Long id = obj.containsKey("id") ? obj.getJsonNumber("id").longValue() : null;
            String email = obj.containsKey("email") ? obj.getString("email") : null;
            return new NotificationRecipientPK(id, email);
        } catch (Exception e) {
            LOG.log(Level.WARNING, "No se pudo parsear mensajeInterno a NotificationRecipientPK", e);
            return null;
        }
    }
}
