package cr.ac.una.flowfx.service;

import cr.ac.una.flowfx.model.NotificationRecipient;
import cr.ac.una.flowfx.model.NotificationRecipientDTO;
import cr.ac.una.flowfx.util.EntityManagerHelper;
import jakarta.persistence.EntityManager;

public class NotificationRecipientService {
    public NotificationRecipientDTO toDTO(NotificationRecipient e) {
        if (e == null) return null;
        return new NotificationRecipientDTO(
            e.getNotificationRecipientPK(), e.getName(), e.getRole()
        );
    }
    public NotificationRecipient fromDTO(NotificationRecipientDTO d) {
        if (d == null) return null;
        NotificationRecipient e = new NotificationRecipient();
        e.setNotificationRecipientPK(d.getNotificationRecipientPK());
        e.setName(d.getName());
        e.setRole(d.getRole());
        return e;
    }
    public NotificationRecipientDTO find(Long id, String email) {
        EntityManager em = EntityManagerHelper.getManager();
        NotificationRecipient pkLookup = em.find(NotificationRecipient.class, new cr.ac.una.flowfx.model.NotificationRecipientPK(id, email));
        return toDTO(pkLookup);
    }
    public NotificationRecipientDTO create(NotificationRecipientDTO dto) {
        EntityManager em = EntityManagerHelper.getManager();
        NotificationRecipient e = fromDTO(dto);
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
    public NotificationRecipientDTO update(NotificationRecipientDTO dto) {
        EntityManager em = EntityManagerHelper.getManager();
        NotificationRecipient e = fromDTO(dto);
        em.getTransaction().begin();
        try {
            NotificationRecipient merged = em.merge(e);
            em.getTransaction().commit();
            return toDTO(merged);
        } catch (RuntimeException ex) {
            em.getTransaction().rollback();
            throw ex;
        }
    }
    public void delete(Long id, String email) {
        EntityManager em = EntityManagerHelper.getManager();
        em.getTransaction().begin();
        try {
            NotificationRecipient e = em.find(NotificationRecipient.class, new cr.ac.una.flowfx.model.NotificationRecipientPK(id, email));
            if (e != null) em.remove(e);
            em.getTransaction().commit();
        } catch (RuntimeException ex) {
            em.getTransaction().rollback();
            throw ex;
        }
    }
}
