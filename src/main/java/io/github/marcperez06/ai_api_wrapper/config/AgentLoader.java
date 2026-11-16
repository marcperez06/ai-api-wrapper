package io.github.marcperez06.ai_api_wrapper.config;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import io.github.marcperez06.ai_api_wrapper.exception.AiConfigurationException;
import io.github.marcperez06.ai_api_wrapper.model.agent.AgentBehavior;

public class AgentLoader {
	
    public static AgentBehavior loadAgentFromYaml(String filePath) {
    	AgentBehavior agentBehavior = null;
    	try {
    		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            agentBehavior = mapper.readValue(new File(filePath), AgentBehavior.class);	
    	} catch (IOException e) {
    		throw new AiConfigurationException("Can not load Agent behavior from file " + filePath, e);
    	}
    	return agentBehavior;
    }

}
