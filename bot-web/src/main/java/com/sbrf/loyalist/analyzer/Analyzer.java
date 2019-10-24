package com.sbrf.loyalist.analyzer;

import org.telegram.telegrambots.meta.api.objects.Message;

public interface Analyzer {

    void analyze(Message message);

    void notifyAdmins(String message);

}
