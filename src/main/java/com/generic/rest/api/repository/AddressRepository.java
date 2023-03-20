package com.generic.rest.api.repository;

import org.springframework.stereotype.Repository;

import com.generic.rest.api.domain.Address;
import com.generic.rest.api.repository.core.BaseApiRepository;

@Repository
public interface AddressRepository extends BaseApiRepository<Address> {

}