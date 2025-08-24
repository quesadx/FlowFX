package cr.ac.una.flowfx.service;

import cr.ac.una.flowfx.model.NotificationDTO;
import cr.ac.una.flowfx.util.Respuesta;
import cr.ac.una.flowfx.ws.FlowFXWS;
import cr.ac.una.flowfx.ws.FlowFXWS_Service;
import jakarta.xml.ws.BindingProvider;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NotificationService {

    private static final Logger LOG = Logger.getLogger(NotificationService.class.getName());
    private FlowFXWS port;

    public NotificationService() {
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
//
//    public Respuesta find(Long id) {
//        try {
//            if (id == null)
//                return new Respuesta(false, "El parámetro 'id' es requerido.", "notification.find.id.null");
//
//            cr.ac.una.flowfx.ws.Respuesta wsResp = port.getNotification(id);
//            Respuesta r = mapRespuesta(wsResp);
//
//            if (Boolean.TRUE.equals(r.getEstado()) && wsResp.getMensajeInterno() != null) {
//                NotificationDTO dto = parseNotification(wsResp.getMensajeInterno());
//                r.setResultado("Notification", dto);
//            }
//
//            return r;
//
//        } catch (Exception ex) {
//            LOG.log(Level.SEVERE, "Error obteniendo notificación [" + id + "]", ex);
//            return new Respuesta(false, "Error obteniendo notificación.", "notification.find " + ex.getMessage());
//        }
//    }
//
//    public Respuesta create(NotificationDTO notification) {
//        try {
//            if (notification == null)
//                return new Respuesta(false, "El parámetro 'notification' es requerido.", "notification.create.null");
//
//            cr.ac.una.flowfx.ws.Respuesta wsResp = port.createNotification(toWs(notification));
//            Respuesta r = mapRespuesta(wsResp);
//
//            if (Boolean.TRUE.equals(r.getEstado()) && wsResp.getMensajeInterno() != null) {
//                NotificationDTO dto = parseNotification(wsResp.getMensajeInterno());
//                r.setResultado("Notification", dto);
//            }
//
//            return r;
//
//        } catch (Exception ex) {
//            LOG.log(Level.SEVERE, "Error creando notificación", ex);
//            return new Respuesta(false, "Error creando notificación.", "notification.create " + ex.getMessage());
//        }
//    }

//    public Respuesta update(NotificationDTO notification) {
//        try {
//            if (notification == null || notification.getId() == null)
//                return new Respuesta(false, "La notificación y su 'id' son requeridos.", "notification.update.null");
//
//            cr.ac.una.flowfx.ws.Respuesta wsResp = port.updateNotification(toWs(notification));
//            Respuesta r = mapRespuesta(wsResp);
//
//            if (Boolean.TRUE.equals(r.getEstado()) && wsResp.getMensajeInterno() != null) {
//                NotificationDTO dto = parseNotification(wsResp.getMensajeInterno());
//                r.setResultado("Notification", dto);
//            }
//
//            return r;
//
//        } catch (Exception ex) {
//            LOG.log(Level.SEVERE, "Error actualizando notificación [" + notification.getId() + "]", ex);
//            return new Respuesta(false, "Error actualizando notificación.", "notification.update " + ex.getMessage());
//        }
//    }

//    public Respuesta delete(Long id) {
//        try {
//            if (id == null)
//                return new Respuesta(false, "El parámetro 'id' es requerido.", "notification.delete.id.null");
//
//            cr.ac.una.flowfx.ws.Respuesta wsResp = port.deleteNotification(id);
//            return mapRespuesta(wsResp);
//
//        } catch (Exception ex) {
//            LOG.log(Level.SEVERE, "Error eliminando notificación [" + id + "]", ex);
//            return new Respuesta(false, "Error eliminando notificación.", "notification.delete " + ex.getMessage());
//        }
//    }

//    public Respuesta findAll() {
//        try {
//            cr.ac.una.flowfx.ws.Respuesta wsResp = port.getAllNotifications();
//            Respuesta r = mapRespuesta(wsResp);
//
//            if (Boolean.TRUE.equals(r.getEstado()) && wsResp.getMensajeInterno() != null) {
//                NotificationDTO[] arr = parseNotificationArray(wsResp.getMensajeInterno());
//                List<NotificationDTO> dtos = Arrays.asList(arr != null ? arr : new NotificationDTO[0]);
//                r.setResultado("Notifications", dtos);
//            }
//
//            return r;
//
//        } catch (Exception ex) {
//            LOG.log(Level.SEVERE, "Error obteniendo notificaciones", ex);
//            return new Respuesta(false, "Error obteniendo notificaciones.", "notification.findAll " + ex.getMessage());
//        }
//    }

    private Respuesta mapRespuesta(cr.ac.una.flowfx.ws.Respuesta ws) {
        if (ws == null) return new Respuesta(false, "Respuesta nula del WS", "ws.response.null");
        return new Respuesta(ws.isEstado(), ws.getMensaje(), ws.getMensajeInterno());
    }

    // Conversiones entre DTO locales y WS
//    private cr.ac.una.flowfx.ws.NotificationDTO toWs(NotificationDTO dto) {
//        cr.ac.una.flowfx.ws.NotificationDTO w = new cr.ac.una.flowfx.ws.NotificationDTO();
//        w.setId(dto.getId());
//        w.setTitle(dto.getTitle());
//        w.setMessage(dto.getMessage());
//        return w;
//    }

//    private NotificationDTO parseNotification(String json) {
//        try (jakarta.json.JsonReader jr = jakarta.json.Json.createReader(new java.io.StringReader(json))) {
//            jakarta.json.JsonObject obj = jr.readObject();
//            NotificationDTO dto = new NotificationDTO();
//            dto.setId(obj.containsKey("id") ? obj.getJsonNumber("id").longValue() : null);
//            dto.setTitle(obj.containsKey("title") ? obj.getString("title") : null);
//            dto.setMessage(obj.containsKey("message") ? obj.getString("message") : null);
//            return dto;
//        } catch (Exception e) {
//            LOG.log(Level.WARNING, "No se pudo parsear mensajeInterno a NotificationDTO", e);
//            return null;
//        }
//    }

//    private NotificationDTO[] parseNotificationArray(String json) {
//        try (jakarta.json.JsonReader jr = jakarta.json.Json.createReader(new java.io.StringReader(json))) {
//            jakarta.json.JsonArray arr = jr.readArray();
//            NotificationDTO[] dtos = new NotificationDTO[arr.size()];
//            for (int i = 0; i < arr.size(); i++) {
//                jakarta.json.JsonObject obj = arr.getJsonObject(i);
//                dtos[i] = parseNotification(obj.toString());
//            }
//            return dtos;
//        } catch (Exception e) {
//            LOG.log(Level.WARNING, "No se pudo parsear mensajeInterno a NotificationDTO[]", e);
//            return new NotificationDTO[0];
//        }
//    }
}
