package com.generic.rest.api.service.core;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.generic.rest.api.BaseConstants;
import com.generic.rest.api.BaseConstants.JWTAUTH;
import com.generic.rest.api.BaseConstants.MSGERROR;
import com.generic.rest.api.domain.core.AuthEntity;
import com.generic.rest.api.util.StringParserUtils;
import com.generic.rest.api.util.TokenUtils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class TokenService {
	
	private static final Logger log = LoggerFactory.getLogger(TokenService.class);

	@Value(JWTAUTH.EXPIRATION_TIME)
	private Long expirationTime; 
	
	@Value(JWTAUTH.SECRET)
	private String secret;
	
	@Value(JWTAUTH.TOKEN_PREFIX)
	private String tokenPrefix;
	
	@Value(JWTAUTH.HEADER_STRINGS)
	private String headerString;
	
	public String generateToken(AuthEntity authEntity) {
		Map<String, Object> claims = new HashMap<>();
		claims.put(JWTAUTH.CLAIM_EXTERNAL_ID, authEntity.getExternalId());
		claims.put(JWTAUTH.CLAIM_PRINCIPAL_CREDENTIAL, authEntity.getPrincipalCredential());
		claims.put(JWTAUTH.CLAIM_CREDENTIAL_ROLE, authEntity.getCredentialRole());
		claims.put(JWTAUTH.CLAIM_ADDITIONAL_INFO, authEntity.getAdditionalInfo());
		
		return Jwts.builder()
		 		 .setSubject(authEntity.getAdditionalInfo())
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
						.get(BaseConstants.JWTAUTH.CLAIM_EXTERNAL_ID);
				
				if (userAccountExternalId != null && !"".equals(userAccountExternalId)) {
					return Boolean.TRUE;
				}
			} catch (Exception e) {
				log.error(MSGERROR.AUTH_ERROR_INVALID_TOKEN, token);
				return Boolean.FALSE;
			}
		}
		
		log.error(MSGERROR.AUTH_ERROR_INVALID_TOKEN, token);
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
				log.error(MSGERROR.AUTH_ERROR_INVALID_TOKEN, token);
				return null;
			}
		}
		
		log.error(MSGERROR.AUTH_ERROR_INVALID_TOKEN, token);
		return null;
	}
	
	public String getTokenFromRequest(HttpServletRequest request) {
		String token = request.getHeader(JWTAUTH.X_ACCESS_TOKEN);
		if (token != null && !"".equals(token)) {
			return token;
		}
		
		token = (String) request.getAttribute(JWTAUTH.TOKEN);
		if (token != null && !"".equals(token)) {
			return token;
		}
		
		return TokenUtils.getTokenFromAuthorizationHeader(request.getHeader(JWTAUTH.AUTHORIZATION));
	}

}