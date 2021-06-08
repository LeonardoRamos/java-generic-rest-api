package com.generic.rest.api.repository.core;

import java.util.Calendar;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import com.generic.rest.api.domain.core.BaseApiEntity;

@NoRepositoryBean
public interface BaseApiRepository<ENTITY extends BaseApiEntity> extends BaseRepository<ENTITY> {
	
	@Transactional
	@Modifying(clearAutomatically = true)
    @Query("UPDATE #{#entityName} as E SET E.active = false, E.deleteDate = :deleteDate WHERE E.slug = :slug")
    Long logicDelete(@Param("slug") String slug, @Param("deleteDate") Calendar deleteDate);

	@Transactional
	Integer deleteBySlug(String slug);

	ENTITY findOneBySlug(String slug);
	
}