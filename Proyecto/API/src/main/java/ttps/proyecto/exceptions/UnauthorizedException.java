package ttps.proyecto.exceptions;

/**
 * Se lanza cuando las credenciales son inválidas o el token no es válido.
 * El GlobalExceptionHandler devuelve HTTP 401.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
