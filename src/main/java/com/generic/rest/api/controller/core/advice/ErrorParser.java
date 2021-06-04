package com.generic.rest.api.controller.core.advice;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import com.generic.rest.api.Constants;
import com.generic.rest.api.Constants.ERRORKEYS;
import com.generic.rest.api.Constants.MSG_ERROR;
import com.generic.rest.api.exception.ApiException;

@Component
public class ErrorParser {
	
	private static Logger log = LoggerFactory.getLogger(ExceptionHandlerControllerAdvice.class);

	public ResponseEntity<Map<String, Object>> createResponseEntity(List<Map<String, String>> errors, HttpStatus status) {
		return new ResponseEntity<>(Collections.singletonMap(ERRORKEYS.KEY, errors), status);
	}
	
	public List<Map<String, String>> formatErrorList(BindingResult bindingResult) {
		List<Map<String, String>> errors = new ArrayList<>();

		if (bindingResult != null) {
			
			for (ObjectError objectError: bindingResult.getAllErrors()) {
				String code = Constants.MSG_ERROR.VALIDATION_ERROR;
				List<Object> data = new ArrayList<>();
				
				if (objectError.getDefaultMessage() != null) {
					
					if (objectError.getDefaultMessage().contains("|")) {
						String[] splitedMsg = objectError.getDefaultMessage().split("\\|");
						code = splitedMsg[0];
						for (int i = 1; i < splitedMsg.length; i++) {
							data.add(splitedMsg[i]);
						}
					
					} else {
						data.add(objectError.getDefaultMessage());
					}
				}
				errors.add(createError(code, data.toArray()));
			}
		}
		
		return errors;
	}
	
	public List<Map<String, String>> formatErrorList(ApiException exception) {
    	return formatErrorList(exception.getCode(), exception.getData());
	}
	
	public List<Map<String, String>> formatErrorList(String message) {
		Object [] data = null;
		return formatErrorList(message, data);
	}
	
	public List<Map<String, String>> formatErrorList(String message, Object... data) {
		List<Map<String, String>> errors = new ArrayList<>();
    	
    	errors.add(createError(message, data));
		
    	return errors;
	}
	
	private Map<String, String> createError(String message, Object... data) {
		Map<String, String> error = new HashMap<>();
    	
		error.put(ERRORKEYS.ERROR_CODE_KEY, getErrorCode(message));
    	error.put(ERRORKEYS.ERROR_MSG_KEY, message);
    	
    	return error;
	}
	
	private String getErrorCode(String message) {
		try {
			Class<?>[] innerClasses = Constants.class.getClasses();
			
			for (int i = 0; i < innerClasses.length; i++) {
				if (innerClasses[i].getSimpleName().equals(ERRORKEYS.MSG_ERROR)) {
					Class<?> msgErrorData = innerClasses[i];
					
					for (Field errorKeysFIelds : msgErrorData.getFields()) {
						if (errorKeysFIelds.get(msgErrorData).equals(message)) {
							return errorKeysFIelds.getName();
						}
					}
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		
		return MSG_ERROR.DEFAULT_ERROR_CODE;
	}

}