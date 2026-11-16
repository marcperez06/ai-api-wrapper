package io.github.marcperez06.ai_api_wrapper.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.marcperez06.ai_api_wrapper.client.ClaudeClient;
import io.github.marcperez06.ai_api_wrapper.client.GeminiClient;
import io.github.marcperez06.ai_api_wrapper.client.OllamaClient;
import io.github.marcperez06.ai_api_wrapper.client.OpenAiCompatibleClient;
import io.github.marcperez06.ai_api_wrapper.config.AiConfig;
import io.github.marcperez06.ai_api_wrapper.model.agent.AgentBehavior;
import io.github.marcperez06.ai_api_wrapper.model.agent.AgentDetails;
import io.github.marcperez06.java_utilities.collection.list.ListUtils;

public class AiRequest {

    private String prompt;
    private Double temperature;
    private Integer maxContent;
    private String model;
    private String systemPrompt;
    private Integer maxChatHistorySize;
    private List<ChatMessage> chatHistory;
    private Map<String, Object> customParameters;
    private AgentBehavior agentBehavior;

    public AiRequest() {
    	this.chatHistory = new ArrayList<ChatMessage>();
    	this.customParameters = new HashMap<String, Object>();
    	this.maxChatHistorySize = -1;
    }

    public AiRequest(String prompt) {
    	this();
        this.prompt = prompt;
    }

    public AiRequest(String prompt, Double temperature, Integer maxContent, String model) {
    	this(prompt);
        this.temperature = temperature;
        this.maxContent = maxContent;
        this.model = model;
    }
    
    public AiRequest(String prompt, AiConfig config) {
    	this(prompt);
        this.temperature = config.getTemperature();
        this.maxContent = config.getMaxContent();
        this.model = config.getModel();
        this.maxChatHistorySize = config.getChatHistorySize();
        this.systemPrompt = config.getSystemPrompt();
    }

    public String getPrompt() {
        return this.prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public Double getTemperature() {
        return this.temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Integer getMaxContent() {
        return this.maxContent;
    }

    public void setMaxContent(Integer maxContent) {
        this.maxContent = maxContent;
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
    
    public Integer getMaxChatHistorySize() {
        return this.maxChatHistorySize;
    }

    public void setMaxChatHistorySize(Integer maxChatHistorySize) {
        this.maxChatHistorySize = maxChatHistorySize;
    }

    public List<ChatMessage> getChatHistory() {
        return this.chatHistory;
    }

    public void setChatHistory(List<ChatMessage> chatHistory) {
        this.chatHistory = chatHistory;
    }
    
   public void clearChatHistory() {
        this.chatHistory.clear();
    }

    public Map<String, Object> getCustomParameters() {
        return this.customParameters;
    }

    public void setCustomParameters(Map<String, Object> customParameters) {
        this.customParameters = customParameters;
    }
    
    public AgentBehavior getAgentBehavior() {
        return this.agentBehavior;
    }

    public void setAgentBehavior(AgentBehavior agentBehavior) {
        this.applyAgentBehavior(agentBehavior);
    }

    public void addMessage(String role, String content) {
        this.chatHistory.add(new ChatMessage(role, content));
        this.enforceMaxChatHistoryLimit();
    }
    
    public void addSystemMessage(String content) {
        this.addMessage("system", content);
    }

    public void addUserMessage(String content) {
        this.addMessage("user", content);
    }

    public void addAiMessage(String content) {
        String aiRole = this.getAiRoleForMessage();
    	this.addMessage(aiRole, content);
    }
    
    private String getAiRoleForMessage() {
    	String aiRole = "assistant";
    	if (this.agentBehavior != null) {
    		String provider = this.agentBehavior.getAgent().getModel().getProvider();
    		
    		switch(provider) {
	            case "gemini":
	            case "google":
	            	aiRole = "model";
	            	break;
	            default:
	            	aiRole = "assitant";
        	}
    		
    	} else if (this.model.toLowerCase().contains("gemini")) {
			aiRole = "model";
    	}
    	
    	return aiRole;
    }
    
    private void enforceMaxChatHistoryLimit() {
    	int maxChatSize = this.maxChatHistorySize * 2; 
    	if (maxChatSize > 0) {
    		// Remove first message if chat can not keep context
            while (this.chatHistory.size() > maxChatSize && this.chatHistory.size() > 2) {
                this.chatHistory.remove(0); // Remove old "user" question
                this.chatHistory.remove(0); // Remove old "model" answer
            }
    	}
    }
    
    public void addMessageToFirstPosition(String role, String content) {
    	if (this.chatHistory != null) {
    		ChatMessage newChatMessage = new ChatMessage(role, content);
    		this.chatHistory.remove(newChatMessage);
    		this.chatHistory.add(0, newChatMessage);
    	}
    }
    
    public void applyAgentBehavior(AgentBehavior behavior) {
        this.agentBehavior = behavior;

        if (behavior != null && behavior.getAgent() != null) {
            AgentDetails details = behavior.getAgent();
            
            if (details.getSystemPrompt() != null) {
                this.systemPrompt = details.getSystemPrompt();
            }
            
            if (details.getModel() != null) {
                if (details.getModel().getName() != null) {
                    this.model = details.getModel().getName();
                }
                if (details.getModel().getTemperature() != null) {
                    this.temperature = details.getModel().getTemperature();
                }
                if (details.getModel().getMaxTokens() != null) {
                    this.maxContent = details.getModel().getMaxTokens();
                }
            }
        }
    }

}
