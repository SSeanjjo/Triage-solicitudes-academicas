# 06 — Servicios y modelos de datos

## ¿Qué es un servicio en Angular?

Un **servicio** es una clase TypeScript que contiene lógica de negocio reutilizable. Se diferencian de los componentes en que:
- No tienen plantilla HTML (no renderizan nada visual)
- Pueden ser usados por múltiples componentes
- Son **singleton**: Angular crea una única instancia que se comparte en toda la app

Los servicios se inyectan en los componentes usando **Inyección de Dependencias**:

```typescript
// En un componente:
constructor(private solicitudService: SolicitudService) {}
// O con la función inject() (más moderna):
private solicitudService = inject(SolicitudService);
```

---

## Modelos de datos

Los modelos son interfaces TypeScript que definen la forma de los objetos de datos. No contienen lógica, solo estructura.

### Modelo de Usuario (`core/models/user.model.ts`)

```typescript
// Los tres roles del sistema
enum Rol {
  ESTUDIANTE = 'ESTUDIANTE',
  RESPONSABLE = 'RESPONSABLE',
  ADMINISTRADOR = 'ADMINISTRADOR',
}

// Un usuario del sistema
interface Usuario {
  id: number;
  nombre: string;
  correo: string;
  rol: Rol;
  activo: boolean;
}

// Datos para hacer login
interface LoginRequest {
  correo: string;
  password: string;
}

// Lo que devuelve el backend al hacer login
interface LoginResponse {
  token: string;   // JWT token para autenticación
  tipo: string;    // "Bearer"
  rol: Rol;
  nombre: string;
  id?: number;
}

// Lo que se guarda en la sesión local del usuario
interface UserSession {
  id: number;
  nombre: string;
  rol: Rol;
  token: string;
}
```

### Modelo de Solicitud (`core/models/solicitud.model.ts`)

#### Estados posibles de una solicitud

```
REGISTRADA → CLASIFICADA → EN_ATENCION → ATENDIDA → CERRADA
```

| Estado | Descripción |
|---|---|
| `REGISTRADA` | Recién creada, pendiente de clasificar |
| `CLASIFICADA` | Tipo y prioridad asignados, pendiente de responsable |
| `EN_ATENCION` | Responsable asignado, en proceso |
| `ATENDIDA` | Trabajo completado, pendiente de cierre |
| `CERRADA` | Solicitud finalizada |

#### Prioridades

| Valor | Significado |
|---|---|
| `ALTA` | Urgente |
| `MEDIA` | Normal |
| `BAJA` | Sin urgencia |

#### Tipos de solicitud

| Valor | Descripción |
|---|---|
| `REGISTRO_ASIGNATURA` | Inscripción en una materia |
| `HOMOLOGACION` | Equivalencia de materias |
| `CANCELACION` | Cancelar una materia |
| `CUPO` | Solicitud de cupo en materia llena |
| `CONSULTA_ACADEMICA` | Pregunta o consulta general |

#### Canales de entrada

| Valor | Descripción |
|---|---|
| `CSU` | Portal web (Campus Virtual) |
| `CORREO` | Por email |
| `SAC` | Sistema de Atención al Cliente |
| `TELEFONICO` | Por teléfono |

#### Estructura completa de una solicitud

```typescript
interface Solicitud {
  id: number;
  titulo: string;
  descripcion: string;
  tipo: TipoSolicitud;
  canal: CanalEntrada;
  estado: EstadoSolicitud;
  prioridad: Prioridad | null;       // null si aún no se ha clasificado
  solicitante: Usuario;
  responsable: Usuario | null;       // null si aún no se ha asignado
  fechaCreacion: string;             // ISO 8601: "2025-03-15T10:30:00"
  fechaActualizacion: string;
}

// Entrada en el historial de cambios
interface HistorialEntry {
  id: number;
  accion: string;         // Descripción del cambio
  usuarioNombre: string;  // Quién hizo el cambio
  timestamp: string;      // Cuándo
  detalle?: string;       // Información adicional opcional
}

// Respuesta de la IA al sugerir clasificación
interface IASugerencia {
  tipoSugerido: TipoSolicitud;
  confianza: number;        // 0.0 a 1.0
  justificacion: string;    // Explicación en texto
}

// Respuesta de la IA al generar resumen
interface IAResumen {
  resumen: string;
}
```

---

## AuthService (`core/services/auth.service.ts`)

Gestiona la autenticación y la sesión del usuario.

**Endpoints que usa:**
- `POST /api/auth/login` — Iniciar sesión
- `POST /api/auth/registro` — Registrar nuevo usuario

**Estado que maneja (signals):**
```typescript
private _session = signal<UserSession | null>(cargarDesdLocalStorage());
```

**Métodos principales:**

| Método | Descripción |
|---|---|
| `login(request)` | Llama al backend, guarda el token y datos en localStorage |
| `registro(request)` | Crea una nueva cuenta de usuario |
| `logout()` | Borra la sesión y redirige a `/login` |
| `getToken()` | Devuelve el JWT token actual |
| `getUserId()` | Extrae el ID del usuario del token JWT |
| `isAuthenticated` | Signal computed: `true` si hay sesión activa |
| `currentRole` | Signal computed: el rol del usuario o `null` |
| `hasRole(...roles)` | Comprueba si el usuario tiene uno de los roles dados |

**¿Cómo funciona la persistencia de sesión?**

Cuando el usuario hace login, la sesión se guarda en `localStorage` (almacenamiento del navegador). Cuando el usuario cierra y vuelve a abrir la app, Angular lee la sesión guardada y el usuario sigue autenticado sin necesidad de volver a hacer login.

---

## SolicitudService (`core/services/solicitud.service.ts`)

Gestiona todas las operaciones sobre solicitudes académicas.

**Endpoints que usa:**

| Método | Endpoint | Descripción |
|---|---|---|
| `getSolicitudes(filtro?)` | `GET /api/solicitudes` | Lista solicitudes con filtros opcionales |
| `getSolicitud(id)` | `GET /api/solicitudes/{id}` | Obtiene una solicitud por ID |
| `createSolicitud(data, solicitanteId)` | `POST /api/solicitudes` | Crea nueva solicitud |
| `clasificar(id, data, usuarioId)` | `PATCH /api/solicitudes/{id}/clasificar` | Asigna tipo y prioridad |
| `asignar(id, responsableId, usuarioId)` | `PATCH /api/solicitudes/{id}/asignar` | Asigna un responsable |
| `atender(id, usuarioId)` | `PATCH /api/solicitudes/{id}/atender` | Marca como atendida |
| `cerrar(id, usuarioId)` | `PATCH /api/solicitudes/{id}/cerrar` | Cierra la solicitud |
| `getHistorial(id)` | `GET /api/solicitudes/{id}/historial` | Obtiene el historial de cambios |

**Filtros disponibles para `getSolicitudes()`:**
```typescript
interface SolicitudFilter {
  estado?: EstadoSolicitud;
  tipo?: TipoSolicitud;
  prioridad?: Prioridad;
  solicitanteId?: number;
}
```

---

## UsuarioService (`core/services/usuario.service.ts`)

Gestiona los usuarios del sistema (solo accesible para ADMINISTRADOR).

**Endpoints que usa:**

| Método | Endpoint | Descripción |
|---|---|---|
| `getUsuarios(activo?, rol?)` | `GET /api/usuarios` | Lista usuarios con filtros opcionales |
| `getUsuario(id)` | `GET /api/usuarios/{id}` | Obtiene un usuario por ID |
| `updateUsuario(id, data)` | `PUT /api/usuarios/{id}` | Actualiza datos de un usuario |

---

## IaService (`core/services/ia.service.ts`)

Integra las funcionalidades de inteligencia artificial del sistema.

**Endpoints que usa:**

| Método | Endpoint | Descripción |
|---|---|---|
| `getSugerenciaClasificacion(descripcion)` | `POST /api/ia/sugerencias/clasificacion` | Sugiere tipo de solicitud basado en texto |
| `getResumen(solicitudId)` | `POST /api/ia/resumen` | Genera resumen de una solicitud |

**¿Qué hace la IA?**
- **Clasificación:** Analiza la descripción de una solicitud y predice qué tipo es (registro, homologación, etc.) con un porcentaje de confianza
- **Resumen:** Genera un resumen conciso y estructurado de la solicitud para que el responsable la comprenda rápidamente

---

## Resumen de endpoints del backend

```
Auth:
  POST   /api/auth/login
  POST   /api/auth/registro

Solicitudes:
  GET    /api/solicitudes              (con query params opcionales: estado, tipo, prioridad, solicitanteId)
  POST   /api/solicitudes
  GET    /api/solicitudes/{id}
  PATCH  /api/solicitudes/{id}/clasificar
  PATCH  /api/solicitudes/{id}/asignar
  PATCH  /api/solicitudes/{id}/atender
  PATCH  /api/solicitudes/{id}/cerrar
  GET    /api/solicitudes/{id}/historial

Usuarios:
  GET    /api/usuarios                 (con query params opcionales: activo, rol)
  GET    /api/usuarios/{id}
  PUT    /api/usuarios/{id}

IA:
  POST   /api/ia/sugerencias/clasificacion
  POST   /api/ia/resumen
```
