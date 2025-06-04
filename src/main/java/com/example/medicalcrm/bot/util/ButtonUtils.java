package com.example.medicalcrm.bot.util;

import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.List;

public class ButtonUtils {

    // Создание кнопки "Записан" или "Записать" в зависимости от параметра marked
    public static InlineKeyboardButton createMarkButton(boolean marked, Long appId) {
        return InlineKeyboardButton.builder()
                .text(marked ? "✅ Записан" : "Записать")
                .callbackData("mark_" + appId)
                .build();
    }

    // Создание кнопки "Удалено ❌" или "Удалить" в зависимости от параметра deleted
    public static InlineKeyboardButton createDeleteButton(boolean deleted, Long appId) {
        return InlineKeyboardButton.builder()
                .text(deleted ? "Удалено ❌" : "Удалить")
                .callbackData("delete_" + appId)
                .build();
    }

    // Обновление кнопок в сообщении: заменяет старые кнопки "Записать/Записан" и "Удалить/Удалено" на новые
    public static void updateButtons(AbsSender sender, CallbackQuery callbackQuery,
                                     InlineKeyboardButton markBtn, InlineKeyboardButton deleteBtn) {
        // Получаем текущую клавиатуру из сообщения
        InlineKeyboardMarkup oldMarkup = (InlineKeyboardMarkup) callbackQuery.getMessage().getReplyMarkup();
        List<List<InlineKeyboardButton>> oldRows = oldMarkup.getKeyboard();

        // Создаём новую клавиатуру, заменяя только нужные кнопки
        List<List<InlineKeyboardButton>> newRows = oldRows.stream()
                .map(row -> row.stream()
                        .map(button -> {
                            String data = button.getCallbackData();
                            if (data != null) {
                                if (data.startsWith("mark_")) return markBtn;
                                if (data.startsWith("delete_")) return deleteBtn;
                            }
                            return button;
                        }).toList())
                .toList();

        // Создаём объект для обновления кнопок в существующем сообщении
        EditMessageReplyMarkup editMarkup = new EditMessageReplyMarkup();
        editMarkup.setChatId(callbackQuery.getMessage().getChatId().toString());
        editMarkup.setMessageId(callbackQuery.getMessage().getMessageId());
        editMarkup.setReplyMarkup(new InlineKeyboardMarkup(newRows));

        // Отправляем обновление
        try {
            sender.execute(editMarkup);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
