package com.generic.rest.api.service.core;

import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import com.generic.rest.api.Constants.JWT_AUTH;
import com.generic.rest.api.Constants.MSG_ERROR;
import com.generic.rest.api.Constants.CONTROLLER.LOGIN;
import com.generic.rest.api.domain.User;
import com.generic.rest.api.service.UserService;
import com.generic.rest.api.util.EncrypterUtils;

@Service
public class AuthenticationService {
	
	@Autowired
	private TokenAuthenticationService tokenAuthenticationService;
	
	@Autowired
	private UserService userService;
	
	public Map<String, String> attemptAuthentication(Map<String, String> credentials) throws AuthenticationException {
		User userAccount = userService.getUserByEmailAndActive(credentials.get(LOGIN.EMAIL_FIELD), Boolean.TRUE);
		
		if (userAccount == null) {
			throw new AuthenticationCredentialsNotFoundException(MSG_ERROR.AUTHENTICATION_ERROR);
		}
		
		if (!EncrypterUtils.matchPassword(credentials.get(LOGIN.PASSWORD_FIELD), userAccount.getPassword())) {
			throw new AuthenticationCredentialsNotFoundException(MSG_ERROR.AUTHENTICATION_ERROR);
		}
		
		return Collections.singletonMap(JWT_AUTH.TOKEN, tokenAuthenticationService.generateToken(userAccount));
	}

}