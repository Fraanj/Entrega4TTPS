package ttps.proyecto.telegram;

/**
 * Estados del flujo de conversación para el bot de Telegram.
 */
public enum ConversationState {
    NONE,
    // Flujo /perdida
    AWAITING_NAME,
    AWAITING_COLOR,
    AWAITING_TAMANIO,
    AWAITING_PHOTO,
    AWAITING_BARRIO,
    // Flujo /avistamiento
    AWAITING_AVISTAMIENTO_DESC,
    AWAITING_AVISTAMIENTO_COLOR,
    AWAITING_AVISTAMIENTO_TAMANIO,
    AWAITING_AVISTAMIENTO_FOTO,
    AWAITING_AVISTAMIENTO_BARRIO,
    // Flujo /recuperada
    AWAITING_RECUPERADA_ID,
    // Flujo /ingresar
    AWAITING_LOGIN_EMAIL,
    AWAITING_LOGIN_PASSWORD,
    // Flujo /registrarme
    AWAITING_REGISTER_NOMBRE,
    AWAITING_REGISTER_APELLIDO,
    AWAITING_REGISTER_EMAIL,
    AWAITING_REGISTER_PASSWORD
}
