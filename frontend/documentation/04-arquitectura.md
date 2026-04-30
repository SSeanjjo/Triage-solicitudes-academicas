# 04 — Arquitectura del frontend

## Visión general

El frontend sigue la arquitectura estándar de Angular con componentes standalone (modernos). Hay cuatro capas principales:

```
┌─────────────────────────────────────────────┐
│              NAVEGADOR (Browser)             │
│                                             │
│  ┌──────────────┐    ┌────────────────────┐ │
│  │   Páginas    │    │  Componentes        │ │
│  │  (Pages)     │◄───│  Compartidos        │ │
│  │              │    │  (Navbar)           │ │
│  └──────┬───────┘    └────────────────────┘ │
│         │                                   │
│  ┌──────▼───────┐                           │
│  │   Servicios  │                           │
│  │  (Services)  │                           │
│  └──────┬───────┘                           │
│         │                                   │
│  ┌──────▼───────┐                           │
│  │ HTTP Client  │◄── Interceptor JWT         │
│  └──────┬───────┘                           │
└─────────│───────────────────────────────────┘
          │ HTTP (JSON)
          ▼
   Backend Java/Spring Boot
   http://localhost:8080/api/...
```

---

## Sistema de rutas (`app.routes.ts`)

Las rutas definen qué componente se muestra según la URL del navegador. No hay recarga de página — Angular intercambia el componente visible en el `<router-outlet>`.

```
URL                         Componente              Restricción
─────────────────────────────────────────────────────────────────
/                        → redirige a /dashboard
/login                   → LoginComponent           Pública
/registro                → RegistroComponent        Pública
/dashboard               → DashboardComponent       Autenticado
/solicitudes             → ListaComponent           Autenticado
/solicitudes/nueva       → NuevaComponent           Estudiante o Admin
/solicitudes/:id         → DetalleComponent         Autenticado
/usuarios                → UsuariosComponent        Solo Admin
/**                      → redirige a /dashboard    (ruta desconocida)
```

El `:id` en `/solicitudes/:id` es un **parámetro de ruta** — se reemplaza por el ID real de la solicitud (ej: `/solicitudes/42`).

### Lazy Loading

Todas las páginas usan **carga diferida** (lazy loading):

```typescript
loadComponent: () => import('./pages/dashboard/dashboard.component')
               .then(m => m.DashboardComponent)
```

Esto significa que el código de cada página solo se descarga cuando el usuario navega a ella, haciendo la carga inicial de la app más rápida.

---

## Guards (Guardianes de rutas)

### `auth.guard.ts`
Se activa en rutas con `canActivate: [authGuard]`. Comprueba si hay una sesión activa. Si no, redirige al usuario a `/login`.

```
Usuario navega a /dashboard
        ↓
  ¿Está autenticado? (hay token en localStorage)
    SÍ → muestra DashboardComponent
    NO → redirige a /login
```

### `role.guard.ts`
Añade una capa extra: además de estar autenticado, el usuario debe tener uno de los roles permitidos para esa ruta.

```
Usuario navega a /solicitudes/nueva
        ↓
  ¿Está autenticado?
    NO → redirige a /login
    SÍ → ¿Tiene rol ESTUDIANTE o ADMINISTRADOR?
           SÍ → muestra NuevaComponent
           NO → redirige a /dashboard
```

---

## Interceptor de autenticación (`auth.interceptor.ts`)

Se ejecuta automáticamente antes de cada petición HTTP. Toma el token JWT guardado en localStorage y lo añade al header `Authorization`:

```
Componente llama a solicitudService.getSolicitudes()
        ↓
  HttpClient prepara la petición GET /api/solicitudes
        ↓
  authInterceptor la intercepta y añade:
  Header: Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
        ↓
  La petición sale hacia el backend con el token
        ↓
  Backend valida el token y responde
```

Sin este interceptor, habría que añadir el token manualmente en cada llamada al backend.

---

## Manejo de estado con Signals

Angular 21 usa **Signals** para el manejo de estado reactivo. Un Signal es una variable que notifica automáticamente a los componentes cuando cambia su valor.

```typescript
// En AuthService:
private _session = signal<UserSession | null>(null);

// Computed: se recalcula automáticamente cuando _session cambia
readonly isAuthenticated = computed(() => this._session() !== null);
readonly currentRole = computed(() => this._session()?.rol ?? null);
```

Cuando `_session` cambia (al hacer login o logout), todos los componentes que usen `isAuthenticated` o `currentRole` se actualizan automáticamente.

Los componentes también usan signals locales para manejar estados de carga:
```typescript
readonly cargando = signal(false);
readonly solicitudes = signal<Solicitud[]>([]);
readonly error = signal<string | null>(null);
```

---

## Comunicación con el backend

Todos los servicios usan `HttpClient` de Angular para hacer peticiones HTTP a la API REST del backend. Las respuestas son **Observables** (de RxJS).

### ¿Qué es un Observable?

Un Observable es como una "promesa mejorada" — representa un valor que llegará en el futuro (la respuesta del servidor). Para obtener el valor debes **suscribirte**:

```typescript
// En un componente:
this.solicitudService.getSolicitudes().subscribe({
  next: (data) => {
    // Se ejecuta cuando llegan los datos
    this.solicitudes.set(data);
  },
  error: (err) => {
    // Se ejecuta si hay un error HTTP
    this.error.set('No se pudo cargar las solicitudes');
  }
});
```

---

## Configuración global (`app.config.ts`)

```typescript
export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes, withComponentInputBinding()),  // Sistema de rutas
    provideHttpClient(
      withInterceptors([authInterceptor]),               // HTTP + interceptor JWT
      withFetch()
    ),
    provideAnimationsAsync(),                            // Animaciones de Material
    provideClientHydration(withEventReplay()),           // SSR support
    provideBrowserGlobalErrorListeners(),                // Captura errores no manejados
  ],
};
```

Este archivo es el "punto de registro" de toda la infraestructura de la aplicación.

---

## Patrón de componentes standalone

Los componentes de este proyecto son **standalone** — cada uno declara explícitamente qué módulos o componentes necesita usar, en lugar de pertenecer a un NgModule compartido. Esto hace el código más explícito y facilita el lazy loading.

```typescript
@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,       // ngIf, ngFor, etc.
    MatCardModule,      // Tarjetas de Material
    MatButtonModule,    // Botones de Material
    RouterLink,         // Enlaces de navegación
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
})
export class DashboardComponent { ... }
```

---

## Flujo de datos resumido

```
1. Usuario abre la app en localhost:4200
2. main.ts arranca Angular con appConfig
3. Angular carga el componente raíz (App) que solo tiene <router-outlet>
4. El router evalúa la URL actual y carga el componente correspondiente
5. El componente se inicializa (ngOnInit) y llama a un servicio
6. El servicio hace una petición HTTP → el interceptor añade el token JWT
7. El proxy redirige /api/* → localhost:8080
8. El backend responde con JSON
9. El componente actualiza sus signals con los datos recibidos
10. Angular re-renderiza la vista automáticamente
```
