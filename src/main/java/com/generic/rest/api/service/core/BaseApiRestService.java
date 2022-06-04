package com.generic.rest.api.service.core;

import java.util.Calendar;

import org.springframework.stereotype.Service;

import com.generic.rest.api.Constants.MSG_ERROR;
import com.generic.rest.api.domain.core.BaseApiEntity;
import com.generic.rest.api.exception.ApiException;
import com.generic.rest.api.exception.NotFoundApiException;
import com.generic.rest.api.repository.core.BaseApiRepository;
import com.generic.rest.api.util.KeyUtils;

@Service
public abstract class BaseApiRestService<ENTITY extends BaseApiEntity, REPOSITORY extends BaseApiRepository<ENTITY>> 
	extends ApiRestService<ENTITY, REPOSITORY>{
	
	public ENTITY findByExternalId(String externalId) throws NotFoundApiException {
		ENTITY entity = (ENTITY) getRepository().findOneByExternalId(externalId);
		
		if (entity == null) {
			throw new NotFoundApiException(String.format(MSG_ERROR.ENTITY_NOT_FOUND_ERROR, externalId));
		}
		
		return entity;
	}
	
	@Override
	public ENTITY update(ENTITY entity) throws ApiException {
		if (entity.getId() == null) {
			ENTITY entityDatabase = findByExternalId(entity.getExternalId());
			entity.setId(entityDatabase.getId());
		}

		entity.setUpdateDate(Calendar.getInstance());
      
		if (entity.getActive() != null && entity.getActive()) {
			entity.setDeleteDate(null);
		}
      
		return getRepository().saveAndFlush(entity);
	}

	public Integer delete(String externalId) throws ApiException {
	   	Integer deletedCount = getRepository().deleteByExternalId(externalId);
	   
	   	if (deletedCount == 0) {
		   	throw new NotFoundApiException(String.format(MSG_ERROR.ENTITY_NOT_FOUND_ERROR, externalId));
	   	}
	   
	   	return deletedCount;
   	}
   
	@Override
   	public ENTITY save(ENTITY entity) throws ApiException {
   		if (entity.getExternalId() == null || "".equals(entity.getExternalId())) {
   			entity.setExternalId(KeyUtils.generate());
   		}
   		
   		entity.setInsertDate(Calendar.getInstance());
   		entity.setUpdateDate(entity.getInsertDate());
	   
   		if (entity.getActive() == null) {
   			entity.setActive(Boolean.TRUE);
   		}
	   
   		entity.setDeleteDate(null);

	   	return getRepository().saveAndFlush(entity);
   	}
	
}