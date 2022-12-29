package com.generic.rest.api.exception;

import org.springframework.http.HttpStatus;

public abstract class ApiException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private final String code;
	private final transient Object[] data;
	private final HttpStatus status;

	protected ApiException(HttpStatus status, String code, String... data) {
		this(status, code, null, data);
	}
	
	protected ApiException(HttpStatus status, String message, Throwable throwable, String... data) {
		super(message, throwable);
		this.code = message;
		this.status = status;
		this.data = data;
	}
	
	public String getCode() {
		return code;
	}

	public Object[] getData() {
		return data;
	}

	public HttpStatus getStatus() {
		return status;
	}
	
}