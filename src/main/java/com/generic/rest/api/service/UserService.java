package com.generic.rest.api.service;

import java.util.Calendar;
import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.generic.rest.api.Constants;
import com.generic.rest.api.Constants.CONTROLLER.LOGIN;
import com.generic.rest.api.Constants.JWTAUTH;
import com.generic.rest.api.Constants.MSGERROR;
import com.generic.rest.api.domain.Address;
import com.generic.rest.api.domain.Role;
import com.generic.rest.api.domain.User;
import com.generic.rest.api.exception.ApiException;
import com.generic.rest.api.exception.NotFoundApiException;
import com.generic.rest.api.repository.UserRepository;
import com.generic.rest.api.service.core.BaseApiRestService;
import com.generic.rest.api.util.EncrypterUtils;
import com.generic.rest.api.util.TokenUtils;

@Service
public class UserService extends BaseApiRestService<User, UserRepository> {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AddressService addressService;
	
	@Autowired
	private TokenAuthenticationService tokenAuthenticationService;
	
	@Override
	protected UserRepository getRepository() {
		return userRepository;
	}
	
	@Override
	protected Class<User> getEntityClass() {
		return User.class;
	}
	
	public Map<String, String> attemptAuthentication(Map<String, String> credentials) throws AuthenticationException {
		User userAccount = getUserByEmailAndActive(credentials.get(LOGIN.EMAIL_FIELD), Boolean.TRUE);
		
		if (userAccount == null) {
			throw new AuthenticationCredentialsNotFoundException(MSGERROR.AUTHENTICATION_ERROR);
		}
		
		if (Boolean.FALSE.equals(EncrypterUtils.matchPassword(credentials.get(LOGIN.PASSWORD_FIELD), userAccount.getPassword()))) {
			throw new AuthenticationCredentialsNotFoundException(MSGERROR.AUTHENTICATION_ERROR);
		}
		
		return Collections.singletonMap(JWTAUTH.TOKEN, tokenAuthenticationService.generateToken(userAccount));
	}
	
	@Transactional
	@Override
	public User save(User user) throws ApiException {
		user.setPassword(EncrypterUtils.encryptPassword(user.getPassword()));
		
		setAddress(user);
		
		return super.save(user);
	}
	
	@Transactional
	@Override
	public User update(User user) throws ApiException {
		User userDatabase = getByExternalId(user.getExternalId());
		
		if (user.getId() == null) {
			user.setId(userDatabase.getId());
		}

		userDatabase.setUpdateDate(Calendar.getInstance());
      
		if (user.getActive() != null && user.getActive()) {
			user.setDeleteDate(null);
		}
		
		if (user.getPassword() == null || "".equals(user.getPassword())) {
			user.setPassword(userDatabase.getPassword());
		
		} else if (!userDatabase.getPassword().equals(user.getPassword())) {
			user.setPassword(EncrypterUtils.encryptPassword(user.getPassword()));
		}
		
		setAddress(user);
		
		return userRepository.saveAndFlush(user);
	}
	
	private void setAddress(User user) {
		if (user.getAddress().getExternalId() == null || "".equals(user.getAddress().getExternalId())) {
			user.setAddress(addressService.save(user.getAddress()));
			return;
		}
		
		try {
			Address address = addressService.getByExternalId(user.getAddress().getExternalId());
			address.setUser(user);
			user.setAddress(addressService.merge(user.getAddress(), address));
			
		} catch (NotFoundApiException e) {
			user.setAddress(addressService.save(user.getAddress()));
		}
	}
	
	public User getUserByEmailAndActive(String email, Boolean active) {
		return userRepository.findByEmailAndActive(email, active);
	}
	
   	public Boolean allowUserAccess(String authorization, String userAccountExternalId) {
   		String token = TokenUtils.getTokenFromAuthorizationHeader(authorization);
   		String userAccountExternalIdClaim = tokenAuthenticationService.getTokenClaim(token, Constants.JWTAUTH.CLAIM_USER_EXTERNAL_ID);
		
   		return userAccountExternalId.equals(userAccountExternalIdClaim) || allowAdminAccess(authorization);
   	}
	
   	public Boolean allowAdminAccess(String authorization) {
   		String token = TokenUtils.getTokenFromAuthorizationHeader(authorization);
   		String roleClaim = tokenAuthenticationService.getTokenClaim(token, Constants.JWTAUTH.CLAIM_ROLE);
		
   		return Role.ADMIN.name().equals(roleClaim);
   	}

}