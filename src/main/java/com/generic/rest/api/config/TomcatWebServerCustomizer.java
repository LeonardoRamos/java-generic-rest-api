package com.generic.rest.api.config;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.stereotype.Component;

import com.generic.rest.api.Constants;

@Component
public class TomcatWebServerCustomizer implements EmbeddedServletContainerCustomizer {

    @Override
    public void customize(ConfigurableEmbeddedServletContainer container) {
        if (container instanceof TomcatEmbeddedServletContainerFactory) {
            TomcatEmbeddedServletContainerFactory containerFactory = (TomcatEmbeddedServletContainerFactory) container;
            containerFactory.addConnectorCustomizers(new TomcatConnectorCustomizer() {
                
            	@Override
                public void customize(Connector connector) {
                    connector.setAttribute(Constants.RELAXED_SERVER_CHARS_KEY, Constants.RELAXED_SERVER_CHARS_VALUE);
                    connector.setAttribute(Constants.RELAXED_SERVER_PATH_KEY, Constants.RELAXED_SERVER_PATH_VALUE);
                }
            });
        }
    }

}