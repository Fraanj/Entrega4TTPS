package ttps.proyecto.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.client.RestTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ttps.proyecto.dto.MascotaDto;
import ttps.proyecto.dto.UbicacionDto;
import ttps.proyecto.models.Mascota;
import ttps.proyecto.models.TamanioMascota;
import ttps.proyecto.models.Ubicacion;
import ttps.proyecto.models.Usuario;
import ttps.proyecto.models.Foto;
import ttps.proyecto.models.enums.EstadoMascota;
import ttps.proyecto.repositories.MascotaRepository;
import ttps.proyecto.repositories.TamanioMascotaRepository;
import ttps.proyecto.repositories.UsuarioRepository;
import ttps.proyecto.exceptions.BadRequestException;
import ttps.proyecto.exceptions.ResourceNotFoundException;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class MascotaService {

    @Autowired
    private RestTemplate restTemplate; // Inyectamos el cliente HTTP

    @Autowired
    private MascotaRepository mascotaRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private TamanioMascotaRepository tamanioRepository;

    public MascotaDto crear(MascotaDto dto, Long publicadorId) {
        Usuario publicador = usuarioRepository.findById(publicadorId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        TamanioMascota tamanio = tamanioRepository.findByNombre(dto.getTamanioNombre())
            .orElseThrow(() -> new BadRequestException("Tamaño no encontrado"));

        Mascota mascota = new Mascota();
        mascota.setNombre(dto.getNombre());
        mascota.setColor(dto.getColor());
        mascota.setDescripcion(dto.getDescripcion());
        mascota.setEstado(dto.getEstado());
        mascota.setFechaPublicacion(LocalDate.now());
        mascota.setPublicador(publicador);
        mascota.setTamanio(tamanio);

        // Lógica GeoRef (Requisito del TP)
        if (dto.getUbicacion() != null && dto.getUbicacion().getLatitud() != null && dto.getUbicacion().getLongitud() != null) {
            Ubicacion ubicacion = new Ubicacion();
            Double lat = dto.getUbicacion().getLatitud();
            Double lon = dto.getUbicacion().getLongitud();

            ubicacion.setLatitud(lat);
            ubicacion.setLongitud(lon);
            ubicacion.setBarrio(obtenerBarrioDesdeGeoref(lat, lon));

            mascota.setUltimaUbicacion(ubicacion);
        }
        
        // Manejo de Fotos
        if (dto.getFotos() != null && !dto.getFotos().isEmpty()) {
            for (String url : dto.getFotos()) {
                Foto foto = new Foto();
                foto.setUrl(url); // Aquí guardamos la URL o el Base64
                mascota.addFoto(foto); // Usamos el helper method de la entidad
            }
        }

        Mascota saved = mascotaRepository.save(mascota);
        return convertToDto(saved);
    }

    public MascotaDto actualizar(Long id, MascotaDto dto) {
        Mascota mascota = mascotaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Mascota no encontrada"));

        mascota.setNombre(dto.getNombre());
        mascota.setColor(dto.getColor());
        mascota.setDescripcion(dto.getDescripcion());
        mascota.setEstado(dto.getEstado());

        if (dto.getTamanioNombre() != null) {
            TamanioMascota tamanio = tamanioRepository.findByNombre(dto.getTamanioNombre())
                .orElseThrow(() -> new BadRequestException("Tamaño no encontrado"));
            mascota.setTamanio(tamanio);
        }

        // Actualizar ubicación: llamar a Georef para resolver barrio (el frontend envía barrio vacío)
        if (dto.getUbicacion() != null && dto.getUbicacion().getLatitud() != null && dto.getUbicacion().getLongitud() != null) {
            Double lat = dto.getUbicacion().getLatitud();
            Double lon = dto.getUbicacion().getLongitud();

            if (mascota.getUltimaUbicacion() == null) {
                mascota.setUltimaUbicacion(new Ubicacion());
            }
            mascota.getUltimaUbicacion().setLatitud(lat);
            mascota.getUltimaUbicacion().setLongitud(lon);
            mascota.getUltimaUbicacion().setBarrio(obtenerBarrioDesdeGeoref(lat, lon));
        }

        Mascota updated = mascotaRepository.save(mascota);
        return convertToDto(updated);
    }

    public void eliminar(Long id) {
        if (!mascotaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Mascota no encontrada");
        }
        mascotaRepository.deleteById(id);
    }

    public MascotaDto obtenerPorId(Long id) {
        Mascota mascota = mascotaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Mascota no encontrada"));
        return convertToDto(mascota);
    }

    public List<MascotaDto> obtenerPorUsuario(Long usuarioId) {
        return mascotaRepository.findByPublicadorId(usuarioId).stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    //Perdidas
    public List<MascotaDto> obtenerMascotasPerdidas() {
        List<EstadoMascota> estadosPerdidos = Arrays.asList(
            EstadoMascota.PERDIDO_PROPIO, 
            EstadoMascota.PERDIDO_AJENO
        );
        return mascotaRepository.findByEstadoIn(estadosPerdidos).stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    //recuperadas
    public List<MascotaDto> obtenerMascotasRecuperadas() {
        return mascotaRepository.findByEstadoIn(Arrays.asList(EstadoMascota.RECUPERADO)).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    //adoptadas
    public List<MascotaDto> obtenerMascotasAdoptadas() {
        return mascotaRepository.findByEstadoIn(Arrays.asList(EstadoMascota.ADOPTADO)).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    public List<MascotaDto> obtenerTodas() {
        return mascotaRepository.findAll().stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    /**
     * Obtiene el nombre del barrio/ciudad a partir de coordenadas usando la API Georef.
     * Usado tanto en crear() como en actualizar() para resolver la ubicación.
     */
    private String obtenerBarrioDesdeGeoref(Double lat, Double lon) {
        try {
            String url = String.format("https://apis.datos.gob.ar/georef/api/ubicacion?lat=%s&lon=%s", lat, lon);
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("ubicacion")) {
                Map<String, Object> ubiData = (Map<String, Object>) response.get("ubicacion");
                Map<String, Object> municipio = (Map<String, Object>) ubiData.get("municipio");
                Map<String, Object> provincia = (Map<String, Object>) ubiData.get("provincia");

                String nombreBarrio = municipio != null && municipio.get("nombre") != null
                        ? (String) municipio.get("nombre")
                        : (provincia != null && provincia.get("nombre") != null ? (String) provincia.get("nombre") : "Desconocido");
                return nombreBarrio;
            }
            return "Desconocido";
        } catch (Exception e) {
            return "Ubicación Manual";
        }
    }

    private MascotaDto convertToDto(Mascota mascota) {
        MascotaDto dto = new MascotaDto();
        dto.setId(mascota.getId());
        dto.setNombre(mascota.getNombre());
        dto.setColor(mascota.getColor());
        dto.setDescripcion(mascota.getDescripcion());
        dto.setFechaPublicacion(mascota.getFechaPublicacion());
        dto.setEstado(mascota.getEstado());
        
        if (mascota.getTamanio() != null) {
            dto.setTamanioNombre(mascota.getTamanio().getNombre());
        }
        
        if (mascota.getPublicador() != null) {
            dto.setPublicadorId(mascota.getPublicador().getId());
            dto.setPublicadorNombre(mascota.getPublicador().getNombre() + " " + mascota.getPublicador().getApellido());
        }
        
        if (mascota.getUltimaUbicacion() != null) {
            UbicacionDto ubicacionDto = new UbicacionDto();
            ubicacionDto.setBarrio(mascota.getUltimaUbicacion().getBarrio());
            ubicacionDto.setLatitud(mascota.getUltimaUbicacion().getLatitud());
            ubicacionDto.setLongitud(mascota.getUltimaUbicacion().getLongitud());
            dto.setUbicacion(ubicacionDto);
        }

        dto.setFotos(mascota.getFotos().stream()
            .map(Foto::getUrl) 
            .collect(Collectors.toList()));
        
        return dto;
    }

    public List<MascotaDto> obtenerMisMascotas(Long usuarioId) {
        
        return mascotaRepository.findByPublicadorId(usuarioId).stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    public List<MascotaDto> buscarConFiltros(String color, String tamanio, String estado) {
        Specification<Mascota> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filtro por color (case-insensitive, contiene)
            if (color != null && !color.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("color")), 
                    "%" + color.toLowerCase() + "%"
                ));
            }

            // Filtro por tamaño
            if (tamanio != null && !tamanio.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(
                    root.get("tamanio").get("nombre"), 
                    tamanio.toUpperCase()
                ));
            }

            // Filtro por estado
            if (estado != null && !estado.trim().isEmpty()) {
                try {
                    EstadoMascota estadoEnum = EstadoMascota.valueOf(estado.toUpperCase());
                    predicates.add(criteriaBuilder.equal(root.get("estado"), estadoEnum));
                } catch (IllegalArgumentException e) {
                    // Si el estado no es válido, ignorar este filtro
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return mascotaRepository.findAll(spec).stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
}