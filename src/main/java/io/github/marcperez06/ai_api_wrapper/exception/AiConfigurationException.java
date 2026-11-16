package io.github.marcperez06.ai_api_wrapper.exception;

public class AiConfigurationException extends AiException {
    private static final long serialVersionUID = 1L;

    public AiConfigurationException(String message) {
        super(message);
    }

    public AiConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
