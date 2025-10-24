package ttps.proyecto.persistence.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import ttps.proyecto.models.Avistamiento;
import ttps.proyecto.persistence.dao.AvistamientoDAO;
import ttps.proyecto.persistence.util.EMF;

public class AvistamientoDAOHibernateJPA extends  GenericDAOHibernateJPA<Avistamiento> implements AvistamientoDAO {

    public AvistamientoDAOHibernateJPA() {
        super(Avistamiento.class);
    }
    public Avistamiento recuperarPorIdConRelaciones(Long id) {
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
        } catch (Exception ex) {
            throw new PersistenceException(ex.getMessage(), ex);
        } finally {
            em.close();
        }
    }
}
