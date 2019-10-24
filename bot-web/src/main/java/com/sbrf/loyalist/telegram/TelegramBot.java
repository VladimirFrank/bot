package com.sbrf.loyalist.telegram;

import com.sbrf.loyalist.analyzer.Analyzer;
import com.sbrf.loyalist.analyzer.AttachmentAnalyzer;
import com.sbrf.loyalist.analyzer.MessageTextAnalyzer;
import com.sbrf.loyalist.entity.SberUser;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatAdministrators;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.methods.groupadministration.KickChatMember;

import java.util.*;

public class TelegramBot extends TelegramLongPollingBot {

    private static String botToken = "921059870:AAF1u0fyctw6Ihpv0PqgJDI1vk84ctZTFi8";

    private static String botUserName = "@spitchenko_v_bot";

    private Map<Integer, SberUser> sberUsers = new HashMap<>();

    private Analyzer textAnalyzer = new MessageTextAnalyzer();

    private AttachmentAnalyzer attachmentAnalyzer = new AttachmentAnalyzer("attachments");

    private User botUser;

    @Override
    public void onUpdateReceived(Update update) {

    }

    public void onUpdatesReceived(List<Update> updates) {
        for (Update update : updates) {
            Message message = update.getMessage();
            if (update.hasMessage()) {
                System.out.println("Получено обновление : " + message.getDate());
                if (message.isUserMessage()) {
                    handlerPrivateMessage(message);
                } else {
                    if (!message.getNewChatMembers().isEmpty()) {
                        for (User newUser : message.getNewChatMembers()) {
                            if (!isVerifiedUser(newUser)) {
                                System.out.println("Обнаружен незарегистрированный пользователь. " +
                                        "Пользователь должен написать личное сообщение боту и зарегистрироваться");
                                try {
                                    this.execute(
                                            new KickChatMember(
                                                    String.valueOf(message.getChatId()), newUser.getId()
                                            )
                                    );
                                } catch (TelegramApiException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } else {
                        List<String> validationResult = textAnalyzer.analyze(message);
                        if (!validationResult.isEmpty()) {
                            String analyseResults = "Сообщение содержит следующие ключевые слова:";
                            for (String keyWord : validationResult) {
                                analyseResults += keyWord + ",";
                            }
                            System.out.println(analyseResults);
                            notifyAdmins(message, analyseResults);
                        }


                    }
                }

                User leftUser = message.getLeftChatMember();
                if (message.getLeftChatMember() != null) {
                    System.out.println("Удалён пользователь :" + leftUser);
                }
            }
        }
    }

    private boolean isVerifiedUser(User newUser) {
        Integer sberUserId = newUser.getId();
        return sberUsers.containsKey(sberUserId) && sberUsers.get(sberUserId).getTelephone() != null;
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
            System.out.println("Бот зарегистрирован");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void handlerPrivateMessage(Message message) {
        Integer userId = message.getFrom().getId();
        boolean hasContact = message.hasContact();

        if (!sberUsers.containsKey(userId)) {
            SberUser sberUser = new SberUser(null, userId);
            sberUsers.put(userId, sberUser);
        }

        if (sberUsers.get(userId).getTelephone() == null && hasContact) {
            String phoneNumber = message.getContact().getPhoneNumber();
            SberUser sberUser = sberUsers.get(userId);
            if (sberUser.getTelephone() == null) {
                sberUser.setTelephone(phoneNumber);
            } else {
                System.out.println("Вы уже зарегистрированы, действий не требуется: телефон = " + phoneNumber);
            }
        } else if (sberUsers.get(userId).getTelephone() == null) {
            SendMessage sendMessage = new SendMessage()
                    .setChatId(message.getChatId())
                    .setText("Требуется предоставить ваш номер телефона");

            // create keyboard
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
            replyKeyboardMarkup.setSelective(true);
            replyKeyboardMarkup.setResizeKeyboard(true);
            replyKeyboardMarkup.setOneTimeKeyboard(true);

            // new list
            List<KeyboardRow> keyboard = new ArrayList<>();

            // first keyboard line
            KeyboardRow keyboardFirstRow = new KeyboardRow();
            KeyboardButton keyboardButton = new KeyboardButton();
            keyboardButton.setText("Отправить номер телефона").setRequestContact(true);
            keyboardFirstRow.add(keyboardButton);

            // add array to list
            keyboard.add(keyboardFirstRow);

            // add list to our keyboard
            replyKeyboardMarkup.setKeyboard(keyboard);

            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Оповестить администраторов о проблемном сообщении.
     */
    public void notifyAdmins(Message message, String analyzeResult) { // Message - это сообщение из чата
        try {
            List<Integer> administrators = getChatAdmins(String.valueOf(message.getChat().getId()));
            for(Integer admin : administrators) {
                execute(new ForwardMessage(String.valueOf(admin), message.getChatId(), message.getMessageId()));
                execute(new SendMessage(String.valueOf(admin), analyzeResult));
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private List<Integer> getChatAdmins(String chatId) {
        GetChatAdministrators getChatAdministrators = new GetChatAdministrators();
        getChatAdministrators.setChatId(chatId);
        List<Integer> admins = new ArrayList<>();
        try {
            for(ChatMember chatMember : execute(getChatAdministrators)) {
                if (!chatMember.getUser().getBot()) {
                    admins.add(chatMember.getUser().getId());
                }
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return admins;
    }

}

