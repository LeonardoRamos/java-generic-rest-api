package com.generic.rest.api.domain.core;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@MappedSuperclass
@JsonInclude(Include.NON_EMPTY)
public class BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, updatable = false)
	private Long id;
	
	@Transient
	private Map<String, Object> sum = new HashMap<>();
	
	@Transient
	private Map<String, Object> avg = new HashMap<>();
	
	@Transient
	private Map<String, Object> count = new HashMap<>();
	
	public BaseEntity() {}

	public BaseEntity(BaseEntityBuilder builder) {
		this.id = builder.id;
		this.sum = builder.sum;
		this.avg = builder.avg;
		this.count = builder.count;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Map<String, Object> getSum() {
		return sum;
	}

	public void setSum(Map<String, Object> sum) {
		this.sum = sum;
	}
	
	@SuppressWarnings("unchecked")
	public void addSum(Map<String, Object> sum) {
		Map.Entry<String, Object> sumEntry = sum.entrySet().iterator().next();
		Map<String, Object> sumCurrent = this.sum;
		
		while (sumCurrent.get(sumEntry.getKey()) != null) {
			sumCurrent = (Map<String, Object>) sumCurrent.get(sumEntry.getKey());
			sum = (Map<String, Object>) sumEntry.getValue();
			sumEntry = sum.entrySet().iterator().next();
		}
		
		sumCurrent.put(sumEntry.getKey(), sumEntry.getValue());
	}

	public Map<String, Object> getAvg() {
		return avg;
	}

	public void setAvg(Map<String, Object> avg) {
		this.avg = avg;
	}
	
	@SuppressWarnings("unchecked")
	public void addAvg(Map<String, Object> avg) {
		Map.Entry<String, Object> avgEntry = avg.entrySet().iterator().next();
		Map<String, Object> avgCurrent = this.avg;
		
		while (avgCurrent.get(avgEntry.getKey()) != null) {
			avgCurrent = (Map<String, Object>) avgCurrent.get(avgEntry.getKey());
			avg = (Map<String, Object>) avgEntry.getValue();
			avgEntry = avg.entrySet().iterator().next();
		}
		
		avgCurrent.put(avgEntry.getKey(), avgEntry.getValue());
	}

	public Map<String, Object> getCount() {
		return count;
	}

	public void setCount(Map<String, Object> count) {
		this.count = count;
	}
	
	@SuppressWarnings("unchecked")
	public void addCount(Map<String, Object> count) {
		Map.Entry<String, Object> countEntry = count.entrySet().iterator().next();
		Map<String, Object> countCurrent = this.count;
		
		while (countCurrent.get(countEntry.getKey()) != null) {
			countCurrent = (Map<String, Object>) countCurrent.get(countEntry.getKey());
			count = (Map<String, Object>) countEntry.getValue();
			countEntry = count.entrySet().iterator().next();
		}
		
		countCurrent.put(countEntry.getKey(), countEntry.getValue());
	}
	
	public static BaseEntityBuilder builder() {
		return new BaseEntityBuilder();
	}
	
	public static class BaseEntityBuilder {
		
		private Long id;
		private Map<String, Object> sum = new HashMap<>();
		private Map<String, Object> avg = new HashMap<>();
		private Map<String, Object> count = new HashMap<>();
		
		public BaseEntityBuilder id(Long id) {
			this.id = id;
			return this;
		}
		
		public BaseEntityBuilder sum(Map<String, Object> sum) {
			this.sum = sum;
			return this;
		}
		
		public BaseEntityBuilder avg(Map<String, Object> avg) {
			this.avg = avg;
			return this;
		}
		
		public BaseEntityBuilder count(Map<String, Object> count) {
			this.count = count;
			return this;
		}
		
		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}
		
		public Map<String, Object> getSum() {
			return sum;
		}

		public void setSum(Map<String, Object> sum) {
			this.sum = sum;
		}

		public Map<String, Object> getAvg() {
			return avg;
		}

		public void setAvg(Map<String, Object> avg) {
			this.avg = avg;
		}

		public Map<String, Object> getCount() {
			return count;
		}

		public void setCount(Map<String, Object> count) {
			this.count = count;
		}
	}
	
}
