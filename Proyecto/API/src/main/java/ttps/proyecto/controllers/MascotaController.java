package ttps.proyecto.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ttps.proyecto.dto.MascotaDto;
import ttps.proyecto.services.MascotaService;

import java.util.List;

@RestController
@RequestMapping("/api/mascotas")
@CrossOrigin(origins = "*")
public class MascotaController {

    @Autowired
    private MascotaService mascotaService;

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody MascotaDto dto, @RequestParam Long usuarioId) {
        try {
            MascotaDto mascota = mascotaService.crear(dto, usuarioId);
            return ResponseEntity.status(HttpStatus.CREATED).body(mascota);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        try {
            MascotaDto mascota = mascotaService.obtenerPorId(id);
            return ResponseEntity.ok(mascota);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<MascotaDto>> obtenerPorUsuario(@PathVariable Long usuarioId) {
        List<MascotaDto> mascotas = mascotaService.obtenerPorUsuario(usuarioId);
        return ResponseEntity.ok(mascotas);
    }

    @GetMapping("/perdidas")
    public ResponseEntity<List<MascotaDto>> obtenerPerdidas() {
        List<MascotaDto> mascotas = mascotaService.obtenerMascotasPerdidas();
        return ResponseEntity.ok(mascotas);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody MascotaDto dto) {
        try {
            MascotaDto mascota = mascotaService.actualizar(id, dto);
            return ResponseEntity.ok(mascota);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            mascotaService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
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