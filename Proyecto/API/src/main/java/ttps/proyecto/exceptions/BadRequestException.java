package ttps.proyecto.exceptions;

/**
 * Se lanza cuando la petición es inválida (datos mal formados, validación fallida).
 * El GlobalExceptionHandler devuelve HTTP 400.
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
