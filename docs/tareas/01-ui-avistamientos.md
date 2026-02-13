# Tarea 1.1: UI de Avistamientos en Frontend

## Objetivo
Permitir que los usuarios reporten avistamientos de mascotas perdidas y ver la lista de avistamientos por mascota.

---

## Archivos creados

| Archivo | Propósito |
|---------|-----------|
| `Front/.../services/avistamiento.service.ts` | Servicio para llamar a la API de avistamientos |
| `Front/.../components/avistamiento/avistamiento-form/avistamiento-form.ts` | Lógica del formulario (mapa, foto, validaciones) |
| `Front/.../components/avistamiento/avistamiento-form/avistamiento-form.html` | Vista del formulario |
| `Front/.../components/avistamiento/avistamiento-list/avistamiento-list.ts` | Lógica de la lista de avistamientos |
| `Front/.../components/avistamiento/avistamiento-list/avistamiento-list.html` | Vista de la lista |

---

## Archivos modificados

| Archivo | Cambio |
|---------|--------|
| `app.routes.ts` | Nueva ruta `avistamiento/reportar/:mascotaId` con AuthGuard |
| `mascota-detail/mascota-detail.ts` | Import y uso de `AvistamientoListComponent` |
| `mascota-detail/mascota-detail.html` | Botón "Reportar Avistamiento" (link a formulario) y sección `<app-avistamiento-list>` |

---

## Clases y métodos involucrados

### Frontend

**AvistamientoService** (`avistamiento.service.ts`)
- `crearAvistamiento(avistamiento)`: `Observable<Avistamiento>` — POST con `reportadorId` del usuario logueado.
- `getAvistamientos()`: lista todos.
- `getAvistamientoById(id)`: uno por ID.
- `getAvistamientosByMascota(mascotaId)`: lista por mascota.

**AvistamientoFormComponent**
- `ngOnInit()`: obtiene `mascotaId` de la ruta, carga nombre de mascota, inicializa mapa.
- `cargarMascota(id)`: GET mascota para mostrar nombre y centrar mapa.
- `initMap()`, `agregarMarcador(lat, lng)`: Leaflet para elegir ubicación.
- `onFileSelected(event)`: validación y conversión de foto a base64.
- `onSubmit()`: valida formulario y ubicación, POST avistamiento, redirige al detalle de mascota.
- `cancelar()`: vuelve al detalle de mascota o listado.

**AvistamientoListComponent**
- `@Input() mascotaId`: ID de la mascota para cargar avistamientos.
- `ngOnInit()`: llama a `cargarAvistamientos()`.
- `cargarAvistamientos()`: GET avistamientos por mascota.
- `formatearFecha(fecha)`: "Hoy", "Ayer", "Hace X días", etc.

### Backend (ya existían, solo se consumen)

- `AvistamientoController`: `POST /api/avistamientos?reportadorId=`, `GET /api/avistamientos`, `GET /api/avistamientos/mascota/{mascotaId}`.

---

## Conceptos útiles

- **Ruta con parámetro**: `avistamiento/reportar/:mascotaId` se lee con `ActivatedRoute.snapshot.paramMap.get('mascotaId')`.
- **Leaflet**: mismo patrón que en mascota-form (tileLayer, marcador, click en mapa).
- **Servicio de notificaciones**: usar `NotificationService.success()` y `.error()` en lugar de `alert()`.
- **Standalone components**: los nuevos componentes son standalone e importan `AvistamientoListComponent` en el detalle de mascota.
