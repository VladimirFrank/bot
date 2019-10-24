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

public class AttachmentAnalyzer {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private String baseDir;

    public AttachmentAnalyzer(String baseDir) {
        this.baseDir = baseDir;
    }

    public void analyze(Message message) {
        // TODO Получить ИД и Имя чата
        Long chatId = message.getChat().getId();
        String chatTitle = message.getChat().getTitle();

        Voice voice = message.getVoice();
        if (voice != null) {
            saveVoice(chatTitle, String.valueOf(chatId), voice);
        }
        Document document = message.getDocument();
        if (document != null) {
            saveDocument(chatTitle, String.valueOf(chatId), document);
        }
        List<PhotoSize> photos = message.getPhoto();
        if (photos != null && !photos.isEmpty()) {
            savePhotos(chatTitle, String.valueOf(chatId), photos);
        }
        createDirForAttachment(chatTitle, String.valueOf(chatId));
    }

    private void saveVoice(String chatTitle, String chatId, Voice voice) {
        createDirForAttachment(chatTitle, chatId);
    }

    private void saveDocument(String chatTitle, String chatId, Document document) {
        createDirForAttachment(chatTitle, chatId);
    }

    private void savePhotos(String chatTitle, String chatId, List<PhotoSize> photoSizes) {
        createDirForAttachment(chatTitle, chatId);


    }

    private void createDirForAttachment(String chatTitle, String chatId) {
        Date date = new Date();
        String formattedDate = DATE_FORMAT.format(date);
        String chatTitleWithoutSpaces = chatTitle.replace(" ", "_");
        Path path = Paths.get(baseDir, chatTitleWithoutSpaces + "#" + chatId, formattedDate);
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

        AttachmentAnalyzer attachmentAnalyzer = new AttachmentAnalyzer("C:\\work\\attachments");
        attachmentAnalyzer.analyze(message);
    }

}
