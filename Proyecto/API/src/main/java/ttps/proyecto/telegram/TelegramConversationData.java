package ttps.proyecto.telegram;

import java.util.ArrayList;
import java.util.List;

/**
 * Datos parciales de la mascota que el usuario está reportando por Telegram.
 */
public class TelegramConversationData {
    private ConversationState state = ConversationState.NONE;
    private String nombreMascota;
    private List<String> fotosBase64 = new ArrayList<>();
    private String barrio;

    public ConversationState getState() { return state; }
    public void setState(ConversationState state) { this.state = state; }
    public String getNombreMascota() { return nombreMascota; }
    public void setNombreMascota(String nombreMascota) { this.nombreMascota = nombreMascota; }
    public List<String> getFotosBase64() { return fotosBase64; }
    public void setFotosBase64(List<String> fotosBase64) { this.fotosBase64 = fotosBase64 != null ? fotosBase64 : new ArrayList<>(); }
    public String getBarrio() { return barrio; }
    public void setBarrio(String barrio) { this.barrio = barrio; }
}
