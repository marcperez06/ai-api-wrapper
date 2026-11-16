package io.github.marcperez06.ai_api_wrapper.client;

import io.github.marcperez06.ai_api_wrapper.config.AiConfig;
import io.github.marcperez06.ai_api_wrapper.exception.AiConfigurationException;
import io.github.marcperez06.ai_api_wrapper.model.AiRequest;
import io.github.marcperez06.ai_api_wrapper.model.AiResponse;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class AiApiClientTest {

    private static class TestableAiApiClient extends BaseAiApiClient {
        public TestableAiApiClient(AiConfig config) {
            super(config);
        }

        @Override
        protected String buildApiUrl() {
            return "https://api.test.com";
        }

        @Override
        protected String buildAiPayload(AiRequest request) {
            return "{}";
        }

        @Override
        public AiResponse generate(AiRequest request) {
            AiRequest merged = mergeRequest(request);
            AiResponse aiResponse = new AiResponse();
            aiResponse.setContent("Test response for: " + merged.getPrompt());
            aiResponse.setRawResponse("raw");
            aiResponse.setModel(merged.getModel());
            aiResponse.setStatusCode(200);
            return aiResponse;
        }

        public AiRequest publicMergeRequest(AiRequest request) {
            return mergeRequest(request);
        }
    }

    @Test
    public void testClientFactoryOpenAiProviders() {
        AiConfig config = new AiConfig();
        
        config.setProvider("openai");
        IAiApiClient client = AiClientFactory.createClient(config);
        assertNotNull(client);
        assertTrue(client instanceof OpenAiCompatibleClient);
        assertEquals(config, client.getConfig());

        config.setProvider("lmstudio");
        client = AiClientFactory.createClient(config);
        assertNotNull(client);
        assertTrue(client instanceof OpenAiCompatibleClient);

        config.setProvider("lm-studio");
        client = AiClientFactory.createClient(config);
        assertNotNull(client);
        assertTrue(client instanceof OpenAiCompatibleClient);

        config.setProvider("custom");
        client = AiClientFactory.createClient(config);
        assertNotNull(client);
        assertTrue(client instanceof OpenAiCompatibleClient);
    }

    @Test
    public void testClientFactoryOllamaProvider() {
        AiConfig config = new AiConfig();
        config.setProvider("ollama");
        IAiApiClient client = AiClientFactory.createClient(config);
        assertNotNull(client);
        assertTrue(client instanceof OllamaClient);
    }

    @Test
    public void testClientFactoryClaudeProviders() {
        AiConfig config = new AiConfig();
        
        config.setProvider("claude");
        IAiApiClient client = AiClientFactory.createClient(config);
        assertNotNull(client);
        assertTrue(client instanceof ClaudeClient);

        config.setProvider("anthropic");
        client = AiClientFactory.createClient(config);
        assertNotNull(client);
        assertTrue(client instanceof ClaudeClient);
    }

    @Test
    public void testClientFactoryGeminiProviders() {
        AiConfig config = new AiConfig();
        
        config.setProvider("gemini");
        IAiApiClient client = AiClientFactory.createClient(config);
        assertNotNull(client);
        assertTrue(client instanceof GeminiClient);

        config.setProvider("google");
        client = AiClientFactory.createClient(config);
        assertNotNull(client);
        assertTrue(client instanceof GeminiClient);
    }

    @Test
    public void testClientFactoryDefaultFallbackAndNullProvider() {
        AiConfig config = new AiConfig();
        config.setProvider("unknown-provider");
        IAiApiClient client = AiClientFactory.createClient(config);
        assertNotNull(client);
        assertTrue(client instanceof OpenAiCompatibleClient);

        config.setProvider(null);
        client = AiClientFactory.createClient(config);
        assertNotNull(client);
        assertTrue(client instanceof OpenAiCompatibleClient);
    }

    @Test(expected = AiConfigurationException.class)
    public void testClientFactoryNullConfigThrowsException() {
        AiClientFactory.createClient(null);
    }

    @Test(expected = AiConfigurationException.class)
    public void testBaseAiApiClientNullConfigThrowsException() {
        new TestableAiApiClient(null);
    }

    @Test
    public void testGenericAiClientInstantiation() {
        AiConfig config = new AiConfig();
        config.setProvider("openai");
        GenericAiClient client = new GenericAiClient(config);
        assertNotNull(client);
        assertEquals(config, client.getConfig());
    }

    @Test
    public void testGenerateWithStringPrompt() {
        AiConfig config = new AiConfig();
        config.setModel("gpt-3.5-turbo");
        TestableAiApiClient client = new TestableAiApiClient(config);

        AiResponse response = client.generate("Hello AI");
        assertNotNull(response);
        assertEquals("Test response for: Hello AI", response.getContent());
        assertEquals("gpt-3.5-turbo", response.getModel());
        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void testMergeRequestWithDefaultsAndOverrides() {
        AiConfig config = new AiConfig();
        config.setModel("config-model");
        config.setTemperature(0.5);
        config.setMaxContent(256);
        Map<String, Object> extraParams = new HashMap<>();
        extraParams.put("extra_key", "extra_val");
        config.setExtraParameters(extraParams);

        TestableAiApiClient client = new TestableAiApiClient(config);

        // Test with null request
        AiRequest mergedNull = client.publicMergeRequest(null);
        assertNotNull(mergedNull);
        assertEquals("config-model", mergedNull.getModel());
        assertEquals(Double.valueOf(0.5), mergedNull.getTemperature());
        assertEquals(Integer.valueOf(256), mergedNull.getMaxContent());
        assertEquals("extra_val", mergedNull.getCustomParameters().get("extra_key"));

        // Test with custom request overrides and additions
        AiRequest request = new AiRequest("Prompt test");
        request.setModel("request-model");
        request.setTemperature(0.9);
        request.setMaxContent(512);
        request.getCustomParameters().put("custom_key", "custom_val");
        request.getCustomParameters().put("extra_key", "override_val");

        request.addMessage("user", "Hi");
        request.addMessage("assistant", "Hello");

        AiRequest merged = client.publicMergeRequest(request);
        assertNotNull(merged);
        assertEquals("Prompt test", merged.getPrompt());
        assertEquals("request-model", merged.getModel());
        assertEquals(Double.valueOf(0.9), merged.getTemperature());
        assertEquals(Integer.valueOf(512), merged.getMaxContent());
        assertEquals("override_val", merged.getCustomParameters().get("extra_key"));
        assertEquals("custom_val", merged.getCustomParameters().get("custom_key"));
        assertEquals(2, merged.getChatHistory().size());
    }
}
