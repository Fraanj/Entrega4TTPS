package ttps.proyecto.persistence.dao;

import ttps.proyecto.models.Rol;

public interface RolDAO extends GenericDAO<Rol> {
    Rol recuperarPorNombre(String nombre);
}