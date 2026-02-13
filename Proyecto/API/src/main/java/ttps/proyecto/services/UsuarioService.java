package ttps.proyecto.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ttps.proyecto.dto.*;
import ttps.proyecto.models.EstadoUsuario;
import ttps.proyecto.models.Rol;
import ttps.proyecto.models.Usuario;
import ttps.proyecto.repositories.AvistamientoRepository;
import ttps.proyecto.repositories.EstadoUsuarioRepository;
import ttps.proyecto.repositories.RolRepository;
import ttps.proyecto.repositories.UsuarioRepository;
import ttps.proyecto.security.JwtUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private RolRepository rolRepository;
    
    @Autowired
    private EstadoUsuarioRepository estadoRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AvistamientoRepository avistamientoRepository;

    public AuthResponse registrar(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        Rol rol = rolRepository.findByNombre("USUARIO")
                .orElseThrow(() -> new RuntimeException("Rol USUARIO no encontrado"));
        EstadoUsuario estado = estadoRepository.findByNombre("HABILITADO")
                .orElseThrow(() -> new RuntimeException("Estado HABILITADO no encontrado"));

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setTelefono(request.getTelefono());
        usuario.setCiudad(request.getCiudad());
        usuario.setRol(rol);
        usuario.setEstado(estado);

        Usuario saved = usuarioRepository.save(usuario);
        UsuarioDto dto = convertToDto(saved);

        // GENERAR TOKEN JWT
        String token = jwtUtil.generateToken(
                saved.getEmail(),
                saved.getRol().getNombre(),
                saved.getId()
        );

        return new AuthResponse(token, dto);
    }

    public AuthResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        UsuarioDto dto = convertToDto(usuario);

        // GENERAR TOKEN JWT
        String token = jwtUtil.generateToken(
                usuario.getEmail(),
                usuario.getRol().getNombre(),
                usuario.getId()
        );

        return new AuthResponse(token, dto);
    }
    public UsuarioDto actualizarPerfil(Long id, UpdateUserRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setTelefono(request.getTelefono());
        usuario.setCiudad(request.getCiudad());
        
        // Solo actualizar password si se proporciona uno nuevo
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        Usuario updated = usuarioRepository.save(usuario);
        return convertToDto(updated);
    }

    public UsuarioDto obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return convertToDto(usuario);
    }

    public Long obtenerIdPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return usuario.getId();
    }

    public Rol obtenerRolPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return usuario.getRol();
    }

    private UsuarioDto convertToDto(Usuario usuario) {
        return new UsuarioDto(
            usuario.getId(),
            usuario.getNombre(),
            usuario.getApellido(),
            usuario.getEmail(),
            usuario.getTelefono(),
            usuario.getCiudad(),
            usuario.getPuntos(),
            usuario.getRol().getNombre(),
            usuario.getEstado().getNombre()
        );
    }

    public List<UsuarioRankingDto> obtenerRanking(int limit) {
        List<Object[]> resultados = avistamientoRepository.findTopReportadores();
        List<UsuarioRankingDto> ranking = new ArrayList<>();

        int count = 0;
        for (Object[] resultado : resultados) {
            if (count >= limit) break;
            
            Long usuarioId = (Long) resultado[0];
            Long cantidadReportes = (Long) resultado[1];
            
            Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
            if (usuario != null) {
                ranking.add(new UsuarioRankingDto(
                    usuario.getId(),
                    usuario.getNombre(),
                    usuario.getApellido(),
                    cantidadReportes
                ));
                count++;
            }
        }

        return ranking;
    }
}