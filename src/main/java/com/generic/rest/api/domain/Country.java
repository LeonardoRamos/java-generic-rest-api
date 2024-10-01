package com.generic.rest.api.domain;

import com.generic.rest.core.domain.BaseApiEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "country")
public class Country extends BaseApiEntity {
	
	@Column(name = "name")
	private String name;
	
	public Country() {}
	
	public Country(CountryBuilder builder) {
		this.name = builder.name;
		this.setId(builder.getId());
		this.setExternalId(builder.getExternalId());
		this.setActive(builder.isActive());
		this.setInsertDate(builder.getInsertDate());
		this.setUpdateDate(builder.getUpdateDate());
		this.setDeleteDate(builder.getRemoveDate());
		this.setSum(builder.getSum());
		this.setAvg(builder.getAvg());
		this.setCount(builder.getCount());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public static CountryBuilder builder() {
		return new CountryBuilder();
	}
	
	public static class CountryBuilder extends BaseApiEntityBuilder {
		
		private String name;
		
		public CountryBuilder name(String name) {
			this.name = name;
			return this;
		}
		
		
		public Country build() {
			return new Country(this);
		}
	}

}