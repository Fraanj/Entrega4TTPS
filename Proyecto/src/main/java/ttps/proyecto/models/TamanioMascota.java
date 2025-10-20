package ttps.proyecto.models;

import jakarta.persistence.*;

/**
 * Nueva entidad de catálogo para el tamaño,
 * reemplazando al Enum.
 */
@Entity
@Table(name = "cat_tamanios_mascota")
public class TamanioMascota {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nombre; // "PEQUENIO", "MEDIANO", "GRANDE"

    public TamanioMascota() {}
    // Getters y Setters...
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}