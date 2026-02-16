package ttps.proyecto.telegram;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;

/**
 * Bot de Telegram para reportar mascotas perdidas.
 * Comandos: /start, /help, /perdida.
 */
@Component
public class DondeEstasTelegramBot extends TelegramLongPollingBot {

    private static final Logger log = LoggerFactory.getLogger(DondeEstasTelegramBot.class);

    @Value("${TELEGRAM_BOT_TOKEN:}")
    private String botToken;

    @Value("${TELEGRAM_BOT_USERNAME:DondeEstasBot}")
    private String botUsername;

    @Autowired
    private TelegramBotService telegramBotService;

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        if (botToken != null && !botToken.isBlank()) return botToken;
        String env = System.getenv("TELEGRAM_BOT_TOKEN");
        return env != null ? env : "";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            var msg = update.getMessage();
            long chatId = msg.getChatId();

            try {
                // Comando (ej. /start, /perdida)
                if (msg.hasText() && msg.getText().trim().startsWith("/")) {
                    String response = telegramBotService.processCommand(chatId, msg.getText());
                    if (response != null) sendText(chatId, response);
                    return;
                }

                // Foto (en flujo /perdida)
                if (msg.hasPhoto() && !msg.getPhoto().isEmpty()) {
                    String photoBase64 = downloadPhotoAsBase64(msg.getPhoto());
                    String response = telegramBotService.processPhoto(chatId, photoBase64);
                    if (response != null) sendText(chatId, response);
                    return;
                }

                // Texto libre (nombre o barrio en el flujo)
                if (msg.hasText()) {
                    String response = telegramBotService.processText(chatId, msg.getText());
                    if (response != null) sendText(chatId, response);
                }
            } catch (Exception e) {
                log.error("Error procesando mensaje de Telegram", e);
                sendText(chatId, "❌ Ocurrió un error. Probá más tarde o usá /help.");
            }
        }
    }

    private String downloadPhotoAsBase64(List<PhotoSize> photos) {
        PhotoSize largest = photos.stream().max(Comparator.comparingInt(PhotoSize::getFileSize)).orElse(photos.get(photos.size() - 1));
        try {
            GetFile getFile = new GetFile(largest.getFileId());
            File file = execute(getFile);
            java.io.File localFile = downloadFile(file);
            if (localFile == null) return null;
            byte[] bytes = Files.readAllBytes(localFile.toPath());
            localFile.delete();
            return Base64.getEncoder().encodeToString(bytes);
        } catch (TelegramApiException | IOException e) {
            log.warn("No se pudo descargar la foto: {}", e.getMessage());
            return null;
        }
    }

    private void sendText(long chatId, String text) {
        SendMessage sm = new SendMessage();
        sm.setChatId(String.valueOf(chatId));
        sm.setText(text);
        sm.setParseMode(null);
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            log.error("Error enviando mensaje a Telegram", e);
        }
    }
}
