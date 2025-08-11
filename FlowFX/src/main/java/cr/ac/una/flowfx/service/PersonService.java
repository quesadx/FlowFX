package cr.ac.una.flowfx.service;

import cr.ac.una.flowfx.model.Person;
import cr.ac.una.flowfx.model.PersonDTO;
import cr.ac.una.flowfx.util.EntityManagerHelper;
import cr.ac.una.flowfx.util.Respuesta;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import java.util.ArrayList;
import java.util.List;

public class PersonService {

    private final EntityManager em = EntityManagerHelper.getManager();
    private EntityTransaction et;

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

    public Respuesta find(Long id) {
        try {
            Person entity = em.find(Person.class, id);
            if (entity == null) {
                return new Respuesta(false, "Persona no encontrada", "find NotFound");
            }
            return new Respuesta(true, "Persona encontrada", "find success", "Person", toDTO(entity));
        } catch (Exception ex) {
            return new Respuesta(false, "Error obteniendo la persona", "find " + ex.getMessage());
        }
    }

    public Respuesta create(PersonDTO dto) {
        try {
            et = em.getTransaction();
            et.begin();
            Person entity = fromDTO(dto);
            em.persist(entity);
            et.commit();
            return new Respuesta(true, "Goofy ahh mensaje: persona creada!", "create success", "Person", toDTO(entity));
        } catch (Exception ex) {
            if (et != null && et.isActive()) et.rollback();
            return new Respuesta(false, "Error creando la persona", "create " + ex.getMessage());
        }
    }

    public Respuesta update(PersonDTO dto) {
        try {
            et = em.getTransaction();
            et.begin();
            Person current = em.find(Person.class, dto.getId());
            if (current == null) {
                et.rollback();
                return new Respuesta(false, "Persona no encontrada para actualizar", "update NotFound");
            }
            current.setFirstName(dto.getFirstName());
            current.setLastName(dto.getLastName());
            current.setEmail(dto.getEmail());
            current.setUsername(dto.getUsername());
            current.setPassword(dto.getPassword());
            current.setStatus(dto.getStatus());
            current.setIsAdmin(dto.getIsAdmin());
            Person merged = em.merge(current);
            et.commit();
            return new Respuesta(true, "Persona actualizada", "update success", "Person", toDTO(merged));
        } catch (Exception ex) {
            if (et != null && et.isActive()) et.rollback();
            return new Respuesta(false, "Error actualizando la persona", "update " + ex.getMessage());
        }
    }

    public Respuesta delete(Long id) {
        try {
            et = em.getTransaction();
            et.begin();
            Person entity = em.find(Person.class, id);
            if (entity == null) {
                et.rollback();
                return new Respuesta(false, "Persona no encontrada", "delete NotFound");
            }
            em.remove(entity);
            et.commit();
            return new Respuesta(true, "Persona eliminada", "delete success");
        } catch (Exception ex) {
            if (et != null && et.isActive()) et.rollback();
            return new Respuesta(false, "Error eliminando la persona", "delete " + ex.getMessage());
        }
    }

    public Respuesta validateCredentials(String username, String password) {
        try {
            Query qry = em.createQuery(
                "SELECT p FROM Person p WHERE p.username = :username AND p.password = :password", Person.class);
            qry.setParameter("username", username);
            qry.setParameter("password", password);
            Person found = (Person) qry.getSingleResult();
            return new Respuesta(true, "Login exitoso", "validateCredentials success", "Person", toDTO(found));
        } catch (NoResultException ex) {
            return new Respuesta(false, "Credenciales inválidas", "validateCredentials NoResult");
        } catch (Exception ex) {
            return new Respuesta(false, "Error validando credenciales", "validateCredentials " + ex.getMessage());
        }
    }

    public Respuesta findAll() {
        try {
            Query q = em.createNamedQuery("Person.findAll", Person.class);
            @SuppressWarnings("unchecked")
            List<Person> entities = q.getResultList();
            List<PersonDTO> dtos = new ArrayList<>();
            for (Person e : entities) dtos.add(toDTO(e));
            return new Respuesta(true, "Personas encontradas", "findAll success", "Persons", dtos);
        } catch (Exception ex) {
            return new Respuesta(false, "Error obteniendo las personas", "findAll " + ex.getMessage());
        }
    }
}
