package ttps.proyecto.persistence.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import ttps.proyecto.models.Medalla;
import ttps.proyecto.models.Usuario;
import ttps.proyecto.persistence.dao.MedallaDAO;
import ttps.proyecto.persistence.util.EMF;

import java.util.HashSet;
import java.util.Set;

public class MedallaDAOHibernateJPA extends GenericDAOHibernateJPA<Medalla> implements MedallaDAO {
    public MedallaDAOHibernateJPA() {
        super(Medalla.class);
    }
    @Override
    public Set<Medalla> getMedallasByUsuario(Usuario usuario) {
        EntityManager em = EMF.getEMF().createEntityManager();
        Set<Medalla> medallas = new HashSet<>();
        try{
            TypedQuery<Medalla> query = em.createQuery(
                    "SELECT m FROM Medalla m WHERE :usuario MEMBER OF m.usuarios", Medalla.class
            );
            query.setParameter("usuario", usuario);
            medallas = new HashSet<>(query.getResultList());
        }
        catch (Exception ex){
            throw new PersistenceException(ex.getMessage(), ex);
        }
        finally {
            em.close();
        }
        return medallas;
    }
}
