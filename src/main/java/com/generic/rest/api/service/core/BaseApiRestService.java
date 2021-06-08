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
	
	public ENTITY findBySlug(String slug) throws NotFoundApiException {
		ENTITY entity = (ENTITY) getRepository().findOneBySlug(slug);
		
		if (entity == null) {
			throw new NotFoundApiException(String.format(MSG_ERROR.ENTITY_NOT_FOUND_ERROR, slug));
		}
		
		return entity;
	}
	
	@Override
	public ENTITY update(ENTITY entity) throws ApiException {
		if (entity.getId() == null) {
			ENTITY entityDatabase = findBySlug(entity.getSlug());
			entity.setId(entityDatabase.getId());
		}

		entity.setUpdateDate(Calendar.getInstance());
      
		if (entity.getActive() != null && entity.getActive()) {
			entity.setDeleteDate(null);
		}
      
		return getRepository().saveAndFlush(entity);
	}

	public Integer delete(String slug) throws ApiException {
	   	Integer deletedCount = getRepository().deleteBySlug(slug);
	   
	   	if (deletedCount == 0) {
		   	throw new NotFoundApiException(String.format(MSG_ERROR.ENTITY_NOT_FOUND_ERROR, slug));
	   	}
	   
	   	return deletedCount;
   	}
   
	@Override
   	public ENTITY save(ENTITY entity) throws ApiException {
   		if (entity.getSlug() == null || "".equals(entity.getSlug())) {
   			entity.setSlug(KeyUtils.generate());
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