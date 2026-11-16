package io.github.marcperez06.ai_api_wrapper.exception;

public class AiException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public AiException(String message) {
        super(message);
    }

    public AiException(String message, Throwable cause) {
        super(message, cause);
    }
}
