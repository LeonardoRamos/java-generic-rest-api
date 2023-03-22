package com.generic.rest.api.repository;

import org.springframework.stereotype.Repository;

import com.generic.rest.api.domain.User;
import com.generic.rest.core.repository.BaseApiRepository;

@Repository
public interface UserRepository extends BaseApiRepository<User> {
	
	User findByEmailAndActive(String email, Boolean active);

}