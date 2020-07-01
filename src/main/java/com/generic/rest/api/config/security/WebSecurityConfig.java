package com.generic.rest.api.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.generic.rest.api.Constants.JWT_AUTH;
import com.generic.rest.api.service.TokenAuthenticationService;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebMvcConfigurerAdapter {
	
	@Autowired
	private TokenAuthenticationService tokenAuthenticationService;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new AuthorizationInterceptor(tokenAuthenticationService))
			.addPathPatterns(JWT_AUTH.ALL_PATH_CORS_REGEX);
	}
		
}