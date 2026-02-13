# Tarea 1.2: Búsqueda con Filtros

## Objetivo
Filtrar mascotas por color, tamaño y estado desde el listado, con un endpoint que combine filtros opcionales.

---

## Archivos modificados (Backend)

| Archivo | Cambio |
|---------|--------|
| `API/.../controllers/MascotaController.java` | Nuevo endpoint `GET /api/mascotas/buscar` con query params opcionales. |
| `API/.../repositories/MascotaRepository.java` | Extiende `JpaSpecificationExecutor<Mascota>` para búsquedas dinámicas. |
| `API/.../services/MascotaService.java` | Nuevo método `buscarConFiltros(color, tamanio, estado)` usando JPA Specifications. |

---

## Archivos modificados (Frontend)

| Archivo | Cambio |
|---------|--------|
| `mascota-list/mascota-list.ts` | Estado `filtros`, `mostrarFiltros`; métodos `aplicarFiltros()`, `limpiarFiltros()`, `toggleFiltros()`; import `FormsModule`. |
| `mascota-list/mascota-list.html` | Panel de filtros (color, tamaño, estado) y botones Aplicar / Limpiar. |
| `mascota.service.ts` | Nuevo método `buscarConFiltros(color, tamanio, estado)` que arma la URL con query params. |

---

## Clases y métodos involucrados

### Backend

**MascotaController**
- `buscar(@RequestParam(required = false) String color, String tamanio, String estado)`: delega en servicio y devuelve `List<MascotaDto>`.

**MascotaRepository**
- Ahora extiende `JpaRepository<Mascota, Long>, JpaSpecificationExecutor<Mascota>`.
- Se usa `findAll(Specification<Mascota>)` en el servicio.

**MascotaService**
- `buscarConFiltros(String color, String tamanio, String estado)`: construye una `Specification<Mascota>` con predicados:
  - **Color**: `LIKE %color%` en minúsculas (case-insensitive).
  - **Tamaño**: igualdad con `tamanio.nombre` (CHICO, MEDIANO, GRANDE).
  - **Estado**: igualdad con enum `EstadoMascota` (solo si el string es válido).
- Retorna lista de DTOs con `convertToDto`.

### Frontend

**MascotaListComponent**
- `filtros = { color: '', tamanio: '', estado: '' }`.
- `aplicarFiltros()`: llama a `mascotaService.buscarConFiltros(...)` y actualiza `mascotas`.
- `limpiarFiltros()`: resetea filtros y llama a `cargarMascotas()` (listado completo).
- `toggleFiltros()`: muestra/oculta el panel.

**MascotaService**
- `buscarConFiltros(color, tamanio, estado)`: arma `URLSearchParams`, construye `/mascotas/buscar?color=...&tamanio=...&estado=...` y hace GET (sin auth).

---

## Conceptos útiles

- **JPA Specification**: permite armar queries dinámicas con `Predicate` (criteria API). Cada filtro opcional se agrega solo si tiene valor.
- **Query params opcionales**: en Spring, `@RequestParam(required = false)`; en Angular, `URLSearchParams` y solo append si el valor existe.
- **FormsModule y ngModel**: necesario para `[(ngModel)]` en inputs y selects del panel de filtros.
- **Mismo componente para listado y filtrado**: no hace falta otro componente; el mismo listado muestra “todas” o “filtradas” según la llamada al backend.
