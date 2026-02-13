# Tarea 1.4: Panel de Administración

## Objetivo
Permitir a usuarios con rol ADMINISTRADOR gestionar usuarios (listado, cambio de estado, borrado) y publicaciones/mascotas (listado, borrado).

---

## Archivos creados

### Backend
| Archivo | Propósito |
|---------|-----------|
| `API/.../controllers/AdminController.java` | REST bajo `/api/admin` con endpoints de usuarios y mascotas. |

### Frontend
| Archivo | Propósito |
|---------|-----------|
| `guards/admin.guard.ts` | Guard que exige autenticación y rol ADMINISTRADOR. |
| `services/admin.service.ts` | Servicio que centraliza llamadas a `/api/admin/*`. |
| `components/admin/admin-usuarios/admin-usuarios.ts` | Lógica del listado de usuarios y acciones. |
| `components/admin/admin-usuarios/admin-usuarios.html` | Tabla de usuarios y botones. |
| `components/admin/admin-publicaciones/admin-publicaciones.ts` | Lógica del listado de publicaciones y eliminar. |
| `components/admin/admin-publicaciones/admin-publicaciones.html` | Tabla de mascotas y botones. |

---

## Archivos modificados

| Archivo | Cambio |
|---------|--------|
| `API/.../services/UsuarioService.java` | `obtenerTodos()`, `cambiarEstado(id, estadoNombre)`, `eliminarUsuario(id)`. |
| `app.routes.ts` | Rutas `admin/usuarios` y `admin/publicaciones` con `AuthGuard` y `AdminGuard`. |
| `layout/main-layout/main-layout.html` | Enlace "Administración" visible solo si `rolNombre === 'ADMINISTRADOR'`. |

---

## Clases y métodos involucrados

### Backend

**AdminController** (base `/api/admin`, `@PreAuthorize("hasRole('ADMINISTRADOR')")`)
- `listarUsuarios()`: `GET /api/admin/usuarios` → `UsuarioService.obtenerTodos()`.
- `cambiarEstadoUsuario(id, body)`: `PUT /api/admin/usuarios/{id}/estado` → body `{ "estado": "HABILITADO" \| "DESHABILITADO" }` → `UsuarioService.cambiarEstado(id, estado)`.
- `eliminarUsuario(id)`: `DELETE /api/admin/usuarios/{id}` → `UsuarioService.eliminarUsuario(id)`.
- `listarMascotas()`: `GET /api/admin/mascotas` → `MascotaService.obtenerTodas()`.
- `eliminarMascota(id)`: `DELETE /api/admin/mascotas/{id}` → `MascotaService.eliminar(id)`.

**UsuarioService**
- `obtenerTodos()`: `usuarioRepository.findAll()` y mapeo a `UsuarioDto`.
- `cambiarEstado(Long id, String estadoNombre)`: busca usuario y estado por nombre, asigna y guarda.
- `eliminarUsuario(Long id)`: `usuarioRepository.deleteById(id)` (validando existencia).

**SecurityConfig**
- Ya tenía: `.requestMatchers("/api/admin/**").hasRole("ADMINISTRADOR")`.

### Frontend

**AdminGuard**
- `canActivate()`: comprueba `isAuthenticated()` y `getCurrentUser()?.rolNombre === 'ADMINISTRADOR'`; si no, redirige a `/home`.

**AdminService**
- `listarUsuarios()`: GET `/admin/usuarios` con auth.
- `cambiarEstadoUsuario(id, estado)`: PUT `/admin/usuarios/{id}/estado` con body `{ estado }`.
- `eliminarUsuario(id)`: DELETE `/admin/usuarios/{id}`.
- `listarMascotas()`: GET `/admin/mascotas`.
- `eliminarMascota(id)`: DELETE `/admin/mascotas/{id}`.

**AdminUsuariosComponent**
- `cargarUsuarios()`: llama a `adminService.listarUsuarios()` y rellena tabla.
- `cambiarEstado(usuario)`: alterna HABILITADO/DESHABILITADO y llama al servicio.
- `eliminar(usuario)`: confirmación y luego `adminService.eliminarUsuario()`.

**AdminPublicacionesComponent**
- `cargarMascotas()`: llama a `adminService.listarMascotas()`.
- `eliminar(mascota)`: confirmación y luego `adminService.eliminarMascota()`.
- `getFotoSegura()`, `getEstadoLabel()`: helpers para la vista.

---

## Conceptos útiles

- **Rol en el front**: el JWT o el usuario guardado deben incluir el rol; aquí se usa `usuario.rolNombre === 'ADMINISTRADOR'` para mostrar el menú y el guard.
- **Guard compuesto**: `canActivate: [AuthGuard, AdminGuard]` asegura primero login y luego rol admin.
- **Estados de usuario**: en BD suele haber catálogo (ej. HABILITADO, DESHABILITADO); cambiar estado es actualizar la FK a otro registro de ese catálogo.
- **No eliminar admins**: en la tabla de usuarios se puede ocultar o deshabilitar el botón "Eliminar" para `rolNombre === 'ADMINISTRADOR'` para evitar borrar el último admin.
- **Seguridad en backend**: aunque el front oculte acciones, la autorización real es `@PreAuthorize("hasRole('ADMINISTRADOR')")` y la configuración de Spring Security en `/api/admin/**`.
