package io.github.marcperez06.ai_api_wrapper.model.agent;

import java.util.List;

public class AgentDetails {

	private String name;
	private String version;
	private String description;

	private ModelConfig model;

	private String systemPrompt;

	private List<ToolConfig> tools;
	private MemoryConfig memory;
	private GuardrailsConfig guardrails;
	
	public AgentDetails() {}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ModelConfig getModel() {
		return this.model;
	}

	public void setModel(ModelConfig model) {
		this.model = model;
	}

	public String getSystemPrompt() {
		return this.systemPrompt;
	}

	public void setSystemPrompt(String systemPrompt) {
		this.systemPrompt = systemPrompt;
	}

	public List<ToolConfig> getTools() {
		return this.tools;
	}

	public void setTools(List<ToolConfig> tools) {
		this.tools = tools;
	}

	public MemoryConfig getMemory() {
		return this.memory;
	}

	public void setMemory(MemoryConfig memory) {
		this.memory = memory;
	}

	public GuardrailsConfig getGuardrails() {
		return this.guardrails;
	}

	public void setGuardrails(GuardrailsConfig guardrails) {
		this.guardrails = guardrails;
	}

}
