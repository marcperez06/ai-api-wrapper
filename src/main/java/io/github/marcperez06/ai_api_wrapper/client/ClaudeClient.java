package io.github.marcperez06.ai_api_wrapper.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.marcperez06.ai_api_wrapper.config.AiConfig;
import io.github.marcperez06.ai_api_wrapper.model.AiRequest;
import io.github.marcperez06.ai_api_wrapper.model.AiResponse;
import io.github.marcperez06.ai_api_wrapper.model.agent.AgentBehavior;
import io.github.marcperez06.ai_api_wrapper.model.agent.AgentDetails;
import io.github.marcperez06.ai_api_wrapper.model.agent.ToolConfig;
import io.github.marcperez06.java_utilities.api.request.Response;
import io.github.marcperez06.java_utilities.json.GsonUtils;
import io.github.marcperez06.java_utilities.uri.UriUtils;
import io.github.marcperez06.java_utilities.validation.ValidationUtils;

public class ClaudeClient extends BaseAiApiClient {

    public ClaudeClient(AiConfig config) {
        super(config);
        this.buildApiUrl();
    }
    
    @Override
    protected String buildApiUrl() {
    	if (ValidationUtils.isNull(super.apiUrl)) {
    		String endpoint = super.config.getEndpoint();
            
            if (ValidationUtils.isEmpty(endpoint)) {
            	endpoint = "https://api.anthropic.com/v1/messages";
            } else if (!endpoint.endsWith("/messages")) {
            	endpoint = UriUtils.path(endpoint, "v1/messages");
            }
            
            super.apiUrl = endpoint;
    	}

    	return super.apiUrl;
    }

    @Override
    protected String buildAiPayload(AiRequest request) {
    	Map<String, Object> requestBody = new HashMap<String, Object>();
    	
    	request.addUserMessage(request.getPrompt());
    	requestBody.put("messages", request.getChatHistory());
    	
    	this.addSystemInstructions(requestBody, request);
    	this.addAgentTools(requestBody, request);
    	this.addGenerationConfig(requestBody, request);
    	
        return GsonUtils.getPrettyJSON(requestBody);
    }
    
    private void addSystemInstructions(Map<String, Object> requestBody, AiRequest request) {
    	if (ValidationUtils.isNotNull(request.getSystemPrompt())) {
    		requestBody.put("system", request.getSystemPrompt());
        }
    }
    
    private void addAgentTools(Map<String, Object> requestBody, AiRequest request) {
    	AgentBehavior agentBehavior = request.getAgentBehavior();
    	if (ValidationUtils.isNotNull(agentBehavior)) {
    		AgentDetails agentDetails = agentBehavior.getAgent();
    		List<ToolConfig> tools = agentDetails.getTools();
    		if (ValidationUtils.isNotEmpty(tools)) {
    			requestBody.put("tools", tools);
    		}
    	}
    }
    
    private void addGenerationConfig(Map<String, Object> requestBody, AiRequest request) {
    	String model = ValidationUtils.isNotEmpty(request.getModel()) ? request.getModel() : "claude-3-opus-20240229";
        requestBody.put("model", model); 
        requestBody.put("temperature", request.getTemperature());
        requestBody.put("max_tokens", request.getMaxContent());
    }
    
    @Override
    public AiResponse generate(AiRequest request) {
        AiRequest mergedRequest = super.mergeRequest(request);
        this.buildApiUrl();

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("x-api-key", super.config.getApiKey());
        headers.put("anthropic-version", "2023-06-01");

        return super.query(headers, mergedRequest);
    }

}
