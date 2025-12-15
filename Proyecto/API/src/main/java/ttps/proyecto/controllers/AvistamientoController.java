package ttps.proyecto.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import ttps.proyecto.dto.AvistamientoDto;
import ttps.proyecto.services.AvistamientoService;

import java.util.List;

@RestController
@RequestMapping("/api/avistamientos")
@CrossOrigin(origins = "*")
public class AvistamientoController {

    @Autowired
    private AvistamientoService avistamientoService;

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody AvistamientoDto dto, @RequestParam Long reportadorId) {
        try {
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
}