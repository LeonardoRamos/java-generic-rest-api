package com.generic.rest.api.service.core;

import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.generic.rest.api.Constants.MSG_ERROR;
import com.generic.rest.api.domain.core.ApiMetadata;
import com.generic.rest.api.domain.core.ApiResponse;
import com.generic.rest.api.domain.core.BaseApiEntity;
import com.generic.rest.api.domain.core.filter.RequestFilter;
import com.generic.rest.api.exception.ApiException;
import com.generic.rest.api.exception.NotFoundApiException;
import com.generic.rest.api.repository.core.ApiRepository;
import com.generic.rest.api.repository.core.BaseRepository;
import com.generic.rest.api.util.KeyUtils;

@Service
public abstract class ApiRestService<ENTITY extends BaseApiEntity, REPOSITORY extends BaseRepository<ENTITY>> {
	
	@Autowired
	private ApiRepository<ENTITY> apiRepository;
	
	protected abstract REPOSITORY getRepository();
	protected abstract Class<ENTITY> getEntityClass();
	
	public ENTITY findBySlug(String slug) throws NotFoundApiException {
		ENTITY entity = (ENTITY) getRepository().findOneBySlug(slug);
		
		if (entity == null) {
			throw new NotFoundApiException(String.format(MSG_ERROR.ENTITY_NOT_FOUND_ERROR, slug));
		}
		
		return entity;
	}
	
	public ApiResponse<ENTITY> findAll(RequestFilter requestFilter) throws ApiException {
		ApiResponse<ENTITY> response = new ApiResponse<ENTITY>();
		List<ENTITY> records = findAllRecords(requestFilter);
		response.setRecords(records);
		
		ApiMetadata metadata = new ApiMetadata();
		metadata.setTotalCount(countAll(requestFilter));
		metadata.setPageOffset(requestFilter.getFetchOffset());
		
		if (requestFilter.hasValidAggregateFunction()) {
			metadata.setPageSize(records.size());
		} else {
			metadata.setPageSize(requestFilter.getFetchLimit());
		}
		
		response.setMetadata(metadata);
		
		return response;
	}
	
	public Long countAll(RequestFilter requestFilter) throws ApiException {
		return apiRepository.countAll(getEntityClass(), requestFilter);
	}
	
	public List<ENTITY> findAllRecords(RequestFilter requestFilter) throws ApiException {
		return apiRepository.findAll(getEntityClass(), requestFilter);
	}
	
	public Page<ENTITY> findAll(Pageable pageable) {
		return getRepository().findAll(pageable);
	}
	
	public Page<ENTITY> findAll(Example<ENTITY> example, Pageable pageable) {
		return getRepository().findAll(example, pageable);
	}
	
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
   
   	public void deleteInBatch(List<ENTITY> entities) {
	   	getRepository().deleteInBatch(entities);
   	}

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

	   	return (ENTITY) getRepository().saveAndFlush(entity);
   	}
	
}