package ttps.proyecto.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ttps.proyecto.dto.AvistamientoDto;
import ttps.proyecto.dto.UbicacionDto;
import ttps.proyecto.models.Avistamiento;
import ttps.proyecto.models.Mascota;
import ttps.proyecto.models.Ubicacion;
import ttps.proyecto.models.Usuario;
import ttps.proyecto.repositories.AvistamientoRepository;
import ttps.proyecto.repositories.MascotaRepository;
import ttps.proyecto.repositories.UsuarioRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AvistamientoService {

    @Autowired
    private AvistamientoRepository avistamientoRepository;
    
    @Autowired
    private MascotaRepository mascotaRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    public AvistamientoDto crear(AvistamientoDto dto, Long reportadorId) {
        if(dto.getComentario() == null || dto.getComentario().trim().isEmpty()) {
            throw new RuntimeException("El comentario es obligatorio");
        }
                
        if (dto.getMascotaId() == null) {
            throw new RuntimeException("La mascota es obligatoria");
        }

        Usuario reportador = usuarioRepository.findById(reportadorId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Mascota mascota = mascotaRepository.findById(dto.getMascotaId())
            .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));

        Avistamiento avistamiento = new Avistamiento();
        avistamiento.setFecha(dto.getFecha() != null ? dto.getFecha() : LocalDate.now());
        avistamiento.setComentario(dto.getComentario());
        avistamiento.setReportador(reportador);
        avistamiento.setMascota(mascota);

        if (dto.getUbicacion() != null) {
            Ubicacion ubicacion = new Ubicacion();
            ubicacion.setBarrio(dto.getUbicacion().getBarrio());
            ubicacion.setLatitud(dto.getUbicacion().getLatitud());
            ubicacion.setLongitud(dto.getUbicacion().getLongitud());
            avistamiento.setUbicacion(ubicacion);
        }

        Avistamiento saved = avistamientoRepository.save(avistamiento);
        return convertToDto(saved);
    }

    public List<AvistamientoDto> listarTodos() {
        return avistamientoRepository.findAll().stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    public List<AvistamientoDto> listarPorMascota(Long mascotaId) {
        return avistamientoRepository.findByMascotaId(mascotaId).stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    public AvistamientoDto obtenerPorId(Long id) {
        Avistamiento avistamiento = avistamientoRepository.findByIdWithRelations(id)
            .orElseThrow(() -> new RuntimeException("Avistamiento no encontrado"));
        return convertToDto(avistamiento);
    }

    private AvistamientoDto convertToDto(Avistamiento avistamiento) {
        AvistamientoDto dto = new AvistamientoDto();
        dto.setId(avistamiento.getId());
        dto.setFecha(avistamiento.getFecha());
        dto.setComentario(avistamiento.getComentario());
        
        if (avistamiento.getMascota() != null) {
            dto.setMascotaId(avistamiento.getMascota().getId());
            dto.setMascotaNombre(avistamiento.getMascota().getNombre());
        }
        
        if (avistamiento.getReportador() != null) {
            dto.setReportadorId(avistamiento.getReportador().getId());
            dto.setReportadorNombre(avistamiento.getReportador().getNombre() + " " + avistamiento.getReportador().getApellido());
        }
        
        if (avistamiento.getUbicacion() != null) {
            UbicacionDto ubicacionDto = new UbicacionDto();
            ubicacionDto.setBarrio(avistamiento.getUbicacion().getBarrio());
            ubicacionDto.setLatitud(avistamiento.getUbicacion().getLatitud());
            ubicacionDto.setLongitud(avistamiento.getUbicacion().getLongitud());
            dto.setUbicacion(ubicacionDto);
        }
        
        return dto;
    }
}