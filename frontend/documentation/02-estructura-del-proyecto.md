# 02 — Estructura del proyecto

Esta es la organización de carpetas y archivos del frontend. Cada sección explica qué hace cada parte.

---

## Vista general

```
frontend/
├── documentation/          ← Esta documentación
├── public/                 ← Archivos estáticos (imágenes, favicon)
├── src/                    ← TODO el código fuente de la aplicación
│   ├── app/                ← Código de la aplicación Angular
│   │   ├── core/           ← Lógica central reutilizable
│   │   ├── pages/          ← Pantallas de la aplicación
│   │   ├── shared/         ← Componentes compartidos (Navbar)
│   │   ├── app.ts          ← Componente raíz
│   │   ├── app.config.ts   ← Configuración global de la app
│   │   ├── app.routes.ts   ← Definición de rutas/navegación
│   │   └── app.html        ← Plantilla del componente raíz
│   ├── index.html          ← Página HTML principal (shell)
│   ├── styles.scss         ← Estilos globales
│   └── main.ts             ← Punto de entrada de la aplicación
├── angular.json            ← Configuración del CLI de Angular
├── package.json            ← Dependencias y scripts npm
├── proxy.conf.json         ← Proxy para desarrollo (redirige /api al backend)
└── tsconfig.json           ← Configuración de TypeScript
```

---

## Archivos raíz explicados

### `package.json`
Define las dependencias del proyecto y los comandos disponibles:

```json
"scripts": {
  "start":  "ng serve",     // Levanta el servidor de desarrollo en localhost:4200
  "build":  "ng build",     // Compila la app para producción
  "test":   "ng test",      // Ejecuta las pruebas unitarias
  "watch":  "ng build --watch --configuration development"  // Compila en modo observación
}
```

Las dependencias principales son:
- `@angular/core`, `@angular/router`, `@angular/forms` — El framework Angular
- `@angular/material`, `@angular/cdk` — Componentes de Material Design
- `rxjs` — Librería para programación reactiva (manejo de eventos asíncronos)

### `angular.json`
Archivo de configuración del Angular CLI. Define cómo compilar el proyecto:
- Archivo de entrada: `src/main.ts`
- Estilos globales: `src/styles.scss`
- Proxy de desarrollo: `proxy.conf.json`
- Configuraciones de build (desarrollo vs. producción)

### `proxy.conf.json`
Durante el desarrollo, el frontend corre en `localhost:4200` y el backend en `localhost:8080`. Para evitar problemas de CORS (bloqueo del navegador al llamar a otro dominio), este archivo redirige:

```
Solicitud del frontend a /api/solicitudes
        ↓ proxy redirige
Backend en http://localhost:8080/api/solicitudes
```

```json
{
  "/api": {
    "target": "http://localhost:8080",
    "secure": false,
    "changeOrigin": true,
    "logLevel": "debug"
  }
}
```

### `tsconfig.json`
Configuración del compilador de TypeScript. Define qué tan estricto es el análisis de tipos y qué versión de JavaScript producir.

---

## La carpeta `src/`

### `src/index.html`
El único archivo HTML real del proyecto. Angular inyecta toda la aplicación dentro del elemento `<app-root>`:

```html
<body>
  <app-root></app-root>  <!-- Angular renderiza aquí toda la app -->
</body>
```

También carga las fuentes de Google (Roboto) y los íconos de Material Design desde internet.

### `src/main.ts`
El punto de entrada. Es lo primero que se ejecuta cuando se carga la app. Simplemente arranca Angular con la configuración definida en `app.config.ts`.

### `src/styles.scss`
Estilos globales que aplican a toda la aplicación:
- Configura el tema de Material Design (colores azul/Azure)
- Define el fondo gris claro de la página
- Estilos globales para tarjetas, scrollbar personalizado, mensajes de error

---

## La carpeta `src/app/`

### `app.ts` — Componente raíz
El componente más "arriba" en la jerarquía. Su única función es mostrar el componente de la ruta activa:

```html
<router-outlet></router-outlet>
```

### `app.config.ts` — Configuración global
Registra todos los proveedores de servicios de la aplicación:
- El router (sistema de rutas)
- El cliente HTTP (para llamar al backend)
- El interceptor de autenticación (que añade el token JWT a cada petición)
- Las animaciones de Material Design

### `app.routes.ts` — Rutas de navegación
Define todas las URLs de la aplicación y qué componente mostrar en cada una. Ver detalles en [04-arquitectura.md](./04-arquitectura.md).

---

## La carpeta `src/app/core/`

Contiene la lógica central que usan múltiples partes de la aplicación.

```
core/
├── guards/
│   ├── auth.guard.ts       ← Protege rutas: redirige a /login si no estás autenticado
│   └── role.guard.ts       ← Protege rutas: redirige si no tienes el rol correcto
├── interceptors/
│   └── auth.interceptor.ts ← Añade el token JWT a cada petición HTTP
├── models/
│   ├── user.model.ts       ← Interfaces de datos: Usuario, roles, sesión, login
│   └── solicitud.model.ts  ← Interfaces de datos: Solicitud, estados, prioridades
└── services/
    ├── auth.service.ts     ← Login, logout, sesión del usuario
    ├── solicitud.service.ts← CRUD de solicitudes académicas
    ├── usuario.service.ts  ← Gestión de usuarios (solo admin)
    └── ia.service.ts       ← Llamadas a la IA para clasificación y resúmenes
```

**Guards:** Son "guardianes" de rutas. Antes de mostrar una página, Angular pregunta al guard si el usuario tiene permiso. Si no lo tiene, lo redirige.

**Interceptors:** Código que se ejecuta automáticamente en cada petición HTTP antes de enviarla. El `authInterceptor` añade el header `Authorization: Bearer <token>` a todas las llamadas al backend.

**Models:** Son interfaces TypeScript que describen la forma de los datos. No contienen lógica, solo definen qué campos tiene un objeto.

**Services:** Clases que contienen la lógica de negocio. Se pueden inyectar en cualquier componente.

---

## La carpeta `src/app/pages/`

Contiene cada pantalla de la aplicación. Cada página tiene tres archivos:

```
pages/
├── login/
│   ├── login.component.ts    ← Lógica (TypeScript)
│   ├── login.component.html  ← Plantilla (HTML)
│   └── login.component.scss  ← Estilos (SCSS, solo para este componente)
├── registro/                 ← Registro de nuevos usuarios
├── dashboard/                ← Pantalla principal con estadísticas
├── solicitudes/
│   ├── lista/                ← Lista de todas las solicitudes
│   ├── nueva/                ← Crear nueva solicitud
│   └── detalle/              ← Ver detalle y gestionar una solicitud
└── usuarios/                 ← Gestión de usuarios (solo admin)
```

---

## La carpeta `src/app/shared/`

Componentes que se reutilizan en varias páginas:

```
shared/
└── components/
    └── navbar/               ← Barra de navegación principal
        ├── navbar.component.ts
        ├── navbar.component.html
        └── navbar.component.scss
```

---

## ¿Qué es un componente Angular?

Un componente es la unidad básica de construcción de Angular. Cada componente tiene tres partes:

| Archivo | Lenguaje | Para qué sirve |
|---|---|---|
| `*.component.ts` | TypeScript | Lógica: datos, eventos, llamadas a servicios |
| `*.component.html` | HTML + Angular | Plantilla: cómo se ve visualmente |
| `*.component.scss` | SCSS | Estilos: colores, tamaños, layout (solo para ese componente) |

Angular combina estas tres partes para renderizar el componente en el navegador.
