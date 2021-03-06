package com.generic.rest.api.config.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.generic.rest.api.Constants.MSG_ERROR;
import com.generic.rest.api.exception.UnauthorizedApiException;
import com.generic.rest.api.service.TokenAuthenticationService;

@Component
public class AuthorizationInterceptor implements HandlerInterceptor {

	@Autowired
	private TokenAuthenticationService tokenAuthenticationService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object objectHandler) throws Exception {
		try {
			Boolean hasAuthorization = Boolean.TRUE;
	         
			if (objectHandler instanceof HandlerMethod) {
				HandlerMethod handler = (HandlerMethod) objectHandler;
	            
				WithoutSecurity withoutSecurity = handler.getMethodAnnotation(WithoutSecurity.class);
	            if (withoutSecurity != null) {
	            	return hasAuthorization;
	            }
				
				String token = tokenAuthenticationService.getTokenFromRequest((HttpServletRequest) request);
	            if (token == null || "".equals(token)) {
	            	hasAuthorization = Boolean.FALSE;
	    		
	            } else if (!tokenAuthenticationService.validateToken(token)) {
    				hasAuthorization = Boolean.FALSE;
	    		}
			}
	            
			if (!hasAuthorization) {
	           throw new UnauthorizedApiException(MSG_ERROR.AUTHORIZATION_TOKEN_NOT_VALID);
	        } 
	        
	        return hasAuthorization;

		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {}
	
}