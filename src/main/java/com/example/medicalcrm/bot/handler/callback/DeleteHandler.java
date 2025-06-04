package com.example.medicalcrm.bot.handler.callback;

import com.example.medicalcrm.bot.cache.DeletedApplicationCache;
import com.example.medicalcrm.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import com.example.medicalcrm.bot.util.ButtonUtils;

@RequiredArgsConstructor
@Component
public class DeleteHandler {

    private final ApplicationService applicationService;
    private final DeletedApplicationCache deletedApplicationCache;

    public void handle(Update update, AbsSender sender) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        String callbackData = callbackQuery.getData();
        Long chatId = callbackQuery.getMessage().getChatId();

        // Обработка нажатия на кнопку "Удалить"
        if (callbackData.startsWith("delete_")) {
            Long appId = Long.parseLong(callbackData.substring("delete_".length()));

            // Извлекаем заявку по ID, сохраняем в кэш удалённых и удаляем из базы
            applicationService.getApplicationById(appId).ifPresent(app -> {
                deletedApplicationCache.getDeletedApplications().put(appId, app);
                applicationService.deleteApplicationById(appId);
            });

            // Создаём кнопку "Записать" вместо "Записан"
            InlineKeyboardButton markButton = ButtonUtils.createMarkButton(false, appId);

            // Создаём кнопку "Удалено ❌"
            InlineKeyboardButton deletedButton = ButtonUtils.createDeleteButton(true, appId);

            // Обновляем интерфейс с новыми кнопками
            ButtonUtils.updateButtons(sender, callbackQuery, markButton, deletedButton);
        }
    }
}
