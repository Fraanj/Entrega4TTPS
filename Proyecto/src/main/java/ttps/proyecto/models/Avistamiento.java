package ttps.proyecto.models;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "avistamientos")
public class Avistamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fecha;
    private String comentario;

    // --- Relaciones ---

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reportador_id")
    private Usuario reportador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mascota_id")
    private Mascota mascota;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "ubicacion_id", referencedColumnName = "id")
    private Ubicacion ubicacion;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "foto_id", referencedColumnName = "id")
    private Foto foto; // Foto opcional del avistamiento

    // Constructor vacío
    public Avistamiento() {}

    // Getters y Setters...
}