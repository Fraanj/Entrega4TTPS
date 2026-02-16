package ttps.proyecto.exceptions;

/**
 * Se lanza cuando el usuario no tiene permiso para la acción (ej: modificar recurso ajeno).
 * El GlobalExceptionHandler devuelve HTTP 403.
 */
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}
