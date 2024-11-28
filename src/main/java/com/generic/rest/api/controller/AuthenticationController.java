package com.generic.rest.api.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.generic.rest.api.ApiConstants.CONTROLLER;
import com.generic.rest.api.ApiConstants.CONTROLLER.LOGIN;
import com.generic.rest.api.service.UserService;
import com.generic.rest.core.config.security.NoSecurity;
import com.generic.rest.core.exception.NotFoundApiException;

@RestController
@RequestMapping(CONTROLLER.LOGIN.PATH)
public class AuthenticationController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);
	
	private UserService userService;
	
	@NoSecurity
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> authenticate(@RequestBody  Map<String, String> credentials) throws NotFoundApiException {
		LOGGER.info("Processing login for user credentials: [{}]", credentials != null ? credentials.get(LOGIN.EMAIL_FIELD) : null);
		return new ResponseEntity<>(userService.attemptAuthentication(credentials), HttpStatus.OK);
	}

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
}