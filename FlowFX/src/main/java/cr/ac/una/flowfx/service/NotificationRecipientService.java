package cr.ac.una.flowfx.service;

import cr.ac.una.flowfx.model.NotificationRecipientDTO;
import cr.ac.una.flowfx.util.Respuesta;
import cr.ac.una.flowfx.ws.FlowFXWS;
import cr.ac.una.flowfx.ws.FlowFXWS_Service;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.xml.ws.BindingProvider;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NotificationRecipientService {

    private static final Logger LOG = Logger.getLogger(NotificationRecipientService.class.getName());

    private static final String ENTITY_KEY = "NotificationRecipient";

    private FlowFXWS port;

    public NotificationRecipientService() {
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

    // ======= CRUD =======

//    public Respuesta find(Long notificationId, String email) {
//        try {
//            if (notificationId == null)
//                return new Respuesta(false, "El parámetro 'notificationId' es requerido.", "find.notificationId.null");
//            if (email == null || email.isBlank())
//                return new Respuesta(false, "El parámetro 'email' es requerido.", "find.email.null");
//
//            // Suponiendo que el WS tiene un método getNotificationRecipient
//            cr.ac.una.flowfx.ws.Respuesta wsResp = port.getNotificationRecipient(notificationId, email);
//            Respuesta r = mapRespuesta(wsResp);
//            if (Boolean.TRUE.equals(r.getEstado())) fillFromMensajeInterno(r);
//            return r;
//
//        } catch (Exception ex) {
//            LOG.log(Level.SEVERE, "Error obteniendo el destinatario [" + notificationId + ", " + email + "]", ex);
//            return new Respuesta(false, "Error obteniendo el destinatario.", "find " + ex.getMessage());
//        }
//    }

//    public Respuesta create(NotificationRecipientDTO dto) {
//        try {
//            if (dto == null)
//                return new Respuesta(false, "El parámetro 'recipient' es requerido.", "create.null");
//
//            cr.ac.una.flowfx.ws.NotificationRecipientDTO wsDto = toWs(dto);
//            cr.ac.una.flowfx.ws.Respuesta wsResp = port.createNotificationRecipient(wsDto);
//            Respuesta r = mapRespuesta(wsResp);
//            if (Boolean.TRUE.equals(r.getEstado())) fillFromMensajeInterno(r);
//            return r;
//
//        } catch (Exception ex) {
//            LOG.log(Level.SEVERE, "Error creando el destinatario", ex);
//            return new Respuesta(false, "Error creando el destinatario.", "create " + ex.getMessage());
//        }
//    }

//    public Respuesta update(NotificationRecipientDTO dto) {
//        try {
//            if (dto == null || dto.getNotificationRecipientPK() == null)
//                return new Respuesta(false, "El destinatario y su clave son requeridos.", "update.null");
//
//            cr.ac.una.flowfx.ws.NotificationRecipientDTO wsDto = toWs(dto);
//            cr.ac.una.flowfx.ws.Respuesta wsResp = port.updateNotificationRecipient(wsDto);
//            Respuesta r = mapRespuesta(wsResp);
//            if (Boolean.TRUE.equals(r.getEstado())) fillFromMensajeInterno(r);
//            return r;
//
//        } catch (Exception ex) {
//            LOG.log(Level.SEVERE, "Error actualizando el destinatario", ex);
//            return new Respuesta(false, "Error actualizando el destinatario.", "update " + ex.getMessage());
//        }
//    }

//    public Respuesta delete(Long notificationId, String email) {
//        try {
//            if (notificationId == null)
//                return new Respuesta(false, "El parámetro 'notificationId' es requerido.", "delete.notificationId.null");
//            if (email == null || email.isBlank())
//                return new Respuesta(false, "El parámetro 'email' es requerido.", "delete.email.null");
//
//            cr.ac.una.flowfx.ws.Respuesta wsResp = port.deleteNotificationRecipient(notificationId, email);
//            return mapRespuesta(wsResp);
//
//        } catch (Exception ex) {
//            LOG.log(Level.SEVERE, "Error eliminando el destinatario [" + notificationId + ", " + email + "]", ex);
//            return new Respuesta(false, "Error eliminando el destinatario.", "delete " + ex.getMessage());
//        }
//    }

    // ======== UTILITARIOS ========

    private Respuesta mapRespuesta(cr.ac.una.flowfx.ws.Respuesta ws) {
        if (ws == null) return new Respuesta(false, "Respuesta nula del WS", "ws.response.null");
        return new Respuesta(ws.isEstado(), ws.getMensaje(), ws.getMensajeInterno());
    }

//    private cr.ac.una.flowfx.ws.NotificationRecipientDTO toWs(NotificationRecipientDTO dto) {
//        if (dto == null) return null;
//        cr.ac.una.flowfx.ws.NotificationRecipientDTO w = new cr.ac.una.flowfx.ws.NotificationRecipientDTO();
//        w.setNotificationRecipientPK(dto.getNotificationRecipientPK());
//        w.setName(dto.getName());
//        w.setEmail(dto.getEmail());
//        return w;
//    }
//
//    private void fillFromMensajeInterno(Respuesta r) {
//        String mi = r.getMensajeInterno();
//        if (mi == null || mi.isBlank()) return;
//
//        try (JsonReader jr = Json.createReader(new StringReader(mi))) {
//            JsonObject obj = jr.readObject();
//            NotificationRecipientDTO dto = new NotificationRecipientDTO();
//            dto.setNotificationRecipientPK(obj.containsKey("notificationRecipientPK") ? obj.getJsonObject("notificationRecipientPK") : null);
//            dto.setName(obj.containsKey("name") ? obj.getString("name") : null);
//            dto.setEmail(obj.containsKey("email") ? obj.getString("email") : null);
//            r.setResultado(ENTITY_KEY, dto);
//        } catch (Exception e) {
//            LOG.log(Level.WARNING, "No se pudo parsear mensajeInterno a DTO", e);
//        }
//    }
}
