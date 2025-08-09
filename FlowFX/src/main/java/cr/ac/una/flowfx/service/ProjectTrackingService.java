package cr.ac.una.flowfx.service;

import cr.ac.una.flowfx.model.ProjectTracking;
import cr.ac.una.flowfx.model.ProjectTrackingDTO;
import cr.ac.una.flowfx.util.EntityManagerHelper;
import jakarta.persistence.EntityManager;

public class ProjectTrackingService {
    public ProjectTrackingDTO toDTO(ProjectTracking e) {
        if (e == null) return null;
        return new ProjectTrackingDTO(
            e.getId(), e.getObservations(), e.getTrackingDate(), e.getProgressPercentage(), e.getCreatedAt()
        );
    }
    public ProjectTracking fromDTO(ProjectTrackingDTO d) {
        if (d == null) return null;
        ProjectTracking e = new ProjectTracking();
        e.setId(d.getId());
        e.setObservations(d.getObservations());
        e.setTrackingDate(d.getTrackingDate());
        e.setProgressPercentage(d.getProgressPercentage());
        e.setCreatedAt(d.getCreatedAt());
        return e;
    }
    public ProjectTrackingDTO find(Long id) {
        EntityManager em = EntityManagerHelper.getManager();
        return toDTO(em.find(ProjectTracking.class, id));
    }
    public ProjectTrackingDTO create(ProjectTrackingDTO dto) {
        EntityManager em = EntityManagerHelper.getManager();
        ProjectTracking e = fromDTO(dto);
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
    public ProjectTrackingDTO update(ProjectTrackingDTO dto) {
        EntityManager em = EntityManagerHelper.getManager();
        ProjectTracking e = fromDTO(dto);
        em.getTransaction().begin();
        try {
            ProjectTracking merged = em.merge(e);
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
            ProjectTracking e = em.find(ProjectTracking.class, id);
            if (e != null) em.remove(e);
            em.getTransaction().commit();
        } catch (RuntimeException ex) {
            em.getTransaction().rollback();
            throw ex;
        }
    }
}
