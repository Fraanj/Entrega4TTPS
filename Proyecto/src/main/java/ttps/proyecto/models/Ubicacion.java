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

    public Ubicacion() {}
    public void setBarrio(String barrio) { this.barrio = barrio; }
    public String getBarrio() { return this.barrio; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }
    public Double getLatitud() { return this.latitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }
    public Double getLongitud() { return this.longitud; }
}