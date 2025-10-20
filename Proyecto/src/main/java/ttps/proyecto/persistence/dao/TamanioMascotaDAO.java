package ttps.proyecto.persistence.dao;

import ttps.proyecto.models.TamanioMascota;

public interface TamanioMascotaDAO extends GenericDAO<TamanioMascota> {
    TamanioMascota recuperarPorNombre(String nombre);
}