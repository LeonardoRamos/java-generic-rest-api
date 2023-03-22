package com.generic.rest.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.generic.rest.api.domain.Address;
import com.generic.rest.api.domain.Country;
import com.generic.rest.api.repository.AddressRepository;
import com.generic.rest.core.exception.ApiException;
import com.generic.rest.core.service.BaseApiRestService;

@Service
public class AddressService extends BaseApiRestService<Address, AddressRepository> {
	
	@Autowired
	private AddressRepository addressRepository;
	
	@Autowired
	private CountryService countryService;
	
	@Override
	protected AddressRepository getRepository() {
		return addressRepository;
	}
	
	@Override
	protected Class<Address> getEntityClass() {
		return Address.class;
	}
	
	@Override
	public Address save(Address address) throws ApiException {
		setCountry(address);
		return super.save(address);
	}
	
	public Address merge(Address address, Address addressDatabase) {
		addressDatabase.setState(address.getState());
		addressDatabase.setStreet(address.getStreet());
		addressDatabase.setStreetNumber(address.getStreetNumber());
		return update(addressDatabase);
	}
	
	@Override
	public Address update(Address address) throws ApiException {
		setCountry(address);
		return super.update(address);
	}

	private void setCountry(Address address) {
		Country country = countryService.getByName(address.getCountry().getName());
		
		if (country != null) {
			address.setCountry(country);
		} else {
			address.setCountry(countryService.save(address.getCountry()));
		}
	}

}