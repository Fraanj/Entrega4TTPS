package ttps.proyecto.dto;

public class UsuarioDto {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String ciudad;
    private Integer puntos;
    private String rolNombre;
    private String estadoNombre;

    // Constructor vacío
    public UsuarioDto() {}

    // Constructor completo
    public UsuarioDto(Long id, String nombre, String apellido, String email, 
                     String telefono, String ciudad, Integer puntos,
                     String rolNombre, String estadoNombre) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.telefono = telefono;
        this.ciudad = ciudad;
        this.puntos = puntos;
        this.rolNombre = rolNombre;
        this.estadoNombre = estadoNombre;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    
    public Integer getPuntos() { return puntos; }
    public void setPuntos(Integer puntos) { this.puntos = puntos; }
    
    public String getRolNombre() { return rolNombre; }
    public void setRolNombre(String rolNombre) { this.rolNombre = rolNombre; }
    
    public String getEstadoNombre() { return estadoNombre; }
    public void setEstadoNombre(String estadoNombre) { this.estadoNombre = estadoNombre; }
}