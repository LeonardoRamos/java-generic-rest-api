package com.generic.rest.api.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import com.generic.rest.core.domain.AuthEntity;
import com.generic.rest.core.domain.BaseApiEntity;

@Entity
@Table(name = "user_account")
public class User extends BaseApiEntity implements AuthEntity {
	
	@Column(name = "name", length = 80)
	private String name;
	
	@Column(name = "email", unique = true, length = 150)
	private String email;
	
	@Column(name = "password", columnDefinition = "text")
	private String password;
	
	@Column(name = "age")
	private Integer age;
	
	@Column(name = "role")
	@Enumerated(EnumType.STRING)
	private Role role;

	@OneToOne(mappedBy = "user")
	@Cascade(CascadeType.SAVE_UPDATE)
	private Address address;
	
	public User() {}
	
	public User(UserBuilder builder) {
		this.name = builder.name;
		this.email = builder.email;
		this.password = builder.password;
		this.age = builder.age;
		this.role = builder.role;
		this.address = builder.address;
		this.setId(builder.getId());
		this.setExternalId(builder.getExternalId());
		this.setActive(builder.getActive());
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
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}
	
	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}
	
	@Override
	public String getPrincipalCredential() {
		return getEmail();
	}

	@Override
	public String getCredentialRole() {
		return getRole().name();
	}

	@Override
	public String getAdditionalInfo() {
		return getName();
	}
	
	public static UserBuilder builder() {
		return new UserBuilder();
	}
	
	public static class UserBuilder extends BaseApiEntityBuilder {
		
		private String name;
		private String email;
		private String password;
		private Integer age;
		private Address address;
		private Role role;
		
		public UserBuilder address(Address address) {
			this.address = address;
			return this;
		}
		
		public UserBuilder name(String name) {
			this.name = name;
			return this;
		}
		
		public UserBuilder email(String email) {
			this.email = email;
			return this;
		}
		
		public UserBuilder password(String password) {
			this.password = password;
			return this;
		}
		
		public UserBuilder age(Integer age) {
			this.age = age;
			return this;
		}
		
		public UserBuilder role(Role role) {
			this.role = role;
			return this;
		}
		
		public User build() {
			return new User(this);
		}
	}

}