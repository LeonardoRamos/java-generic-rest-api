package com.generic.rest.api.repository.core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.generic.rest.api.domain.core.BaseEntity;

@NoRepositoryBean
public interface BaseRepository<ENTITY extends BaseEntity> extends JpaRepository<ENTITY, Long> {}