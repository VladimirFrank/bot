package com.sbrf.loyalist;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import com.sbrf.loyalist.configuration.RootSpringConfiguration;

/**
 * Инициализатор веб-приложения.
 */
public class Initializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    protected Class<?>[] getRootConfigClasses() {
        return new Class[0];
    }

    protected Class<?>[] getServletConfigClasses() {
        return new Class[] {RootSpringConfiguration.class};
    }

    protected String[] getServletMappings() {
        return new String[] {"/"};
    }

}
