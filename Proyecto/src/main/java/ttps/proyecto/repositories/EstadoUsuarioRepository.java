package ttps.proyecto.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ttps.proyecto.models.EstadoUsuario;

import java.util.Optional;

@Repository
public interface EstadoUsuarioRepository extends JpaRepository<EstadoUsuario, Long> {
    Optional<EstadoUsuario> findByNombre(String nombre);
}