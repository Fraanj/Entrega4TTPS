package ttps.proyecto.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ttps.proyecto.dto.MascotaDto;
import ttps.proyecto.exceptions.ForbiddenException;
import ttps.proyecto.services.MascotaService;
import ttps.proyecto.services.UsuarioService;

import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/api/mascotas")
@CrossOrigin(origins = "*")
public class MascotaController {

    @Autowired
    private MascotaService mascotaService;

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    @PreAuthorize("hasRole('USUARIO') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> crear(@RequestBody MascotaDto dto, Authentication authentication) {
        String email = authentication.getName();
        Long usuarioId = usuarioService.obtenerIdPorEmail(email);
        MascotaDto mascota = mascotaService.crear(dto, usuarioId);
        return ResponseEntity.status(HttpStatus.CREATED).body(mascota);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        MascotaDto mascota = mascotaService.obtenerPorId(id);
        return ResponseEntity.ok(mascota);
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
    @GetMapping("/recuperadas")
    public ResponseEntity<List<MascotaDto>> obtenerRecuperadas() {
        List<MascotaDto> mascotas = mascotaService.obtenerMascotasRecuperadas();
        return ResponseEntity.ok(mascotas);
    }
    @GetMapping("/adoptadas")
    public  ResponseEntity<List<MascotaDto>> obtenerAdoptadas() {
        List<MascotaDto> mascotas = mascotaService.obtenerMascotasAdoptadas();
        return ResponseEntity.ok(mascotas);
    }

    @GetMapping("/obtenerTodas")
    public ResponseEntity<List<MascotaDto>> obtenerTodas() {
        List<MascotaDto> mascotas = mascotaService.obtenerTodas();
        return ResponseEntity.ok(mascotas);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<MascotaDto>> buscar(
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String tamanio,
            @RequestParam(required = false) String estado) {
        List<MascotaDto> mascotas = mascotaService.buscarConFiltros(color, tamanio, estado);
        return ResponseEntity.ok(mascotas);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USUARIO') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody MascotaDto dto, Authentication authentication) {
        String email = authentication.getName();
        Long usuarioId = usuarioService.obtenerIdPorEmail(email);

        MascotaDto mascotaExistente = mascotaService.obtenerPorId(id);
        if (!mascotaExistente.getPublicadorId().equals(usuarioId) && !usuarioService.obtenerRolPorId(usuarioId).getNombre().equals("ADMINISTRADOR")) {
            throw new ForbiddenException("No tienes permiso para modificar esta mascota");
        }

        MascotaDto mascota = mascotaService.actualizar(id, dto);
        return ResponseEntity.ok(mascota);
    }

    @GetMapping("/misMascotas")
    @PreAuthorize("hasRole('USUARIO') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> obtenerMisMascotas(Authentication authentication) {
        String email = authentication.getName();
        Long id = usuarioService.obtenerIdPorEmail(email);
        List<MascotaDto> mascotas = mascotaService.obtenerMisMascotas(id);
        return ResponseEntity.ok(mascotas);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USUARIO') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> eliminar(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        Long usuarioId = usuarioService.obtenerIdPorEmail(email);

        MascotaDto mascotaExistente = mascotaService.obtenerPorId(id);
        if (!mascotaExistente.getPublicadorId().equals(usuarioId) && !usuarioService.obtenerRolPorId(usuarioId).getNombre().equals("ADMINISTRADOR")) {
            throw new ForbiddenException("No tienes permiso para eliminar esta mascota");
        }

        mascotaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}