package ttps.proyecto.models;

import jakarta.persistence.*;
import ttps.proyecto.models.EstadoUsuario;
import ttps.proyecto.models.Rol;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String apellido;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String telefono;
    private String ciudad;
    private Integer puntos = 0;

    // --- Relaciones con Catálogos ---
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_usuario_id", nullable = false)
    private EstadoUsuario estado;

    // --- Relaciones de Dominio ---
    @OneToMany(mappedBy = "publicador", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Mascota> mascotasPublicadas = new ArrayList<>();

    @OneToMany(mappedBy = "reportador", fetch = FetchType.LAZY)
    private List<Avistamiento> avistamientosReportados = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "usuarios_medallas",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "medalla_id")
    )
    private Set<Medalla> medallas = new HashSet<>();

    public Usuario() {
    }

    // Getters y Setters...
    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getCiudad() {
        return ciudad;
    }

    public Integer getPuntos() {
        return puntos;
    }

    public Rol getRol() {
        return rol;
    }

    public EstadoUsuario getEstado() {
        return estado;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public void setEstado(EstadoUsuario estado) {
        this.estado = estado;
    }

    public List<Mascota> getMascotasPublicadas() {
        return mascotasPublicadas;
    }

    public void setMascotasPublicadas(List<Mascota> mascotasPublicadas) {
        this.mascotasPublicadas = mascotasPublicadas;
    }

    public Set<Medalla> getMedallas() {
        return this.medallas;
    }

    public void setMedallas(Set<Medalla> medallas) {
        this.medallas = medallas;
    }

    public List<Avistamiento> getAvistamientosReportados() {
        return avistamientosReportados;
    }

    public void setAvistamientosReportados(List<Avistamiento> avistamientosReportados) {
        this.avistamientosReportados = avistamientosReportados;
    }

    public void addMedalla(Medalla medalla) {
        this.medallas.add(medalla);
    }
}