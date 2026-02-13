package ttps.proyecto.dto;

public class UsuarioRankingDto {
    private Long id;
    private String nombre;
    private String apellido;
    private Long cantidadReportes;

    public UsuarioRankingDto() {}

    public UsuarioRankingDto(Long id, String nombre, String apellido, Long cantidadReportes) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.cantidadReportes = cantidadReportes;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public Long getCantidadReportes() {
        return cantidadReportes;
    }

    public void setCantidadReportes(Long cantidadReportes) {
        this.cantidadReportes = cantidadReportes;
    }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
}
