package com.example.medicalcrm.bot.handler.callback;

import com.example.medicalcrm.bot.cache.DeletedApplicationCache;
import com.example.medicalcrm.bot.util.ButtonUtils;
import com.example.medicalcrm.entity.Application;
import com.example.medicalcrm.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.time.LocalDate;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class MarkHandler {

    private final ApplicationService applicationService;
    private final DeletedApplicationCache deletedApplicationCache;

    public void handle(Update update, AbsSender sender) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        String callbackData = callbackQuery.getData();

        // Обработка кнопки "Записать" или повторной записи после удаления
        if (callbackData.startsWith("mark_")) {
            // Извлекаем ID заявки из callbackData
            String appIdStr = callbackData.substring("mark_".length());
            Long appId = Long.parseLong(appIdStr);

            // Проверяем, есть ли заявка в базе
            Optional<Application> existing = applicationService.getApplicationById(appId);
            if (existing.isEmpty()) {
                // Если заявки нет — пробуем восстановить её из кэша
                Application restored = deletedApplicationCache.getDeletedApplications().get(appId);
                if (restored != null) {
                    // Проверяем, нет ли уже аналогичной заявки в базе
                    boolean alreadyExists = applicationService.getAllApplications().stream()
                            .anyMatch(a -> a.getPatient() != null
                                    && restored.getPatient() != null
                                    && a.getPatient().getUsername().equals(restored.getPatient().getUsername())
                                    && a.getText().equals(restored.getText()));

                    if (!alreadyExists) {
                        // Обнуляем ID и дату — подготавливаем к повторному сохранению
                        restored.setId(null);
                        restored.setCreatedAt(LocalDate.now());
                        applicationService.saveApplication(restored);
                    }
                }

                // Обновляем кнопки: "✅ Записан" и "Удалить"
                InlineKeyboardButton markedButton = ButtonUtils.createMarkButton(true, appId);
                InlineKeyboardButton deleteButton = ButtonUtils.createDeleteButton(false, appId);
                ButtonUtils.updateButtons(sender, callbackQuery, markedButton, deleteButton);
                return;
            }

            // Если заявка есть в базе — просто обновляем кнопки
            InlineKeyboardButton markedButton = ButtonUtils.createMarkButton(true, appId);
            InlineKeyboardButton deleteButton = ButtonUtils.createDeleteButton(false, appId);
            ButtonUtils.updateButtons(sender, callbackQuery, markedButton, deleteButton);
        }
    }
}
