package cr.ac.una.flowfx.service;

import cr.ac.una.flowfx.model.Person;
import cr.ac.una.flowfx.model.PersonDTO;
import cr.ac.una.flowfx.util.EntityManagerHelper;
import jakarta.persistence.EntityManager;

public class PersonService {

    public PersonDTO toDTO(Person entity) {
        if (entity == null) return null;
        return new PersonDTO(
            entity.getId(),
            entity.getFirstName(),
            entity.getLastName(),
            entity.getEmail(),
            entity.getUsername(),
            entity.getPassword(),
            entity.getStatus(),
            entity.getIsAdmin()
        );
    }

    public Person fromDTO(PersonDTO dto) {
        if (dto == null) return null;
        Person p = new Person();
        p.setId(dto.getId());
        p.setFirstName(dto.getFirstName());
        p.setLastName(dto.getLastName());
        p.setEmail(dto.getEmail());
        p.setUsername(dto.getUsername());
        p.setPassword(dto.getPassword());
        p.setStatus(dto.getStatus());
        p.setIsAdmin(dto.getIsAdmin());
        return p;
    }

    public PersonDTO find(Long id) {
        EntityManager em = EntityManagerHelper.getManager();
        Person entity = em.find(Person.class, id);
        return toDTO(entity);
    }

    public PersonDTO create(PersonDTO dto) {
        EntityManager em = EntityManagerHelper.getManager();
        Person entity = fromDTO(dto);
        em.getTransaction().begin();
        try {
            em.persist(entity);
            em.getTransaction().commit();
            return toDTO(entity);
        } catch (RuntimeException ex) {
            em.getTransaction().rollback();
            throw ex;
        }
    }

    public PersonDTO update(PersonDTO dto) {
        EntityManager em = EntityManagerHelper.getManager();
        Person entity = fromDTO(dto);
        em.getTransaction().begin();
        try {
            Person merged = em.merge(entity);
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
            Person entity = em.find(Person.class, id);
            if (entity != null) {
                em.remove(entity);
            }
            em.getTransaction().commit();
        } catch (RuntimeException ex) {
            em.getTransaction().rollback();
            throw ex;
        }
    }
}
