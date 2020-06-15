package com.generic.rest.api.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import com.generic.rest.api.domain.core.BaseApiEntity;

@Entity
@Table(name = "address")
public class Address extends BaseApiEntity {
	
	@Column(name = "street")
	private String street;
	
	@Column(name = "street_number")
	private String streetNumber;
	
	@Column(name = "state")
	private String state;
	
	@OneToOne
	@Cascade(CascadeType.SAVE_UPDATE)
	@JoinColumn(name = "id_country")
	private Country country;
	
	public Address() {}
	
	public Address(AddressBuilder builder) {
		this.street = builder.street;
		this.streetNumber = builder.streetNumber;
		this.state = builder.state;
		this.country = builder.country;
		this.setId(builder.getId());
		this.setSlug(builder.getSlug());
		this.setActive(builder.getActive());
		this.setInsertDate(builder.getInsertDate());
		this.setUpdateDate(builder.getUpdateDate());
		this.setDeleteDate(builder.getRemoveDate());
		this.setSum(builder.getSum());
		this.setAvg(builder.getAvg());
		this.setCount(builder.getCount());
		this.setCountDistinct(builder.getCountDistinct());
	}
	
	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getStreetNumber() {
		return streetNumber;
	}

	public void setStreetNumber(String streetNumber) {
		this.streetNumber = streetNumber;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public static AddressBuilder builder() {
		return new AddressBuilder();
	}
	
	public static class AddressBuilder extends BaseApiEntityBuilder {
		
		private String street;
		private String streetNumber;
		private String state;
		private Country country;
		
		public AddressBuilder street(String street) {
			this.street = street;
			return this;
		}
		
		public AddressBuilder streetNumber(String streetNumber) {
			this.streetNumber = streetNumber;
			return this;
		}
		
		public AddressBuilder state(String state) {
			this.state = state;
			return this;
		}
		
		public AddressBuilder country(Country country) {
			this.country = country;
			return this;
		}
		
		public Address build() {
			return new Address(this);
		}
	}

}