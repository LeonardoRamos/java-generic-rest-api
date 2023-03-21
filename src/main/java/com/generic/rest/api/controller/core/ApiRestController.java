package com.generic.rest.api.controller.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.generic.rest.api.domain.core.ApiResponse;
import com.generic.rest.api.domain.core.BaseEntity;
import com.generic.rest.api.domain.core.filter.RequestFilter;
import com.generic.rest.api.exception.ApiException;
import com.generic.rest.api.service.core.ApiRestService;

@SuppressWarnings({ "rawtypes", "unchecked"} )
public abstract class ApiRestController<E extends BaseEntity, S extends ApiRestService> {
	
	private static final Logger log = LoggerFactory.getLogger(ApiRestController.class);
	
	public abstract S getService();
	
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<E>> getAll(
    		@ModelAttribute("RequestFilter") RequestFilter requestFilter) throws ApiException {
    	log.info("Finding Entity by requestFilter=[{}]", requestFilter);
		return new ResponseEntity<>(getService().findAll(requestFilter), HttpStatus.OK);
    }
    
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<E> insert(@RequestBody E entity) throws ApiException {
    	log.info("Processing insert of data: [{}]", entity);
		return (ResponseEntity<E>) new ResponseEntity<>(getService().save(entity), HttpStatus.CREATED);
    }
    
}