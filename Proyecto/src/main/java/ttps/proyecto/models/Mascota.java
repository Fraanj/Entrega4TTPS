package ttps.proyecto.models;

import jakarta.persistence.*;
import ttps.proyecto.models.enums.EstadoMascota; // El único Enum
import ttps.proyecto.models.TamanioMascota; // Es entidad
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mascotas")
public class Mascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String color;
    private String descripcion;
    private LocalDate fechaPublicacion;

    /**
     * El único Enum del sistema.
     * Se guarda como String en la BD.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoMascota estado;

    // --- Relaciones con Catálogos ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tamanio_mascota_id")
    private TamanioMascota tamanio;

    // --- Relaciones de Dominio ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publicador_id")
    private Usuario publicador;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "ubicacion_id", referencedColumnName = "id")
    private Ubicacion ultimaUbicacion;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "mascota_id")
    private List<Foto> fotos = new ArrayList<>();

    @OneToMany(mappedBy = "mascota", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Avistamiento> avistamientos = new ArrayList<>();

    public Mascota() {}
    // Getters y Setters...
    public Long getId() { return id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setEstado(EstadoMascota estado) { this.estado = estado; }
    public EstadoMascota getEstado() { return estado; }
    public String getNombre() { return nombre; }
    public void setTamanio(TamanioMascota tamanio) { this.tamanio = tamanio; }
    public void setPublicador(Usuario publicador) { this.publicador = publicador; }
    public Usuario getPublicador() { return publicador; }
    public void setUltimaUbicacion(Ubicacion ubicacion) { this.ultimaUbicacion = ubicacion; }
    public Ubicacion getUltimaUbicacion() { return ultimaUbicacion; }
}