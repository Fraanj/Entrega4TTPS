package ttps.proyecto.persistence.impl;


import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import ttps.proyecto.models.Usuario;
import ttps.proyecto.persistence.dao.UsuarioDAO;
import ttps.proyecto.persistence.util.EMF;

public class UsuarioDAOHibernateJPA  extends  GenericDAOHibernateJPA<Usuario>implements UsuarioDAO  {

    public UsuarioDAOHibernateJPA() {
        super(Usuario.class);
    }

    @Override
    public Usuario recuperarPorEmail(String email) {
        EntityManager em = EMF.getEMF().createEntityManager();
        try{
            Query query = em.createQuery("SELECT  u FROM Usuario u WHERE u.email = :email");
            query.setParameter("email", email);
            return  (Usuario) query.getSingleResult();
        }catch(Exception e){
            return null;
        }
        finally {
            em.close();
        }
    }
}
