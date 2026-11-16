package io.github.marcperez06.ai_api_wrapper.client;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.marcperez06.ai_api_wrapper.config.AiConfig;
import io.github.marcperez06.ai_api_wrapper.exception.AiApiException;
import io.github.marcperez06.ai_api_wrapper.exception.AiConfigurationException;
import io.github.marcperez06.ai_api_wrapper.model.AiRequest;
import io.github.marcperez06.ai_api_wrapper.model.AiResponse;
import io.github.marcperez06.ai_api_wrapper.model.AiResponseTransformer;
import io.github.marcperez06.ai_api_wrapper.model.ChatMessage;
import io.github.marcperez06.ai_api_wrapper.model.agent.AgentBehavior;
import io.github.marcperez06.java_utilities.api.request.Request;
import io.github.marcperez06.java_utilities.api.request.Response;
import io.github.marcperez06.java_utilities.api.request.ResponseTypeHolder;
import io.github.marcperez06.java_utilities.api.request.enums.HttpMethodEnum;
import io.github.marcperez06.java_utilities.api.rest.UnirestClient;
import io.github.marcperez06.java_utilities.validation.ValidationUtils;

public abstract class BaseAiApiClient implements IAiApiClient {

    protected final AiConfig config;
    protected String apiUrl;
    protected static final ObjectMapper objectMapper = new ObjectMapper();

    protected BaseAiApiClient(AiConfig config) {
        if (config == null) {
            throw new AiConfigurationException("AiConfig cannot be null");
        }
        this.config = config;
    }
    

    @Override
    public AiConfig getConfig() {
        return this.config;
    }
    
    protected abstract String buildApiUrl();
    protected abstract String buildAiPayload(AiRequest request);

    @Override
    public AiResponse generate(String prompt) {
        return generate(new AiRequest(prompt));
    }

    protected AiRequest mergeRequest(AiRequest request) {
        AiRequest merged = new AiRequest();
        request = ValidationUtils.isNotNull(request) ? request : new AiRequest();
        
        String prompt = request.getPrompt();
        String model = ValidationUtils.isNotNull(request.getModel()) ? request.getModel() : this.config.getModel();
        Double temperature = ValidationUtils.isNotNull(request.getTemperature()) ? request.getTemperature() : this.config.getTemperature();
        Integer maxContent = ValidationUtils.isNotNull(request.getMaxContent()) ? request.getMaxContent() : this.config.getMaxContent();
        AgentBehavior agent = ValidationUtils.isNotNull(request.getAgentBehavior()) ? request.getAgentBehavior() : this.config.getAgentBehavior();
        Map<String, Object> customParams = request.getCustomParameters();
        Map<String, Object> configExtraParams = this.config.getExtraParameters();
        Integer maxChatHistorySize = this.config.getChatHistorySize();
        List<ChatMessage> chatHistory = request.getChatHistory();
        
        merged.setPrompt(prompt);
        merged.setModel(model);
        merged.setTemperature(temperature);       
        merged.setMaxContent(maxContent);
        merged.setMaxChatHistorySize(maxChatHistorySize);
        merged.setAgentBehavior(agent);
        
        if (ValidationUtils.isNotEmpty(customParams)) {
            merged.getCustomParameters().putAll(customParams);
        }
        
        if (ValidationUtils.isNotNull(configExtraParams)) {
            for (Map.Entry<String, Object> entry : configExtraParams.entrySet()) {
                if (!merged.getCustomParameters().containsKey(entry.getKey())) {
                    merged.getCustomParameters().put(entry.getKey(), entry.getValue());
                }
            }
        }
        
        if (ValidationUtils.isNotEmpty(chatHistory)) {
            merged.getChatHistory().addAll(chatHistory);
        }

        return merged;
    }
    
    protected AiResponse query(Map<String, String> headers, AiRequest request) {
    	String jsonPayload = this.buildAiPayload(request);
        Response<Map<String, Object>> response = this.executePost(this.apiUrl, headers, jsonPayload);
        AiResponse aiResponse = this.extractResponse(response);
        request.addAiMessage(aiResponse.getContent());
        return aiResponse;
    }

    protected Response<Map<String, Object>> executePost(String urlString, Map<String, String> headers, String jsonBody) {
    	int timeoutSeconds = ValidationUtils.isNotNull(this.config.getTimeout()) ? this.config.getTimeout() : 30;
    	int timeoutMs = timeoutSeconds * 1000;
    	
    	UnirestClient apiClient = new UnirestClient();
    	//apiClient.connectionTimeout(timeoutMs);
    	
    	Request request = new Request(HttpMethodEnum.POST, urlString);
		request.setResponseType(new ResponseTypeHolder<Map<String, Object>>() {});
		request.jsonRequest();
		request.setBody(jsonBody);
		
		if (headers != null) {
			request.addHeaders(headers);
        }
		
		Response<Map<String, Object>> response = apiClient.send(request);
		
		if (!response.isSuccessful()) {
			System.out.println("Response is not successful, Status: " + response.getStatusCode());
			String error = "Error from AI: HTTP code " + response.getStatusCode() + " - API Response: " + response.getOriginalBody();
			throw new AiApiException(error, response.getStatusCode());
		}
		
		return response;
    }
    
    protected AiResponse extractResponse(Response<Map<String, Object>> response) {
    	AiResponse responseObj = null;

    	if (response.isSuccessful()) {
            Optional<Map<String, Object>> optionalResponseObj = response.getResponseObject();
            
            if (optionalResponseObj.isPresent()) {
            	String provider = AiConfig.getConfiguration().getProvider();
            	responseObj = AiResponseTransformer.fromMap(optionalResponseObj.get(), response.getStatusCode(), provider);
            } else {
            	throw new AiApiException("Failed to parse response", response.getStatusCode());
            }
            
        } else {
        	throw new AiApiException("API error: " + response.getOriginalBody(), response.getStatusCode());
        }
    	
    	return responseObj;
    }

}
