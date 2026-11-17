package io.github.marcperez06.ai_api_wrapper.model.agent;

public class MemoryConfig {

	private String type;
	private Integer maxTurns;
	
	public MemoryConfig() {}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getMaxTurns() {
		return this.maxTurns;
	}

	public void setMaxTurns(Integer maxTurns) {
		this.maxTurns = maxTurns;
	}

}
