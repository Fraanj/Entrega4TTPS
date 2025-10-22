package ttps.proyecto.persistence.impl;

import ttps.proyecto.models.Avistamiento;
import ttps.proyecto.persistence.dao.AvistamientoDAO;
import ttps.proyecto.persistence.util.EMF;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

/**
 * Implementación JPA/Hibernate para AvistamientoDAO.
 * Se sobrescribe recuperarPorId para inicializar relaciones importantes (mascota, reportador, ubicacion, foto)
 * y así evitar LazyInitializationException en tests que consultan estas propiedades fuera del EntityManager.
 */
public class AvistamientoDAOHibernateJPA extends GenericDAOHibernateJPA<Avistamiento> implements AvistamientoDAO {
    public AvistamientoDAOHibernateJPA() {
        super(Avistamiento.class);
    }

    @Override
    public Avistamiento recuperarPorId(Long id) {
        EntityManager em = EMF.getEMF().createEntityManager();
        try {
            TypedQuery<Avistamiento> q = em.createQuery(
                    "SELECT a FROM Avistamiento a " +
                    "LEFT JOIN FETCH a.mascota m " +
                    "LEFT JOIN FETCH a.reportador r " +
                    "LEFT JOIN FETCH a.ubicacion u " +
                    "LEFT JOIN FETCH a.foto f " +
                    "WHERE a.id = :id", Avistamiento.class);
            q.setParameter("id", id);
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }
}