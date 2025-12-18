package ttps.proyecto.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ttps.proyecto.dto.AuthResponse;
import ttps.proyecto.dto.LoginRequest;
import ttps.proyecto.dto.RegisterRequest;
import ttps.proyecto.dto.UsuarioDto;
import ttps.proyecto.models.EstadoUsuario;
import ttps.proyecto.models.Rol;
import ttps.proyecto.models.Usuario;
import ttps.proyecto.repositories.EstadoUsuarioRepository;
import ttps.proyecto.repositories.RolRepository;
import ttps.proyecto.repositories.UsuarioRepository;
import ttps.proyecto.security.JwtUtil;

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
    public UsuarioDto actualizarPerfil(Long id, RegisterRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setTelefono(request.getTelefono());
        usuario.setCiudad(request.getCiudad());
        
        // Solo actualizar password si se proporciona uno nuevo
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            usuario.setPassword(request.getPassword());
        }

        Usuario updated = usuarioRepository.save(usuario);
        return convertToDto(updated);
    }

    public UsuarioDto obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return convertToDto(usuario);
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
}