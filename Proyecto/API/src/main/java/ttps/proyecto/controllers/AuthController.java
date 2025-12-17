package ttps.proyecto.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ttps.proyecto.dto.AuthResponse;
import ttps.proyecto.dto.LoginRequest;
import ttps.proyecto.dto.RegisterRequest;
import ttps.proyecto.dto.UsuarioDto;
import ttps.proyecto.models.Usuario;
import ttps.proyecto.repositories.UsuarioRepository;
import ttps.proyecto.services.TokenServices;
import ttps.proyecto.services.UsuarioService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private TokenServices tokenServices;
    private final int EXPIRATION_TIME = 864000; // 1 día en milisegundos

    @PostMapping("/register")
    public ResponseEntity<?> registrar(@Valid @RequestBody RegisterRequest request) {
        try {
            UsuarioDto usuario = usuarioService.registrar(request);
            String token = tokenServices.generateToken(usuario.getEmail(), EXPIRATION_TIME);
            return ResponseEntity.ok()
                    .header("Authorization", "Bearer " + token)
                    .header("Expiration-Time", String.valueOf(EXPIRATION_TIME))
                    .header("User-Email", usuario.getEmail())
                    .body(new AuthResponse(token, EXPIRATION_TIME, usuario.getEmail()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            UsuarioDto usuario = usuarioService.login(request);
            String token = tokenServices.generateToken(usuario.getEmail(), EXPIRATION_TIME);
            return ResponseEntity.ok()
                    .header("Authorization", "Bearer " + token)
                    .header("Expiration-Time", String.valueOf(EXPIRATION_TIME))
                    .header("User-Email", usuario.getEmail())
                    .body(new AuthResponse(token, EXPIRATION_TIME, usuario.getEmail()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(e.getMessage()));
        }
    }

    // Clase interna para respuestas de error
    static class ErrorResponse {
        private String message;
        
        public ErrorResponse(String message) {
            this.message = message;
        }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}