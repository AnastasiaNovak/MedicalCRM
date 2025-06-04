package com.example.medicalcrm.bot.handler;

import com.example.medicalcrm.entity.Application;
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
public class DoctorCommandHandler {

    public void handle(Update update, AbsSender sender, ApplicationService applicationService) {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.enableHtml(true);

        if (text.equalsIgnoreCase("/отчёт") || text.equalsIgnoreCase("/отчет")) {
            message.setText("""
<b>🧠 Отчёт за апрель</b>

📣 <b>Реклама (Таргет):</b>
— Подписки: 142
— Сообщений: 26
— Записались: 9
💸 Цена клика: 19.3₽
👁️ Охват: 6 820

🌱 <b>Органика:</b>
— Подписки: 78
— Сообщений: 12
— Записались: 4
👁️ Охват: 1 240

📌 <b>Топ-посты:</b>
VK: “Пациентка из Екатеринбурга — фото до/после”
https://vk.com/...
Insta: “Отзыв о биовинтах” — см. пост от 12.04
""");
        } else if (text.equalsIgnoreCase("/пациенты") || text.equalsIgnoreCase("/пациент")) {
            List<Application> applications = applicationService.getAllApplications();
            StringBuilder response = new StringBuilder("📋 Список новых заявок:\n\n");

            if (applications.isEmpty()) {
                response.append("Нет новых заявок.");
            } else {
                for (Application app : applications) {
                    String name = app.getPatient() != null ? app.getPatient().getName() : "";
                    String username = app.getPatient() != null ? app.getPatient().getUsername() : "";
                    String applicationText = app.getText();

                    response.append("<b>").append(name).append("</b>")
                            .append(" (@").append(username).append(")")
                            .append("\n")
                            .append(applicationText)
                            .append("\n\n");
                }
            }
            message.setText(response.toString());
        }
        else {
            message.setText("""
        <b>Здравствуйте, доктор!</b>
        Я — ваш ассистент в Telegram. Помогаю:

        • получать заявки от пациентов  
        • отмечать, кто записался или оперировался
        • получать аналитику по соцсетям и рекламе  
        • отвечать за вас, если вы заняты (с помощью ИИ)
        • подсказывать, готов ли пациент к платному лечению (на основе переписки)

        <b>Выберите действие:</b>
        """);

            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();

            List<InlineKeyboardButton> row = List.of
                    (InlineKeyboardButton.builder()
                    .text("👥 Пациенты")
                    .callbackData("DOCTOR_PATIENTS")
                    .build(),
                    InlineKeyboardButton.builder()
                            .text("📄 Отчёт")
                            .callbackData("DOCTOR_REPORT")
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
