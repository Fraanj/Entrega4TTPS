package ttps.proyecto.models;

import jakarta.persistence.*;

@Entity
@Table(name = "fotos")
public class Foto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String url;

    // Constructor vacío, Getters y Setters...
    public Foto() {}

    public String getUtl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}