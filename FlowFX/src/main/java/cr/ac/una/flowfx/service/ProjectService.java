package cr.ac.una.flowfx.service;

import cr.ac.una.flowfx.model.Person;
import cr.ac.una.flowfx.model.Project;
import cr.ac.una.flowfx.model.ProjectDTO;
import cr.ac.una.flowfx.util.EntityManagerHelper;
import cr.ac.una.flowfx.util.Respuesta;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProjectService {

    private final EntityManager em = EntityManagerHelper.getManager();
    private EntityTransaction et;

    public ProjectDTO toDTO(Project e) {
        if (e == null) return null;
        return new ProjectDTO(
            e.getId(), e.getName(), e.getPlannedStartDate(), e.getPlannedEndDate(),
            e.getActualStartDate(), e.getActualEndDate(), e.getStatus(), e.getCreatedAt(), e.getUpdatedAt(),
            e.getLeaderUserId() != null ? e.getLeaderUserId().getId() : null,
            e.getTechLeaderId() != null ? e.getTechLeaderId().getId() : null,
            e.getSponsorId() != null ? e.getSponsorId().getId() : null
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

    public Respuesta find(Long id) {
        try {
            Project e = em.find(Project.class, id);
            if (e == null) return new Respuesta(false, "Proyecto no encontrado", "find NotFound");
            return new Respuesta(true, "Proyecto encontrado", "find success", "Project", toDTO(e));
        } catch (Exception ex) {
            return new Respuesta(false, "Error obteniendo el proyecto", "find " + ex.getMessage());
        }
    }

    public Respuesta findProjectsForUser(Long userId) {
        try {
            Query q = em.createQuery(
                "SELECT p FROM Project p " +
                "WHERE p.leaderUserId.id = :uid OR p.techLeaderId.id = :uid OR p.sponsorId.id = :uid " +
                "ORDER BY p.createdAt DESC",
                Project.class
            );
            q.setParameter("uid", userId);
            @SuppressWarnings("unchecked")
            List<Project> entities = q.getResultList();
            List<ProjectDTO> dtos = new ArrayList<>();
            for (Project e : entities) dtos.add(toDTO(e));
            return new Respuesta(true, "Proyectos del usuario", "findProjectsForUser success", "Projects", dtos);
        } catch (Exception ex) {
            return new Respuesta(false, "Error obteniendo proyectos del usuario", "findProjectsForUser " + ex.getMessage());
        }
    }

    private String validateBusinessRules(ProjectDTO dto) {
        if (dto.getName() == null || dto.getName().isBlank()) return "El nombre del proyecto es requerido.";
        if (dto.getPlannedStartDate() == null) return "La fecha de inicio planificada es requerida.";
        if (dto.getPlannedEndDate() == null) return "La fecha de fin planificada es requerida.";
        if (dto.getPlannedEndDate().before(dto.getPlannedStartDate())) return "La fecha fin planificada no puede ser anterior al inicio planificado.";
        if (dto.getActualStartDate() != null && dto.getActualEndDate() != null && dto.getActualEndDate().before(dto.getActualStartDate()))
            return "La fecha fin real no puede ser anterior al inicio real.";
        if (dto.getStatus() == null) return "El estado del proyecto es requerido.";
        char s = Character.toUpperCase(dto.getStatus());
        if (s != 'P' && s != 'R' && s != 'S' && s != 'C') return "Estado inválido. Use P,R,S o C.";
        return null;
    }

    public Respuesta create(ProjectDTO dto, Long leaderId, Long techLeaderId, Long sponsorId) {
        try {
            // defaults
            if (dto.getStatus() == null) dto.setStatus('P');
            if (dto.getCreatedAt() == null) dto.setCreatedAt(new Date());
            String v = validateBusinessRules(dto);
            if (v != null) return new Respuesta(false, v, "create Validation");

            Person leader = em.find(Person.class, leaderId);
            Person tech = em.find(Person.class, techLeaderId);
            Person sponsor = em.find(Person.class, sponsorId);
            if (leader == null) return new Respuesta(false, "Líder no existe.", "create LeaderNotFound");
            if (tech == null) return new Respuesta(false, "Líder técnico no existe.", "create TechLeaderNotFound");
            if (sponsor == null) return new Respuesta(false, "Patrocinador no existe.", "create SponsorNotFound");

            et = em.getTransaction();
            et.begin();
            Project e = fromDTO(dto);
            e.setLeaderUserId(leader);
            e.setTechLeaderId(tech);
            e.setSponsorId(sponsor);
            em.persist(e);
            em.flush(); // ID populated by sequence
            et.commit();
            return new Respuesta(true, "Proyecto creado", "create success", "Project", toDTO(e));
        } catch (Exception ex) {
            if (et != null && et.isActive()) et.rollback();
            return new Respuesta(false, "Error creando el proyecto", buildInternalMessage("create", ex));
        }
    }

    public Respuesta update(ProjectDTO dto) {
        try {
            String v = validateBusinessRules(dto);
            if (v != null) return new Respuesta(false, v, "update Validation");
            et = em.getTransaction();
            et.begin();
            Project current = em.find(Project.class, dto.getId());
            if (current == null) {
                et.rollback();
                return new Respuesta(false, "Proyecto no encontrado para actualizar", "update NotFound");
            }
            current.setName(dto.getName());
            current.setPlannedStartDate(dto.getPlannedStartDate());
            current.setPlannedEndDate(dto.getPlannedEndDate());
            current.setActualStartDate(dto.getActualStartDate());
            current.setActualEndDate(dto.getActualEndDate());
            current.setStatus(dto.getStatus());
            current.setUpdatedAt(new Date());
            Project merged = em.merge(current);
            et.commit();
            return new Respuesta(true, "Proyecto actualizado", "update success", "Project", toDTO(merged));
        } catch (Exception ex) {
            if (et != null && et.isActive()) et.rollback();
            return new Respuesta(false, "Error actualizando el proyecto", buildInternalMessage("update", ex));
        }
    }

    public Respuesta delete(Long id) {
        try {
            et = em.getTransaction();
            et.begin();
            Project e = em.find(Project.class, id);
            if (e == null) {
                et.rollback();
                return new Respuesta(false, "Proyecto no encontrado", "delete NotFound");
            }
            em.remove(e);
            et.commit();
            return new Respuesta(true, "Proyecto eliminado", "delete success");
        } catch (Exception ex) {
            if (et != null && et.isActive()) et.rollback();
            return new Respuesta(false, "Error eliminando el proyecto", buildInternalMessage("delete", ex));
        }
    }

    private String buildInternalMessage(String where, Throwable ex) {
        StringBuilder sb = new StringBuilder(where);
        sb.append(": ");
        Throwable cur = ex;
        boolean first = true;
        while (cur != null) {
            if (!first) sb.append(" | cause: ");
            sb.append(cur.getClass().getSimpleName()).append(": ").append(cur.getMessage());
            cur = cur.getCause();
            first = false;
        }
        return sb.toString();
    }
}
