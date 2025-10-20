package ttps.proyecto.persistence.impl;

import ttps.proyecto.models.TamanioMascota;
import ttps.proyecto.persistence.dao.TamanioMascotaDAO;
import ttps.proyecto.persistence.util.EMF;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;

public class TamanioMascotaDAOHibernateJPA extends GenericDAOHibernateJPA<TamanioMascota> implements TamanioMascotaDAO {

    public TamanioMascotaDAOHibernateJPA() {
        super(TamanioMascota.class);
    }

    /**
     * Implementación de método específico (idéntico a Teoria 4.4, pag 6)
     */
    @Override
    public TamanioMascota recuperarPorNombre(String nombre) {
        EntityManager em = EMF.getEMF().createEntityManager();
        try {
            Query query = em.createQuery("SELECT e FROM TamanioMascota e WHERE e.nombre = :nombre");
            query.setParameter("nombre", nombre);
            return (TamanioMascota) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }
}