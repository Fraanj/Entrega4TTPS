package ttps.proyecto.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ttps.proyecto.models.Mascota;
import ttps.proyecto.models.enums.EstadoMascota;

import java.util.List;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Long> {
    List<Mascota> findByEstado(EstadoMascota estado);
    List<Mascota> findByPublicadorId(Long publicadorId);
    List<Mascota> findByEstadoIn(List<EstadoMascota> estados);
}
