package io.github.marcperez06.ai_api_wrapper.client;

import io.github.marcperez06.ai_api_wrapper.config.AiConfig;

public class GenericAiClient extends OpenAiCompatibleClient {
    public GenericAiClient(AiConfig config) {
        super(config);
    }
}
