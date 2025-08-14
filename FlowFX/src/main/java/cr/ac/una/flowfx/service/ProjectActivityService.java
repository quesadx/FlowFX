package cr.ac.una.flowfx.service;

import cr.ac.una.flowfx.model.ProjectActivity;
import cr.ac.una.flowfx.model.ProjectActivityDTO;
import cr.ac.una.flowfx.util.EntityManagerHelper;
import cr.ac.una.flowfx.util.Respuesta;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // Dashboard helpers
    public Respuesta findRecentForUser(Long userId, int maxResults) {
        try {
            EntityManager em = EntityManagerHelper.getManager();
            Query q = em.createQuery(
                "SELECT a FROM ProjectActivity a " +
                "WHERE a.responsibleId.id = :uid OR a.createdBy.id = :uid " +
                "ORDER BY COALESCE(a.createdAt, a.plannedStartDate) DESC",
                ProjectActivity.class
            );
            q.setParameter("uid", userId);
            q.setMaxResults(maxResults);
            @SuppressWarnings("unchecked")
            List<ProjectActivity> entities = q.getResultList();
            List<ProjectActivityDTO> dtos = new ArrayList<>();
            for (ProjectActivity e : entities) dtos.add(toDTO(e));
            return new Respuesta(true, "Actividades del usuario", "findRecentForUser success", "Activities", dtos);
        } catch (Exception ex) {
            return new Respuesta(false, "Error obteniendo actividades", "findRecentForUser " + ex.getMessage());
        }
    }

    public Respuesta countByProjectIds(List<Long> projectIds) {
        try {
            if (projectIds == null || projectIds.isEmpty()) {
                return new Respuesta(true, "Sin proyectos", "countByProjectIds empty", "Counts", new HashMap<Long, Long>());
            }
            EntityManager em = EntityManagerHelper.getManager();
            Query q = em.createQuery(
                "SELECT a.projectId.id, COUNT(a) FROM ProjectActivity a WHERE a.projectId.id IN :ids GROUP BY a.projectId.id"
            );
            q.setParameter("ids", projectIds);
            @SuppressWarnings("unchecked")
            List<Object[]> rows = q.getResultList();
            Map<Long, Long> counts = new HashMap<>();
            for (Object[] row : rows) {
                Long pid = (Long) row[0];
                Long cnt = (row[1] instanceof Long) ? (Long) row[1] : ((Number) row[1]).longValue();
                counts.put(pid, cnt);
            }
            return new Respuesta(true, "Conteos por proyecto", "countByProjectIds success", "Counts", counts);
        } catch (Exception ex) {
            return new Respuesta(false, "Error contando actividades por proyecto", "countByProjectIds " + ex.getMessage());
        }
    }
}
