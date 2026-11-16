package io.github.marcperez06.ai_api_wrapper;

import io.github.marcperez06.ai_api_wrapper.config.AgentLoader;
import io.github.marcperez06.ai_api_wrapper.information.Paths;
import io.github.marcperez06.ai_api_wrapper.model.agent.AgentBehavior;
import org.junit.Test;

import static org.junit.Assert.*;

public class MainTest {

    @Test
    public void testLoadAgentFromYaml() {
        String yamlPath = Paths.RESOURCES_FOLDER + "agent_config" + Paths.FILE_SEPARATOR + "agent-config-example.yml";
        AgentBehavior behavior = AgentLoader.loadAgentFromYaml(yamlPath);
        assertNotNull(behavior);
        assertEquals("support-assistant-v2", behavior.getAgent().getName());
        assertEquals("2.1.0", behavior.getAgent().getVersion());
    }

}

