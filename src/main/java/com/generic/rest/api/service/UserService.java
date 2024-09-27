package com.generic.rest.api.service;

import java.util.Calendar;
import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.generic.rest.api.ApiConstants.CONTROLLER.LOGIN;
import com.generic.rest.api.ApiConstants.MSGERROR;
import com.generic.rest.api.domain.Address;
import com.generic.rest.api.domain.User;
import com.generic.rest.api.repository.UserRepository;
import com.generic.rest.core.BaseConstants.JWTAUTH;
import com.generic.rest.core.exception.ApiException;
import com.generic.rest.core.exception.NotFoundApiException;
import com.generic.rest.core.service.AuthenticationService;
import com.generic.rest.core.service.impl.BaseApiRestServiceImpl;
import com.generic.rest.core.service.impl.TokenService;
import com.generic.rest.core.util.encrypter.BCryptPasswordEncrypter;

@Service
public class UserService extends BaseApiRestServiceImpl<User, UserRepository> implements AuthenticationService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AddressService addressService;
	
	@Autowired
	private TokenService tokenService;
	
	private BCryptPasswordEncrypter passwordEncrypter = new BCryptPasswordEncrypter();
	
	@Override
	protected UserRepository getRepository() {
		return userRepository;
	}
	
	@Override
	protected Class<User> getEntityClass() {
		return User.class;
	}
	
	@Override
	public Map<String, String> attemptAuthentication(Map<String, String> credentials) throws AuthenticationException {
		User userAccount = getUserByEmailAndActive(credentials.get(LOGIN.EMAIL_FIELD), true);
		
		if (userAccount == null) {
			throw new AuthenticationCredentialsNotFoundException(MSGERROR.AUTHENTICATION_ERROR);
		}
		
		if (!this.passwordEncrypter.matchPassword(credentials.get(LOGIN.PASSWORD_FIELD), userAccount.getPassword())) {
			throw new AuthenticationCredentialsNotFoundException(MSGERROR.AUTHENTICATION_ERROR);
		}
		
		return Collections.singletonMap(JWTAUTH.TOKEN, this.tokenService.generateToken(userAccount));
	}
	
	@Transactional
	@Override
	public User save(User user) throws ApiException {
		user.setPassword(this.passwordEncrypter.encryptPassword(user.getPassword()));
		
		this.setAddress(user);
		
		User userSaved = super.save(user);
		userSaved.getAddress().setUser(userSaved);
		
		this.addressService.save(userSaved.getAddress());
		
		return userSaved;
	}
	
	@Transactional
	@Override
	public User update(User user) throws ApiException {
		User userDatabase = this.getByExternalId(user.getExternalId());
		
		if (user.getId() == null) {
			user.setId(userDatabase.getId());
		}

		userDatabase.setUpdateDate(Calendar.getInstance());
		
		if (user.isActive()) {
			user.setDeleteDate(null);
		}
		
		if (user.getPassword() == null || "".equals(user.getPassword())) {
			user.setPassword(userDatabase.getPassword());
		
		} else if (!userDatabase.getPassword().equals(user.getPassword())) {
			user.setPassword(this.passwordEncrypter.encryptPassword(user.getPassword()));
		}
		
		this.setAddress(user);
		
		return this.getRepository().saveAndFlush(user);
	}
	
	private void setAddress(User user) {
		if (user.getAddress().getExternalId() == null || "".equals(user.getAddress().getExternalId())) {
			user.setAddress(this.addressService.save(user.getAddress()));
			return;
		}
		
		try {
			Address address = this.addressService.getByExternalId(user.getAddress().getExternalId());
			address.setUser(user);
			user.setAddress(this.addressService.merge(user.getAddress(), address));
			
		} catch (NotFoundApiException e) {
			user.setAddress(this.addressService.save(user.getAddress()));
		}
	}
	
	public User getUserByEmailAndActive(String email, boolean active) {
		return this.getRepository().findByEmailAndActive(email, active);
	}
	
}