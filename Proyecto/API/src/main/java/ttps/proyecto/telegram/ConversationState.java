package ttps.proyecto.telegram;

/**
 * Estados del flujo de conversación para reportar una mascota perdida vía Telegram.
 */
public enum ConversationState {
    NONE,
    AWAITING_NAME,
    AWAITING_PHOTO,
    AWAITING_BARRIO
}
