package ttps.proyecto.persistence.impl;

import ttps.proyecto.models.EstadoUsuario;
import ttps.proyecto.persistence.dao.EstadoUsuarioDAO;
import ttps.proyecto.persistence.util.EMF;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;

public class EstadoUsuarioDAOHibernateJPA extends GenericDAOHibernateJPA<EstadoUsuario> implements EstadoUsuarioDAO {

    public EstadoUsuarioDAOHibernateJPA() {
        super(EstadoUsuario.class);
    }

    @Override
    public EstadoUsuario recuperarPorNombre(String nombre) {
        EntityManager em = EMF.getEMF().createEntityManager();
        try {
            Query query = em.createQuery("SELECT r FROM Rol r WHERE r.nombre = :nombre");
            query.setParameter("nombre", nombre);
            return (EstadoUsuario) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }
}