# Tarea 2.1: Manejo Global de Excepciones

## Objetivo
Unificar las respuestas de error de la API con un formato JSON estándar y códigos HTTP coherentes (404, 400, 401, 403, 500), usando `@ControllerAdvice` y excepciones específicas en los servicios.

---

## Archivos creados

| Archivo | Propósito |
|---------|-----------|
| `dto/ErrorResponse.java` | DTO con `error`, `message`, `timestamp`, `path` para todas las respuestas de error. |
| `exceptions/ResourceNotFoundException.java` | Recurso no encontrado → HTTP 404. |
| `exceptions/BadRequestException.java` | Petición inválida o validación → HTTP 400. |
| `exceptions/UnauthorizedException.java` | Credenciales/token inválidos → HTTP 401. |
| `exceptions/ForbiddenException.java` | Sin permiso para la acción → HTTP 403. |
| `exceptions/GlobalExceptionHandler.java` | Clase con `@RestControllerAdvice` y varios `@ExceptionHandler`. |

---

## Archivos modificados

| Archivo | Cambio |
|---------|--------|
| `UsuarioService.java` | Import de excepciones; reemplazo de `RuntimeException` por `BadRequestException`, `UnauthorizedException`, `ResourceNotFoundException`. |
| `MascotaService.java` | Import de excepciones; reemplazo por `ResourceNotFoundException` y `BadRequestException`. |
| `AvistamientoService.java` | Import de excepciones; reemplazo por `BadRequestException` y `ResourceNotFoundException`. |

---

## Clases y métodos involucrados

### ErrorResponse (DTO)
- Constructores: sin args (timestamp ahora), `(message)`, `(error, message)`, `(error, message, path)`.
- Campos: `error`, `message`, `timestamp`, `path` (opcional).
- Getters/setters para todos.

### Excepciones (extienden RuntimeException)
- **ResourceNotFoundException**: mensaje libre o `(resourceName, id)` → “X no encontrado con id: Y”.
- **BadRequestException**, **UnauthorizedException**, **ForbiddenException**: solo mensaje.

### GlobalExceptionHandler (@RestControllerAdvice)
- **handleResourceNotFound**: `ResourceNotFoundException` → 404 + body con `error: "Not Found"`.
- **handleBadRequest**: `BadRequestException` → 400.
- **handleUnauthorized**: `UnauthorizedException` → 401.
- **handleForbidden**: `ForbiddenException` → 403.
- **handleRuntimeException**: Cualquier otra `RuntimeException`; si el mensaje contiene “no encontrado”, “ya está registrado”, “inválid”, “Credenciales”, etc. → 400; si no → 500.
- **handleGenericException**: Cualquier `Exception` → 500.

Cada método recibe `HttpServletRequest` para setear `path` en el `ErrorResponse`.

### Servicios (reemplazos realizados)
- **UsuarioService**: registro (email duplicado → BadRequest), login (credenciales → Unauthorized), obtenerPorId / actualizarPerfil / obtenerIdPorEmail / obtenerRolPorId / cambiarEstado / eliminarUsuario (no encontrado / estado inválido → ResourceNotFound / BadRequest).
- **MascotaService**: crear (usuario/tamaño no encontrado), actualizar, obtenerPorId, eliminar (mascota no encontrada → ResourceNotFound; tamaño no encontrado → BadRequest).
- **AvistamientoService**: crear (comentario/mascota obligatorio → BadRequest; usuario/mascota no encontrado → ResourceNotFound), obtenerPorId (avistamiento no encontrado → ResourceNotFound).

---

## Formato de respuesta de error

Todas las respuestas que pasa por el handler tienen forma:

```json
{
  "error": "Not Found",
  "message": "Usuario no encontrado",
  "timestamp": "2026-02-12T12:34:56.789Z",
  "path": "/api/usuarios/999"
}
```

- **error**: tipo/categoría (Not Found, Bad Request, Unauthorized, Forbidden, Internal Server Error).
- **message**: mensaje para el cliente (ej. mensaje de la excepción).
- **timestamp**: instante del error (ISO-8601).
- **path**: ruta del request (opcional).

---

## Conceptos útiles

- **@RestControllerAdvice**: aplica a todos los `@RestController`; centraliza el manejo de excepciones sin repetir try/catch en cada controller.
- **@ExceptionHandler**: método que maneja un tipo de excepción; el que sea más específico tiene prioridad (por ejemplo `ResourceNotFoundException` antes que `RuntimeException`).
- **Orden de los handlers**: Spring elige el método que coincida con la excepción lanzada (el más concreto). Por eso `RuntimeException` y `Exception` van al final.
- **Propagación**: si un servicio lanza `ResourceNotFoundException`, no hace falta catch en el controller; la petición llega al handler y se responde 404.
- **Controllers con try/catch**: los que ya capturan excepciones y devuelven `ResponseEntity` siguen funcionando; el handler solo actúa cuando la excepción no es capturada.
- **ForbiddenException**: definida para uso futuro (ej. “no podés modificar esta mascota”); los controllers que hoy devuelven 403 con body pueden migrar a lanzar esta excepción y dejar que el handler arme la respuesta.
