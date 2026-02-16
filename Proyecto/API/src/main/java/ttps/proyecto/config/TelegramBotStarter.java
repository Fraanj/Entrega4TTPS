package ttps.proyecto.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ttps.proyecto.telegram.DondeEstasTelegramBot;

/**
 * Registra el bot de Telegram al iniciar la aplicación, si está configurado el token.
 */
@Component
public class TelegramBotStarter implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(TelegramBotStarter.class);

    @Autowired
    private DondeEstasTelegramBot dondeEstasTelegramBot;

    @Override
    public void afterPropertiesSet() {
        String token = System.getenv("TELEGRAM_BOT_TOKEN");
        if (token == null || token.isBlank()) {
            log.info("TELEGRAM_BOT_TOKEN no configurado: el bot de Telegram no se iniciará.");
            return;
        }
        try {
            TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(dondeEstasTelegramBot);
            log.info("Bot de Telegram registrado correctamente.");
        } catch (TelegramApiException e) {
            log.error("Error al registrar el bot de Telegram", e);
        }
    }
}
