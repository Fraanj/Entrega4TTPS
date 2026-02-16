package ttps.proyecto.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ttps.proyecto.dto.MascotaDto;
import ttps.proyecto.dto.UsuarioDto;
import ttps.proyecto.exceptions.BadRequestException;
import ttps.proyecto.services.MascotaService;
import ttps.proyecto.services.UsuarioService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class AdminController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private MascotaService mascotaService;

    // --- Gestión de Usuarios ---

    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioDto>> listarUsuarios() {
        List<UsuarioDto> usuarios = usuarioService.obtenerTodos();
        return ResponseEntity.ok(usuarios);
    }

    @PutMapping("/usuarios/{id}/estado")
    public ResponseEntity<?> cambiarEstadoUsuario(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String estado = body != null ? body.get("estado") : null;
        if (estado == null || estado.isBlank()) {
            throw new BadRequestException("El campo 'estado' es requerido (HABILITADO o DESHABILITADO)");
        }
        usuarioService.cambiarEstado(id, estado.trim().toUpperCase());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    // --- Gestión de Publicaciones (Mascotas) ---

    @GetMapping("/mascotas")
    public ResponseEntity<List<MascotaDto>> listarMascotas() {
        List<MascotaDto> mascotas = mascotaService.obtenerTodas();
        return ResponseEntity.ok(mascotas);
    }

    @DeleteMapping("/mascotas/{id}")
    public ResponseEntity<?> eliminarMascota(@PathVariable Long id) {
        mascotaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
