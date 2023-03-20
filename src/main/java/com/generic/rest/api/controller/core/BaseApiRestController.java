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
import com.generic.rest.api.domain.core.BaseApiEntity;
import com.generic.rest.api.exception.ApiException;
import com.generic.rest.api.service.core.BaseApiRestService;

@SuppressWarnings({ "rawtypes", "unchecked"} )
public abstract class BaseApiRestController<E extends BaseApiEntity, S extends BaseApiRestService>
	extends ApiRestController<E, S> {
	
	private static final Logger log = LoggerFactory.getLogger(BaseApiRestController.class);
	
	@GetMapping(value = CONTROLLER.EXTERNAL_ID_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<E> getOne(@PathVariable(CONTROLLER.EXTERNAL_ID) String externalId) throws ApiException {
    	log.info("Processing get of entity of externalId: [{}]", externalId);
		return (ResponseEntity<E>) new ResponseEntity<>(getService().getByExternalId(externalId), HttpStatus.OK);
    }
	
    @PutMapping(value = CONTROLLER.EXTERNAL_ID_PATH, 
			consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<E> update(@PathVariable(CONTROLLER.EXTERNAL_ID) String externalId, 
    		@RequestBody E entity) throws ApiException {
    	log.info("Processing update of entity of externalId: [{}]", externalId);
		return (ResponseEntity<E>) new ResponseEntity<>(getService().update(entity), HttpStatus.OK);
    }
    
    @DeleteMapping(value = CONTROLLER.EXTERNAL_ID_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> delete(@PathVariable(CONTROLLER.EXTERNAL_ID) String externalId) throws ApiException {
    	log.info("Processing delete of entity of externalId: [{}]", externalId);
    	getService().delete(externalId);
		return new ResponseEntity<>(HttpStatus.OK);
    }
    
}