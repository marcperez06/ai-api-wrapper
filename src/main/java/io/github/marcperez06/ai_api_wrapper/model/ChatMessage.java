package io.github.marcperez06.ai_api_wrapper.model;

import io.github.marcperez06.java_utilities.validation.ValidationUtils;

public class ChatMessage {
	
    private String role; // "system", "user", "assistant"
    private String content;

    public ChatMessage() {}

    public ChatMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    
    @Override
    public boolean equals(Object other) {
    	ChatMessage otherChat = (ChatMessage) other;
    	boolean areEquals = ValidationUtils.equalsIgnoreCase(this.role, otherChat.role);
    	areEquals &= ValidationUtils.equalsIgnoreCase(this.content, otherChat.content);
    	return areEquals;
    }

    @Override
    public String toString() {
        return "ChatMessage {" +
                "role='" + role + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
