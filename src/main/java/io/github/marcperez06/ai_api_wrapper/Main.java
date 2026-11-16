package io.github.marcperez06.ai_api_wrapper;

import io.github.marcperez06.ai_api_wrapper.client.AiClientFactory;
import io.github.marcperez06.ai_api_wrapper.client.IAiApiClient;
import io.github.marcperez06.ai_api_wrapper.config.AgentLoader;
import io.github.marcperez06.ai_api_wrapper.config.AiConfig;
import io.github.marcperez06.ai_api_wrapper.config.AiConfigLoader;
import io.github.marcperez06.ai_api_wrapper.information.Paths;
import io.github.marcperez06.ai_api_wrapper.model.AiRequest;
import io.github.marcperez06.ai_api_wrapper.model.AiResponse;
import io.github.marcperez06.ai_api_wrapper.model.agent.AgentBehavior;

/**
 * Hello world!
 *
 */
public class Main {
	
    public static void main( String[] args ) {
    	// 1. Load the behavior from your YAML configuration
    	String yamlPath = Paths.RESOURCES_FOLDER + "agent_config" + Paths.FILE_SEPARATOR + "agent-config-example.yml";
    	//AgentBehavior behavior = AgentLoader.loadAgentFromYaml(yamlPath);
    	
    	AiConfig aiConfig = AiConfigLoader.load(Paths.PROPERTIES_PATH);
    	//aiConfig.setAgentBehavior(behavior);
    	
    	IAiApiClient aiClient = AiClientFactory.createClient(aiConfig);
    	
    	AiResponse aiResponse = aiClient.generate("Where is my package?");
    	
    	System.out.println("Ai response " + aiResponse.getContent());
    	
    	aiResponse = aiClient.generate("intersting I not remember it, I think it was lost");
    	
    	System.out.println("Ai response " + aiResponse.getContent());
    	
    	aiResponse = aiClient.generate("Give me the price for a PS5");
    	
    	System.out.println("Ai response " + aiResponse.getContent());

    	// 2. Initialize and configure the request using the agent behavior
    	//AiRequest request = new AiRequest("Where is my package?");
	    //request.applyAgentBehavior(behavior);
	    
    }

}
