package com.sbrf.loyaltist.api;

import java.io.InputStream;

/**
 * Интерфейс сервиса для анализа вложений.
 */
public interface AttachmentAnalyzer {

    void checkAttachment(InputStream stream);

}
