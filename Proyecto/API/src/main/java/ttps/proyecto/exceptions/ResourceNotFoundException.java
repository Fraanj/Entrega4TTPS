package ttps.proyecto.exceptions;

/**
 * Se lanza cuando no se encuentra un recurso (ej: usuario, mascota por ID).
 * El GlobalExceptionHandler devuelve HTTP 404.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, Long id) {
        super(String.format("%s no encontrado con id: %d", resourceName, id));
    }
}
