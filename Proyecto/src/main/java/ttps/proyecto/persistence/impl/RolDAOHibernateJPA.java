package ttps.proyecto.persistence.impl;

import ttps.proyecto.models.Rol;
import ttps.proyecto.persistence.dao.RolDAO;
import ttps.proyecto.persistence.util.EMF;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;

public class RolDAOHibernateJPA extends GenericDAOHibernateJPA<Rol> implements RolDAO {

    public RolDAOHibernateJPA() {
        super(Rol.class);
    }

    @Override
    public Rol recuperarPorNombre(String nombre) {
        EntityManager em = EMF.getEMF().createEntityManager();
        try {
            Query query = em.createQuery("SELECT r FROM Rol r WHERE r.nombre = :nombre");
            query.setParameter("nombre", nombre);
            return (Rol) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }
}