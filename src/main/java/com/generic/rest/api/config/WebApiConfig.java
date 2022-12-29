package com.generic.rest.api.config;

import java.util.TimeZone;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.generic.rest.api.config.health.DatabaseHealthIndicator;

@Configuration
public class WebApiConfig {

	@Bean
    @Autowired
    public HealthIndicator dbHealthIndicator(DataSource dataSource) {
        return new DatabaseHealthIndicator(dataSource);
    }
	
	@Bean
	public Jackson2ObjectMapperBuilderCustomizer jacksonObjectMapperCustomization() {
        return builder -> builder.timeZone(TimeZone.getDefault());
	}
	
}