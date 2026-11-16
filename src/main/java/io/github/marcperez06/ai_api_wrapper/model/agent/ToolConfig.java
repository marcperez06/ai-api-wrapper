package io.github.marcperez06.ai_api_wrapper.model.agent;

import java.util.Map;

public class ToolConfig {

	private String name;
	private String description;
	private Map<String, Object> parameters;

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Map<String, Object> getParameters() {
		return this.parameters;
	}

	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}

}
