package com.generic.rest.api.domain.core;

import java.util.List;

public class ApiResponse<ENTITY extends BaseEntity> {
	
	private List<ENTITY> records;
	private ApiMetadata metadata;
	
	public List<ENTITY> getRecords() {
		return records;
	}

	public void setRecords(List<ENTITY> records) {
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