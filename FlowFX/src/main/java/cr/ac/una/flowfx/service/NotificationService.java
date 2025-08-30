package cr.ac.una.flowfx.service;

import cr.ac.una.flowfx.model.NotificationDTO;
import cr.ac.una.flowfx.util.Respuesta;
import cr.ac.una.flowfx.ws.FlowFXWS;
import cr.ac.una.flowfx.ws.FlowFXWS_Service;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import jakarta.xml.ws.BindingProvider;

import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service cliente para gestionar NOTIFICATION contra el WS.
 * Preparado para recibir lo que envíe el WS:
 * - Soporta mensajeInterno como objeto, arreglo o valor simple.
 * - Reconoce envolturas: notification/Notification/NOTIFICATION y
 *   notifications/Notifications/NOTIFICATIONS, data, list.
 * - Acepta alias de campos, consistente con tu DDL:
 *   id: id | notification_id | notificationId | NOTIFICATION_ID
 *   projectId: project_id | projectId | PROJECT_ID
 *   activityId: activity_id | activityId | ACTIVITY_ID
 *   subject: subject | SUBJECT
 *   message: message | MESSAGE | body
 *   sentAt: sent_at | sentAt | SENT_AT
 *   status: status | STATUS  ('P','S','F')
 *   eventType: event_type | eventType | EVENT_TYPE ('P','S','A','T')
 *
 * Nota:
 * - Ajusta los nombres de las operaciones del WS si difieren de tu WSDL generado.
 * - No modifica tus DTOs. Si tu NotificationDTO tiene campos adicionales, puedo mapearlos.
 */
public class NotificationService {

    // private static final Logger LOG = Logger.getLogger(NotificationService.class.getName());
    // private static final String ENTITY_KEY = "Notification";
    // private static final String LIST_KEY = "Notifications";

    // private FlowFXWS port;

    // public NotificationService() {
    //     try {
    //         FlowFXWS_Service service = new FlowFXWS_Service();
    //         port = service.getFlowFXWSPort();
    //         if (port instanceof BindingProvider) {
    //             ((BindingProvider) port).getRequestContext().put(
    //                     BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
    //                     "http://localhost:8080/FlowFXWS/FlowFXWS"
    //             );
    //         }
    //     } catch (Exception e) {
    //         LOG.log(Level.SEVERE, "Error inicializando port del WS", e);
    //     }
    // }

    // // ===================== CRUD/Queries =====================

    // public Respuesta find(Long id) {
    //     try {
    //         if (id == null) {
    //             return new Respuesta(false, "El parámetro 'id' es requerido.", "notification.find.id.null");
    //         }
    //         // Ajusta el nombre si tu port usa otro
    //         cr.ac.una.flowfx.ws.Respuesta wsResp = port.getNotification(id);
    //         Respuesta r = mapRespuesta(wsResp);
    //         if (Boolean.TRUE.equals(r.getEstado())) fillSingleFromMensajeInterno(r);
    //         return r;
    //     } catch (Exception ex) {
    //         LOG.log(Level.SEVERE, "Error obteniendo notificación [" + id + "]", ex);
    //         return new Respuesta(false, "Error obteniendo la notificación.", "notification.find " + ex.getMessage());
    //     }
    // }

    // public Respuesta findByProject(Long projectId) {
    //     try {
    //         if (projectId == null) {
    //             return new Respuesta(false, "El parámetro 'projectId' es requerido.", "notification.findByProject.projectId.null");
    //         }
    //         // Ajusta el nombre si tu port usa otro
    //         cr.ac.una.flowfx.ws.Respuesta wsResp = port.getNotificationsByProject(projectId);
    //         Respuesta r = mapRespuesta(wsResp);
    //         if (Boolean.TRUE.equals(r.getEstado())) fillListFromMensajeInterno(r);
    //         return r;
    //     } catch (Exception ex) {
    //         LOG.log(Level.SEVERE, "Error obteniendo notificaciones por proyecto [" + projectId + "]", ex);
    //         return new Respuesta(false, "Error obteniendo notificaciones del proyecto.", "notification.findByProject " + ex.getMessage());
    //     }
    // }

    // public Respuesta findAll() {
    //     try {
    //         // Ajusta el nombre si tu port usa otro
    //         cr.ac.una.flowfx.ws.Respuesta wsResp = port.getAllNotifications();
    //         Respuesta r = mapRespuesta(wsResp);
    //         if (Boolean.TRUE.equals(r.getEstado())) fillListFromMensajeInterno(r);
    //         return r;
    //     } catch (Exception ex) {
    //         LOG.log(Level.SEVERE, "Error obteniendo notificaciones", ex);
    //         return new Respuesta(false, "Error obteniendo notificaciones.", "notification.findAll " + ex.getMessage());
    //     }
    // }

    // public Respuesta create(NotificationDTO notification) {
    //     try {
    //         if (notification == null) {
    //             return new Respuesta(false, "El parámetro 'notification' es requerido.", "notification.create.null");
    //         }
    //         cr.ac.una.flowfx.ws.NotificationDTO wsDto = toWs(notification);
    //         // Ajusta el nombre si tu port usa otro
    //         cr.ac.una.flowfx.ws.Respuesta wsResp = port.createNotification(wsDto);
    //         Respuesta r = mapRespuesta(wsResp);
    //         if (Boolean.TRUE.equals(r.getEstado())) fillSingleFromMensajeInterno(r);
    //         return r;
    //     } catch (Exception ex) {
    //         LOG.log(Level.SEVERE, "Error creando notificación", ex);
    //         return new Respuesta(false, "Error creando notificación.", "notification.create " + ex.getMessage());
    //     }
    // }

    // public Respuesta update(NotificationDTO notification) {
    //     try {
    //         if (notification == null || notification.getId() == null) {
    //             return new Respuesta(false, "La notificación y su 'id' son requeridos.", "notification.update.null");
    //         }
    //         cr.ac.una.flowfx.ws.NotificationDTO wsDto = toWs(notification);
    //         // Ajusta el nombre si tu port usa otro
    //         cr.ac.una.flowfx.ws.Respuesta wsResp = port.updateNotification(wsDto);
    //         Respuesta r = mapRespuesta(wsResp);
    //         if (Boolean.TRUE.equals(r.getEstado())) fillSingleFromMensajeInterno(r);
    //         return r;
    //     } catch (Exception ex) {
    //         LOG.log(Level.SEVERE, "Error actualizando notificación [" + (notification != null ? notification.getId() : null) + "]", ex);
    //         return new Respuesta(false, "Error actualizando notificación.", "notification.update " + ex.getMessage());
    //     }
    // }

    // public Respuesta delete(Long id) {
    //     try {
    //         if (id == null) {
    //             return new Respuesta(false, "El parámetro 'id' es requerido.", "notification.delete.id.null");
    //         }
    //         // Ajusta el nombre si tu port usa otro
    //         cr.ac.una.flowfx.ws.Respuesta wsResp = port.deleteNotification(id);
    //         return mapRespuesta(wsResp);
    //     } catch (Exception ex) {
    //         LOG.log(Level.SEVERE, "Error eliminando notificación [" + id + "]", ex);
    //         return new Respuesta(false, "Error eliminando notificación.", "notification.delete " + ex.getMessage());
    //     }
    // }

    // // ===================== Mapping WS <-> App =====================

    // private cr.ac.una.flowfx.ws.NotificationDTO toWs(NotificationDTO d) {
    //     if (d == null) return null;
    //     cr.ac.una.flowfx.ws.NotificationDTO w = new cr.ac.una.flowfx.ws.NotificationDTO();
    //     w.setId(d.getId());
    //     w.setProjectId(d.getProjectId());
    //     w.setActivityId(d.getActivityId());
    //     w.setSubject(d.getSubject());
    //     w.setMessage(d.getMessage());
    //     // status/eventType mapeados como String de 1 char si tu WS los define así
    //     if (d.getStatus() != null) w.setStatus(String.valueOf(d.getStatus()));
    //     if (d.getEventType() != null) w.setEventType(String.valueOf(d.getEventType()));
    //     // Fechas/timestamps si tu WS las acepta como java.util.Date
    //     w.setSentAt(d.getSentAt());
    //     return w;
    // }

    // // ===================== Parsing mensajeInterno =====================

    // private Respuesta mapRespuesta(cr.ac.una.flowfx.ws.Respuesta ws) {
    //     if (ws == null) return new Respuesta(false, "Respuesta nula del WS", "ws.response.null");
    //     return new Respuesta(ws.isEstado(), ws.getMensaje(), ws.getMensajeInterno());
    // }

    // private void fillSingleFromMensajeInterno(Respuesta r) {
    //     String mi = r.getMensajeInterno();
    //     if (mi == null || mi.isBlank()) return;

    //     // 1) Leer como value genérico
    //     try (JsonReader jr = Json.createReader(new StringReader(mi))) {
    //         JsonValue v = jr.readValue();
    //         switch (v.getValueType()) {
    //             case OBJECT: {
    //                 JsonObject obj = (JsonObject) v;
    //                 obj = unwrapObject(obj, "notification", "Notification", "NOTIFICATION");
    //                 NotificationDTO dto = fromJsonNotification(obj);
    //                 if (dto != null) {
    //                     r.setResultado(ENTITY_KEY, dto);
    //                     return;
    //                 }
    //                 break;
    //             }
    //             case ARRAY: {
    //                 JsonArray arr = (JsonArray) v;
    //                 if (!arr.isEmpty() && arr.get(0).getValueType() == JsonValue.ValueType.OBJECT) {
    //                     NotificationDTO dto = fromJsonNotification(arr.getJsonObject(0));
    //                     if (dto != null) {
    //                         r.setResultado(ENTITY_KEY, dto);
    //                         return;
    //                     }
    //                 }
    //                 break;
    //             }
    //             default:
    //                 break;
    //         }
    //     } catch (Exception ignore) {
    //     }

    //     // 2) Fallback: leer estrictamente como objeto
    //     try (JsonReader jr = Json.createReader(new StringReader(mi))) {
    //         JsonObject obj = jr.readObject();
    //         obj = unwrapObject(obj, "notification", "Notification", "NOTIFICATION");
    //         NotificationDTO dto = fromJsonNotification(obj);
    //         if (dto != null) {
    //             r.setResultado(ENTITY_KEY, dto);
    //         }
    //     } catch (Exception ignore) {
    //     }
    // }

    // private void fillListFromMensajeInterno(Respuesta r) {
    //     String mi = r.getMensajeInterno();
    //     if (mi == null || mi.isBlank()) return;

    //     List<NotificationDTO> list = new ArrayList<>();

    //     // 1) Arreglo directo
    //     try (JsonReader jr = Json.createReader(new StringReader(mi))) {
    //         JsonArray arr = jr.readArray();
    //         for (int i = 0; i < arr.size(); i++) {
    //             if (arr.get(i).getValueType() == JsonValue.ValueType.OBJECT) {
    //                 JsonObject obj = arr.getJsonObject(i);
    //                 NotificationDTO dto = fromJsonNotification(obj);
    //                 if (dto != null) list.add(dto);
    //             }
    //         }
    //         r.setResultado(LIST_KEY, list);
    //         return;
    //     } catch (Exception ignore) {
    //     }

    //     // 2) Objeto con arreglo interno
    //     try (JsonReader jr = Json.createReader(new StringReader(mi))) {
    //         JsonObject root = jr.readObject();
    //         JsonArray arr = unwrapArray(root,
    //                 "notifications", "Notifications", "NOTIFICATIONS",
    //                 "data", "list"
    //         );
    //         if (arr != null) {
    //             for (int i = 0; i < arr.size(); i++) {
    //                 if (arr.get(i).getValueType() == JsonValue.ValueType.OBJECT) {
    //                     JsonObject obj = arr.getJsonObject(i);
    //                     NotificationDTO dto = fromJsonNotification(obj);
    //                     if (dto != null) list.add(dto);
    //                 }
    //             }
    //         }
    //     } catch (Exception ex) {
    //         LOG.log(Level.WARNING, "No se pudo parsear mensajeInterno a List<NotificationDTO>", ex);
    //     }

    //     r.setResultado(LIST_KEY, list);
    // }

    // private JsonObject unwrapObject(JsonObject obj, String... keys) {
    //     for (String k : keys) {
    //         if (obj.containsKey(k) && obj.get(k).getValueType() == JsonValue.ValueType.OBJECT) {
    //             return obj.getJsonObject(k);
    //         }
    //     }
    //     return obj;
    // }

    // private JsonArray unwrapArray(JsonObject obj, String... keys) {
    //     for (String k : keys) {
    //         if (obj.containsKey(k) && obj.get(k).getValueType() == JsonValue.ValueType.ARRAY) {
    //             return obj.getJsonArray(k);
    //         }
    //     }
    //     return null;
    // }

    // private NotificationDTO fromJsonNotification(JsonObject o) {
    //     if (o == null) return null;
    //     NotificationDTO d = new NotificationDTO();

    //     // IDs y relaciones
    //     d.setId(getLong(o, "id", "notification_id", "notificationId", "NOTIFICATION_ID"));
    //     d.setProjectId(getLong(o, "project_id", "projectId", "PROJECT_ID"));
    //     d.setActivityId(getLong(o, "activity_id", "activityId", "ACTIVITY_ID"));

    //     // Texto
    //     d.setSubject(getString(o, "subject", "SUBJECT"));
    //     String msg = getString(o, "message", "MESSAGE", "body", "BODY");
    //     d.setMessage(msg);

    //     // Estado y tipo de evento
    //     Character st = getChar(o, "status", "STATUS");
    //     d.setStatus(st);
    //     Character ev = getChar(o, "event_type", "eventType", "EVENT_TYPE");
    //     d.setEventType(ev);

    //     // Timestamp de envío
    //     d.setSentAt(getDate(o, "sent_at", "sentAt", "SENT_AT"));

    //     return d;
    // }

    // // ===================== JSON helpers =====================

    // private String getString(JsonObject obj, String... keys) {
    //     for (String k : keys) {
    //         if (obj.containsKey(k) && !obj.isNull(k)) {
    //             try {
    //                 switch (obj.get(k).getValueType()) {
    //                     case STRING:
    //                         return ((JsonString) obj.get(k)).getString();
    //                     case NUMBER:
    //                         return ((JsonNumber) obj.get(k)).toString();
    //                     case TRUE:
    //                         return "true";
    //                     case FALSE:
    //                         return "false";
    //                     default:
    //                         return obj.get(k).toString();
    //                 }
    //             } catch (Exception ignore) {
    //             }
    //         }
    //     }
    //     return null;
    // }

    // private Long getLong(JsonObject obj, String... keys) {
    //     for (String k : keys) {
    //         if (obj.containsKey(k) && !obj.isNull(k)) {
    //             try {
    //                 JsonValue v = obj.get(k);
    //                 switch (v.getValueType()) {
    //                     case NUMBER:
    //                         return obj.getJsonNumber(k).longValue();
    //                     case STRING:
    //                         String s = obj.getString(k);
    //                         if (s != null && !s.isBlank()) return Long.parseLong(s.trim());
    //                         break;
    //                     default:
    //                         String raw = v.toString();
    //                         if (raw != null && !raw.isBlank()) return Long.parseLong(raw.replace("\"", "").trim());
    //                 }
    //             } catch (Exception ignore) {
    //             }
    //         }
    //     }
    //     return null;
    // }

    // private Character getChar(JsonObject obj, String... keys) {
    //     String s = getString(obj, keys);
    //     if (s != null) {
    //         s = s.trim();
    //         if (!s.isEmpty()) return s.charAt(0);
    //     }
    //     return null;
    // }

    // private Date getDate(JsonObject obj, String... keys) {
    //     // Primero intenta string (ISO, yyyy-MM-dd HH:mm:ss, yyyy-MM-dd)
    //     String s = getString(obj, keys);
    //     if (s != null && !s.isBlank()) {
    //         // ISO-8601
    //         try {
    //             Instant inst = Instant.parse(s);
    //             return Date.from(inst);
    //         } catch (DateTimeParseException ignore) {
    //         }
    //         // yyyy-MM-dd HH:mm:ss
    //         try {
    //             return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(s);
    //         } catch (ParseException ignore) {
    //         }
    //         // yyyy-MM-dd
    //         try {
    //             return new SimpleDateFormat("yyyy-MM-dd").parse(s);
    //         } catch (ParseException ignore) {
    //         }
    //     }
    //     // Luego intenta numérico epoch
    //     for (String k : keys) {
    //         if (obj.containsKey(k) && obj.get(k).getValueType() == JsonValue.ValueType.NUMBER) {
    //             try {
    //                 long epoch = obj.getJsonNumber(k).longValue();
    //                 if (String.valueOf(epoch).length() <= 10) {
    //                     epoch = epoch * 1000L; // seconds -> millis
    //                 }
    //                 return new Date(epoch);
    //             } catch (Exception ignore) {
    //             }
    //         }
    //     }
    //     return null;
    // }
}