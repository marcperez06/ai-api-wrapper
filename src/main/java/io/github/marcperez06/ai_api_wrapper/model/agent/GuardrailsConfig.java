package io.github.marcperez06.ai_api_wrapper.model.agent;

import java.util.List;

public class GuardrailsConfig {
	
	private Boolean contentFiltering;
	private List<String> blockTopics;
	private Integer maxRetryOnError;
	
	public GuardrailsConfig() {}

	public Boolean getContentFiltering() {
		return this.contentFiltering;
	}

	public void setContentFiltering(Boolean contentFiltering) {
		this.contentFiltering = contentFiltering;
	}

	public List<String> getBlockTopics() {
		return this.blockTopics;
	}

	public void setBlockTopics(List<String> blockTopics) {
		this.blockTopics = blockTopics;
	}

	public Integer getMaxRetryOnError() {
		return this.maxRetryOnError;
	}

	public void setMaxRetryOnError(Integer maxRetryOnError) {
		this.maxRetryOnError = maxRetryOnError;
	}
}
