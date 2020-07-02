package com.generic.rest.api.config;

import java.util.TimeZone;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.generic.rest.api.Constants.JWT_AUTH;

@Configuration
public class WebConfig extends WebMvcConfigurationSupport {
	
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping(JWT_AUTH.ALL_PATH_CORS_REGEX)
                .allowedMethods(
                		HttpMethod.HEAD.name(), 
                		HttpMethod.OPTIONS.name(), 
                		HttpMethod.GET.name(), 
                		HttpMethod.PUT.name(), 
                		HttpMethod.POST.name(), 
                		HttpMethod.DELETE.name(), 
                		HttpMethod.PATCH.name());
    }
    
    @Bean
	public Jackson2ObjectMapperBuilderCustomizer jacksonObjectMapperCustomization() {
        return builder -> {
        	builder.timeZone(TimeZone.getDefault());
        };
	}
    
}