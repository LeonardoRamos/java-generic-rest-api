package com.generic.rest.api.config.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.generic.rest.api.Constants.MSGERROR;
import com.generic.rest.api.exception.UnauthorizedApiException;
import com.generic.rest.api.service.TokenAuthenticationService;

@Component
public class AuthorizationInterceptor implements HandlerInterceptor {

	@Autowired
	private TokenAuthenticationService tokenAuthenticationService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object objectHandler) {
		if (objectHandler instanceof HandlerMethod) {
			HandlerMethod handler = (HandlerMethod) objectHandler;
            
			NoSecurity noSecurity = handler.getMethodAnnotation(NoSecurity.class);
            if (noSecurity == null) {
            	
            	String token = tokenAuthenticationService.getTokenFromRequest(request);
            	if (token == null || "".equals(token) || Boolean.FALSE.equals(tokenAuthenticationService.validateToken(token))) {
            		throw new UnauthorizedApiException(MSGERROR.AUTHORIZATION_TOKEN_NOT_VALID);
            	}
            }
		}
            
        return Boolean.TRUE;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		/* Does not need postHandle implementation */
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		/* Does not need afterCompletion implementation */
	}
	
}