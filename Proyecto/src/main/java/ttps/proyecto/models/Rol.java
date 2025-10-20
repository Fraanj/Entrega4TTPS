package ttps.proyecto.models;

import jakarta.persistence.*;

@Entity
@Table(name = "cat_roles")
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nombre; // "USUARIO", "ADMINISTRADOR"

    public Rol() {}
    // Getters y Setters...
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}