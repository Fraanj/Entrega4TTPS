package ttps.proyecto.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ttps.proyecto.dto.ErrorResponse;

/**
 * Manejo global de excepciones. Convierte excepciones lanzadas en servicios/controllers
 * en respuestas JSON estandarizadas con código HTTP apropiado.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse("Not Found", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(
            BadRequestException ex,
            HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse("Bad Request", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(
            UnauthorizedException ex,
            HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse("Unauthorized", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(
            ForbiddenException ex,
            HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse("Forbidden", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    /**
     * Captura cualquier otra RuntimeException (incluidas las que sigan lanzando los servicios).
     * Devuelve 400 si el mensaje sugiere error de negocio, 500 para el resto.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(
            RuntimeException ex,
            HttpServletRequest request) {
        String error = "Internal Server Error";
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String msg = ex.getMessage();

        // Mensajes típicos de validación/negocio -> 400
        if (msg != null && (msg.contains("no encontrado") || msg.contains("ya está registrado")
                || msg.contains("inválid") || msg.contains("no válido") || msg.contains("Credenciales"))) {
            error = "Bad Request";
            status = HttpStatus.BAD_REQUEST;
        }

        ErrorResponse body = new ErrorResponse(error, msg != null ? msg : "Error interno", request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse(
                "Internal Server Error",
                ex.getMessage() != null ? ex.getMessage() : "Error inesperado",
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
