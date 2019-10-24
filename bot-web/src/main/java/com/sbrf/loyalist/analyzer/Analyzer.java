package com.sbrf.loyalist.analyzer;

import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

public interface Analyzer {

    List<String> analyze(Message message);

}
