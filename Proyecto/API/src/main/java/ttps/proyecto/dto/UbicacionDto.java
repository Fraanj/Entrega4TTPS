package ttps.proyecto.dto;

public class UbicacionDto {
    private Double latitud;
    private Double longitud;
    private String barrio;

    public UbicacionDto() {}

    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }
    
    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }
    
    public String getBarrio() { return barrio; }
    public void setBarrio(String barrio) { this.barrio = barrio; }
}