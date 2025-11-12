package ttps.proyecto.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AvistamientoDto {
    private Long id;
    private LocalDate fecha;
    @NotBlank(message = "El comentario es obligatorio")
    private String comentario;
    @NotNull(message = "La mascota es obligatoria")
    private Long mascotaId;
    private String mascotaNombre;
    @NotNull(message = "El reportador es obligatorio")
    private Long reportadorId;
    private String reportadorNombre;
    private UbicacionDto ubicacion;

    public AvistamientoDto() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
    
    public Long getMascotaId() { return mascotaId; }
    public void setMascotaId(Long mascotaId) { this.mascotaId = mascotaId; }
    
    public String getMascotaNombre() { return mascotaNombre; }
    public void setMascotaNombre(String mascotaNombre) { this.mascotaNombre = mascotaNombre; }
    
    public Long getReportadorId() { return reportadorId; }
    public void setReportadorId(Long reportadorId) { this.reportadorId = reportadorId; }
    
    public String getReportadorNombre() { return reportadorNombre; }
    public void setReportadorNombre(String reportadorNombre) { this.reportadorNombre = reportadorNombre; }
    
    public UbicacionDto getUbicacion() { return ubicacion; }
    public void setUbicacion(UbicacionDto ubicacion) { this.ubicacion = ubicacion; }
}