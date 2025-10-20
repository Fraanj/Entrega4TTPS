package ttps.proyecto.models;

import jakarta.persistence.*;

@Entity
@Table(name = "ubicaciones")
public class Ubicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double latitud;
    private Double longitud;
    private String barrio;

    // Constructor vacío, Getters y Setters...
    public Ubicacion() {}
}