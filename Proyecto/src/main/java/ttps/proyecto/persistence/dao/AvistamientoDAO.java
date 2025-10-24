package ttps.proyecto.persistence.dao;

import ttps.proyecto.models.Avistamiento;

public interface AvistamientoDAO extends GenericDAO<Avistamiento> {
    public Avistamiento recuperarPorIdConRelaciones(Long id);
}