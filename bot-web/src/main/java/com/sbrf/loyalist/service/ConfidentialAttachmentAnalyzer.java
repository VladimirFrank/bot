package com.sbrf.loyalist.service;

import com.sbrf.loyaltist.api.AttachmentAnalyzer;

import java.io.InputStream;

/**
 * Сервис анализа вложений на предмет нарушения требований к передачи
 * конфиденциальной информации.
 */
public class ConfidentialAttachmentAnalyzer implements AttachmentAnalyzer {

    @Override
    public void checkAttachment(InputStream stream) {

    }

}
