# Plan de Tareas - DondeEstas

**Grupo**: 3 integrantes  
**Objetivo**: Completar Entregable 6 y avanzar en Entregable 7

---

## 🎯 FASE 1: Completar Entregable 6 (Prioridad ALTA)

### Tarea 1.1: UI de Avistamientos en Frontend
**Objetivo**: Crear componente para reportar avistamientos de mascotas  
**Archivos a crear**:
- `Front/DondeEstas/src/app/components/avistamiento/avistamiento-form/avistamiento-form.ts`
- `Front/DondeEstas/src/app/components/avistamiento/avistamiento-form/avistamiento-form.html`
- `Front/DondeEstas/src/app/components/avistamiento/avistamiento-list/avistamiento-list.ts`
- `Front/DondeEstas/src/app/components/avistamiento/avistamiento-list/avistamiento-list.html`

**Archivos a modificar**:
- `Front/DondeEstas/src/app/services/avistamiento.service.ts` (crear si no existe)
- `Front/DondeEstas/src/app/app.routes.ts` (agregar rutas)
- `Front/DondeEstas/src/app/components/mascota/mascota-detail/mascota-detail.ts` (mostrar avistamientos)

**Funcionalidad**:
- Formulario para crear avistamiento con mapa (Leaflet)
- Lista de avistamientos por mascota
- Integración con endpoints existentes del backend

---

### Tarea 1.2: Búsqueda con Filtros
**Objetivo**: Implementar búsqueda de mascotas por características y ubicación

**Backend** - Archivos a modificar:
- `API/src/main/java/ttps/proyecto/controllers/MascotaController.java`
- `API/src/main/java/ttps/proyecto/services/MascotaService.java`
- `API/src/main/java/ttps/proyecto/repositories/MascotaRepository.java`

**Frontend** - Archivos a crear/modificar:
- `Front/DondeEstas/src/app/components/mascota/mascota-search/mascota-search.ts` (crear)
- `Front/DondeEstas/src/app/components/mascota/mascota-list/mascota-list.ts` (modificar)

**Funcionalidad**:
- Endpoint: `GET /api/mascotas/buscar?color=X&tamanio=Y&ubicacion=Z`
- Filtros por: color, tamaño, estado, ubicación (radio)
- UI con formulario de filtros

---

### Tarea 1.3: Ranking Real de Usuarios
**Objetivo**: Mostrar usuarios que más reportaron avistamientos

**Backend** - Archivos a crear/modificar:
- `API/src/main/java/ttps/proyecto/controllers/UsuarioController.java`
- `API/src/main/java/ttps/proyecto/services/UsuarioService.java`
- `API/src/main/java/ttps/proyecto/dto/UsuarioRankingDto.java` (crear)

**Frontend** - Archivos a modificar:
- `Front/DondeEstas/src/app/components/home/home.component.ts`
- `Front/DondeEstas/src/app/services/usuario.service.ts`

**Funcionalidad**:
- Endpoint: `GET /api/usuarios/ranking?limit=10`
- Calcular top usuarios por cantidad de avistamientos reportados
- Reemplazar datos hardcodeados en HomeComponent

---

### Tarea 1.4: Panel de Administración
**Objetivo**: Gestión de usuarios y publicaciones para administradores

**Backend** - Archivos a crear/modificar:
- `API/src/main/java/ttps/proyecto/controllers/AdminController.java` (crear)
- `API/src/main/java/ttps/proyecto/services/AdminService.java` (crear)

**Frontend** - Archivos a crear:
- `Front/DondeEstas/src/app/components/admin/admin-usuarios/admin-usuarios.ts`
- `Front/DondeEstas/src/app/components/admin/admin-publicaciones/admin-publicaciones.ts`
- `Front/DondeEstas/src/app/guards/admin.guard.ts`

**Funcionalidad**:
- Endpoints:
  - `GET /api/admin/usuarios` - Listar todos los usuarios
  - `PUT /api/admin/usuarios/{id}/estado` - Cambiar estado (activo/inactivo)
  - `DELETE /api/admin/usuarios/{id}` - Borrar usuario
  - `GET /api/admin/mascotas` - Listar todas las publicaciones
  - `DELETE /api/admin/mascotas/{id}` - Borrar publicación
- UI con tablas y acciones de administración
- Guard para proteger rutas (solo ADMINISTRADOR)

---

## 🚀 FASE 2: Entregable 7 (Prioridad MEDIA-ALTA)

### Tarea 2.1: Manejo Global de Excepciones
**Objetivo**: Implementar `@ControllerAdvice` para respuestas de error estandarizadas

**Backend** - Archivos a crear:
- `API/src/main/java/ttps/proyecto/exceptions/GlobalExceptionHandler.java`
- `API/src/main/java/ttps/proyecto/exceptions/ResourceNotFoundException.java`
- `API/src/main/java/ttps/proyecto/exceptions/UnauthorizedException.java`
- `API/src/main/java/ttps/proyecto/exceptions/BadRequestException.java`
- `API/src/main/java/ttps/proyecto/dto/ErrorResponse.java`

**Archivos a modificar**:
- Todos los Services y Controllers (reemplazar `RuntimeException` por excepciones específicas)

**Funcionalidad**:
- Clase con `@ControllerAdvice`
- Métodos `@ExceptionHandler` para diferentes excepciones
- Respuestas JSON estandarizadas: `{ "error": "...", "message": "...", "timestamp": "..." }`
- Códigos HTTP apropiados (404, 400, 401, 403, 500)

---

### Tarea 2.2: Bot de Telegram
**Objetivo**: Implementar bot para reportar mascotas perdidas desde Telegram

**Backend** - Archivos a crear:
- `API/src/main/java/ttps/proyecto/telegram/TelegramBot.java`
- `API/src/main/java/ttps/proyecto/telegram/TelegramBotService.java`
- `API/src/main/java/ttps/proyecto/telegram/ConversationState.java`
- `API/src/main/java/ttps/proyecto/config/TelegramConfig.java`

**Archivos a modificar**:
- `API/pom.xml` (agregar dependencia `telegrambots`)
- `API/src/main/resources/application.properties` (crear, agregar token de bot)

**Funcionalidad**:
- Comandos:
  - `/start` - Bienvenida
  - `/help` - Ayuda
  - `/perdida` - Iniciar reporte de mascota perdida
- Conversación interactiva:
  1. Solicitar nombre de mascota
  2. Solicitar foto
  3. Solicitar barrio/ubicación
  4. Crear mascota en el sistema
  5. Confirmar registro
- Manejo de estados de conversación por usuario
- Integración con `MascotaService` para crear mascotas

---

### Tarea 2.3: Endpoints Faltantes
**Objetivo**: Completar endpoints necesarios para funcionalidades del frontend

**Backend** - Archivos a modificar:
- `API/src/main/java/ttps/proyecto/controllers/AvistamientoController.java`
- `API/src/main/java/ttps/proyecto/controllers/MascotaController.java`

**Funcionalidad**:
- Cualquier endpoint que el frontend necesite y no esté implementado
- Validaciones adicionales
- Paginación en listados grandes

---

## 📋 Orden de Ejecución Sugerido

### Semana 1 (Completar Entregable 6):
1. ✅ Tarea 1.1: UI de Avistamientos (2-3 horas)
2. ✅ Tarea 1.3: Ranking Real (1-2 horas)
3. ✅ Tarea 1.2: Búsqueda con Filtros (2-3 horas)
4. ✅ Tarea 1.4: Panel de Administración (3-4 horas)

### Semana 2 (Avanzar Entregable 7):
5. ✅ Tarea 2.1: Manejo Global de Excepciones (2-3 horas)
6. ✅ Tarea 2.2: Bot de Telegram (4-6 horas)
7. ✅ Tarea 2.3: Endpoints Faltantes (1-2 horas)

---

## 🎯 Criterio de Aceptación

### Entregable 6 Completo:
- ✅ Todas las funcionalidades del PDF implementadas
- ✅ UI funcional y navegable
- ✅ Autenticación JWT funcionando
- ✅ Panel de admin operativo
- ✅ Búsquedas con filtros funcionando

### Entregable 7 Completo:
- ✅ Manejo global de excepciones implementado
- ✅ Bot de Telegram funcional con comando `/perdida`
- ✅ Todas las funcionalidades del frontend completadas
- ✅ Sistema estable y sin errores críticos

---

## 📝 Notas Importantes

- **Commits**: Hacer commit después de cada tarea completada
- **Testing**: Probar cada funcionalidad antes de continuar
- **Docker**: Asegurar que todo funcione en el entorno Docker
- **Grupo de 3**: NO implementar suscripciones de Telegram (exclusivo de grupos de 4)

---

**Estado actual**: Listo para comenzar con Tarea 1.1
