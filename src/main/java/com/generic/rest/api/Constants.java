package com.generic.rest.api;

public interface Constants {

	final String LOCALE_PT = "pt";
	final String LOCALE_BR = "BR";
	final String DATE_TIMEZONE = "UTC";
	final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm'Z'";
	final String NULL_VALUE = "null";
	
	public interface TOMCAT {
		final String RELAXED_SERVER_CHARS_KEY = "relaxedQueryChars";
		final String RELAXED_SERVER_CHARS_VALUE = "[]|{}^&#x5c;&#x60;&quot;&lt;&gt;";
		final String RELAXED_SERVER_PATH_KEY = "relaxedPathChars";
		final String RELAXED_SERVER_PATH_VALUE = "[]|";
	}
	
	public interface HEALTH_CHECK {
		
		public interface DATABASE {
			final String FAILED_MESSAGE = "DataSource health check failed";
			final String NO_DATASOURCE = "DataSource for DatabaseHealthIndicator must be specified";
			final String KEY = "database";
			final String UNKNOWN_DATASOURCE = "unknown";
		}
	}
	
	public interface CONTROLLER {
		final String VERSION = "v1";
		final String PATH_SEPARATOR = "/";
		final String SLUG = "slug";
		final String SLUG_PARAM = "{" + SLUG + "}";
		final String SLUG_PATH = PATH_SEPARATOR + SLUG_PARAM;
		final String ID = "id";
		final String ID_PARAM = "{" + ID + "}";
		final String ID_PATH = PATH_SEPARATOR + ID_PARAM;
		final String HEALTH_PATH = "/health";
		final String BASE_PATH = PATH_SEPARATOR + VERSION + PATH_SEPARATOR;

		public interface LOGIN {
			final String EMAIL_FIELD = "email";
			final String PASSWORD_FIELD = "password";
			final String NAME = "auth/login";
			final String PATH = PATH_SEPARATOR + NAME;
		}

		public interface USER {
			final String NAME = "users";
			final String PATH = BASE_PATH + NAME;
		}
	}

	public interface JWT_AUTH {
		final String EXPIRATION_TIME = "${jwt.expiration}";
		final String SECRET = "${jwt.secret}";
		final String TOKEN_PREFIX = "${jwt.prefix}";
		final String HEADER_STRINGS = "${jwt.header.strings}";
		final String CLAIM_EMAIL = "email";
		final String CLAIM_NAME = "name";
		final String CLAIM_ROLE = "role";
		final String CLAIM_USER_SLUG = "userSlug";
		final String TOKEN = "token";
		final String BEARER = "Bearer";
		final String X_ACCESS_TOKEN = "x-access-token";
		final String CONTENT_TYPE = "Content-Type";
		final String CACHE_CONTROL = "Cache-Control";
		final String AUTHORIZATION = "Authorization";
		final String CONTENT_DISPOSITION = "Content-Disposition";
		final String ALL_PATH_CORS_REGEX = "/**";
		final String ALL_PATH_ORIGIN_REGEX = "*";
	}

	public interface MSG_ERROR {
		final String AUTHENTICATION_ERROR = "Wrong username or password";
		final String ENTITIES_NOT_FOUND_ERROR = "No entities found for requestFilter [%s]";
		final String BAD_REQUEST_ERROR = "Malformed request for requestFilter [%s]";
		final String ENTITY_NOT_FOUND_ERROR = "No entity found for slug [%s]";
		final String BASE_ENTITY_NOT_FOUND_ERROR = "No entity found for id [%s]";
		final String PARSE_PROJECTIONS_ERROR = "Error parsing projections of filter [%s]";
		final String PARSE_FILTER_FIELDS_ERROR = "Error parsing filter fields of filter [%s]";
		final String PARSE_SORT_ORDER_ERROR = "Error parsing sort order of filter [%s]";
		final String UNEXPECTED_FETCHING_ERROR = "Unexpected error processing query data [%s]";
		final String INVALID_AGGREGATION_ERROR = "Invalid aggregation fields";
		final String ERROR_PARSE_DATE = "Error while parsing date [{}] to Calendar";
		final String AUTH_ERROR_INVALID_TOKEN = "Invalid token [{}].";
		final String AUTHORIZATION_TOKEN_NOT_VALID = "Authorization token not valid";
		final String KEY = "message";
		final String INTERNAL_ERROR = "Unexpected error";
	}

}
