package com.example.medicalcrm.bot.handler;

import com.example.medicalcrm.service.ApplicationService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.ArrayList;
import java.util.List;

@Component
public class SmmCommandHandler {

    public void handle(Update update, AbsSender sender, ApplicationService applicationService) {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());

        if (text.equalsIgnoreCase("/smm") || text.equalsIgnoreCase("/смм")) {
            message.setText("""
                📊 SMM-отчёт за неделю

                👥 VK:
                👁️ Охват: 6 120
                🤝 Вовлечённость: 4.2%
                📌 Подписки: 84
                🔁 Репостов: 28
                💬 Комментарии: 19

                📷 Instagram:
                👁️ Охват: 3 420
                🤝 Вовлечённость: 5.1%
                📌 Подписки: 44
                🔁 Репостов: 14
                💬 Комментарии: 22

                📉 Слабые посты:
                VK:  “Плоскостопие” — 230 охвата, 1 лайк
                Insta: “Вальгус у детей” — 260 охвата, 1 заявка
            """);
        } else {
            message.setText("""
        👩‍🎨 Привет! Вы вошли как SMM-специалист.

        Я помогу вам:

        • отслеживать охваты, вовлечённость, подписки и комментарии  
        • видеть топ-посты и слабые публикации по VK и Instagram  
        • анализировать видео по уровню досмотра
        • смотреть, какие посты дали заявки и записи  
        • формировать отчёты и делиться ими с врачом

        Выберите действие:
        📊 /smm_аналитика   📄 /отчёт_для_врача
        """);

            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();

            List<InlineKeyboardButton> row = List.of
                    (InlineKeyboardButton.builder()
                                    .text("📊 Smm_аналитика")
                                    .callbackData("SMM_ANALYTICS")
                                    .build(),
                            InlineKeyboardButton.builder()
                                    .text("📄 Отчёт_для_врача")
                                    .callbackData("SMM_SEND_REPORT_TO_DOCTOR")
                                    .build());
            rows.add(row);
            markup.setKeyboard(rows);
            message.setReplyMarkup(markup);
        }

        try {
            sender.execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}