package ttps.proyecto.models;

import jakarta.persistence.*;
import ttps.proyecto.models.enums.EstadoMascota; // El único Enum
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoMascota estado;

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
    public Mascota(String nombre, EstadoMascota estado, String color , String descripcion) {
        this.color = color;
        this.descripcion = descripcion;
        this.nombre = nombre;
        this.estado = estado;
        this.fechaPublicacion = LocalDate.now();
    }
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
    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(LocalDate fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public TamanioMascota getTamanio() {
        return tamanio;
    }

    public List<Foto> getFotos() {
        return fotos;
    }
    public void setFotos(List<Foto> fotos) {
        this.fotos = (fotos != null) ? fotos : new ArrayList<>();
    }
    public void addFoto(Foto foto) {
            this.fotos.add(foto);

    }
    public void removeFoto(Foto foto) {
        this.fotos.remove(foto);
    }
    public List<Avistamiento> getAvistamientos() {
        return avistamientos;
    }
    public void setAvistamientos(List<Avistamiento> avistamientos) {
        this.avistamientos = (avistamientos != null) ? avistamientos : new ArrayList<>();
    }
    public void addAvistamiento(Avistamiento avistamiento) {
        if (avistamiento != null) {
            this.avistamientos.add(avistamiento);
            // keep bidirectional consistency if Avistamiento has setMascota(...)
            try {
                avistamiento.setMascota(this);
            } catch (Exception ignored) { }
        }
    }
    public void removeAvistamiento(Avistamiento avistamiento) {
        if (avistamiento != null) {
            this.avistamientos.remove(avistamiento);
            try {
                if (avistamiento.getMascota() == this) {
                    avistamiento.setMascota(null);
                }
            } catch (Exception ignored) { }
        }
    }

}