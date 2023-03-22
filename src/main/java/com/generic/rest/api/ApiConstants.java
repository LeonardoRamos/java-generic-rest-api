package com.generic.rest.api;

import com.generic.rest.core.BaseConstants;

public final class ApiConstants {
	
	private ApiConstants() {
		
	}
	
	public static final class CONTROLLER {
		
		private CONTROLLER() {
			
		}
		
		public static final class LOGIN {
			
			private LOGIN() {
				
			}
			
			public static final String EMAIL_FIELD = "email";
			public static final String PASSWORD_FIELD = "password";
			public static final String NAME = "auth/login";
			public static final String PATH = BaseConstants.CONTROLLER.PATH_SEPARATOR + NAME;
		}

		public static final class USER {
			
			private USER() {
				
			}
			
			public static final String NAME = "users";
			public static final String PATH = BaseConstants.CONTROLLER.BASE_PATH + NAME;
		}
	}

	public static final class MSGERROR {
		
		private MSGERROR() {
			
		}
		
		public static final String AUTHENTICATION_ERROR = "Wrong username or password";
	}

}
