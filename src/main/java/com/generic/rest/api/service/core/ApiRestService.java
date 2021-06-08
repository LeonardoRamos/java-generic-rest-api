package com.generic.rest.api.service.core;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.generic.rest.api.domain.core.ApiMetadata;
import com.generic.rest.api.domain.core.ApiResponse;
import com.generic.rest.api.domain.core.BaseEntity;
import com.generic.rest.api.domain.core.filter.RequestFilter;
import com.generic.rest.api.exception.ApiException;
import com.generic.rest.api.repository.core.ApiRepository;
import com.generic.rest.api.repository.core.BaseRepository;

@Service
public abstract class ApiRestService<ENTITY extends BaseEntity, REPOSITORY extends BaseRepository<ENTITY>> {
	
	@Autowired
	private ApiRepository<ENTITY> apiRepository;
	
	protected abstract REPOSITORY getRepository();
	protected abstract Class<ENTITY> getEntityClass();
	
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
	
   	public void deleteInBatch(List<ENTITY> entities) {
	   	getRepository().deleteInBatch(entities);
   	}
   	
   	public abstract ENTITY update(ENTITY entity) throws ApiException;

   	public abstract ENTITY save(ENTITY entity) throws ApiException;
	
}