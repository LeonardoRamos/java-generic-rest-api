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
import com.generic.rest.api.domain.core.BaseEntity;
import com.generic.rest.api.exception.ApiException;
import com.generic.rest.api.service.core.BaseRestService;

@SuppressWarnings({ "rawtypes", "unchecked"} )
public abstract class BaseRestController<ENTITY extends BaseEntity, SERVICE extends BaseRestService> 
	extends ApiRestController<ENTITY, SERVICE> {
	
	private static final Logger log = LoggerFactory.getLogger(BaseRestController.class);
	
	public abstract SERVICE getService();
	
    @PutMapping(value = CONTROLLER.ID_PATH, 
			consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ENTITY> update(@PathVariable(CONTROLLER.ID) Long id, 
    		@RequestBody ENTITY entity) throws ApiException {
    	log.info("Processing update of entity of id: [{}]", id);
		return (ResponseEntity<ENTITY>) new ResponseEntity<>(getService().update(entity), HttpStatus.OK);
    }
    
    @DeleteMapping(value = CONTROLLER.ID_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> delete(@PathVariable(CONTROLLER.ID) Long id) throws ApiException {
    	log.info("Processing delete of entity of id: [{}]", id);
    	getService().delete(id);
		return new ResponseEntity<>(HttpStatus.OK);
    }
    
}