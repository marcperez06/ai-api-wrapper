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
import io.github.marcperez06.java_utilities.json.GsonUtils;
import io.github.marcperez06.java_utilities.uri.UriUtils;
import io.github.marcperez06.java_utilities.validation.ValidationUtils;

public class OpenAiCompatibleClient extends BaseAiApiClient {

    public OpenAiCompatibleClient(AiConfig config) {
        super(config);
    }

    @Override
    protected String buildApiUrl() {
    	if (ValidationUtils.isNull(super.apiUrl)) {
    		String endpoint = super.config.getEndpoint();
            
            if (ValidationUtils.isEmpty(endpoint)) {
            	endpoint = "https://api.openai.com/v1";
            }
            
            if (!endpoint.endsWith("chat/completions")) {
            	endpoint = UriUtils.path(endpoint, "chat/completions");
            }
            
            super.apiUrl = endpoint;
    	}

    	return super.apiUrl;
    }

    @Override
    protected String buildAiPayload(AiRequest request) {
        Map<String, Object> requestBody = new HashMap<String, Object>();

        this.addSystemInstructions(requestBody, request);
    	
    	request.addUserMessage(request.getPrompt());
    	requestBody.put("messages", request.getChatHistory());
    	
    	this.addAgentTools(requestBody, request);
    	this.addGenerationConfig(requestBody, request);
    	
    	Map<String, Object> customParams = request.getCustomParameters();
    	
    	if (ValidationUtils.isNotNull(customParams)) {
            for (Map.Entry<String, Object> entry : customParams.entrySet()) {
                requestBody.put(entry.getKey(), entry.getValue());
            }
        }
    	
        return GsonUtils.getPrettyJSON(requestBody);
    }
    
    private void addSystemInstructions(Map<String, Object> requestBody, AiRequest request) {
    	if (ValidationUtils.isNotNull(request.getSystemPrompt())) {
    		request.addMessageToFirstPosition("system", request.getSystemPrompt());
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
    	String model = ValidationUtils.isNotEmpty(request.getModel()) ? request.getModel() : "gpt-3.5-turbo";
    	requestBody.put("model", model);
    	requestBody.put("temperature", request.getTemperature());
    	requestBody.put("max_tokens", request.getMaxContent());
    }

    @Override
    public AiResponse generate(AiRequest request) {
    	AiRequest mergedRequest = super.mergeRequest(request);
        this.buildApiUrl();

        Map<String, String> headers = new HashMap<String, String>();
        if (ValidationUtils.isNotEmpty(super.config.getApiKey())) {
            headers.put("Authorization", "Bearer " + super.config.getApiKey());
        }

        return super.query(headers, mergedRequest);
    }
}
