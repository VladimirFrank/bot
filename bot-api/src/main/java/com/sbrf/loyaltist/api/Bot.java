package com.sbrf.loyaltist.api;

/**
 * Интерфейс бота участника чата или группы.
 */
public interface Bot {

    void join(String url);

    void sendMessage(String receiver, String message);

    void excludeUser(String name);

}
