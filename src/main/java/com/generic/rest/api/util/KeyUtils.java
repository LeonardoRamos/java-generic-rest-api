package com.generic.rest.api.util;

import java.util.UUID;

public class KeyUtils {
	
	private KeyUtils() {
		
	}
	
	public static String generate() {
		return StringParserUtils.replace(UUID.randomUUID().toString(), "-", "");
	}

}