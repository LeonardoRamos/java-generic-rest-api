package com.generic.rest.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.generic.rest.api.domain.Address;
import com.generic.rest.api.domain.Country;
import com.generic.rest.api.repository.AddressRepository;
import com.generic.rest.core.exception.ApiException;
import com.generic.rest.core.service.impl.BaseApiRestServiceImpl;

@Service
public class AddressService extends BaseApiRestServiceImpl<Address, AddressRepository> {
	
	private AddressRepository addressRepository;
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
		this.setCountry(address.getCountry(), address);
		return super.save(address);
	}
	
	public Address merge(Address address, Address addressDatabase) {
		addressDatabase.setState(address.getState());
		addressDatabase.setStreet(address.getStreet());
		addressDatabase.setStreetNumber(address.getStreetNumber());
		
		if (address.getCountry() != null && !address.getCountry().getName().equals(addressDatabase.getCountry().getName())) {
			this.setCountry(address.getCountry(), addressDatabase);
		}
		
		return super.update(address.getExternalId(), addressDatabase);
	}

	private void setCountry(Country country, Address address) {
		Country countryDatabase = this.countryService.getByName(country.getName());
		
		if (countryDatabase != null) {
			address.setCountry(countryDatabase);
		} else {
			address.setCountry(this.countryService.save(country));
		}
	}

	@Autowired
	public void setAddressRepository(AddressRepository addressRepository) {
		this.addressRepository = addressRepository;
	}

	@Autowired
	public void setCountryService(CountryService countryService) {
		this.countryService = countryService;
	}
	
}