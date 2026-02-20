package ttps.proyecto.telegram;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ttps.proyecto.dto.AuthResponse;
import ttps.proyecto.dto.LoginRequest;
import ttps.proyecto.dto.MascotaDto;
import ttps.proyecto.dto.RegisterRequest;
import ttps.proyecto.dto.UbicacionDto;
import ttps.proyecto.models.enums.EstadoMascota;
import ttps.proyecto.services.MascotaService;
import ttps.proyecto.services.UsuarioService;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Lógica del flujo de conversación del bot de Telegram.
 * Comandos: /ingresar, /registrarme, /salir,
 *           /perdida, /avistamiento, /buscar, /recuperada, /cancelar, /start, /help.
 */
@Service
public class TelegramBotService {

    private static final Logger log = LoggerFactory.getLogger(TelegramBotService.class);

    @Autowired
    private MascotaService mascotaService;

    @Autowired
    private UsuarioService usuarioService;

    /** Fallback: usuario sistema si no hay sesión autenticada */
    @Value("${TELEGRAM_BOT_PUBLICADOR_ID:1}")
    private Long defaultPublicadorId;

    /** chatId -> usuarioId autenticado */
    private final ConcurrentHashMap<Long, Long> authenticatedUsers = new ConcurrentHashMap<>();

    /** chatId -> estado de conversación activo */
    private final ConcurrentHashMap<Long, TelegramConversationData> conversations = new ConcurrentHashMap<>();

    // ─── COMANDOS ──────────────────────────────────────────────────────────────

    public String processCommand(long chatId, String command) {
        if (command == null) return null;
        String raw = command.trim();
        String cmd = raw.toLowerCase().split("\\s+")[0];

        switch (cmd) {
            case "/start":
                return buildBienvenida(chatId);

            case "/help":
                return buildHelp();

            case "/ingresar":
                startFlujo(chatId, "login");
                return "✉️ Ingresá tu email:";

            case "/registrarme":
                startFlujo(chatId, "register");
                return "👤 Ingresá tu nombre:";

            case "/salir":
                authenticatedUsers.remove(chatId);
                cancelConversation(chatId);
                return "👋 Sesión cerrada. Usá /ingresar para volver a entrar.";

            case "/perdida":
                if (!isAuthenticated(chatId)) return notAuthMsg();
                startFlujo(chatId, "perdida");
                return "🐾 Ingresá el nombre de la mascota:";

            case "/avistamiento":
                if (!isAuthenticated(chatId)) return notAuthMsg();
                startFlujo(chatId, "avistamiento");
                return "🔍 Describí la mascota que avistaste (raza, color, nombre si lo sabés):";

            case "/buscar":
                return processBuscar(raw);

            case "/recuperada":
                if (!isAuthenticated(chatId)) return notAuthMsg();
                startFlujo(chatId, "recuperada");
                return "🏠 Ingresá el ID de la mascota recuperada:";

            case "/cancelar":
                cancelConversation(chatId);
                return "🚫 Operación cancelada. Usá /help para ver los comandos.";

            default:
                return null;
        }
    }

    // ─── TEXTO LIBRE ───────────────────────────────────────────────────────────

    public String processText(long chatId, String text) {
        TelegramConversationData data = conversations.get(chatId);
        if (data == null || data.getState() == ConversationState.NONE) return null;

        ConversationState state = data.getState();
        String t = text != null ? text.trim() : "";

        // ── Flujo /ingresar ──
        if (state == ConversationState.AWAITING_LOGIN_EMAIL) {
            data.setTempEmail(t);
            data.setState(ConversationState.AWAITING_LOGIN_PASSWORD);
            return "🔑 Ingresá tu contraseña:";
        }
        if (state == ConversationState.AWAITING_LOGIN_PASSWORD) {
            return tryLogin(chatId, data.getTempEmail(), t);
        }

        // ── Flujo /registrarme ──
        if (state == ConversationState.AWAITING_REGISTER_NOMBRE) {
            data.setTempNombre(t);
            data.setState(ConversationState.AWAITING_REGISTER_APELLIDO);
            return "👤 Ingresá tu apellido:";
        }
        if (state == ConversationState.AWAITING_REGISTER_APELLIDO) {
            data.setTempApellido(t);
            data.setState(ConversationState.AWAITING_REGISTER_EMAIL);
            return "✉️ Ingresá tu email:";
        }
        if (state == ConversationState.AWAITING_REGISTER_EMAIL) {
            data.setTempEmail(t);
            data.setState(ConversationState.AWAITING_REGISTER_PASSWORD);
            return "🔑 Creá tu contraseña (mínimo 6 caracteres):";
        }
        if (state == ConversationState.AWAITING_REGISTER_PASSWORD) {
            return tryRegister(chatId, data, t);
        }

        // ── Flujo /perdida ──
        if (state == ConversationState.AWAITING_NAME) {
            data.setNombreMascota(t);
            data.setState(ConversationState.AWAITING_COLOR);
            return "🎨 ¿De qué color es " + t + "? (ej: marrón, blanco, negro con manchas):";
        }
        if (state == ConversationState.AWAITING_COLOR) {
            data.setColor(t);
            data.setState(ConversationState.AWAITING_TAMANIO);
            return "📏 ¿Qué tamaño tiene?\n1 — Pequeño\n2 — Mediano\n3 — Grande\nRespondé con 1, 2 o 3:";
        }
        if (state == ConversationState.AWAITING_TAMANIO) {
            String tamanio = parseTamanio(t);
            if (tamanio == null) return "❌ Opción inválida. Respondé con 1 (Pequeño), 2 (Mediano) o 3 (Grande):";
            data.setTamanio(tamanio);
            data.setState(ConversationState.AWAITING_PHOTO);
            return "📷 Enviá una foto de " + data.getNombreMascota() + ":";
        }
        if (state == ConversationState.AWAITING_BARRIO) {
            data.setBarrio(t);
            return tryCreateMascota(chatId, data);
        }
        // Recordatorio: se espera foto, no texto
        if (state == ConversationState.AWAITING_PHOTO) {
            return "📷 Por favor enviá una foto de " + data.getNombreMascota() + " (necesito una imagen, no texto):";
        }

        // ── Flujo /avistamiento ──
        if (state == ConversationState.AWAITING_AVISTAMIENTO_DESC) {
            data.setNombreMascota(t);
            data.setState(ConversationState.AWAITING_AVISTAMIENTO_COLOR);
            return "🎨 ¿De qué color es la mascota?:";
        }
        if (state == ConversationState.AWAITING_AVISTAMIENTO_COLOR) {
            data.setColor(t);
            data.setState(ConversationState.AWAITING_AVISTAMIENTO_TAMANIO);
            return "📏 ¿Qué tamaño tiene?\n1 — Pequeño\n2 — Mediano\n3 — Grande\nRespondé con 1, 2 o 3:";
        }
        if (state == ConversationState.AWAITING_AVISTAMIENTO_TAMANIO) {
            String tamanio = parseTamanio(t);
            if (tamanio == null) return "❌ Opción inválida. Respondé con 1 (Pequeño), 2 (Mediano) o 3 (Grande):";
            data.setTamanio(tamanio);
            data.setState(ConversationState.AWAITING_AVISTAMIENTO_FOTO);
            return "📷 Enviá una foto del avistamiento:";
        }
        // Recordatorio: se espera foto, no texto
        if (state == ConversationState.AWAITING_AVISTAMIENTO_FOTO) {
            return "📷 Por favor enviá una foto del avistamiento (necesito una imagen, no texto):";
        }
        if (state == ConversationState.AWAITING_AVISTAMIENTO_BARRIO) {
            data.setBarrio(t);
            return tryCreateAvistamiento(chatId, data);
        }

        // ── Flujo /recuperada ──
        if (state == ConversationState.AWAITING_RECUPERADA_ID) {
            return tryMarcarRecuperada(chatId, t);
        }

        return null;
    }

    // ─── FOTO ──────────────────────────────────────────────────────────────────

    public String processPhoto(long chatId, String photoBase64) {
        TelegramConversationData data = conversations.get(chatId);
        if (data == null) return null;

        ConversationState state = data.getState();

        if (state == ConversationState.AWAITING_PHOTO) {
            if (photoBase64 != null && !photoBase64.isEmpty()) data.getFotosBase64().add(photoBase64);
            data.setState(ConversationState.AWAITING_BARRIO);
            return "🗺 Ingresá el barrio o zona donde se perdió:";
        }

        if (state == ConversationState.AWAITING_AVISTAMIENTO_FOTO) {
            if (photoBase64 != null && !photoBase64.isEmpty()) data.getFotosBase64().add(photoBase64);
            data.setState(ConversationState.AWAITING_AVISTAMIENTO_BARRIO);
            return "🗺 Ingresá el barrio o zona donde la viste:";
        }

        return null;
    }

    // ─── AUTH ──────────────────────────────────────────────────────────────────

    private String tryLogin(long chatId, String email, String password) {
        cancelConversation(chatId);
        try {
            LoginRequest req = new LoginRequest();
            req.setEmail(email);
            req.setPassword(password);
            AuthResponse auth = usuarioService.login(req);
            authenticatedUsers.put(chatId, auth.getUsuario().getId());
            return "✅ ¡Hola, " + auth.getUsuario().getNombre() + "! Sesión iniciada.\n" +
                   "Usá /help para ver los comandos disponibles.";
        } catch (Exception e) {
            log.warn("Login fallido para chatId={}: {}", chatId, e.getMessage());
            return "❌ Credenciales inválidas. Usá /ingresar para intentar de nuevo.";
        }
    }

    private String tryRegister(long chatId, TelegramConversationData data, String password) {
        cancelConversation(chatId);
        try {
            RegisterRequest req = new RegisterRequest();
            req.setNombre(data.getTempNombre());
            req.setApellido(data.getTempApellido());
            req.setEmail(data.getTempEmail());
            req.setPassword(password);
            AuthResponse auth = usuarioService.registrar(req);
            authenticatedUsers.put(chatId, auth.getUsuario().getId());
            return "✅ ¡Registrado exitosamente! Bienvenido, " + auth.getUsuario().getNombre() + ".\n" +
                   "Ya podés usar /perdida, /avistamiento y más.";
        } catch (Exception e) {
            log.warn("Registro fallido para chatId={}: {}", chatId, e.getMessage());
            return "❌ No se pudo registrar: " + e.getMessage() + "\nUsá /registrarme para intentar de nuevo.";
        }
    }

    // ─── /buscar ───────────────────────────────────────────────────────────────

    private String processBuscar(String rawCommand) {
        String[] parts = rawCommand.split("\\s+", 2);
        if (parts.length < 2 || parts[1].isBlank()) {
            return "🔍 Indicá el barrio a buscar. Ejemplo: /buscar Palermo";
        }
        String barrioBuscado = parts[1].trim().toLowerCase();
        List<MascotaDto> perdidas = mascotaService.obtenerMascotasPerdidas();
        List<MascotaDto> filtradas = perdidas.stream()
            .filter(m -> m.getUbicacion() != null &&
                         m.getUbicacion().getBarrio() != null &&
                         m.getUbicacion().getBarrio().toLowerCase().contains(barrioBuscado))
            .collect(Collectors.toList());

        if (filtradas.isEmpty()) {
            return "😔 No hay mascotas reportadas en \"" + parts[1].trim() + "\". Probá con otro barrio.";
        }

        StringBuilder sb = new StringBuilder("🔍 Mascotas perdidas en " + parts[1].trim() + ":\n\n");
        for (int i = 0; i < filtradas.size(); i++) {
            MascotaDto m = filtradas.get(i);
            sb.append((i + 1)).append(". ").append(m.getNombre());
            if (m.getColor() != null) sb.append(" · ").append(m.getColor());
            if (m.getTamanioNombre() != null) sb.append(" · ").append(formatTamanio(m.getTamanioNombre()));
            if (m.getPublicadorNombre() != null) sb.append(" — ").append(m.getPublicadorNombre());
            if (m.getFechaPublicacion() != null) sb.append(" — ").append(m.getFechaPublicacion());
            sb.append(" (ID: ").append(m.getId()).append(")\n");
        }
        return sb.toString();
    }

    // ─── HELPERS FLUJOS ────────────────────────────────────────────────────────

    private void startFlujo(long chatId, String tipo) {
        TelegramConversationData data = new TelegramConversationData();
        data.setTipoFlujo(tipo);
        switch (tipo) {
            case "login":        data.setState(ConversationState.AWAITING_LOGIN_EMAIL); break;
            case "register":     data.setState(ConversationState.AWAITING_REGISTER_NOMBRE); break;
            case "perdida":      data.setState(ConversationState.AWAITING_NAME); break;
            case "avistamiento": data.setState(ConversationState.AWAITING_AVISTAMIENTO_DESC); break;
            case "recuperada":   data.setState(ConversationState.AWAITING_RECUPERADA_ID); break;
        }
        conversations.put(chatId, data);
    }

    private String tryCreateMascota(long chatId, TelegramConversationData data) {
        String nombre = data.getNombreMascota();
        String barrio = data.getBarrio();
        if (nombre == null || nombre.isBlank()) {
            cancelConversation(chatId);
            return "❌ Se canceló el reporte. Usá /perdida para intentar de nuevo.";
        }
        MascotaDto dto = new MascotaDto();
        dto.setNombre(nombre);
        dto.setColor(data.getColor() != null ? data.getColor() : "No especificado");
        dto.setDescripcion("Reportado desde Telegram.");
        dto.setEstado(EstadoMascota.PERDIDO_PROPIO);
        dto.setTamanioNombre(data.getTamanio() != null ? data.getTamanio() : "MEDIANO");
        if (barrio != null && !barrio.isBlank()) {
            UbicacionDto ubi = new UbicacionDto();
            ubi.setBarrio(barrio);
            dto.setUbicacion(ubi);
        }
        dto.setFotos(data.getFotosBase64() != null && !data.getFotosBase64().isEmpty()
            ? data.getFotosBase64() : Collections.emptyList());
        try {
            mascotaService.crear(dto, getPublicadorId(chatId));
            cancelConversation(chatId);
            return "✅ ¡" + nombre + " fue reportada y compartida con la comunidad!";
        } catch (Exception e) {
            log.error("Error creando mascota perdida para chatId={}: {}", chatId, e.getMessage(), e);
            cancelConversation(chatId);
            return "❌ No se pudo registrar: " + e.getMessage() + ". Probá con /perdida.";
        }
    }

    private String tryCreateAvistamiento(long chatId, TelegramConversationData data) {
        String descripcion = data.getNombreMascota();
        String barrio = data.getBarrio();
        MascotaDto dto = new MascotaDto();
        dto.setNombre("Mascota avistada");
        dto.setColor(data.getColor() != null ? data.getColor() : "No especificado");
        dto.setDescripcion("Avistamiento desde Telegram: " + (descripcion != null ? descripcion : ""));
        dto.setEstado(EstadoMascota.PERDIDO_AJENO);
        dto.setTamanioNombre(data.getTamanio() != null ? data.getTamanio() : "MEDIANO");
        if (barrio != null && !barrio.isBlank()) {
            UbicacionDto ubi = new UbicacionDto();
            ubi.setBarrio(barrio);
            dto.setUbicacion(ubi);
        }
        dto.setFotos(data.getFotosBase64() != null && !data.getFotosBase64().isEmpty()
            ? data.getFotosBase64() : Collections.emptyList());
        try {
            mascotaService.crear(dto, getPublicadorId(chatId));
            cancelConversation(chatId);
            return "✅ ¡Avistamiento registrado! Gracias por ayudar a la comunidad.";
        } catch (Exception e) {
            log.error("Error creando avistamiento para chatId={}: {}", chatId, e.getMessage(), e);
            cancelConversation(chatId);
            return "❌ No se pudo registrar el avistamiento: " + e.getMessage();
        }
    }

    private String tryMarcarRecuperada(long chatId, String idTexto) {
        cancelConversation(chatId);
        try {
            Long id = Long.parseLong(idTexto.trim());
            MascotaDto existente = mascotaService.obtenerPorId(id);
            MascotaDto dto = new MascotaDto();
            dto.setNombre(existente.getNombre());
            dto.setColor(existente.getColor());
            dto.setDescripcion(existente.getDescripcion());
            dto.setEstado(EstadoMascota.RECUPERADO);
            dto.setTamanioNombre(existente.getTamanioNombre());
            dto.setUbicacion(existente.getUbicacion());
            mascotaService.actualizar(id, dto);
            return "🏠 ¡" + existente.getNombre() + " fue marcada como recuperada! ¡Qué alegría! 🎉";
        } catch (NumberFormatException e) {
            return "❌ ID inválido. Ingresá solo el número (ej: 42). Usá /recuperada para intentar de nuevo.";
        } catch (Exception e) {
            log.warn("Error marcando recuperada para chatId={}: {}", chatId, e.getMessage());
            return "❌ No se encontró la mascota con ese ID: " + e.getMessage();
        }
    }

    // ─── UTILS ─────────────────────────────────────────────────────────────────

    private boolean isAuthenticated(long chatId) {
        return authenticatedUsers.containsKey(chatId);
    }

    private String notAuthMsg() {
        return "🔒 Necesitás iniciar sesión para usar este comando.\n" +
               "Usá /ingresar o /registrarme.";
    }

    private long getPublicadorId(long chatId) {
        Long userId = authenticatedUsers.get(chatId);
        if (userId != null) return userId;
        String env = System.getenv("TELEGRAM_BOT_PUBLICADOR_ID");
        if (env != null && !env.isBlank()) {
            try { return Long.parseLong(env.trim()); } catch (NumberFormatException ignored) {}
        }
        return defaultPublicadorId;
    }

    private String buildBienvenida(long chatId) {
        String base = "🐶 ¡Hola! Soy el bot de ¿Dónde estás? — Volvé a casa.\n\n";
        if (isAuthenticated(chatId)) {
            try {
                String nombre = usuarioService.obtenerPorId(authenticatedUsers.get(chatId)).getNombre();
                return base + "Estás conectado como " + nombre + ". Usá /help para ver los comandos.";
            } catch (Exception ignored) {}
        }
        return base + "Para reportar mascotas necesitás cuenta:\n" +
               "/ingresar     — Iniciar sesión\n" +
               "/registrarme  — Crear cuenta\n\n" +
               "O explorá: /buscar [barrio] — Ver mascotas perdidas";
    }

    private String buildHelp() {
        return "📋 Comandos disponibles:\n\n" +
               "👤 Cuenta:\n" +
               "/ingresar        — Iniciar sesión\n" +
               "/registrarme     — Crear cuenta nueva\n" +
               "/salir           — Cerrar sesión\n\n" +
               "🐾 Mascotas (requieren sesión):\n" +
               "/perdida         — Reportar mascota perdida\n" +
               "/avistamiento    — Reportar mascota avistada\n" +
               "/recuperada      — Marcar mascota como recuperada\n\n" +
               "🔍 Sin sesión:\n" +
               "/buscar [barrio] — Buscar mascotas perdidas\n\n" +
               "/cancelar        — Cancelar la operación actual";
    }

    private void cancelConversation(long chatId) {
        conversations.remove(chatId);
    }

    /** Convierte "1/2/3" o el nombre literal a nombre de tamaño en DB */
    private String parseTamanio(String input) {
        if (input == null) return null;
        switch (input.trim().toLowerCase()) {
            case "1": case "pequeño": case "pequenio": case "chico": return "PEQUENIO";
            case "2": case "mediano": return "MEDIANO";
            case "3": case "grande": return "GRANDE";
            default: return null;
        }
    }

    /** Convierte el nombre de tamaño de DB a texto legible para el usuario */
    private String formatTamanio(String tamanio) {
        if (tamanio == null) return "";
        switch (tamanio.toUpperCase()) {
            case "PEQUENIO": return "Pequeño";
            case "MEDIANO":  return "Mediano";
            case "GRANDE":   return "Grande";
            default:         return tamanio;
        }
    }
}
