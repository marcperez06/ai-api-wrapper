package io.github.marcperez06.ai_api_wrapper.model;

import java.util.List;
import java.util.Map;

import io.github.marcperez06.ai_api_wrapper.exception.AiException;

public class AiResponseTransformer {
	
	public static AiResponse fromMap(Map<String, Object> map, int statusCode, String provider) {
		AiResponse response = new AiResponse();
		
		switch (provider) {
	        case "openai":
	        case "lmstudio":
	        case "lm-studio":
	        case "custom":
	        	response = fromOpenAI(map, statusCode);
	        	break;
	        case "ollama":
	        	response = fromOllama(map, statusCode);
	        	break;
	        case "claude":
	        case "anthropic":
	        	response = fromAnthropic(map, statusCode);
	        	break;
	        case "gemini":
	        case "google":
	        	response = fromGemini(map, statusCode);
	        	break;
	        default:
	        	throw new AiException("Provider not compatible for transform response");
	    }
		
		return response;
	}
	
	/**
     * Transforms an OpenAI Chat Completion response Map to AiResponse.
     * OpenAI structure typically contains: model, choices[0].message.content, choices[0].finish_reason, usage[...]
     */
    @SuppressWarnings("unchecked")
    public static AiResponse fromOpenAI(Map<String, Object> map, int statusCode) {
        AiResponse res = new AiResponse();
        res.setStatusCode(statusCode);
        res.setModel((String) map.get("model"));

        // Extract Choices / Content / Finish Reason
        List<Map<String, Object>> choices = (List<Map<String, Object>>) map.get("choices");
        if (choices != null && !choices.isEmpty()) {
            Map<String, Object> firstChoice = choices.get(0);
            res.setFinishReason((String) firstChoice.get("finish_reason"));
            
            Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
            if (message != null) {
                res.setContent((String) message.get("content"));
            }
        }

        // Extract Token Usage
        Map<String, Object> usage = (Map<String, Object>) map.get("usage");
        if (usage != null) {
            res.setPromptTokens(asInteger(usage.get("prompt_tokens")));
            res.setCompletionTokens(asInteger(usage.get("completion_tokens")));
            res.setTotalTokens(asInteger(usage.get("total_tokens")));
        }

        res.setMetadata(map);
        return res;
    }

    /**
     * Transforms an Anthropic Messages response Map to AiResponse.
     * Anthropic structure typically contains: model, content[0].text, stop_reason, usage[...]
     */
    @SuppressWarnings("unchecked")
    public static AiResponse fromAnthropic(Map<String, Object> map, int statusCode) {
        AiResponse res = new AiResponse();
        res.setStatusCode(statusCode);
        res.setModel((String) map.get("model"));
        res.setFinishReason((String) map.get("stop_reason"));

        // Extract Content array
        List<Map<String, Object>> contentList = (List<Map<String, Object>>) map.get("content");
        if (contentList != null && !contentList.isEmpty()) {
            // Usually type is "text"
            res.setContent((String) contentList.get(0).get("text"));
        }

        // Extract Token Usage
        Map<String, Object> usage = (Map<String, Object>) map.get("usage");
        if (usage != null) {
            res.setPromptTokens(asInteger(usage.get("input_tokens")));
            res.setCompletionTokens(asInteger(usage.get("output_tokens")));
            if (res.getPromptTokens() != null && res.getCompletionTokens() != null) {
                res.setTotalTokens(res.getPromptTokens() + res.getCompletionTokens());
            }
        }

        res.setMetadata(map);
        return res;
    }

    /**
     * Transforms a Google Gemini response Map to AiResponse.
     * Gemini structure typically contains: modelVersion, candidates[0].content.parts[0].text, 
     * candidates[0].finishReason, usageMetadata[...]
     */
    @SuppressWarnings("unchecked")
    public static AiResponse fromGemini(Map<String, Object> map, int statusCode) {
        AiResponse res = new AiResponse();
        res.setStatusCode(statusCode);
        res.setModel((String) map.get("modelVersion"));

        // Extract Candidates
        List<Map<String, Object>> candidates = (List<Map<String, Object>>) map.get("candidates");
        if (candidates != null && !candidates.isEmpty()) {
            Map<String, Object> firstCandidate = candidates.get(0);
            res.setFinishReason((String) firstCandidate.get("finishReason"));

            Map<String, Object> content = (Map<String, Object>) firstCandidate.get("content");
            if (content != null) {
                List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                if (parts != null && !parts.isEmpty()) {
                    res.setContent((String) parts.get(0).get("text"));
                }
            }
        }

        // Extract Token Usage
        Map<String, Object> usageMetadata = (Map<String, Object>) map.get("usageMetadata");
        if (usageMetadata != null) {
            res.setPromptTokens(asInteger(usageMetadata.get("promptTokenCount")));
            res.setCompletionTokens(asInteger(usageMetadata.get("candidatesTokenCount")));
            res.setTotalTokens(asInteger(usageMetadata.get("totalTokenCount")));
        }

        res.setMetadata(map);
        return res;
    }
    
    /**
     * Transforms an Ollama chat/generate response Map to AiResponse.
     * Ollama structure typically contains: model, message.content (or response for generate), 
     * done, prompt_eval_count, eval_count[...]
     */
    @SuppressWarnings("unchecked")
    public static AiResponse fromOllama(Map<String, Object> map, int statusCode) {
        AiResponse res = new AiResponse();
        res.setStatusCode(statusCode);
        res.setModel((String) map.get("model"));

        // Ollama uses 'done_reason' or a boolean 'done'
        Object done = map.get("done");
        if (done instanceof Boolean && (Boolean) done) {
            res.setFinishReason("stop");
        } else {
            res.setFinishReason((String) map.get("done_reason"));
        }

        // Support both chat endpoint (/api/chat -> message.content) 
        // and generate endpoint (/api/generate -> response)
        Map<String, Object> message = (Map<String, Object>) map.get("message");
        if (message != null) {
            res.setContent((String) message.get("content"));
        } else {
            res.setContent((String) map.get("response"));
        }

        // Extract Token Usage (Ollama reports prompt evaluation and evaluation tokens)
        Integer promptTokens = asInteger(map.get("prompt_eval_count"));
        Integer completionTokens = asInteger(map.get("eval_count"));
        
        res.setPromptTokens(promptTokens);
        res.setCompletionTokens(completionTokens);
        
        if (promptTokens != null && completionTokens != null) {
            res.setTotalTokens(promptTokens + completionTokens);
        }

        res.setMetadata(map);
        return res;
    }

    // Safe casting helper for JSON numbers which might parse as Integer or Long depending on the JSON parser
    private static Integer asInteger(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Number) {
            return ((Number) obj).intValue();
        }
        return Integer.valueOf(obj.toString());
    }

}