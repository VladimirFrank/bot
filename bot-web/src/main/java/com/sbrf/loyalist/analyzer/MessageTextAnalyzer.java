package com.sbrf.loyalist.analyzer;

import com.sbrf.loyalist.entity.SberUser;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MessageTextAnalyzer implements Analyzer {

    private static final List<String> CONFIDENTIAL_WORDS =
            Arrays.asList(
                    "паспорт",
                    "фио",
                    "скриншот",
                    "экран",
                    "монитор",
                    "телефон"
            );

    private static final String POTENTIAL_CONFIDENTIAL_CRIME = "Потенциальное нарушение правил " +
            "обращения с конфиденциальной информацией. При необходимости предпринять действия по регламенту";

    private List<SberUser> admins;

    public MessageTextAnalyzer(List<SberUser> admins) {
        this.admins = admins;
    }

    @Override
    public void analyze(Message message) {
        String messageText = message.getText();
        boolean hasConfidential = false;
        if (messageText != null && !messageText.isBlank()) {
            for (String confidentialWord : CONFIDENTIAL_WORDS) {
                if (messageText.toLowerCase().contains(confidentialWord)) {
                    hasConfidential = true;
                    System.out.println(POTENTIAL_CONFIDENTIAL_CRIME);
                    notifyAdmins(messageText);
                    // Достаточно найти одно вхождение для уведомления админов.
                    break;
                }
            }
        }

        if (!hasConfidential) {
            System.out.println("Ничего незаконного не обнаружено");
        }
    }

    @Override
    public void notifyAdmins(String message) { // Message - это сообщение из чата
        for (SberUser admin : admins) {
            // TODO Добавить отправку сообщения админам
            //admin.ssss
        }
    }

}
