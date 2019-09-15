package com.sbrf.loyalist.configuration;

import com.sbrf.loyalist.controller.Index;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Корневая конфигурация приложения.
 */
@Configuration
public class RootSpringConfiguration {

    @Bean
    public Index index() {
        return new Index();
    }

}
