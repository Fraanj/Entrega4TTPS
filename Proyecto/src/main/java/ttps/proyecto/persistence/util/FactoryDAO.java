package ttps.proyecto.persistence.util;

import ttps.proyecto.persistence.dao.*;
import ttps.proyecto.persistence.impl.*;

/**
 * Provee los DAOs para todas las entidades raíz y de catálogo.
 */
public class FactoryDAO {

    // DAOs de entidades de dominio
    public static UsuarioDAO getUsuarioDAO() {
        return new UsuarioDAOHibernateJPA();
    }
    public static MascotaDAO getMascotaDAO() {
        return new MascotaDAOHibernateJPA();
    }
    public static AvistamientoDAO getAvistamientoDAO() {
        return new AvistamientoDAOHibernateJPA();
    }
    public static MedallaDAO getMedallaDAO() {
        return new MedallaDAOHibernateJPA();
    }

    // DAOs de entidades de catálogo
    public static RolDAO getRolDAO() {
        return new RolDAOHibernateJPA();
    }
    public static EstadoUsuarioDAO getEstadoUsuarioDAO() {
        return new EstadoUsuarioDAOHibernateJPA();
    }
    public static TamanioMascotaDAO getTamanioMascotaDAO() {
        return new TamanioMascotaDAOHibernateJPA();
    }

    // No creamos DAOs para Foto ni Ubicacion (son parte de un Agregado)
}