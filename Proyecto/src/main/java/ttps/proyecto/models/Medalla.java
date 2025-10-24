package ttps.proyecto.models;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "medallas")
public class Medalla {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nombre;

    private String descripcion;

    /**
     * Lado "no dueño" de la relación Muchos-a-Muchos con Usuario.
     * mappedBy="medallas" hace referencia al campo 'medallas' en la clase Usuario.
     */
    @ManyToMany(mappedBy = "medallas")
    private Set<Usuario> usuarios = new HashSet<>();

    // Constructor vacío, Getters y Setters...
    public Medalla() {}

    public Long getId(){return this.id;}
    public String getNombre(){return this.nombre;}
    public void setNombre(String nombre){this.nombre = nombre;}
    public String getDescripcion(){return this.descripcion;}
    public void setDescripcion(String descripcion){this.descripcion = descripcion;}
    public Set<Usuario> getUsuarios() {return this.usuarios;}
    public void setUsuarios(Set<Usuario> usuarios) {this.usuarios = usuarios;}
    public void setUsuario(Usuario usuario) {this.usuarios.add(usuario);}
}