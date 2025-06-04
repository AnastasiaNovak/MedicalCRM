package com.example.medicalcrm.bot.handler;

import com.example.medicalcrm.bot.handler.callback.DeleteHandler;
import com.example.medicalcrm.bot.handler.callback.MarkHandler;
import com.example.medicalcrm.entity.Application;
import com.example.medicalcrm.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Component
public class CallbackHandler {

    private final ApplicationService applicationService;
    private final Map<Long, Application> deletedApplications = new ConcurrentHashMap<>();

    private final MarkHandler markHandler;
    private final DeleteHandler deleteHandler;

    public void handle(Update update, AbsSender sender) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        String callbackData = callbackQuery.getData();
        Long chatId = callbackQuery.getMessage().getChatId();

        // 📄 Отчёт врача
        if (callbackData.equals("DOCTOR_REPORT")) {
            handleDoctorReport(chatId, sender);
            return;
        }

        // 📋 Список пациентов
        if (callbackData.equals("DOCTOR_PATIENTS")) {
            handleDoctorPatients(sender);
            return;
        }

        // ✅ Обработка кнопки "Записать"
        if (callbackData.startsWith("mark_")) {
            markHandler.handle(update, sender);
            return;
        }

        // ❌ Обработка кнопки "Удалить"
        if (callbackData.startsWith("delete_")) {
            deleteHandler.handle(update, sender);
        }
    }

    // 👉 Обработка кнопки "Отчёт"
    private void handleDoctorReport(Long chatId, AbsSender sender) {
        String report = """
<b>🧠 Отчёт за апрель</b>
""";
        SendMessage reportMsg = new SendMessage();
        reportMsg.setChatId(chatId.toString());
        reportMsg.setText(report);
        reportMsg.enableHtml(true);

        try {
            sender.execute(reportMsg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 👉 Обработка кнопки "Пациенты"
    private void handleDoctorPatients(AbsSender sender) {
        List<Application> applications = applicationService.getAllApplications();

        if (applications.isEmpty()) {
            SendMessage noApps = new SendMessage("-1002677424734", "\uD83D\uDCCB Новых заявок пока нет.");
            noApps.enableHtml(true);
            try {
                sender.execute(noApps);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        for (Application app : applications) {
            String name = app.getPatient() != null ? app.getPatient().getName() : "";
            String username = app.getPatient() != null ? app.getPatient().getUsername() : "";
            String text = app.getText() != null ? app.getText() : "(без текста)";
            String fullInfo = String.format("<b>%s</b> (@%s)\n%s", name, username, text);

            SendMessage msg = new SendMessage("-1002677424734", fullInfo);
            msg.enableHtml(true);

            // 🔘 Кнопка "Открыть чат"
            InlineKeyboardButton openChat = InlineKeyboardButton.builder()
                    .text("\uD83D\uDCAC Открыть чат")
                    .url("https://t.me/" + username)
                    .build();

            // 🔘 Кнопка "Записать"
            InlineKeyboardButton markButton = InlineKeyboardButton.builder()
                    .text("Записать")
                    .callbackData("mark_" + app.getId())
                    .build();
// 🔘 Кнопка "Удалить"
            InlineKeyboardButton deleteButton = InlineKeyboardButton.builder()
                    .text("Удалить")
                    .callbackData("delete_" + app.getId())
                    .build();

            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            markup.setKeyboard(List.of(
                    List.of(openChat),
                    List.of(markButton, deleteButton)
            ));
            msg.setReplyMarkup(markup);

            try {
                sender.execute(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 📥 Доступ к удалённым заявкам
    public Map<Long, Application> getDeletedApplications() {
        return deletedApplications;
    }
}
