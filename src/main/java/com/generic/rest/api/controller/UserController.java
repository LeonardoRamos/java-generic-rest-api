package com.generic.rest.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.generic.rest.api.Constants.CONTROLLER;
import com.generic.rest.api.controller.core.BaseApiRestController;
import com.generic.rest.api.domain.User;
import com.generic.rest.api.service.UserService;

@RestController
@RequestMapping(CONTROLLER.USER.PATH)
public class UserController extends BaseApiRestController<User, UserService>{
	
	@Autowired
	private UserService userService;

	@Override
	public UserService getService() {
		return userService;
	}
    
}