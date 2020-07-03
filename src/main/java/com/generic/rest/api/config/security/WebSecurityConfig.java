package com.generic.rest.api.config.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.generic.rest.api.Constants.JWT_AUTH;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {
	
	@Autowired
	private AuthorizationInterceptor authorizationInterceptor;
	
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().cors();
    }
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(authorizationInterceptor)
			.addPathPatterns(JWT_AUTH.ALL_PATH_CORS_REGEX);
	}
	
	@Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        
        configuration.setAllowedOrigins(Arrays.asList(JWT_AUTH.ALL_PATH_ORIGIN_REGEX));
        configuration.setAllowedMethods(Arrays.asList(
        		HttpMethod.HEAD.name(), 
        		HttpMethod.OPTIONS.name(), 
        		HttpMethod.GET.name(), 
        		HttpMethod.PUT.name(), 
        		HttpMethod.POST.name(), 
        		HttpMethod.DELETE.name(), 
        		HttpMethod.PATCH.name()));
        
        configuration.setAllowCredentials(true);
        configuration.addExposedHeader(JWT_AUTH.CONTENT_DISPOSITION);
        configuration.setAllowedHeaders(Arrays.asList(
        		JWT_AUTH.AUTHORIZATION, 
        		JWT_AUTH.CACHE_CONTROL, 
        		JWT_AUTH.CONTENT_TYPE,
        		JWT_AUTH.X_ACCESS_TOKEN));

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(JWT_AUTH.ALL_PATH_CORS_REGEX, configuration);
        
        return source;
    }
    
}