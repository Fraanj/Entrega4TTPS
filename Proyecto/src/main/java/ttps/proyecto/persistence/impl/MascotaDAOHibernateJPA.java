package ttps.proyecto.persistence.impl;

import ttps.proyecto.models.Mascota;
import ttps.proyecto.models.enums.EstadoMascota;
import ttps.proyecto.persistence.dao.MascotaDAO;
import ttps.proyecto.persistence.util.EMF;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.List;

public class MascotaDAOHibernateJPA extends GenericDAOHibernateJPA<Mascota> implements MascotaDAO {

    public MascotaDAOHibernateJPA() {
        super(Mascota.class);
    }

    @Override
    public List<Mascota> buscarPorEstado(EstadoMascota estado) {
        EntityManager em = EMF.getEMF().createEntityManager();
        try {
            // La consulta JPQL ahora compara directamente el campo Enum
            Query query = em.createQuery("SELECT m FROM Mascota m WHERE m.estado = :estado");
            query.setParameter("estado", estado);
            return (List<Mascota>) query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Mascota> buscarPorBarrio(String barrio) {
        EntityManager em = EMF.getEMF().createEntityManager();
        try {
            Query query = em.createQuery("SELECT m FROM Mascota m WHERE m.ultimaUbicacion.barrio = :barrio");
            query.setParameter("barrio", barrio);
            return (List<Mascota>) query.getResultList();
        } finally {
            em.close();
        }
    }
}