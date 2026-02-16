package ttps.proyecto.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

/**
 * Respuesta estandarizada para errores de la API.
 * Usado por GlobalExceptionHandler.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private String error;
    private String message;
    private Instant timestamp;
    private String path;

    public ErrorResponse() {
        this.timestamp = Instant.now();
    }

    public ErrorResponse(String message) {
        this();
        this.message = message;
        this.error = "Bad Request";
    }

    public ErrorResponse(String error, String message) {
        this();
        this.error = error;
        this.message = message;
    }

    public ErrorResponse(String error, String message, String path) {
        this(error, message);
        this.path = path;
    }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
}
