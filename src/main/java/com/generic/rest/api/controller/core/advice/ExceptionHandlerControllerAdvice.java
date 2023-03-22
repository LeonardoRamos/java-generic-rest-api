
package com.generic.rest.api.controller.core.advice;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.generic.rest.api.BaseConstants.MSGERROR;
import com.generic.rest.api.exception.ApiException;

@RestControllerAdvice
public class ExceptionHandlerControllerAdvice {
	
	private static Logger log = LoggerFactory.getLogger(ExceptionHandlerControllerAdvice.class);

	@Autowired
	private ErrorParser errorParser;

	private ResponseEntity<Map<String, Object>> handler(Exception exception, List<Map<String, String>> errors, HttpStatus status) {
		if (status.is5xxServerError()) {
			log.error(exception.getMessage(), exception);
		} else {
			log.warn(exception.getMessage(), exception);
		}
		return errorParser.createResponseEntity(errors, status);
	}

	@ExceptionHandler(ApiException.class)
	public ResponseEntity<Map<String, Object>> apiException(ApiException exception) {
		return handler(exception, errorParser.formatErrorList(exception), exception.getStatus());
	}
	
	@ExceptionHandler({Exception.class })
	public ResponseEntity<Map<String, Object>> exception(Exception exception) {
		return handler(exception, errorParser.formatErrorList(MSGERROR.INTERNAL_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler({
		HttpRequestMethodNotSupportedException.class, 
		HttpMediaTypeNotSupportedException.class, 
		HttpMessageNotReadableException.class,
		HttpMediaTypeNotAcceptableException.class,
		MethodArgumentNotValidException.class, 
		MissingServletRequestPartException.class})
	public ResponseEntity<Map<String, Object>> badRequest(HttpServletRequest req, Exception exception) {

		HttpStatus status = HttpStatus.BAD_REQUEST;
		
		if (exception instanceof MethodArgumentNotValidException) {
			MethodArgumentNotValidException methodArgumentNotValidException = (MethodArgumentNotValidException) exception;
			return handler(exception, errorParser.formatErrorList(methodArgumentNotValidException.getBindingResult()), status);
		}

		if (exception instanceof HttpMediaTypeNotSupportedException) {
			return handler(exception, errorParser.formatErrorList(MSGERROR.MEDIA_TYPE_NOT_SUPPORTED), status);
		}
		
		if (exception instanceof HttpRequestMethodNotSupportedException) {
			HttpRequestMethodNotSupportedException httpRequestMethodNotSupportedException = (HttpRequestMethodNotSupportedException) exception;
			return handler(exception, errorParser.formatErrorList(MSGERROR.METHOD_NOT_SUPPORTED, httpRequestMethodNotSupportedException.getMethod()), status);
		
		}
		if (exception instanceof HttpMediaTypeNotAcceptableException) {
			HttpMediaTypeNotAcceptableException httpMediaTypeNotAcceptableException = (HttpMediaTypeNotAcceptableException) exception;
			return handler(exception, errorParser.formatErrorList(MSGERROR.MEDIA_TYPE_NOT_ACCEPTABLE, httpMediaTypeNotAcceptableException.getSupportedMediaTypes().toString()), status);
		}

		if (exception instanceof MissingServletRequestPartException) {
			MissingServletRequestPartException missingServletRequestPartException = (MissingServletRequestPartException) exception;
			return handler(exception, errorParser.formatErrorList(MSGERROR.PARAMETER_NOT_PRESENT, missingServletRequestPartException.getRequestPartName()), status);
		}
		
		return handleFormatExceptions(exception, status);
	}

	private ResponseEntity<Map<String, Object>> handleFormatExceptions(Exception exception, HttpStatus status) {
		if (exception instanceof HttpMessageNotReadableException) {

			if (exception.getCause() instanceof UnrecognizedPropertyException) {
				UnrecognizedPropertyException ex = ((UnrecognizedPropertyException) exception.getCause());
				return handler(exception, errorParser.formatErrorList(MSGERROR.UNRECOGNIZED_FIELD, ex.getPropertyName()), status);

			} else if (exception.getCause() instanceof InvalidFormatException) {
				StringBuilder path = buildFormatErrorPath(exception);
				return handler(exception, errorParser.formatErrorList(MSGERROR.INVALID_VALUE, path.toString()), status);

			} else if (exception.getCause() instanceof JsonMappingException) {
				return handler(exception, errorParser.formatErrorList(MSGERROR.BODY_INVALID), status);

			} else {
				return handler(exception, errorParser.formatErrorList(MSGERROR.MESSAGE_NOT_READABLE), status);
			}
		}

		return handler(exception, errorParser.formatErrorList(MSGERROR.BAD_REQUEST_ERROR), status);
	}

	private StringBuilder buildFormatErrorPath(Exception exception) {
		InvalidFormatException ex = ((InvalidFormatException) exception.getCause());
		StringBuilder path = new StringBuilder();

		for (Reference reference : ex.getPath()) {
			if (!path.toString().equals("")) {
				path.append(".");
			}

			if (reference.getIndex() > - 1) {
				path.append("[").append(reference.getIndex()).append("]");
			} else {
				path.append(reference.getFieldName());
			}
		}
		return path;
	}
    
}