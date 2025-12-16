src/
└── app/
    ├── models/           <-- Contratos de datos
    │   ├── mascota.model.ts
    │   ├── avistamiento.model.ts
    │   └── ubicacion.model.ts
    │
    ├── services/         <-- Lógica de negocio y HTTP
    │   ├── mascota.service.ts
    │   └── avistamiento.service.ts
    │
    ├── components/       <-- Vistas y UI
    │   ├── home/                 <-- Página principal (Listado general)
    │   ├── mascota/              <-- Agrupamos todo lo de mascotas
    │   │   ├── mascota-card/     <-- Componente "tarjeta" reutilizable
    │   │   ├── mascota-detail/   <-- Ver una mascota individual
    │   │   └── mascota-form/     <-- Formulario para Crear/Editar (Reutilizable)
    │   │
    │   └── avistamiento/         <-- Agrupamos lo de avistamientos
    │       └── avistamiento-form/
    │
    ├── shared/           <-- Cosas comunes (Opcional pero recomendado)
    │   └── navbar/       <-- La barra de navegación (para navegar entre Home, Login, etc.)
    │
    └── interceptors/     <-- Para el Token JWT

## Esta organización responde a los principios de Separación de Responsabilidades e Inyección de Dependencias explicados en la teoría:


### models/ (Tipo Fuerte y Seguridad):
TypeScript te permite definir "tipos".

### services/ (Lógica centralizada):
Un componente (la vista) no debería saber cómo buscar los datos, solo pedirlos. Los servicios encapsulan la comunicación HTTP (HttpClient). Si mañana cambia la URL de la API, solo tocas el archivo mascota.service.ts. Además, al ser @Injectable, Angular crea una instancia única (Singleton) que puedes reutilizar en toda la app.

### components/ (Componentes Standalone):
La teoría muestra que Angular moderno usa componentes standalone: true. Esto elimina la necesidad de un archivo app.module.ts gigante y complejo. Cada componente importa solo lo que necesita (ej. formularios, módulos comunes).
Agrupación: Separar mascota-card de mascota-form permite reutilizar código. Por ejemplo, puedes usar la misma mascota-card en el Home (para ver todas) y en el Perfil (para ver las tuyas), sin duplicar HTML.

### interceptors/ (JWT):
El requerimiento pide explícitamente usar interceptores para la autenticación. Esto permite que automáticamente se agregue el token a cada petición que hagan tus servicios, sin que tengas que escribirlo manualmente en cada llamada http.get o http.post.