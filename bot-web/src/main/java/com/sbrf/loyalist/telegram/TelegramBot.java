package com.sbrf.loyalist.telegram;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.groupadministration.KickChatMember;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatAdministrators;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.sbrf.loyalist.analyzer.Analyzer;
import com.sbrf.loyalist.analyzer.AttachmentAnalyzer;
import com.sbrf.loyalist.analyzer.MessageTextAnalyzer;
import com.sbrf.loyalist.dao.BlackListDao;
import com.sbrf.loyalist.entity.BlackList;
import com.sbrf.loyalist.entity.BlackListEntity;
import com.sbrf.loyalist.entity.SberChat;
import com.sbrf.loyalist.entity.SberUser;

public class TelegramBot extends TelegramLongPollingBot {

    private static String botToken = "921059870:AAF1u0fyctw6Ihpv0PqgJDI1vk84ctZTFi8";

    private static String botUserName = "@spitchenko_v_bot";

    private BlackListDao blackListDao;

    private List<SberChat> chats = new ArrayList<>();

    private Map<Integer, SberUser> sberUsers = new HashMap<>();

    private Analyzer textAnalyzer = new MessageTextAnalyzer();

    private AttachmentAnalyzer attachmentAnalyzer;

    public TelegramBot(BlackListDao blackListDao) {
        super();
        this.blackListDao = blackListDao;
        String userHomePath = System.getProperty("user.home");
        Path attachmentsPath = Paths.get(userHomePath, "attachments");
        this.attachmentAnalyzer = new AttachmentAnalyzer(attachmentsPath, this);
    }

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
                    handleGroupMessage(message);
                }
            }
        }
    }

    public List<SberUser> kick() {
        List<SberUser> kickedUsers = new ArrayList<>();
        BlackList blackList = blackListDao.get();
        List<BlackListEntity> blackListEntities = blackList.get();
        for (BlackListEntity blackListEntity : blackListEntities) {
            SberUser sberUserToBan = null;
            for (Map.Entry<Integer, SberUser> sberUserEntry : sberUsers.entrySet()) {
                String phone = sberUserEntry.getValue().getTelephone();
                if (phone != null && phone.equals(blackListEntity.getTelephone())) {
                    sberUserToBan = sberUserEntry.getValue();
                    break;
                }
            }

            if (sberUserToBan != null) {
                for (SberChat chat : chats) {
                    if (chat.containsUser(sberUserToBan)) {
                        kickUserFromChat(chat.getChatId(), sberUserToBan.getId());
                        chat.deleteUserIfInChat(sberUserToBan.getId());
                        kickedUsers.add(sberUserToBan);
                    }
                }
            }
        }

        return kickedUsers;
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

    private void handleGroupMessage(Message message) {
        Long chatId = message.getChatId();
        if (!message.getNewChatMembers().isEmpty()) {
            for (User newUser : message.getNewChatMembers()) {
                Integer newUserId = newUser.getId();
                // Если не знаем телефон пользователя, то кикаем его.
                if (!isVerifiedUser(newUser)) {
                    System.out.println("Обнаружен незарегистрированный пользователь. " +
                            "Пользователь должен написать личное сообщение боту и зарегистрироваться");
                    kickUserFromChat(chatId, newUserId);
                } else {
                    SberUser existingUser = sberUsers.get(newUserId);
                    if (isUserInBlackList(existingUser)) {
                        System.out.println("Попытка добавить заблокированного пользователя в чат. Пользователь будет исключен");
                        kickUserFromChat(chatId, newUserId);
                    } else {
                        // Если знаем телефон пользователя, то сохраняем его в структуру чата.
                        System.out.println("Пользователь нам известен, сохраним его в чате");
                        saveUserInChat(chatId, newUserId);
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
            attachmentAnalyzer.analyze(message);
        }
    }

    private void saveUserInChat(Long newChatId, Integer newUserId) {
        SberUser sberUser = sberUsers.get(newUserId);
        SberChat newChat = new SberChat(newChatId);
        boolean chatExists = false;
        for (SberChat chat : chats) {
            if (chat.equals(newChat)) {
                chatExists = true;

                if (!chat.containsUser(sberUser)) {
                    chat.addUser(sberUser);
                }
            }
        }

        if (!chatExists) {
            chats.add(newChat);
            newChat.addUser(sberUser);
        }
    }



    private void kickUserFromChat(Long chatId, Integer userId) {
        try {
            KickChatMember kickChatMember = new KickChatMember(chatId, userId);
            kickChatMember.setUntilDate(31);
            this.execute(kickChatMember);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private boolean isUserInBlackList(SberUser user) {
        BlackList blackList = blackListDao.get();
        String telephone = user.getTelephone();

        return blackList.isUserInBlackList(telephone);
    }

    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new TelegramBot(null));
            System.out.println("Бот зарегистрирован");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}

