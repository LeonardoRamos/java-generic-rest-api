package com.generic.rest.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.generic.rest.api.Constants;
import com.generic.rest.api.domain.Role;
import com.generic.rest.api.domain.User;
import com.generic.rest.api.exception.ApiException;
import com.generic.rest.api.repository.UserRepository;
import com.generic.rest.api.service.core.ApiRestService;
import com.generic.rest.api.util.EncrypterUtils;
import com.generic.rest.api.util.TokenUtils;

@Service
public class UserService extends ApiRestService<User, UserRepository> {
	
	@Autowired
	private UserRepository userRepository;
	
	@Override
	protected UserRepository getRepository() {
		return userRepository;
	}
	
	@Override
	protected Class<User> getEntityClass() {
		return User.class;
	}
	
	@Override
	public User save(User user) throws ApiException {
		user.setPassword(EncrypterUtils.encryptPassword(user.getPassword()));
		return super.save(user);
	}
	
	public User getUserByEmailAndActive(String email, Boolean active) {
		return userRepository.findByEmailAndActive(email, active);
	}
	
   	public Boolean allowUserAccess(String authorization, String userAccountSlug) {
   		String token = TokenUtils.getTokenFromAuthorizationHeader(authorization);
   		String userAccountSlugClaim = tokenAuthenticationService.getTokenClaim(token, Constants.JWT_AUTH.CLAIM_USER_SLUG);
		
   		return userAccountSlug.equals(userAccountSlugClaim) || allowAdminAccess(authorization);
   	}
	
   	public Boolean allowAdminAccess(String authorization) {
   		String token = TokenUtils.getTokenFromAuthorizationHeader(authorization);
   		String roleClaim = tokenAuthenticationService.getTokenClaim(token, Constants.JWT_AUTH.CLAIM_ROLE);
		
   		return Role.ADMIN.name().equals(roleClaim);
   	}

}