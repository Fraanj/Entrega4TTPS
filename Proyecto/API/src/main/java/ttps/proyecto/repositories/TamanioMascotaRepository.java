package ttps.proyecto.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ttps.proyecto.models.TamanioMascota;

import java.util.Optional;

@Repository
public interface TamanioMascotaRepository extends JpaRepository<TamanioMascota, Long> {
    Optional<TamanioMascota> findByNombre(String nombre);
}