# Tarea 1.3: Ranking Real de Usuarios

## Objetivo
Mostrar en el home el top de usuarios que más avistamientos reportaron, reemplazando datos hardcodeados por datos del backend.

---

## Archivos creados (Backend)

| Archivo | Propósito |
|---------|-----------|
| `API/.../dto/UsuarioRankingDto.java` | DTO con id, nombre, apellido, cantidadReportes y getNombreCompleto(). |

---

## Archivos modificados (Backend)

| Archivo | Cambio |
|---------|--------|
| `AvistamientoRepository.java` | Query `findTopReportadores()`: agrupa por reportador, cuenta y ordena descendente. |
| `UsuarioService.java` | Inyección de `AvistamientoRepository`; nuevo método `obtenerRanking(int limit)`. |
| `UsuarioController.java` | Endpoint `GET /api/usuarios/ranking?limit=10`. |

---

## Archivos modificados (Frontend)

| Archivo | Cambio |
|---------|--------|
| `usuario.service.ts` | Interfaz `UsuarioRanking`; método `obtenerRanking(limit)` que hace GET al ranking. |
| `home.component.ts` | Propiedades `ranking`, `loadingRanking`; inyección de `UsuarioService`; `cargarRanking()` y llamada en `ngOnInit()`. |
| `home.component.html` | Reemplazo del bloque hardcodeado del ranking por lista dinámica con loading y vacío. |

---

## Clases y métodos involucrados

### Backend

**UsuarioRankingDto**
- Campos: `id`, `nombre`, `apellido`, `cantidadReportes`.
- `getNombreCompleto()`: `nombre + " " + apellido`.

**AvistamientoRepository**
- `findTopReportadores()`:  
  `SELECT a.reportador.id, COUNT(a) FROM Avistamiento a GROUP BY a.reportador.id ORDER BY COUNT(a) DESC`.  
  Retorna `List<Object[]>` (id usuario, cantidad).

**UsuarioService**
- `obtenerRanking(int limit)`:  
  - Llama a `findTopReportadores()`.  
  - Para cada par (usuarioId, cantidad) busca el `Usuario` y arma un `UsuarioRankingDto`.  
  - Limita la lista a `limit` elementos.  
  - Retorna `List<UsuarioRankingDto>`.

**UsuarioController**
- `obtenerRanking(@RequestParam(defaultValue = "10") int limit)`: público, devuelve `List<UsuarioRankingDto>`.

### Frontend

**UsuarioService**
- `UsuarioRanking`: `id`, `nombre`, `apellido`, `cantidadReportes`, `nombreCompleto?`.
- `obtenerRanking(limit = 10)`: `Observable<UsuarioRanking[]>` vía GET `/usuarios/ranking?limit=...`.

**HomeComponent**
- `ranking: UsuarioRanking[]`, `loadingRanking: boolean`.
- `cargarRanking()`: subscribe a `usuarioService.obtenerRanking(10)` y actualiza `ranking` y `loadingRanking`.
- En template: medallas 🥇🥈🥉 para los 3 primeros, número para el resto; se muestra "Nombre A." y "X reportes".

---

## Conceptos útiles

- **JPQL con agregación**: `GROUP BY` y `COUNT()` en el repositorio; el resultado es `Object[]` (en este caso [Long, Long]).
- **DTO dedicado para ranking**: no exponer entidad `Usuario` completa; solo lo necesario para la vista.
- **Parámetro con default**: `@RequestParam(defaultValue = "10")` evita error si el front no envía `limit`.
- **Estados de UI**: loading y “aún no hay reportes” mejoran la experiencia cuando no hay datos.
