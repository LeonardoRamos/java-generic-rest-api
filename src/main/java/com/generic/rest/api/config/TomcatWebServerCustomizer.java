package com.generic.rest.api.config;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.generic.rest.api.BaseConstants.TOMCAT;

@Configuration
public class TomcatWebServerCustomizer {
	
	@Bean
    public ConfigurableServletWebServerFactory webServerFactory() {
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
        
        factory.addConnectorCustomizers(connector -> {
            connector.setProperty(TOMCAT.RELAXED_SERVER_CHARS_KEY, TOMCAT.RELAXED_SERVER_CHARS_VALUE);
            connector.setProperty(TOMCAT.RELAXED_SERVER_PATH_KEY, TOMCAT.RELAXED_SERVER_PATH_VALUE);
        });
     
        return factory;
    }
	
}