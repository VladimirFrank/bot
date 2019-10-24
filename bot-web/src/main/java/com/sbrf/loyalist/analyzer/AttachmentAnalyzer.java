package com.sbrf.loyalist.analyzer;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import org.telegram.telegrambots.meta.api.objects.*;

import com.sbrf.loyalist.telegram.TelegramBot;

public class AttachmentAnalyzer {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final DateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");

    private Path baseDir;

    private TelegramBot telegramBot;

    public AttachmentAnalyzer(Path baseDir, TelegramBot telegramBot) {
        this.baseDir = baseDir;
        this.telegramBot = telegramBot;
    }

    public void analyze(Message message) {
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
        createDirForAttachment(chatTitle, String.valueOf(chatId));
        if (photos != null && !photos.isEmpty()) {
            savePhotos(chatTitle, String.valueOf(chatId), photos);
        }
    }

    private void saveVoice(String chatTitle, String chatId, Voice voice) {
        Path path = createDirForAttachment(chatTitle, chatId);
        try {
            uploadFile(path, DATE_TIME_FORMAT.format(new Date()) + ".ogg", voice.getFileId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveDocument(String chatTitle, String chatId, Document document) {
        Path path = createDirForAttachment(chatTitle, chatId);
        try {
            uploadFile(path, document.getFileName(), document.getFileId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void uploadFile(Path upPath, String file_name, String file_id) throws IOException{
        URL url = new URL("https://api.telegram.org/bot" + telegramBot.getBotToken() + "/getFile?file_id=" + file_id);
        BufferedReader in = new BufferedReader(new InputStreamReader( url.openStream()));
        String res = in.readLine();
        JSONObject jresult = new JSONObject(res);
        JSONObject path = jresult.getJSONObject("result");
        String file_path = path.getString("file_path");
        URL download = new URL("https://api.telegram.org/file/bot" + telegramBot.getBotToken() + "/" + file_path);

        FileOutputStream fos = new FileOutputStream(upPath.resolve(file_name).toString());
        System.out.println("Start upload");
        ReadableByteChannel rbc = Channels.newChannel(download.openStream());
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();
        System.out.println("Uploaded!");
    }

    private void savePhotos(String chatTitle, String chatId, List<PhotoSize> photoSizes) {
        Path path = createDirForAttachment(chatTitle, chatId);
        for (PhotoSize photo : photoSizes) {
            try {
                uploadFile(path,
                        DATE_TIME_FORMAT.format(new Date()) + "_" + photo.getWidth() + "_" + photo.getHeight() + ".jpg", photo.getFileId());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Path createDirForAttachment(String chatTitle, String chatId) {
        Date date = new Date();
        String formattedDate = DATE_FORMAT.format(date);
        String chatTitleWithoutSpaces = chatTitle.replace(" ", "_");
        Path path = Paths.get(baseDir.toString(), chatTitleWithoutSpaces + "#" + chatId, formattedDate);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return path;
    }

}
