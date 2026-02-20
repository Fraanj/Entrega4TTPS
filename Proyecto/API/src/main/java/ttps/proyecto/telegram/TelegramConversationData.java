package ttps.proyecto.telegram;

import java.util.ArrayList;
import java.util.List;

/**
 * Datos parciales de la mascota/auth que el usuario está ingresando por Telegram.
 */
public class TelegramConversationData {
    private ConversationState state = ConversationState.NONE;
    private String tipoFlujo;

    // Datos de mascota
    private String nombreMascota;
    private String color;
    private String tamanio;
    private List<String> fotosBase64 = new ArrayList<>();
    private String barrio;

    // Datos temporales para registro/login
    private String tempNombre;
    private String tempApellido;
    private String tempEmail;

    public ConversationState getState() { return state; }
    public void setState(ConversationState state) { this.state = state; }

    public String getTipoFlujo() { return tipoFlujo; }
    public void setTipoFlujo(String tipoFlujo) { this.tipoFlujo = tipoFlujo; }

    public String getNombreMascota() { return nombreMascota; }
    public void setNombreMascota(String nombreMascota) { this.nombreMascota = nombreMascota; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getTamanio() { return tamanio; }
    public void setTamanio(String tamanio) { this.tamanio = tamanio; }

    public List<String> getFotosBase64() { return fotosBase64; }
    public void setFotosBase64(List<String> fotosBase64) { this.fotosBase64 = fotosBase64 != null ? fotosBase64 : new ArrayList<>(); }

    public String getBarrio() { return barrio; }
    public void setBarrio(String barrio) { this.barrio = barrio; }

    public String getTempNombre() { return tempNombre; }
    public void setTempNombre(String tempNombre) { this.tempNombre = tempNombre; }

    public String getTempApellido() { return tempApellido; }
    public void setTempApellido(String tempApellido) { this.tempApellido = tempApellido; }

    public String getTempEmail() { return tempEmail; }
    public void setTempEmail(String tempEmail) { this.tempEmail = tempEmail; }
}
