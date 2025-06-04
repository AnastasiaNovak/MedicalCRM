package com.example.medicalcrm.bot.handler;

import com.example.medicalcrm.entity.Application;
import com.example.medicalcrm.entity.Patient;
import com.example.medicalcrm.service.ApplicationService;
import com.example.medicalcrm.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Component
@RequiredArgsConstructor
public class PatientCommandHandler {

    private final PatientService patientService;

    public void handle(Update update, AbsSender sender, ApplicationService applicationService) {
        Long chatId = update.getMessage().getChatId();
        String username = update.getMessage().getFrom().getUserName();
        String firstName = update.getMessage().getFrom().getFirstName();
        String lastName = update.getMessage().getFrom().getLastName();

        // Склеиваем имя и фамилию (если есть)
        String fullName = (firstName != null ? firstName : "") +
                (lastName != null ? " " + lastName : "");

        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());

        // Если /start — отправляем приветствие
        if (update.getMessage().hasText()) {
            String text = update.getMessage().getText();

            if (text.equalsIgnoreCase("/start")) {
                message.setText("Добро пожаловать, " + firstName + "! Чем могу помочь?");
            } else {
                // Получаем пациента по Telegram ID
                Patient patient = patientService.getByTelegramId(chatId.toString());

                // Если пациента нет — создаём нового
                if (patient == null) {
                    patient = new Patient();
                    patient.setTgId(chatId.toString());
                    patient.setName(fullName.isBlank() ? "Без имени" : fullName);
                    patient.setUsername(username);
                    patientService.savePatient(patient);
                }

                // Создаём заявку
                Application app = new Application();
                app.setText(text);
                app.setPatient(patient);

                applicationService.saveApplication(app);
                message.setText("Спасибо за сообщение! Мы с вами свяжемся 🧡");
            }
        } else if (update.getMessage().hasPhoto()) {
            message.setText("Фото получено! Спасибо, мы передадим его врачу.");
        }

        try {
            sender.execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
