package io.github.marcperez06.ai_api_wrapper.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.marcperez06.ai_api_wrapper.config.AiConfig;
import io.github.marcperez06.ai_api_wrapper.model.AiRequest;
import io.github.marcperez06.ai_api_wrapper.model.AiResponse;
import io.github.marcperez06.ai_api_wrapper.model.ChatMessage;
import io.github.marcperez06.ai_api_wrapper.model.agent.AgentBehavior;
import io.github.marcperez06.ai_api_wrapper.model.agent.AgentDetails;
import io.github.marcperez06.ai_api_wrapper.model.agent.ToolConfig;
import io.github.marcperez06.java_utilities.json.GsonUtils;
import io.github.marcperez06.java_utilities.validation.ValidationUtils;

public class GeminiClient extends BaseAiApiClient {

    public GeminiClient(AiConfig config) {
        super(config);
        this.buildApiUrl();
    }
    
    @Override
    protected String buildApiUrl() {
    	String modelName = ValidationUtils.isNotNull(super.config.getModel()) ? super.config.getModel() : "gemini-pro";
    	return this.buildApiUrl(modelName);
    }
    
    private String buildApiUrl(String modelName) {
    	if (this.shouldBuildApiUrl(modelName)) {
    		String endpoint = super.config.getEndpoint();
            String apiKey = super.config.getApiKey();
            
            if (ValidationUtils.isEmpty(endpoint)) {
                endpoint = "https://generativelanguage.googleapis.com/v1/models/" + modelName + ":generateContent?key=" + config.getApiKey();
            } else if (!endpoint.contains("key=") && ValidationUtils.isNotNull(apiKey)) {
            	String keyParam = (endpoint.contains("?") ? "&" : "?") + "key=" + apiKey;
            	endpoint = endpoint + keyParam;
            }
            
            super.apiUrl = endpoint;
    	}

    	return super.apiUrl;
    }
    
    private boolean shouldBuildApiUrl(String modelName) {
    	String configModelName = super.config.getModel();
    	boolean buildApiUrl = !ValidationUtils.equalsIgnoreCase(configModelName, modelName);
    	buildApiUrl |= ValidationUtils.isNull(this.apiUrl);
    	return buildApiUrl;
    }

    @Override
    protected String buildAiPayload(AiRequest request) {
    	Map<String, Object> requestBody = new HashMap<String, Object>();
    	
    	this.addChatHistory(requestBody, request);
    	this.addSystemInstructions(requestBody, request);
    	this.addAgentTools(requestBody, request);
    	this.addGenerationConfig(requestBody, request);
    	
        return GsonUtils.getPrettyJSON(requestBody);
    }
    
    private void addChatHistory(Map<String, Object> requestBody, AiRequest request) {
    	List<Map<String, Object>> chatHistory = new ArrayList<Map<String, Object>>();
    	request.addUserMessage(request.getPrompt());
    	
    	for (ChatMessage chatMessage : request.getChatHistory()) {
    		Map<String, Object> chat = new HashMap<String, Object>();
    		Map<String, Object> chatPrompt = this.createTextPart(chatMessage.getContent());
    		chat.put("role", chatMessage.getRole());
    		chat.putAll(chatPrompt);
    		chatHistory.add(chat);
    	}
    	
    	requestBody.put("contents", chatHistory);
    }
    
    private void addSystemInstructions(Map<String, Object> requestBody, AiRequest request) {
    	if (ValidationUtils.isNotNull(request.getSystemPrompt())) {
    		Map<String, Object> systemInstructions = this.createTextPart(request.getSystemPrompt());
	        requestBody.put("systemInstruction", systemInstructions);
        }
    }
    
    private Map<String, Object> createTextPart(String prompt) {
    	Map<String, Object> textPart = new HashMap<String, Object>();
    	textPart.put("parts", Collections.singletonList(
	            Collections.singletonMap("text", prompt)
        ));
    	return textPart;
    }
    
    private void addAgentTools(Map<String, Object> requestBody, AiRequest request) {
    	AgentBehavior agentBehavior = request.getAgentBehavior();
    	if (ValidationUtils.isNotNull(agentBehavior)) {
    		AgentDetails agentDetails = agentBehavior.getAgent();
    		List<ToolConfig> tools = agentDetails.getTools();
    		if (ValidationUtils.isNotEmpty(tools)) {
    			List<Map<String, List<ToolConfig>>> toolsList = new ArrayList<Map<String, List<ToolConfig>>>();
    			Map<String, List<ToolConfig>> functionDeclarations = new HashMap<String, List<ToolConfig>>();
    			
    			functionDeclarations.put("functionDeclarations", tools);
    			toolsList.add(functionDeclarations);
    			requestBody.put("tools", toolsList);
    		}
    	}
    }
    
    private void addGenerationConfig(Map<String, Object> requestBody, AiRequest request) {
    	Map<String, Object> generationConfig = new HashMap<String, Object>();
        generationConfig.put("temperature", request.getTemperature());
        generationConfig.put("maxOutputTokens", request.getMaxContent());
        requestBody.put("generationConfig", generationConfig);
    }
    
    @Override
    public AiResponse generate(AiRequest request) {
        AiRequest mergedRequest = mergeRequest(request);

        String modelName = ValidationUtils.isNotNull(mergedRequest.getModel()) ? mergedRequest.getModel() : "gemini-pro";
        this.buildApiUrl(modelName);
        
        return super.query(null, mergedRequest);
    }

}
