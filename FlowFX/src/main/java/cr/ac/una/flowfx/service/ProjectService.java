package cr.ac.una.flowfx.service;

import cr.ac.una.flowfx.model.Project;
import cr.ac.una.flowfx.model.ProjectDTO;
import cr.ac.una.flowfx.util.EntityManagerHelper;
import jakarta.persistence.EntityManager;

public class ProjectService {
    public ProjectDTO toDTO(Project e) {
        if (e == null) return null;
        return new ProjectDTO(
            e.getId(), e.getName(), e.getPlannedStartDate(), e.getPlannedEndDate(),
            e.getActualStartDate(), e.getActualEndDate(), e.getStatus(), e.getCreatedAt(), e.getUpdatedAt()
        );
    }
    public Project fromDTO(ProjectDTO d) {
        if (d == null) return null;
        Project e = new Project();
        e.setId(d.getId());
        e.setName(d.getName());
        e.setPlannedStartDate(d.getPlannedStartDate());
        e.setPlannedEndDate(d.getPlannedEndDate());
        e.setActualStartDate(d.getActualStartDate());
        e.setActualEndDate(d.getActualEndDate());
        e.setStatus(d.getStatus());
        e.setCreatedAt(d.getCreatedAt());
        e.setUpdatedAt(d.getUpdatedAt());
        return e;
    }
    public ProjectDTO find(Long id) {
        EntityManager em = EntityManagerHelper.getManager();
        return toDTO(em.find(Project.class, id));
    }
    public ProjectDTO create(ProjectDTO dto) {
        EntityManager em = EntityManagerHelper.getManager();
        Project e = fromDTO(dto);
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
    public ProjectDTO update(ProjectDTO dto) {
        EntityManager em = EntityManagerHelper.getManager();
        Project e = fromDTO(dto);
        em.getTransaction().begin();
        try {
            Project merged = em.merge(e);
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
            Project e = em.find(Project.class, id);
            if (e != null) em.remove(e);
            em.getTransaction().commit();
        } catch (RuntimeException ex) {
            em.getTransaction().rollback();
            throw ex;
        }
    }
}
