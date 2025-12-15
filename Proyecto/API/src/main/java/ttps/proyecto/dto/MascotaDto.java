package ttps.proyecto.dto;

import ttps.proyecto.models.enums.EstadoMascota;
import java.time.LocalDate;

public class MascotaDto {
    private Long id;
    private String nombre;
    private String color;
    private String descripcion;
    private LocalDate fechaPublicacion;
    private EstadoMascota estado;
    private String tamanioNombre;
    private Long publicadorId;
    private String publicadorNombre;
    private UbicacionDto ubicacion;

    // Constructores
    public MascotaDto() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public LocalDate getFechaPublicacion() { return fechaPublicacion; }
    public void setFechaPublicacion(LocalDate fechaPublicacion) { this.fechaPublicacion = fechaPublicacion; }
    
    public EstadoMascota getEstado() { return estado; }
    public void setEstado(EstadoMascota estado) { this.estado = estado; }
    
    public String getTamanioNombre() { return tamanioNombre; }
    public void setTamanioNombre(String tamanioNombre) { this.tamanioNombre = tamanioNombre; }
    
    public Long getPublicadorId() { return publicadorId; }
    public void setPublicadorId(Long publicadorId) { this.publicadorId = publicadorId; }
    
    public String getPublicadorNombre() { return publicadorNombre; }
    public void setPublicadorNombre(String publicadorNombre) { this.publicadorNombre = publicadorNombre; }
    
    public UbicacionDto getUbicacion() { return ubicacion; }
    public void setUbicacion(UbicacionDto ubicacion) { this.ubicacion = ubicacion; }
}