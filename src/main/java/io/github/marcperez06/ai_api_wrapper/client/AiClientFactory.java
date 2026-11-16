package io.github.marcperez06.ai_api_wrapper.client;

import io.github.marcperez06.ai_api_wrapper.config.AiConfig;
import io.github.marcperez06.ai_api_wrapper.exception.AiConfigurationException;
import io.github.marcperez06.java_utilities.validation.ValidationUtils;

public class AiClientFactory {

    public static IAiApiClient createClient(AiConfig config) {
        IAiApiClient apiClient = null;

        if (ValidationUtils.isNotNull(config)) {
        	String provider = config.getProvider();
            if (ValidationUtils.isNull(provider)) {
                provider = "openai"; // default fallback
            }
            provider = provider.toLowerCase().trim();

            switch (provider) {
                case "openai":
                case "lmstudio":
                case "lm-studio":
                case "custom":
                	apiClient = new OpenAiCompatibleClient(config);
                	break;
                case "ollama":
                	apiClient = new OllamaClient(config);
                	break;
                case "claude":
                case "anthropic":
                	apiClient = new ClaudeClient(config);
                	break;
                case "gemini":
                case "google":
                	apiClient = new GeminiClient(config);
                	break;
                default:
                	apiClient = new OpenAiCompatibleClient(config);
            }
        } else {
        	throw new AiConfigurationException("AiConfig cannot be null");
        }
        
    	return apiClient;
    }
}
