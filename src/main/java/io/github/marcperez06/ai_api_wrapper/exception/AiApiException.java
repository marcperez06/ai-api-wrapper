package io.github.marcperez06.ai_api_wrapper.exception;

public class AiApiException extends AiException {
    private static final long serialVersionUID = 1L;
    private final int statusCode;

    public AiApiException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public AiApiException(String message, int statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
