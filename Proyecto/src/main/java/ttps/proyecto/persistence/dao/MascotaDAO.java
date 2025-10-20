package ttps.proyecto.persistence.dao;

import ttps.proyecto.models.Mascota;
import ttps.proyecto.models.enums.EstadoMascota; // Importa el Enum
import java.util.List;

public interface MascotaDAO extends GenericDAO<Mascota> {
    List<Mascota> buscarPorEstado(EstadoMascota estado);
    List<Mascota> buscarPorBarrio(String barrio);
}