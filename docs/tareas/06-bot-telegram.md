# Tarea 2.2: Bot de Telegram

## Objetivo
Implementar un bot de Telegram para reportar mascotas perdidas desde la app de mensajería. Comandos `/start`, `/help` y `/perdida` con flujo conversacional: nombre → foto → barrio → creación en el sistema.

---

## Archivos creados

| Archivo | Propósito |
|---------|-----------|
| `telegram/ConversationState.java` | Enum de estados: NONE, AWAITING_NAME, AWAITING_PHOTO, AWAITING_BARRIO. |
| `telegram/TelegramConversationData.java` | Datos de la conversación: estado, nombre, fotos (base64), barrio. |
| `telegram/TelegramBotService.java` | Lógica del flujo: procesar comandos, texto y fotos; al completar arma `MascotaDto` y llama a `MascotaService.crear`. |
| `telegram/DondeEstasTelegramBot.java` | `@Component` que extiende `TelegramLongPollingBot`; token y username por `@Value`/env; delega al servicio y envía respuestas. |
| `config/TelegramBotStarter.java` | `@Component` que implementa `InitializingBean`; si existe `TELEGRAM_BOT_TOKEN`, registra el bot con `TelegramBotsApi`. |

---

## Archivos modificados

| Archivo | Cambio |
|---------|--------|
| `API/pom.xml` | Dependencia `org.telegram:telegrambots` 6.9.7.1. |

---

## Variables de entorno

El bot se activa solo si está definida la variable de entorno del token. No se usa `application.properties` obligatorio; todo se configura por entorno (útil para Docker).

| Variable | Obligatoria | Descripción |
|----------|-------------|-------------|
| `TELEGRAM_BOT_TOKEN` | **Sí** (para activar el bot) | Token del bot obtenido con [@BotFather](https://t.me/BotFather). Si no está definida, el bot no se registra y la API arranca igual. |
| `TELEGRAM_BOT_USERNAME` | No | Nombre del bot en Telegram (ej. `DondeEstasBot`). Por defecto: `DondeEstasBot`. |
| `TELEGRAM_BOT_PUBLICADOR_ID` | No | ID del usuario en la BD que se usa como “publicador” de las mascotas reportadas por el bot. Por defecto: `1`. Debe existir un usuario con ese ID. |

**Recomendación**: Crear en la base de datos un usuario tipo “Sistema Telegram” (o similar) y usar su ID como `TELEGRAM_BOT_PUBLICADOR_ID`.

---

## Flujo del comando /perdida

1. Usuario envía `/perdida`.
2. Bot pide el **nombre** de la mascota.
3. Usuario escribe el nombre → Bot pide una **foto**.
4. Usuario envía una foto (se descarga la de mayor tamaño y se guarda en base64).
5. Bot pide **barrio o ubicación**.
6. Usuario escribe el barrio.
7. Se arma un `MascotaDto` con: estado PERDIDO_AJENO, tamaño MEDIANO, ubicación con barrio y coordenadas 0; se llama a `mascotaService.crear(dto, publicadorId)`.
8. Se confirma al usuario y se limpia el estado de la conversación.

---

## Clases y métodos involucrados

### TelegramBotService
- **processCommand(chatId, command)**: Responde a `/start`, `/help`, `/perdida` (inicia flujo) o comando desconocido.
- **processText(chatId, text)**: Según estado (AWAITING_NAME, AWAITING_BARRIO), guarda nombre o barrio y avanza; si no hay conversación, ignora.
- **processPhoto(chatId, photoBase64)**: En estado AWAITING_PHOTO guarda la foto y pide barrio.
- **getPublicadorId()**: Lee `TELEGRAM_BOT_PUBLICADOR_ID` de env o usa el valor por defecto inyectado.

### DondeEstasTelegramBot
- **getBotUsername()**, **getBotToken()**: Token desde `@Value` o `System.getenv("TELEGRAM_BOT_TOKEN")`.
- **onUpdateReceived(Update)**: Si hay texto que empieza con `/` → `processCommand`; si hay texto → `processText`; si hay foto → descarga archivo, convierte a base64, `processPhoto`. Envía respuestas con `execute(SendMessage)`.

### TelegramBotStarter
- **afterPropertiesSet()**: Si `TELEGRAM_BOT_TOKEN` está definido y no está vacío, crea `TelegramBotsApi(DefaultBotSession.class)` y registra `DondeEstasTelegramBot`.

---

## Conceptos útiles

- **Long polling**: El bot recibe updates mediante polling; no hace falta exponer un endpoint público para Telegram.
- **Estado por chat**: Se usa un `ConcurrentHashMap<Long, TelegramConversationData>` indexado por `chatId` para mantener una conversación por chat.
- **Mascota creada por el bot**: Se crea con el usuario indicado por `TELEGRAM_BOT_PUBLICADOR_ID`; el resto de datos se completa con valores por defecto (tamaño MEDIANO, coordenadas 0, etc.) según lo que pide la tarea.
