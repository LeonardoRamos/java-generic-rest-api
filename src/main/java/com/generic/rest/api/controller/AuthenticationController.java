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

import com.generic.rest.api.Constants.CONTROLLER;
import com.generic.rest.api.Constants.JWT_AUTH;
import com.generic.rest.api.config.security.WithoutSecurity;
import com.generic.rest.api.exception.NotFoundApiException;
import com.generic.rest.api.service.UserService;

@RestController
@RequestMapping(CONTROLLER.LOGIN.PATH)
public class AuthenticationController {
	
	private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);
	
	@Autowired
	private UserService userService;
	
	@WithoutSecurity
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> authenticate(@RequestBody  Map<String, String> credentials) throws NotFoundApiException {
		log.info("Processing login for user credentials: [{}]", credentials != null ? credentials.get(JWT_AUTH.CLAIM_EMAIL) : null);
		return new ResponseEntity<>(userService.attemptAuthentication(credentials), HttpStatus.OK);
	}
	
}