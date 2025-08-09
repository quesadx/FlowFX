package cr.ac.una.flowfx.service;

import cr.ac.una.flowfx.model.Notification;
import cr.ac.una.flowfx.model.NotificationDTO;
import cr.ac.una.flowfx.util.EntityManagerHelper;
import jakarta.persistence.EntityManager;

public class NotificationService {
    public NotificationDTO toDTO(Notification e) {
        if (e == null) return null;
        return new NotificationDTO(
            e.getId(), e.getSubject(), e.getMessage(), e.getSentAt(), e.getStatus(), e.getEventType()
        );
    }
    public Notification fromDTO(NotificationDTO d) {
        if (d == null) return null;
        Notification e = new Notification();
        e.setId(d.getId());
        e.setSubject(d.getSubject());
        e.setMessage(d.getMessage());
        e.setSentAt(d.getSentAt());
        e.setStatus(d.getStatus());
        e.setEventType(d.getEventType());
        return e;
    }
    public NotificationDTO find(Long id) {
        EntityManager em = EntityManagerHelper.getManager();
        return toDTO(em.find(Notification.class, id));
    }
    public NotificationDTO create(NotificationDTO dto) {
        EntityManager em = EntityManagerHelper.getManager();
        Notification e = fromDTO(dto);
        em.getTransaction().begin();
        try {
            em.persist(e);
            em.getTransaction().commit();
            return toDTO(e);
        } catch (RuntimeException ex) {
            em.getTransaction().rollback();
            throw ex;
        }
    }
    public NotificationDTO update(NotificationDTO dto) {
        EntityManager em = EntityManagerHelper.getManager();
        Notification e = fromDTO(dto);
        em.getTransaction().begin();
        try {
            Notification merged = em.merge(e);
            em.getTransaction().commit();
            return toDTO(merged);
        } catch (RuntimeException ex) {
            em.getTransaction().rollback();
            throw ex;
        }
    }
    public void delete(Long id) {
        EntityManager em = EntityManagerHelper.getManager();
        em.getTransaction().begin();
        try {
            Notification e = em.find(Notification.class, id);
            if (e != null) em.remove(e);
            em.getTransaction().commit();
        } catch (RuntimeException ex) {
            em.getTransaction().rollback();
            throw ex;
        }
    }
}
