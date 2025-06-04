package com.example.medicalcrm.bot;

import com.example.medicalcrm.config.BotConfig;
import com.example.medicalcrm.entity.BotUser;
import com.example.medicalcrm.service.ApplicationService;
import com.example.medicalcrm.service.BotUserService;
import com.example.medicalcrm.bot.handler.DoctorCommandHandler;
import com.example.medicalcrm.bot.handler.SmmCommandHandler;
import com.example.medicalcrm.bot.handler.PatientCommandHandler;
import com.example.medicalcrm.bot.handler.CallbackHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component("mainBot")
@RequiredArgsConstructor
public class MainBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final BotUserService botUserService;
    private final DoctorCommandHandler doctorCommandHandler;
    private final SmmCommandHandler smmCommandHandler;
    private final PatientCommandHandler patientCommandHandler;
    private final CallbackHandler callbackHandler;
    private final ApplicationService applicationService;

    @Override
    public String getBotUsername() {
        return botConfig.getBotUsername();
    }

    @Override
    public String getBotToken() {
        return botConfig.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            callbackHandler.handle(update, this);
            return;
        }

        if (update.hasMessage()) {
            Long chatId = update.getMessage().getChatId();
            String username = update.getMessage().getFrom().getUserName();

            // Регистрируем пользователя, если он новый
            botUserService.getBotUserByTelegramId(chatId).orElseGet(() -> {
                BotUser user = new BotUser();
                user.setTelegramId(chatId);
                user.setUsername(username);
                user.setRole("PATIENT");
                return botUserService.saveBotUser(user);
            });

            String role = botUserService.getBotUserByTelegramId(chatId)
                    .map(BotUser::getRole)
                    .orElse("PATIENT");

            switch (role) {
                case "DOCTOR" -> doctorCommandHandler.handle(update, this, applicationService);
                case "SMM" -> smmCommandHandler.handle(update, this, applicationService);
                default -> patientCommandHandler.handle(update, this, applicationService);
            }
        }
    }
}