package com.generic.rest.api.controller.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.generic.rest.api.Constants.CONTROLLER;
import com.generic.rest.api.domain.core.BaseEntity;
import com.generic.rest.api.exception.ApiException;
import com.generic.rest.api.service.core.BaseRestService;

@SuppressWarnings({ "rawtypes", "unchecked"} )
public abstract class BaseRestController<E extends BaseEntity, S extends BaseRestService> 
	extends ApiRestController<E, S> {
	
	private static final Logger log = LoggerFactory.getLogger(BaseRestController.class);
	
	@GetMapping(value = CONTROLLER.ID_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<E> getOne(@PathVariable(CONTROLLER.ID) Long id) throws ApiException {
    	log.info("Processing get of entity of id: [{}]", id);
		return (ResponseEntity<E>) new ResponseEntity<>(getService().findById(id), HttpStatus.OK);
    }
	
    @PutMapping(value = CONTROLLER.ID_PATH, 
			consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<E> update(@PathVariable(CONTROLLER.ID) Long id, 
    		@RequestBody E entity) throws ApiException {
    	log.info("Processing update of entity of id: [{}]", id);
		return (ResponseEntity<E>) new ResponseEntity<>(getService().update(entity), HttpStatus.OK);
    }
    
    @DeleteMapping(value = CONTROLLER.ID_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> delete(@PathVariable(CONTROLLER.ID) Long id) throws ApiException {
    	log.info("Processing delete of entity of id: [{}]", id);
    	getService().delete(id);
		return new ResponseEntity<>(HttpStatus.OK);
    }
    
}