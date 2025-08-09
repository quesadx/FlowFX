package cr.ac.una.flowfx.service;

import cr.ac.una.flowfx.model.ProjectActivity;
import cr.ac.una.flowfx.model.ProjectActivityDTO;
import cr.ac.una.flowfx.util.EntityManagerHelper;
import jakarta.persistence.EntityManager;

public class ProjectActivityService {
    public ProjectActivityDTO toDTO(ProjectActivity e) {
        if (e == null) return null;
        return new ProjectActivityDTO(
            e.getId(), e.getDescription(), e.getStatus(), e.getPlannedStartDate(), e.getPlannedEndDate(),
            e.getActualStartDate(), e.getActualEndDate(), e.getExecutionOrder(), e.getCreatedAt(), e.getUpdatedAt()
        );
    }
    public ProjectActivity fromDTO(ProjectActivityDTO d) {
        if (d == null) return null;
        ProjectActivity e = new ProjectActivity();
        e.setId(d.getId());
        e.setDescription(d.getDescription());
        e.setStatus(d.getStatus());
        e.setPlannedStartDate(d.getPlannedStartDate());
        e.setPlannedEndDate(d.getPlannedEndDate());
        e.setActualStartDate(d.getActualStartDate());
        e.setActualEndDate(d.getActualEndDate());
        e.setExecutionOrder(d.getExecutionOrder());
        e.setCreatedAt(d.getCreatedAt());
        e.setUpdatedAt(d.getUpdatedAt());
        return e;
    }
    public ProjectActivityDTO find(Long id) {
        EntityManager em = EntityManagerHelper.getManager();
        return toDTO(em.find(ProjectActivity.class, id));
    }
    public ProjectActivityDTO create(ProjectActivityDTO dto) {
        EntityManager em = EntityManagerHelper.getManager();
        ProjectActivity e = fromDTO(dto);
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
    public ProjectActivityDTO update(ProjectActivityDTO dto) {
        EntityManager em = EntityManagerHelper.getManager();
        ProjectActivity e = fromDTO(dto);
        em.getTransaction().begin();
        try {
            ProjectActivity merged = em.merge(e);
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
            ProjectActivity e = em.find(ProjectActivity.class, id);
            if (e != null) em.remove(e);
            em.getTransaction().commit();
        } catch (RuntimeException ex) {
            em.getTransaction().rollback();
            throw ex;
        }
    }
}
