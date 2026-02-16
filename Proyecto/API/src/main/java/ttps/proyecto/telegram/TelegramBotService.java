package ttps.proyecto.telegram;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ttps.proyecto.dto.MascotaDto;
import ttps.proyecto.dto.UbicacionDto;
import ttps.proyecto.models.enums.EstadoMascota;
import ttps.proyecto.services.MascotaService;

import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Lógica del flujo de conversación del bot de Telegram y creación de mascotas.
 */
@Service
public class TelegramBotService {

    @Autowired
    private MascotaService mascotaService;

    /** ID del usuario "sistema" que se usa como publicador al reportar desde Telegram. Debe existir en la BD. */
    @Value("${TELEGRAM_BOT_PUBLICADOR_ID:1}")
    private Long publicadorId;

    private long getPublicadorId() {
        String env = System.getenv("TELEGRAM_BOT_PUBLICADOR_ID");
        if (env != null && !env.isBlank()) {
            try {
                return Long.parseLong(env.trim());
            } catch (NumberFormatException ignored) { }
        }
        return publicadorId;
    }

    private final ConcurrentHashMap<Long, TelegramConversationData> conversations = new ConcurrentHashMap<>();

    public String processCommand(long chatId, String command) {
        if (command == null) return null;
        String cmd = command.trim().toLowerCase().split("\\s+")[0];

        switch (cmd) {
            case "/start":
                return "🐶 ¡Hola! Soy el bot de ¿Dónde estás? — Volvé a casa.\n\n" +
                        "Podés reportar una mascota perdida con /perdida\n" +
                        "Ayuda: /help";
            case "/help":
                return "📋 Comandos:\n" +
                        "/perdida — Reportar una mascota perdida (nombre, foto y barrio)\n" +
                        "/start — Ver bienvenida";
            case "/perdida":
                startPerdidaFlow(chatId);
                return "🐾 Ingresá el nombre de la mascota:";
            default:
                return null;
        }
    }

    public String processText(long chatId, String text) {
        TelegramConversationData data = conversations.get(chatId);
        if (data == null || data.getState() == ConversationState.NONE) {
            return null;
        }

        if (data.getState() == ConversationState.AWAITING_NAME) {
            data.setNombreMascota(text != null ? text.trim() : "");
            data.setState(ConversationState.AWAITING_PHOTO);
            return "📷 Enviá una foto de la mascota:";
        }

        if (data.getState() == ConversationState.AWAITING_BARRIO) {
            data.setBarrio(text != null ? text.trim() : "");
            return tryCreateMascota(chatId, data);
        }

        return null;
    }

    public String processPhoto(long chatId, String photoBase64) {
        TelegramConversationData data = conversations.get(chatId);
        if (data == null || data.getState() != ConversationState.AWAITING_PHOTO) {
            return null;
        }
        if (photoBase64 != null && !photoBase64.isEmpty()) {
            data.getFotosBase64().add(photoBase64);
        }
        data.setState(ConversationState.AWAITING_BARRIO);
        return "🗺 Ingresá el barrio o zona donde se perdió:";
    }

    private void startPerdidaFlow(long chatId) {
        conversations.put(chatId, new TelegramConversationData());
        conversations.get(chatId).setState(ConversationState.AWAITING_NAME);
    }

    private String tryCreateMascota(long chatId, TelegramConversationData data) {
        String nombre = data.getNombreMascota();
        String barrio = data.getBarrio();
        if (nombre == null || nombre.isBlank()) {
            cancelConversation(chatId);
            return "❌ Se canceló el reporte. Usá /perdida para intentar de nuevo.";
        }

        MascotaDto dto = new MascotaDto();
        dto.setNombre(nombre.trim());
        dto.setColor("No especificado");
        dto.setDescripcion("Reportado desde Telegram. Barrio: " + (barrio != null ? barrio : "No indicado"));
        dto.setEstado(EstadoMascota.PERDIDO_AJENO);
        dto.setTamanioNombre("MEDIANO");

        UbicacionDto ubi = new UbicacionDto();
        ubi.setBarrio(barrio != null && !barrio.isBlank() ? barrio : "No indicado");
        ubi.setLatitud(0.0);
        ubi.setLongitud(0.0);
        dto.setUbicacion(ubi);

        if (data.getFotosBase64() != null && !data.getFotosBase64().isEmpty()) {
            dto.setFotos(data.getFotosBase64());
        } else {
            dto.setFotos(Collections.emptyList());
        }

        try {
            mascotaService.crear(dto, getPublicadorId());
            cancelConversation(chatId);
            return "✅ Mascota registrada y compartida con la comunidad.";
        } catch (Exception e) {
            return "❌ No se pudo registrar. " + e.getMessage() + ". Probá de nuevo con /perdida.";
        }
    }

    private void cancelConversation(long chatId) {
        conversations.remove(chatId);
    }
}
