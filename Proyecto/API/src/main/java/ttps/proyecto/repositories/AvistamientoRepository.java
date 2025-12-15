package ttps.proyecto.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ttps.proyecto.models.Avistamiento;

import java.util.List;
import java.util.Optional;

@Repository
public interface AvistamientoRepository extends JpaRepository<Avistamiento, Long> {
    
    List<Avistamiento> findByMascotaId(Long mascotaId);
    
    @Query("SELECT a FROM Avistamiento a " +
           "LEFT JOIN FETCH a.mascota " +
           "LEFT JOIN FETCH a.reportador " +
           "LEFT JOIN FETCH a.ubicacion " +
           "WHERE a.id = :id")
    Optional<Avistamiento> findByIdWithRelations(@Param("id") Long id);
}