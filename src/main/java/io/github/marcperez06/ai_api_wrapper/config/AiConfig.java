package io.github.marcperez06.ai_api_wrapper.config;

import java.util.HashMap;
import java.util.Map;

import io.github.marcperez06.ai_api_wrapper.model.agent.AgentBehavior;

public class AiConfig {
	
	private static AiConfig instance;

    private String provider;
    private String apiKey;
    private String endpoint;
    private String model;
    private String systemPrompt;
    private Double temperature;
    private Integer timeout;
    private Integer maxContent;
    private Integer chatHistorySize;
    private Map<String, Object> extraParameters;
    private AgentBehavior agentBehavior;
    
    public AiConfig() {
        this.temperature = 0.7;
        this.timeout = 30;
        this.maxContent = 1024;
        this.chatHistorySize = 3;
        this.extraParameters = new HashMap<String, Object>();
        instance = this;
    }
    
    public static AiConfig getConfiguration() {
		if (instance == null) {
			instance = new AiConfig();
		}
		return instance;
	}

    public String getProvider() {
        return this.provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getApiKey() {
        return this.apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getEndpoint() {
        return this.endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getModel() {
        return this.model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSystemPrompt() {
        return this.systemPrompt;
    }

    public void setSystemPrompt(String systemPrompt) {
        this.systemPrompt = systemPrompt;
    }

    public Double getTemperature() {
        return this.temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Integer getTimeout() {
        return this.timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public Integer getMaxContent() {
        return this.maxContent;
    }

    public void setMaxContent(Integer maxContent) {
        this.maxContent = maxContent;
    }
    
    public Integer getChatHistorySize() {
        return this.chatHistorySize;
    }

    public void setChatHistorySize(Integer chatHistorySize) {
        this.chatHistorySize = chatHistorySize;
    }

    public Map<String, Object> getExtraParameters() {
        return this.extraParameters;
    }

    public void setExtraParameters(Map<String, Object> extraParameters) {
        this.extraParameters = extraParameters;
    }
    
    public AgentBehavior getAgentBehavior() {
        return this.agentBehavior;
    }

    public void setAgentBehavior(AgentBehavior agentBehavior) {
        this.agentBehavior = agentBehavior;
    }

}
