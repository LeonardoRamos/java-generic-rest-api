package com.generic.rest.api.repository;

import org.springframework.stereotype.Repository;

import com.generic.rest.api.domain.User;
import com.generic.rest.api.repository.core.BaseRepository;

@Repository
public interface UserRepository extends BaseRepository<User> {

	User findByEmailAndActive(String email, Boolean active);

}