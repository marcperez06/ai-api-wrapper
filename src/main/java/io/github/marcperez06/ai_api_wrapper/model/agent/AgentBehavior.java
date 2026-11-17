package io.github.marcperez06.ai_api_wrapper.model.agent;

public class AgentBehavior {
	
	private AgentDetails agent;
	
	public AgentBehavior() {
		this.agent = null;
	}

    public AgentDetails getAgent() {
        return this.agent;
    }

    public void setAgent(AgentDetails agent) {
        this.agent = agent;
    }
    
}
