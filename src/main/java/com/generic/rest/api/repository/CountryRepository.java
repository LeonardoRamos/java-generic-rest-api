package com.generic.rest.api.repository;

import org.springframework.stereotype.Repository;

import com.generic.rest.api.domain.Country;
import com.generic.rest.core.repository.BaseApiRepository;

@Repository
public interface CountryRepository extends BaseApiRepository<Country> {
	
	Country findByName(String name);

}