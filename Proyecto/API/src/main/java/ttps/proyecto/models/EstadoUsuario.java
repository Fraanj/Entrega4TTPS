package ttps.proyecto.models;

import jakarta.persistence.*;

@Entity
@Table(name = "estados_usuario")
public class EstadoUsuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nombre; // "HABILITADO", "DESHABILITADO"

    public EstadoUsuario() {}
    public Long getId() {
        return id;
    }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}