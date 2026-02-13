package ttps.proyecto.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ttps.proyecto.dto.RegisterRequest;
import ttps.proyecto.dto.UpdateUserRequest;
import ttps.proyecto.dto.UsuarioDto;
import ttps.proyecto.dto.UsuarioRankingDto;
import ttps.proyecto.services.UsuarioService;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        try {
            UsuarioDto usuario = usuarioService.obtenerPorId(id);
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USUARIO') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        try {
            UsuarioDto usuario = usuarioService.actualizarPerfil(id, request);
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/ranking")
    public ResponseEntity<List<UsuarioRankingDto>> obtenerRanking(
            @RequestParam(defaultValue = "10") int limit) {
        List<UsuarioRankingDto> ranking = usuarioService.obtenerRanking(limit);
        return ResponseEntity.ok(ranking);
    }

    static class ErrorResponse {
        private String message;
        
        public ErrorResponse(String message) {
            this.message = message;
        }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}