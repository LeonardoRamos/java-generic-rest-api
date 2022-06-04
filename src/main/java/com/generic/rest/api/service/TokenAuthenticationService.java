package com.generic.rest.api.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.generic.rest.api.Constants;
import com.generic.rest.api.Constants.JWT_AUTH;
import com.generic.rest.api.Constants.MSG_ERROR;
import com.generic.rest.api.domain.User;
import com.generic.rest.api.util.StringParserUtils;
import com.generic.rest.api.util.TokenUtils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class TokenAuthenticationService {
	
	private static final Logger log = LoggerFactory.getLogger(TokenAuthenticationService.class);

	@Value(JWT_AUTH.EXPIRATION_TIME)
	private Long expirationTime; 
	
	@Value(JWT_AUTH.SECRET)
	private String secret;
	
	@Value(JWT_AUTH.TOKEN_PREFIX)
	private String tokenPrefix;
	
	@Value(JWT_AUTH.HEADER_STRINGS)
	private String headerString;
	
	public String generateToken(User user) {
		Map<String, Object> claims = new HashMap<>();
		claims.put(JWT_AUTH.CLAIM_USER_EXTERNAL_ID, user.getExternalId());
		claims.put(JWT_AUTH.CLAIM_EMAIL, user.getEmail());
		claims.put(JWT_AUTH.CLAIM_ROLE, user.getRole().name());
		claims.put(JWT_AUTH.CLAIM_NAME, user.getName());
		
		return Jwts.builder()
		 		 .setSubject(user.getName())
		 		 .setClaims(claims)
		 		 .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
				 .signWith(SignatureAlgorithm.HS512, secret)
				 .compact();
	}
	
	public Boolean validateToken(String token) {
		if (token != null) {
			try {
				String userAccountExternalId = (String) Jwts.parser()
						.setSigningKey(secret)
						.parseClaimsJws(StringParserUtils.replace(token, tokenPrefix, ""))
						.getBody()
						.get(Constants.JWT_AUTH.CLAIM_USER_EXTERNAL_ID);
				
				if (userAccountExternalId != null && !"".equals(userAccountExternalId)) {
					return Boolean.TRUE;
				}
			} catch (Exception e) {
				log.error(MSG_ERROR.AUTH_ERROR_INVALID_TOKEN, token);
				return Boolean.FALSE;
			}
		}
		
		log.error(MSG_ERROR.AUTH_ERROR_INVALID_TOKEN, token);
		return Boolean.FALSE;
	}
	
	public String getTokenClaim(String token, String tokenClaim) {
		if (token != null) {
			try {
				String claim = (String) Jwts.parser()
						.setSigningKey(secret)
						.parseClaimsJws(StringParserUtils.replace(token, tokenPrefix, ""))
						.getBody()
						.get(tokenClaim);
				
				if (claim != null && !"".equals(claim)) {
					return claim;
				}
			} catch (Exception e) {
				log.error(MSG_ERROR.AUTH_ERROR_INVALID_TOKEN, token);
				return null;
			}
		}
		
		log.error(MSG_ERROR.AUTH_ERROR_INVALID_TOKEN, token);
		return null;
	}
	
	public String getTokenFromRequest(HttpServletRequest request) {
		String token = request.getHeader(JWT_AUTH.X_ACCESS_TOKEN);
		if (token != null && !"".equals(token)) {
			return token;
		}
		
		token = (String) request.getAttribute(JWT_AUTH.TOKEN);
		if (token != null && !"".equals(token)) {
			return token;
		}
		
		return TokenUtils.getTokenFromAuthorizationHeader((String) request.getHeader(JWT_AUTH.AUTHORIZATION));
	}

}