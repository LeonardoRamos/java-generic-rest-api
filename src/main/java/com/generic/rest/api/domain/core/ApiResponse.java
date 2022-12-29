package com.generic.rest.api.domain.core;

import java.util.List;

public class ApiResponse<E extends BaseEntity> {
	
	private List<E> records;
	private ApiMetadata metadata;
	
	public List<E> getRecords() {
		return records;
	}

	public void setRecords(List<E> records) {
		this.records = records;
	}

	public ApiMetadata getMetadata() {
		return metadata;
	}

	public void setMetadata(ApiMetadata metadata) {
		this.metadata = metadata;
	}
	
	@Override
	public String toString() {
		return "ApiResponse [records=" + records + ", metadata=" + metadata + "]";
	}
	
}