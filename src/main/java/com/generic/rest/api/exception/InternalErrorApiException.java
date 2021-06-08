package com.generic.rest.api.exception;

import org.springframework.http.HttpStatus;

public class InternalErrorApiException extends ApiException {

	private static final long serialVersionUID = 1L;

	public InternalErrorApiException(String message, String... data) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message, data);
    }

    public InternalErrorApiException(String message, Throwable throwable, String... data) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message, throwable, data);
    }

}