package ttps.proyecto.services;

import org.springframework.beans.factory.annotation.Autowired;
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

import java.time.LocalDate;
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
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        TamanioMascota tamanio = tamanioRepository.findByNombre(dto.getTamanioNombre())
            .orElseThrow(() -> new RuntimeException("Tamaño no encontrado"));

        Mascota mascota = new Mascota();
        mascota.setNombre(dto.getNombre());
        mascota.setColor(dto.getColor());
        mascota.setDescripcion(dto.getDescripcion());
        mascota.setEstado(dto.getEstado());
        mascota.setFechaPublicacion(LocalDate.now());
        mascota.setPublicador(publicador);
        mascota.setTamanio(tamanio);

        // Lógica GeoRef (Requisito del TP)
        if (dto.getUbicacion() != null) {
            Ubicacion ubicacion = new Ubicacion();
            Double lat = dto.getUbicacion().getLatitud();
            Double lon = dto.getUbicacion().getLongitud();
            
            ubicacion.setLatitud(lat);
            ubicacion.setLongitud(lon);

            // LLAMADA A API EXTERNA
            try {
                String url = String.format("https://apis.datos.gob.ar/georef/api/ubicacion?lat=%s&lon=%s", lat, lon);
                // Mapeamos la respuesta a un objeto auxiliar o un Map (por simplicidad usamos Map)
                System.out.println("LLAMADA A GEOREF");
                Map<String, Object> response = restTemplate.getForObject(url, Map.class);
                System.out.println("FINALIZO LLAMADA A GEOREF");
                
                if (response != null && response.containsKey("ubicacion")) {
                    Map<String, Object> ubiData = (Map<String, Object>) response.get("ubicacion");
                    
                    // Obtenemos municipio o departamento según corresponda
                    Map<String, Object> municipio = (Map<String, Object>) ubiData.get("municipio");
                    Map<String, Object> provincia = (Map<String, Object>) ubiData.get("provincia");
                    
                    String nombreBarrio = municipio.get("nombre") != null ? (String) municipio.get("nombre") : (String) provincia.get("nombre");
                    ubicacion.setBarrio(nombreBarrio); // Guardamos el dato real obtenido de la API
                } else {
                    ubicacion.setBarrio("Desconocido");
                }
            } catch (Exception e) {
                System.out.println("Error conectando con GeoRef: " + e.getMessage());
                ubicacion.setBarrio("Ubicación Manual"); // Fallback
            }

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
            .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));

        mascota.setNombre(dto.getNombre());
        mascota.setColor(dto.getColor());
        mascota.setDescripcion(dto.getDescripcion());
        mascota.setEstado(dto.getEstado());

        if (dto.getTamanioNombre() != null) {
            TamanioMascota tamanio = tamanioRepository.findByNombre(dto.getTamanioNombre())
                .orElseThrow(() -> new RuntimeException("Tamaño no encontrado"));
            mascota.setTamanio(tamanio);
        }

        if (dto.getUbicacion() != null) {
            if (mascota.getUltimaUbicacion() == null) {
                mascota.setUltimaUbicacion(new Ubicacion());
            }
            mascota.getUltimaUbicacion().setBarrio(dto.getUbicacion().getBarrio());
            mascota.getUltimaUbicacion().setLatitud(dto.getUbicacion().getLatitud());
            mascota.getUltimaUbicacion().setLongitud(dto.getUbicacion().getLongitud());
        }

        Mascota updated = mascotaRepository.save(mascota);
        return convertToDto(updated);
    }

    public void eliminar(Long id) {
        if (!mascotaRepository.existsById(id)) {
            throw new RuntimeException("Mascota no encontrada");
        }
        mascotaRepository.deleteById(id);
    }

    public MascotaDto obtenerPorId(Long id) {
        Mascota mascota = mascotaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));
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
}