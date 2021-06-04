package com.generic.rest.api.exception;

import org.springframework.http.HttpStatus;

public class NotFoundApiException extends ApiException {

	private static final long serialVersionUID = 1L;

	public NotFoundApiException(String message, String... data) {
        super(HttpStatus.NOT_FOUND, message, data);
    }

    public NotFoundApiException(String message, Throwable throwable, String... data) {
        super(HttpStatus.NOT_FOUND, message, throwable, data);
    }

}