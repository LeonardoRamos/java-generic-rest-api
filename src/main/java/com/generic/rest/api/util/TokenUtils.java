package com.generic.rest.api.util;

import java.util.List;

import com.generic.rest.api.Constants.JWTAUTH;

public class TokenUtils {
	
	private TokenUtils() {
		
	}
	
	public static String getTokenFromAuthorizationHeader(String authorizationHeader) {
		if (authorizationHeader != null && !"".equals(authorizationHeader)) {
			List<String> authorizationHeaderData = StringParserUtils.splitStringList(authorizationHeader, ' ');
			
			if (authorizationHeaderData != null && authorizationHeaderData.size() > 1 && 
					authorizationHeaderData.get(0).equals(JWTAUTH.BEARER)) {
				
				return authorizationHeaderData.get(1);
			}
		}	
		
		return null;
   }

}