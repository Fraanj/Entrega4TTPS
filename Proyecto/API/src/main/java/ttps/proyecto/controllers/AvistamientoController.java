package ttps.proyecto.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import ttps.proyecto.dto.AvistamientoDto;
import ttps.proyecto.services.AvistamientoService;
import ttps.proyecto.services.UsuarioService;

import java.util.List;

@RestController
@RequestMapping("/api/avistamientos")
@CrossOrigin(origins = "*")
public class AvistamientoController {

    @Autowired
    private AvistamientoService avistamientoService;

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    @PreAuthorize("hasRole('USUARIO') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> crear(@Valid @RequestBody AvistamientoDto dto, Authentication authentication) {
        try {
            String email = authentication.getName();
            Long reportadorId = usuarioService.obtenerIdPorEmail(email);
            AvistamientoDto avistamiento = avistamientoService.crear(dto, reportadorId);
            return ResponseEntity.status(HttpStatus.CREATED).body(avistamiento);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<AvistamientoDto>> listarTodos() {
        List<AvistamientoDto> avistamientos = avistamientoService.listarTodos();
        return ResponseEntity.ok(avistamientos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        try {
            AvistamientoDto avistamiento = avistamientoService.obtenerPorId(id);
            return ResponseEntity.ok(avistamiento);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/mascota/{mascotaId}")
    public ResponseEntity<List<AvistamientoDto>> listarPorMascota(@PathVariable Long mascotaId) {
        List<AvistamientoDto> avistamientos = avistamientoService.listarPorMascota(mascotaId);
        return ResponseEntity.ok(avistamientos);
    }

    static class ErrorResponse {
        private String message;
        
        public ErrorResponse(String message) {
            this.message = message;
        }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasRole('USUARIO') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<AvistamientoDto>> listarPorUsuario(@PathVariable Long usuarioId, Authentication authentication) {
        String email = authentication.getName();
        Long currentUserId = usuarioService.obtenerIdPorEmail(email);
        
        if (!currentUserId.equals(usuarioId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<AvistamientoDto> avistamientos = avistamientoService.listarPorReportador(usuarioId);
        return ResponseEntity.ok(avistamientos);
    }
}