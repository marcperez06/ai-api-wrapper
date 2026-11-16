package io.github.marcperez06.ai_api_wrapper;

import io.github.marcperez06.ai_api_wrapper.client.IAiApiClient;
import io.github.marcperez06.ai_api_wrapper.client.AiClientFactory;
import io.github.marcperez06.ai_api_wrapper.client.ClaudeClient;
import io.github.marcperez06.ai_api_wrapper.client.OllamaClient;
import io.github.marcperez06.ai_api_wrapper.client.OpenAiCompatibleClient;
import io.github.marcperez06.ai_api_wrapper.config.AiConfig;
import io.github.marcperez06.ai_api_wrapper.config.AiConfigLoader;
import io.github.marcperez06.ai_api_wrapper.information.Paths;
import io.github.marcperez06.ai_api_wrapper.model.AiRequest;
import org.junit.Test;

import static org.junit.Assert.*;

public class AiApiWrapperTest {
	
	private static String SRC_TEST_RESOURCE = Paths.USER_DIR + Paths.FILE_SEPARATOR + "src" + Paths.FILE_SEPARATOR + "test" + Paths.FILE_SEPARATOR + "resources" + Paths.FILE_SEPARATOR; 

    @Test
    public void testLoadPropertiesConfig() {
        AiConfig config = AiConfigLoader.load(SRC_TEST_RESOURCE + "ai-config.properties");
        assertNotNull(config);
        assertEquals("openai", config.getProvider());
        assertEquals("test-api-key", config.getApiKey());
        assertEquals("https://api.openai.com/v1", config.getEndpoint());
        assertEquals("gpt-4", config.getModel());
        assertEquals(Double.valueOf(0.5), config.getTemperature());
        assertEquals(Integer.valueOf(20), config.getTimeout());
        assertEquals(Integer.valueOf(512), config.getMaxContent());

        IAiApiClient client = AiClientFactory.createClient(config);
        assertNotNull(client);
        assertTrue(client instanceof OpenAiCompatibleClient);
    }

    @Test
    public void testLoadJsonConfig() {
        AiConfig config = AiConfigLoader.load(SRC_TEST_RESOURCE + "ai-config.json");
        assertNotNull(config);
        assertEquals("ollama", config.getProvider());
        assertEquals("", config.getApiKey());
        assertEquals("http://localhost:11434", config.getEndpoint());
        assertEquals("llama3", config.getModel());
        assertEquals(Double.valueOf(0.7), config.getTemperature());
        assertEquals(Integer.valueOf(30), config.getTimeout());
        assertEquals(Integer.valueOf(1024), config.getMaxContent());

        IAiApiClient client = AiClientFactory.createClient(config);
        assertNotNull(client);
        assertTrue(client instanceof OllamaClient);
    }

    @Test
    public void testLoadYmlConfig() {
        AiConfig config = AiConfigLoader.load(SRC_TEST_RESOURCE + "ai-config.yml");
        assertNotNull(config);
        assertEquals("claude", config.getProvider());
        assertEquals("test-claude-key", config.getApiKey());
        assertEquals("https://api.anthropic.com/v1", config.getEndpoint());
        assertEquals("claude-3-opus-20240229", config.getModel());
        assertEquals(Double.valueOf(0.2), config.getTemperature());
        assertEquals(Integer.valueOf(15), config.getTimeout());
        assertEquals(Integer.valueOf(2048), config.getMaxContent());

        IAiApiClient client = AiClientFactory.createClient(config);
        assertNotNull(client);
        assertTrue(client instanceof ClaudeClient);
    }

    @Test
    public void testAiRequestAndConfigMerging() {
        AiConfig config = new AiConfig();
        config.setProvider("openai");
        config.setModel("gpt-3.5-turbo");
        config.setTemperature(0.7);
        config.setMaxContent(500);

        IAiApiClient client = AiClientFactory.createClient(config);
        assertNotNull(client);
        assertEquals(config, client.getConfig());

        AiRequest request = new AiRequest("Hello AI");
        request.setModel("gpt-4");
        request.setTemperature(0.1);

        assertEquals("Hello AI", request.getPrompt());
        assertEquals("gpt-4", request.getModel());
        assertEquals(Double.valueOf(0.1), request.getTemperature());
    }
}
