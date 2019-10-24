package com.sbrf.loyalist.telegram;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.methods.send.SendContact;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.methods.groupadministration.KickChatMember;

import java.util.List;

public class TelegramBot extends TelegramLongPollingBot {

    private static String botToken = "921059870:AAF1u0fyctw6Ihpv0PqgJDI1vk84ctZTFi8";

    private static String botUserName = "@spitchenko_v_bot";

    @Override
    public void onUpdateReceived(Update update) {

    }

    public void onUpdatesReceived(List<Update> updates) {
        for (Update update : updates) {
            Message message = update.getMessage();
            if (update.hasMessage()) {
                System.out.println("Получено обновление : " + message.getDate());
                User leftUser = message.getLeftChatMember();
                if (message.getNewChatMembers() != null) {
                    for (User newUser : message.getNewChatMembers()) {
                        System.out.println("Добавлен пользователь :" + newUser);
                    }
                }                if (message.getLeftChatMember() != null) {
                    System.out.println("Удалён пользователь :" + leftUser);
                }
                if (message.getText() != null) {
                    System.out.println("Текст сообщения :" + message.getText());
                    if (message.getText().equals("command")) {
                        try {
                            Chat chat = this.execute(new GetChat(message.getChatId()));
                            SendContact sc = new SendContact();
                         } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }

                    if (message.getText().equals("kick")) {
                        try {
                            this.execute(new KickChatMember(String.valueOf(message.getChatId()), 880820055));
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        }
    }


    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new TelegramBot());

//            telegramBotsApi.registerBot(new ChannelHandlers());
//            telegramBotsApi.registerBot(new DirectionsHandlers());
//            telegramBotsApi.registerBot(new RaeHandlers());
//            telegramBotsApi.registerBot(new WeatherHandlers());
//            telegramBotsApi.registerBot(new TransifexHandlers());
//            telegramBotsApi.registerBot(new FilesHandlers());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}

