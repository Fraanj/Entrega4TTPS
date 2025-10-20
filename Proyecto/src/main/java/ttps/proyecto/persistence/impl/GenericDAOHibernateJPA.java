package ttps.proyecto.persistence.impl;

import ttps.proyecto.persistence.dao.GenericDAO;
import ttps.proyecto.persistence.util.EMF;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;
import java.util.List;

/**
 * Implementación genérica de los métodos C.R.U.D. (idéntica a Teoria 4.4, pag 5).
 * @param <T> El tipo de la entidad.
 */
public abstract class GenericDAOHibernateJPA<T> implements GenericDAO<T> {

    protected Class<T> persistentClass;

    public GenericDAOHibernateJPA(Class<T> clase) {
        this.persistentClass = clase;
    }

    public Class<T> getPersistentClass() {
        return persistentClass;
    }

    @Override
    public void persistir(T entity) {
        EntityManager em = EMF.getEMF().createEntityManager();
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            em.persist(entity);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public T actualizar(T entity) {
        EntityManager em = EMF.getEMF().createEntityManager();
        EntityTransaction tx = null;
        T entityMerged = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            entityMerged = em.merge(entity);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
        return entityMerged;
    }

    @Override
    public T recuperarPorId(Long id) {
        EntityManager em = EMF.getEMF().createEntityManager();
        try {
            return em.find(this.getPersistentClass(), id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<T> recuperarTodos() {
        EntityManager em = EMF.getEMF().createEntityManager();
        try {
            Query consulta = em.createQuery("SELECT e FROM " +
                    this.getPersistentClass().getSimpleName() + " e");
            return (List<T>) consulta.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void eliminar(Long id) {
        T entity = this.recuperarPorId(id);
        if (entity != null) {
            this.eliminar(entity);
        }
    }

    @Override
    public void eliminar(T entity) {
        EntityManager em = EMF.getEMF().createEntityManager();
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            em.remove(em.merge(entity));
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}