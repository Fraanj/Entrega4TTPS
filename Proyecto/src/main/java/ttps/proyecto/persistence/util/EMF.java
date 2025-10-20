package ttps.proyecto.persistence.util;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceException;

/**
 * Singleton para el EntityManagerFactory (idéntico a Teoria 4.4, pag 7).
 */
public class EMF {
    private static EntityManagerFactory emf;
    static {
        try {
            emf = Persistence.createEntityManagerFactory("lostpets-pu");
        } catch (PersistenceException e) {
            System.err.println("Error al crear EntityManagerFactory: " + e.getMessage());
            throw new RuntimeException("Error inicializando EMF", e);
        }
    }
    public static EntityManagerFactory getEMF() {
        return emf;
    }
    private EMF() {}
}