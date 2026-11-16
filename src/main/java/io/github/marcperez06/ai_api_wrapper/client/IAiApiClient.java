package io.github.marcperez06.ai_api_wrapper.client;

import io.github.marcperez06.ai_api_wrapper.config.AiConfig;
import io.github.marcperez06.ai_api_wrapper.model.AiRequest;
import io.github.marcperez06.ai_api_wrapper.model.AiResponse;

public interface IAiApiClient {
    AiResponse generate(String prompt);
    AiResponse generate(AiRequest request);
    AiConfig getConfig();
}
