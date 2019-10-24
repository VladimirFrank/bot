package com.sbrf.loyalist.analyzer;

import org.mockito.Mockito;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AttachmentAnalyzer implements Analyzer {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private String baseDir;
    private TelegramLongPollingBot bot;

    public AttachmentAnalyzer(String baseDir, TelegramLongPollingBot bot) {
        this.baseDir = baseDir;
        this.bot = bot;
    }

    @Override
    public void analyze(Message message) {
        // TODO Получить ИД и Имя чата
        Long chatId = message.getChat().getId();
        String chatName = message.getChat().getFirstName();

        Voice voice = message.getVoice();
        if (voice != null)
            saveVoice(chatName, String.valueOf(chatId), voice);

        Document document = message.getDocument();
        if (document != null)
            saveDocument(chatName, String.valueOf(chatId), document);

        List<PhotoSize> photos = message.getPhoto();
        if (photos != null && !photos.isEmpty())
            savePhotos(chatName, String.valueOf(chatId), photos);

        createDirForAttachment(chatName, String.valueOf(chatId));
    }

    @Override
    public void notifyAdmins(String message) {

    }

    private void saveVoice(String chatName, String chatId, Voice voice) {
        createDirForAttachment(chatName, chatId);
        //bot.downloadFile()

    }

    private void saveDocument(String chatName, String chatId, Document document) {
        createDirForAttachment(chatName, chatId);


    }

    private void savePhotos(String chatName, String chatId, List<PhotoSize> photoSizes) {
        createDirForAttachment(chatName, chatId);


    }

    private void createDirForAttachment(String chatName, String chatId) {
        Date date = new Date();
        String formattedDate = DATE_FORMAT.format(date);
        String chatNameWithoutSpaces = chatName.replace(" ", "_");
        Path path = Paths.get(baseDir, chatNameWithoutSpaces + "#" + chatId, formattedDate);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Chat chat = Mockito.mock(Chat.class);
        Mockito.when(chat.getId()).thenReturn(5006666900L);
        Mockito.when(chat.getFirstName()).thenReturn("Мой супер чат");

        Message message = Mockito.mock(Message.class);
        Mockito.when(message.getChat()).thenReturn(chat);

        AttachmentAnalyzer attachmentAnalyzer = new AttachmentAnalyzer("C:\\work\\attachments", null);
        attachmentAnalyzer.analyze(message);
    }

}
