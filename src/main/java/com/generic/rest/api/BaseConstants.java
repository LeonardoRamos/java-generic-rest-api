package com.generic.rest.api;

public final class BaseConstants {
	
	private BaseConstants() {
		
	}
	
	public static final String LOCALE_PT = "pt";
	public static final String LOCALE_BR = "BR";
	public static final String DATE_TIMEZONE = "UTC";
	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm'Z'";
	public static final String NULL_VALUE = "null";
	
	public final class TOMCAT {
		
		private TOMCAT() {
			
		}
		
		public static final String RELAXED_SERVER_CHARS_KEY = "relaxedQueryChars";
		public static final String RELAXED_SERVER_CHARS_VALUE = "[]|{}^&#x5c;&#x60;&quot;&lt;&gt;";
		public static final String RELAXED_SERVER_PATH_KEY = "relaxedPathChars";
		public static final String RELAXED_SERVER_PATH_VALUE = "[]|";
	}
	
	public static final class HEALTHCHECK {
		
		private HEALTHCHECK() {
			
		}
		
		public static final class DATABASE {
			
			private DATABASE() {
				
			}
			
			public static final String FAILED_MESSAGE = "DataSource health check failed";
			public static final String NO_DATASOURCE = "DataSource for DatabaseHealthIndicator must be specified";
			public static final String KEY = "database";
			public static final String UNKNOWN_DATASOURCE = "unknown";
		}
	}
	
	public static final class CONTROLLER {
		
		private CONTROLLER() {
			
		}
		
		public static final String VERSION = "v1";
		public static final String PATH_SEPARATOR = "/";
		public static final String EXTERNAL_ID = "externalId";
		public static final String EXTERNAL_ID_PARAM = "{" + EXTERNAL_ID + "}";
		public static final String EXTERNAL_ID_PATH = PATH_SEPARATOR + EXTERNAL_ID_PARAM;
		public static final String ID = "id";
		public static final String ID_PARAM = "{" + ID + "}";
		public static final String ID_PATH = PATH_SEPARATOR + ID_PARAM;
		public static final String HEALTH_PATH = PATH_SEPARATOR + "health";
		public static final String BASE_PATH = PATH_SEPARATOR + VERSION + PATH_SEPARATOR;
	}

	public final class JWTAUTH {
		
		private JWTAUTH() {
			
		}
		
		public static final String EXPIRATION_TIME = "${jwt.expiration}";
		public static final String SECRET = "${jwt.secret}";
		public static final String TOKEN_PREFIX = "${jwt.prefix}";
		public static final String HEADER_STRINGS = "${jwt.header.strings}";
		public static final String CLAIM_PRINCIPAL_CREDENTIAL = "principalCredential";
		public static final String CLAIM_ADDITIONAL_INFO = "additionalInfo";
		public static final String CLAIM_CREDENTIAL_ROLE = "credentialRole";
		public static final String CLAIM_EXTERNAL_ID = "externalId";
		public static final String TOKEN = "token";
		public static final String BEARER = "Bearer";
		public static final String X_ACCESS_TOKEN = "x-access-token";
		public static final String CONTENT_TYPE = "Content-Type";
		public static final String CACHE_CONTROL = "Cache-Control";
		public static final String AUTHORIZATION = "Authorization";
		public static final String CONTENT_DISPOSITION = "Content-Disposition";
		public static final String ALL_PATH_CORS_REGEX = "/**";
		public static final String ALL_PATH_ORIGIN_REGEX = "*";
	}
	
	public static final class ERRORKEYS {
		
		private ERRORKEYS() {
			
		}
		
		public static final String ERROR_CODE_KEY = "code";
		public static final String ERROR_MSG_KEY = "message";
		public static final String KEY = "errors";
		public static final String MSG_ERROR = "MSG_ERROR";
	}

	public static final class MSGERROR {
		
		private MSGERROR() {
			
		}
		
		public static final String DEFAULT_ERROR_CODE = "ERROR_CODE";
		public static final String ENTITIES_NOT_FOUND_ERROR = "No entities found for requestFilter [%s]";
		public static final String BAD_REQUEST_ERROR = "Malformed request for requestFilter [%s]";
		public static final String ENTITY_NOT_FOUND_ERROR = "No entity found for externalId [%s]";
		public static final String BASE_ENTITY_NOT_FOUND_ERROR = "No entity found for id [%s]";
		public static final String PARSE_PROJECTIONS_ERROR = "Error parsing projections of filter [%s]";
		public static final String PARSE_FILTER_FIELDS_ERROR = "Error parsing filter fields of filter [%s]";
		public static final String PARSE_SORT_ORDER_ERROR = "Error parsing sort order of filter [%s]";
		public static final String UNEXPECTED_FETCHING_ERROR = "Unexpected error processing query data [%s]";
		public static final String INVALID_AGGREGATION_ERROR = "Invalid aggregation fields";
		public static final String ERROR_PARSE_DATE = "Error while parsing date [{}] to Calendar";
		public static final String AUTH_ERROR_INVALID_TOKEN = "Invalid token [{}]";
		public static final String AUTHORIZATION_TOKEN_NOT_VALID = "Authorization token not valid";
		public static final String INTERNAL_ERROR = "Unexpected error";
		public static final String MEDIA_TYPE_NOT_SUPPORTED = "Media type not supported";
		public static final String METHOD_NOT_SUPPORTED = "Method not supported";
		public static final String MEDIA_TYPE_NOT_ACCEPTABLE = "Media type not accepted";
		public static final String PARAMETER_NOT_PRESENT = "Parameter not present";
		public static final String UNRECOGNIZED_FIELD = "Unrecognized field";
		public static final String INVALID_VALUE = "Invalid value";
		public static final String BODY_INVALID = "Body invalid";
		public static final String MESSAGE_NOT_READABLE = "Message not readable";
		public static final String VALIDATION_ERROR = "Validation error";
	}

}
