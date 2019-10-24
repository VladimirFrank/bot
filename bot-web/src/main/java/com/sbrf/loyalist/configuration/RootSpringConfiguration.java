package com.sbrf.loyalist.configuration;

import com.sbrf.loyalist.controller.Index;
import com.sbrf.loyalist.dao.BlackListDao;
import com.sbrf.loyalist.dao.BlackListFileDao;
import com.sbrf.loyalist.telegram.TelegramBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Корневая конфигурация приложения.
 */
@Configuration
public class RootSpringConfiguration implements ApplicationListener<ContextRefreshedEvent> {

    static {
        ApiContextInitializer.init();
    }

    private TelegramBot telegramBot;

    @Bean
    public BlackListDao blackListDao() {
        String userHomePath = System.getProperty("user.home");
        Path defaultBlackListPath = Paths.get(userHomePath, "black-list");

        return new BlackListFileDao(defaultBlackListPath.toString());
    }

    @Bean
    public TelegramBot telegramBot(@Autowired BlackListDao blackListDao) {
        telegramBot = new TelegramBot(blackListDao);

        return telegramBot;
    }

    @Bean
    public Index index(@Autowired TelegramBot telegramBot) {
        return new Index(telegramBot);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            System.out.println("Регистрация бота");
            telegramBotsApi.registerBot(telegramBot);
            System.out.println("Бот зарегистрирован");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
