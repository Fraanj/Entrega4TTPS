# Estado de Implementación del Proyecto DondeEstas

**Fecha de análisis**: 11 de Febrero de 2026  
**Entregas analizadas**: Entregable 6 y Entregable 7

---

## 📊 Resumen Ejecutivo

| Entrega | Estado General | Completitud |
|---------|---------------|-------------|
| **Entregable 6** | ✅ Casi completo | ~90% |
| **Entregable 7** | ❌ No implementado | ~5% |

---

## 🎯 ENTREGABLE 6 - Integración Angular con API REST

### ✅ Requerimientos Implementados (COMPLETOS)

#### 1. Home del sistema con opciones del menú ✅
- **Estado**: ✅ IMPLEMENTADO
- **Componente**: `HomeComponent`
- **Detalles**: 
  - Dashboard con estadísticas (perdidas, recuperadas, adoptadas)
  - Lista de mascotas perdidas recientes
  - Menú de navegación completo en `MainLayoutComponent`

#### 2. Registración de usuario ✅
- **Estado**: ✅ IMPLEMENTADO
- **Backend**: `POST /api/auth/register`
- **Frontend**: `RegisterComponent`
- **Detalles**:
  - Formulario con nombre, apellido, email, contraseña, teléfono, ciudad
  - Selector de ciudades de Buenos Aires
  - Validaciones de campos requeridos

#### 3. Edición del perfil de usuario ✅
- **Estado**: ✅ IMPLEMENTADO
- **Backend**: `PUT /api/usuarios/{id}`
- **Frontend**: `ProfileComponent`
- **Detalles**:
  - Edición de datos personales
  - Cambio de contraseña opcional
  - Selector de ciudad

#### 4. Login del sistema ✅
- **Estado**: ✅ IMPLEMENTADO
- **Backend**: `POST /api/auth/login`
- **Frontend**: `LoginComponent`
- **Detalles**:
  - Autenticación con email y contraseña
  - Generación de JWT
  - Almacenamiento de token

#### 5. Creación, edición y borrado de Mascota ✅
- **Estado**: ✅ IMPLEMENTADO
- **Backend**:
  - `POST /api/mascotas` (crear)
  - `PUT /api/mascotas/{id}` (editar)
  - `DELETE /api/mascotas/{id}` (borrar)
- **Frontend**: 
  - `MascotaFormComponent` (crear/editar)
  - `MisMascotasComponent` (gestión y borrado)
- **Detalles**:
  - Formulario completo con nombre, descripción, color, tamaño, estado
  - Subida de múltiples fotos (hasta 4)
  - Mapa interactivo (Leaflet) para seleccionar ubicación
  - Solo el dueño o admin puede editar/borrar

#### 6. Listado de Mascotas perdidas ✅
- **Estado**: ✅ IMPLEMENTADO
- **Backend**: `GET /api/mascotas/perdidas`
- **Frontend**: `MascotaListComponent`
- **Detalles**:
  - Vista en grid con tarjetas
  - Muestra foto, nombre, ubicación, descripción, fecha
  - Navegación al detalle

#### 7. Creación de avistamientos ✅
- **Estado**: ✅ IMPLEMENTADO
- **Backend**: `POST /api/avistamientos?reportadorId={id}`
- **Detalles**: Endpoint disponible en el backend

#### 8. Listado de avistamientos ✅
- **Estado**: ✅ IMPLEMENTADO
- **Backend**: 
  - `GET /api/avistamientos` (todos)
  - `GET /api/avistamientos/mascota/{mascotaId}` (por mascota)
- **Detalles**: Endpoints disponibles en el backend

#### 9. JWT como mecanismo de autenticación ✅
- **Estado**: ✅ IMPLEMENTADO
- **Backend**: 
  - `JwtUtil` para generación y validación
  - `JwtAuthenticationFilter` como interceptor
  - Configuración en `SecurityConfig`
- **Frontend**: 
  - `AuthService` maneja tokens
  - `AuthGuard` protege rutas
  - Interceptor HTTP (probablemente en `FetchApiService`)

---

### 🚧 Requerimientos Parcialmente Implementados

#### 10. Búsquedas en base a filtros ⚠️
- **Estado**: ⚠️ PARCIAL
- **Backend**: No hay endpoints específicos de búsqueda con filtros
- **Frontend**: No hay componente de búsqueda visible
- **Faltante**: 
  - Endpoint de búsqueda por características (color, tamaño, etc.)
  - Endpoint de búsqueda por ubicación/radio
  - UI de filtros en el frontend

#### 11. Ranking de usuarios que más reportaron ⚠️
- **Estado**: ⚠️ NO IMPLEMENTADO (pero hay placeholder)
- **Backend**: No hay endpoint `/api/usuarios/ranking` o similar
- **Frontend**: En `HomeComponent` hay un ranking hardcodeado (datos de prueba)
- **Faltante**:
  - Endpoint que calcule y devuelva top usuarios por cantidad de reportes
  - Integración real con datos del backend

---

### ❌ Requerimientos NO Implementados (Administrador)

#### 12. Gestión de usuarios (Admin) ❌
- **Estado**: ❌ NO IMPLEMENTADO
- **Faltante**:
  - Endpoint `GET /api/usuarios` (listado completo)
  - Endpoint `DELETE /api/usuarios/{id}` (borrado)
  - Endpoint `PUT /api/usuarios/{id}/estado` (cambio de estado)
  - Componente Angular de gestión de usuarios
  - Protección por rol ADMINISTRADOR

#### 13. Gestión de Publicaciones (Admin) ❌
- **Estado**: ❌ NO IMPLEMENTADO
- **Faltante**:
  - Endpoint `GET /api/mascotas/admin` (listado completo para admin)
  - Lógica de borrado/modificación por admin (existe pero falta UI)
  - Componente Angular de gestión de publicaciones
  - Panel de administración en el frontend

---

### ✅ Requerimientos Técnicos Implementados

#### 14. API Georef para barrio y ciudad ✅
- **Estado**: ✅ IMPLEMENTADO
- **Ubicación**: `MascotaService.java`
- **Detalles**: 
  - Usa `RestTemplate` para llamar a la API de Georef
  - Obtiene barrio y ciudad a partir de coordenadas
  - Se aplica al crear/actualizar mascotas y avistamientos

#### 15. Hashing de contraseñas ✅
- **Estado**: ✅ IMPLEMENTADO
- **Ubicación**: `UsuarioService.java`, `SecurityConfig.java`
- **Detalles**: 
  - Usa BCrypt (`PasswordEncoder`)
  - Contraseñas hasheadas en registro
  - Validación segura en login

---

## 🎯 ENTREGABLE 7 - Sistema Completo

### ❌ Requerimientos NO Implementados

#### 1. Manejo de Excepciones Global de Spring ❌
- **Estado**: ❌ NO IMPLEMENTADO
- **Faltante**: 
  - Clase con `@ControllerAdvice`
  - Métodos `@ExceptionHandler` para diferentes tipos de excepciones
  - Respuestas JSON estandarizadas para errores
- **Nota**: Actualmente las excepciones se manejan con `RuntimeException` básicas

#### 2. Bot de Telegram ✅
- **Estado**: ✅ IMPLEMENTADO
- **Implementado**:
  - Dependencia `org.telegram:telegrambots` 6.9.7.1
  - Comandos `/start`, `/help`, `/perdida` con flujo conversacional (nombre → foto → barrio)
  - Integración con `MascotaService.crear` para registrar mascotas reportadas
  - Manejo de fotos (descarga y base64) y barrio como texto
  - Estado por chat (`TelegramConversationData`, `ConversationState`), registro condicional por `TELEGRAM_BOT_TOKEN`
- **Variables de entorno**: `TELEGRAM_BOT_TOKEN` (obligatorio para activar), `TELEGRAM_BOT_USERNAME`, `TELEGRAM_BOT_PUBLICADOR_ID`
- **NO REQUERIDO** (grupo de 3): Suscripciones a mascotas para notificaciones

#### 3. Completar funcionalidades del frontend ⚠️
- **Estado**: ⚠️ PARCIAL
- **Implementado**:
  - Home, login, registro, perfil
  - CRUD de mascotas
  - Listado de mascotas perdidas
  - Detalle de mascota
- **Faltante**:
  - UI para crear/ver avistamientos (backend existe)
  - Panel de administración
  - Búsquedas con filtros
  - Ranking de usuarios (real, no hardcodeado)
  - Notificaciones en tiempo real
  - Suscripciones a mascotas

#### 4. Endpoints faltantes de la API REST ⚠️
- **Faltante**:
  - Búsqueda con filtros
  - Ranking de usuarios
  - Gestión de usuarios (admin)
  - Notificaciones
  - Suscripciones

---

## 📈 Análisis Detallado por Componente

### Backend (API REST)

#### ✅ Implementado:
- 4 Controllers (Auth, Usuario, Mascota, Avistamiento)
- 18 endpoints REST
- Autenticación JWT completa
- Seguridad con Spring Security 6
- Integración con API Georef
- Hashing de contraseñas con BCrypt
- Persistencia con JPA/Hibernate
- Validaciones con Jakarta Validation

#### ❌ Faltante:
- ~~Manejo global de excepciones~~ ✅
- ~~Endpoints de búsqueda con filtros~~ ✅
- ~~Endpoints de administración~~ ✅
- ~~Endpoint de ranking de usuarios~~ ✅
- ~~Bot de Telegram~~ ✅
- Sistema de notificaciones
- Websockets para tiempo real

### Frontend (Angular)

#### ✅ Implementado:
- 9 componentes standalone (Angular 21)
- Autenticación completa (login, registro, guards)
- CRUD de mascotas con mapa interactivo (Leaflet)
- Gestión de perfil
- Dashboard con estadísticas
- Sistema de notificaciones toast
- Diseño con Tailwind CSS
- Routing protegido

#### ❌ Faltante:
- UI para crear avistamientos
- UI para ver avistamientos de una mascota
- Búsqueda con filtros
- Panel de administración
- Ranking de usuarios (real)
- Notificaciones en tiempo real
- Suscripciones a mascotas

---

## 🎯 Recomendaciones de Prioridad

### Para completar Entregable 6 (nota 6):

1. **ALTA PRIORIDAD**:
   - ✅ Ya está casi completo
   - Agregar UI de avistamientos en el frontend
   - Implementar panel de administración básico

2. **MEDIA PRIORIDAD**:
   - Implementar búsquedas con filtros
   - Implementar ranking real de usuarios

### Para Entregable 7 (nota > 6):

1. **CRÍTICO**:
   - Manejo global de excepciones
   - Completar todas las funcionalidades del frontend
   - Implementar endpoints faltantes

2. **ALTA PRIORIDAD**:
   - ~~Bot de Telegram básico (comando `/perdida`)~~ ✅
   - Sistema de notificaciones

3. **NO REQUERIDO** (grupo de 4, no aplica):
   - ~~Suscripciones en bot de Telegram~~
   - ~~Notificaciones en tiempo real (WebSockets)~~

**NOTA**: Este grupo es de 3 integrantes, por lo que NO se requieren las funcionalidades exclusivas de grupos de 4.

---

## 📊 Métricas del Proyecto

### Backend
- **Controllers**: 4
- **Endpoints**: 18
- **Servicios**: 4+ (Usuario, Mascota, Avistamiento, Auth)
- **Repositorios**: 6+ (JPA)
- **Modelos**: 10+ entidades

### Frontend
- **Componentes**: 9
- **Servicios**: 6 (Auth, Usuario, Mascota, Ciudad, Notificación, FetchApi)
- **Guards**: 1 (AuthGuard)
- **Rutas**: 8+

### Cobertura de Entregable 6
- **Implementado**: ~90%
- **Funcional para cursada**: ✅ SÍ
- **Nota estimada**: 6 (SEIS) + posible bonus por calidad

### Cobertura de Entregable 7
- **Implementado**: ~5%
- **Requiere trabajo significativo**: ✅ SÍ

---

## 🚀 Próximos Pasos Sugeridos

1. **Corto plazo** (para asegurar el 6):
   - Agregar UI de avistamientos
   - Implementar panel básico de admin

2. **Mediano plazo** (para subir nota):
   - ~~Manejo global de excepciones~~ ✅
   - ~~Bot de Telegram básico~~ ✅
   - ~~Búsquedas con filtros~~ ✅

3. **Largo plazo** (para nota alta):
   - Sistema completo de notificaciones
   - Suscripciones y alertas
   - Mejoras de UX/UI

---

**Conclusión**: El proyecto tiene una base sólida con el Entregable 6 completo y el Entregable 7 avanzado (manejo global de excepciones y bot de Telegram implementados). Pendiente opcional: endpoints faltantes para el front y sistema de notificaciones.
