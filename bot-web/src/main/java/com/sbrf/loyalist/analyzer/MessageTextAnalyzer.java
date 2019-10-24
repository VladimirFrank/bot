package com.sbrf.loyalist.analyzer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.telegram.telegrambots.meta.api.objects.Message;

public class MessageTextAnalyzer implements Analyzer {

    private static final List<String> CONFIDENTIAL_WORDS =
            Arrays.asList(
                    "паспорт",
                    "фио",
                    "скриншот",
                    "экран",
                    "монитор",
                    "телефон",
                    "клиент"
            );

    private static final String POTENTIAL_CONFIDENTIAL_CRIME = "Потенциальное нарушение правил " +
            "обращения с конфиденциальной информацией. При необходимости предпринять действия по регламенту";

    @Override
    public List<String> analyze(Message message) {
        String messageText = message.getText();
        List<String> validationResult = new ArrayList<>();
        if (messageText != null && !messageText.isBlank()) {
            for (String confidentialWord : CONFIDENTIAL_WORDS) {
                if (messageText.toLowerCase().contains(confidentialWord)) {
                    validationResult.add(confidentialWord);
                    System.out.println(POTENTIAL_CONFIDENTIAL_CRIME);
                }
            }
        }
        return validationResult;
    }

}
