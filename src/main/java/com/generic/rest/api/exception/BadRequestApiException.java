package com.generic.rest.api.exception;

import org.springframework.http.HttpStatus;

public class BadRequestApiException extends ApiException {

	private static final long serialVersionUID = 1L;

	public BadRequestApiException(String message, String... data) {
        super(HttpStatus.BAD_REQUEST, message, data);
    }

    public BadRequestApiException(String message, Throwable throwable, String... data) {
        super(HttpStatus.BAD_REQUEST, message, throwable, data);
    }

}