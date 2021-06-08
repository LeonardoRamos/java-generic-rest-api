package com.generic.rest.api.service.core;

import org.springframework.stereotype.Service;

import com.generic.rest.api.Constants.MSG_ERROR;
import com.generic.rest.api.domain.core.BaseEntity;
import com.generic.rest.api.exception.ApiException;
import com.generic.rest.api.exception.NotFoundApiException;
import com.generic.rest.api.repository.core.BaseRepository;

@Service
public abstract class BaseRestService<ENTITY extends BaseEntity, REPOSITORY extends BaseRepository<ENTITY>> 
	extends ApiRestService<ENTITY, REPOSITORY> {
	
	@Override
	public ENTITY update(ENTITY entity) throws ApiException {
		validateExists(entity.getId());
		
		return getRepository().saveAndFlush(entity);
	}
	
	public Boolean delete(Long id) throws ApiException {
		validateExists(id);
		
		getRepository().deleteById(id);
	   
	   	return Boolean.TRUE;
   	}

	private void validateExists(Long id) throws NotFoundApiException {
		Boolean existsEntity = getRepository().existsById(id);
		
		if (!existsEntity) {
			throw new NotFoundApiException(String.format(MSG_ERROR.BASE_ENTITY_NOT_FOUND_ERROR, id));
		}
	}
   
	@Override
   	public ENTITY save(ENTITY entity) throws ApiException {
	   	return getRepository().saveAndFlush(entity);
   	}
	
}