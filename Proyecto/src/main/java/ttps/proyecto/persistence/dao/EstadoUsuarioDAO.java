package ttps.proyecto.persistence.dao;

import ttps.proyecto.models.EstadoUsuario;

public interface EstadoUsuarioDAO extends GenericDAO<EstadoUsuario> {
    EstadoUsuario recuperarPorNombre(String nombre);
}