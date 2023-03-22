package com.generic.rest.api.service.core;

import java.util.Map;

import org.springframework.security.core.AuthenticationException;

public interface AuthenticationService {

	public Map<String, String> attemptAuthentication(Map<String, String> credentials) throws AuthenticationException;
	
}