package com.generic.rest.api.controller.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.generic.rest.api.Constants.CONTROLLER;
import com.generic.rest.api.domain.core.BaseApiEntity;
import com.generic.rest.api.exception.ApiException;
import com.generic.rest.api.service.core.BaseApiRestService;

@SuppressWarnings({ "rawtypes", "unchecked"} )
public abstract class BaseApiRestController<ENTITY extends BaseApiEntity, SERVICE extends BaseApiRestService>
	extends ApiRestController<ENTITY, SERVICE> {
	
	private static final Logger log = LoggerFactory.getLogger(BaseApiRestController.class);
	
    @PutMapping(value = CONTROLLER.SLUG_PATH, 
			consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ENTITY> update(@PathVariable(CONTROLLER.SLUG) String slug, 
    		@RequestBody ENTITY entity) throws ApiException {
    	log.info("Processing update of entity of slug: [{}]", slug);
		return (ResponseEntity<ENTITY>) new ResponseEntity<>(getService().update(entity), HttpStatus.OK);
    }
    
    @DeleteMapping(value = CONTROLLER.SLUG_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> delete(@PathVariable(CONTROLLER.SLUG) String slug) throws ApiException {
    	log.info("Processing delete of entity of slug: [{}]", slug);
    	getService().delete(slug);
		return new ResponseEntity<>(HttpStatus.OK);
    }
    
}