package com.generic.rest.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.generic.rest.api.domain.Country;
import com.generic.rest.api.exception.ApiException;
import com.generic.rest.api.repository.CountryRepository;
import com.generic.rest.api.service.core.BaseApiRestService;

@Service
public class CountryService extends BaseApiRestService<Country, CountryRepository> {
	
	@Autowired
	private CountryRepository countryRepository;
	
	@Override
	protected CountryRepository getRepository() {
		return countryRepository;
	}
	
	@Override
	protected Class<Country> getEntityClass() {
		return Country.class;
	}
	
	@Override
	public Country save(Country country) throws ApiException {
		country.setName(country.getName().toUpperCase());		
		return super.save(country);
	}
	
	@Override
	public Country update(Country country) throws ApiException {
		country.setName(country.getName().toUpperCase());		
		return super.update(country);
	}
	
	public Country getByName(String name) {
		return countryRepository.findByName(name != null ? name.toUpperCase() : name);
	}
	
}