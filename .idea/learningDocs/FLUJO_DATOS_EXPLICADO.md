# 📋 Flujo de Datos del Backend - Gestión de Solicitudes Académicas

## 🔍 Resumen General

Este backend implementa un sistema de gestión de solicitudes académicas con autenticación JWT. Los datos fluyen a través de diferentes capas siguiendo el patrón **MVC** (Model-View-Controller) junto con una capa de **Servicios** y **Repositorios**.

---

## 🏗️ Arquitectura en Capas

```
┌─────────────────────────────────────────────────────────────┐
│                         CLIENTE/FRONTEND                     │
│                    (Envía HTTP Requests)                     │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                      CONTROLADORES                           │
│         (AuthController, SolicitudController, etc)          │
│            ├─ Reciben las peticiones HTTP                   │
│            ├─ Validan parámetros                            │
│            └─ Delegan al servicio                           │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                      SERVICIOS                              │
│      (AuthServiceImpl, SolicitudServiceImpl, etc)            │
│            ├─ Contienen la lógica de negocio               │
│            ├─ Interactúan con repositorios                 │
│            ├─ Validan reglas de negocio                    │
│            └─ Transforman datos (DTOs → Entidades)         │
└────────────────────────┬────────────────────────────────────┘
                         │
        ┌────────────────┼────────────────┐
        ▼                ▼                ▼
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│ REPOSITORIOS │  │  REPOSITORIOS │ │  REPOSITORIOS │
│  Usuario Repo│  │ Solicitud Repo│ │ Historial Repo│
│              │  │              │  │              │
│  Realiza CRUD│  │  Realiza CRUD│  │  Realiza CRUD│
│  en BD       │  │   en BD      │  │   en BD      │
└──────────────┘  └──────────────┘  └──────────────┘
        │                │                │
        └────────────────┼────────────────┘
                         │
                         ▼
        ┌────────────────────────────────┐
        │      BASE DE DATOS (PostgreSQL)│
        │                                 │
        │  ├─ Tabla: usuarios            │
        │  ├─ Tabla: solicitudes         │
        │  └─ Tabla: historial_solicitudes
        │                                 │
        └────────────────────────────────┘
```

---

## 🔐 1. AUTENTICACIÓN (Login)

### Paso 1: El cliente envía sus credenciales
```
POST /api/auth/login
{
  "correo": "usuario@example.com",
  "password": "password123"
}
```

### Paso 2: AuthController recibe la solicitud
📂 **Archivo**: `AuthController.java`
```java
@PostMapping("/login")
public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
    return ResponseEntity.ok(authService.login(request));
}
```

### Paso 3: AuthService valida las credenciales
📂 **Archivo**: `AuthServiceImpl.java`

El servicio:
1. Busca el usuario en la base de datos por correo
2. Verifica que la contraseña coincida
3. Genera un token JWT si es válido
4. Retorna `LoginResponse` con el token

```java
public LoginResponse login(LoginRequest request) {
    Usuario usuario = usuarioRepository.findByCorreo(request.getCorreo())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    
    // Validar contraseña
    // Generar JWT
    String token = jwtUtil.generarToken(usuario.getCorreo(), usuario.getRol().toString());
    
    return new LoginResponse(token, usuario.getRol().toString());
}
```

### Paso 4: Cliente recibe el JWT
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "rol": "ESTUDIANTE"
}
```

### Paso 5: JWT se usa en peticiones posteriores
Todas las peticiones futuras incluyen el token en el header:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**JwtFilter** valida el token antes de procesar cada request.

---

## 📝 2. FLUJO DE CREACIÓN DE UNA SOLICITUD (DETALLADO)

Este es el flujo completo desde que un estudiante crea una solicitud desde CERO:

### **PASO A PASO:**

#### 1️⃣ **Cliente envía petición HTTP POST**
```http
POST /api/solicitudes?solicitanteId=1
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "tipoSolicitud": "CAMBIO_CALIFICACION",
  "descripcion": "Quiero reconsiderar mi calificación en el parcial de Cálculo",
  "canalOrigen": "PORTAL_WEB"
}
```

#### 2️⃣ **SolicitudController recibe la petición**
📂 **Archivo**: `SolicitudController.java` (línea 28-34)

```java
@PostMapping
public ResponseEntity<SolicitudResponse> crear(
        @RequestBody SolicitudCreateRequest request,
        @RequestParam Long solicitanteId) {
    return ResponseEntity.status(HttpStatus.CREATED)
            .body(solicitudService.crear(request, solicitanteId));
}
```

**¿Qué ocurre?**
- El controlador recibe el `SolicitudCreateRequest` (DTO de entrada)
- Extrae el `solicitanteId` de los parámetros
- Delega al servicio

#### 3️⃣ **SolicitudServiceImpl procesa la lógica de negocio**
📂 **Archivo**: `SolicitudServiceImpl.java` (línea 33-50)

```java
@Override
public SolicitudResponse crear(SolicitudCreateRequest request, Long solicitanteId) {
    
    // PASO 1: Buscar al usuario solicitante en la BD
    Usuario solicitante = usuarioRepository.findById(solicitanteId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    // PASO 2: Crear la instancia de Solicitud (ENTIDAD DE DOMINIO)
    // El constructor fija automáticamente:
    // - Estado = REGISTRADA
    // - FechaCreacion = NOW()
    // - FechaActualizacion = NOW()
    Solicitud solicitud = new Solicitud(
            request.getDescripcion(),
            solicitante,
            request.getCanalOrigen()
    );
    
    // PASO 3: Establecer el tipo (en el CREATE_REQUEST viene el tipo)
    solicitud.setTipo(request.getTipoSolicitud());
    
    // PASO 4: Guardar la solicitud en la BD
    solicitudRepository.save(solicitud);

    // PASO 5: Registrar en el historial (auditoría)
    registrarHistorial(solicitud, solicitante, "Solicitud creada", null,
            null, EstadoSolicitud.REGISTRADA);

    // PASO 6: Transformar la entidad a DTO (SolicitudResponse)
    return mapearResponse(solicitud);
}
```

#### 4️⃣ **SolicitudRepository interactúa con la BD**
📂 **Archivo**: `SolicitudRepository.java`

La clase `SolicitudRepository` extiende `JpaRepository<Solicitud, Long>`, que proporciona:
- `save(solicitud)` - Inserta o actualiza en la tabla `solicitudes`
- `findById(id)` - Busca por ID
- `findAll()` - Obtiene todas
- Etc.

**¿Qué ocurre en la BD?**

Se ejecuta un SQL como:
```sql
INSERT INTO solicitudes (
    descripcion, 
    fecha_creacion, 
    fecha_actualizacion, 
    estado, 
    tipo_solicitud, 
    canal_origen, 
    solicitante_id
) VALUES (
    'Quiero reconsiderar mi calificación en el parcial de Cálculo',
    '2026-04-14 10:30:45',
    '2026-04-14 10:30:45',
    'REGISTRADA',
    'CAMBIO_CALIFICACION',
    'PORTAL_WEB',
    1
);
```

**Resultado**: Se genera un `id` automático (supongamos que es `id=42`)

#### 5️⃣ **Se registra en el Historial**
Al mismo tiempo, se ejecuta:
```java
registrarHistorial(solicitud, solicitante, "Solicitud creada", null,
        null, EstadoSolicitud.REGISTRADA);
```

Esto crea un `HistorialSolicitud`:
```sql
INSERT INTO historial_solicitudes (
    solicitud_id, 
    usuario_accion_id, 
    accion, 
    fecha, 
    estado_anterior, 
    estado_nuevo
) VALUES (
    42,
    1,
    'Solicitud creada',
    '2026-04-14 10:30:45',
    NULL,  -- No hay estado anterior
    'REGISTRADA'
);
```

#### 6️⃣ **Se transforma a DTO (SolicitudResponse)**
```java
private SolicitudResponse mapearResponse(Solicitud solicitud) {
    SolicitudResponse response = new SolicitudResponse();
    response.setId(solicitud.getId());                          // 42
    response.setEstado(solicitud.getEstado());                  // REGISTRADA
    response.setPrioridad(solicitud.getPrioridad());            // null
    response.setTipoSolicitud(solicitud.getTipo());             // CAMBIO_CALIFICACION
    response.setCanalOrigen(solicitud.getCanalOrigen());        // PORTAL_WEB
    response.setDescripcion(solicitud.getDescripcion());        // ...
    response.setFechaCreacion(solicitud.getFechaCreacion());    // 2026-04-14 10:30:45
    response.setFechaActualizacion(solicitud.getFechaActualizacion()); // 2026-04-14 10:30:45
    response.setSolicitanteId(solicitud.getSolicitante().getId()); // 1
    response.setResponsableId(null);                            // Sin responsable aún
    return response;
}
```

#### 7️⃣ **El cliente recibe la respuesta**
```json
HTTP 201 Created

{
  "id": 42,
  "estado": "REGISTRADA",
  "prioridad": null,
  "tipoSolicitud": "CAMBIO_CALIFICACION",
  "canalOrigen": "PORTAL_WEB",
  "solicitanteId": 1,
  "responsableId": null,
  "descripcion": "Quiero reconsiderar mi calificación en el parcial de Cálculo",
  "justificacionPrioridad": null,
  "observacionCierre": null,
  "fechaCreacion": "2026-04-14T10:30:45",
  "fechaActualizacion": "2026-04-14T10:30:45"
}
```

---

## 🔄 3. CICLO DE VIDA DE UNA SOLICITUD

Una solicitud pasa por estos estados en orden:

```
REGISTRADA → CLASIFICADA → EN_ATENCION → ATENDIDA → CERRADA
```

### Estados:
1. **REGISTRADA** - Acaba de crearse
2. **CLASIFICADA** - Clasificada por tipo y prioridad
3. **EN_ATENCION** - Asignada a un responsable
4. **ATENDIDA** - Se ha procesado
5. **CERRADA** - Finalizada

### Validaciones de transición:
La clase `Solicitud` tiene un método `puedeTransicionarA()` que valida si la transición es válida:

```java
public boolean puedeTransicionarA(EstadoSolicitud nuevoEstado) {
    switch (this.estado) {
        case REGISTRADA:
            return nuevoEstado == EstadoSolicitud.CLASIFICADA; // Solo puede ir a CLASIFICADA
        case CLASIFICADA:
            return nuevoEstado == EstadoSolicitud.EN_ATENCION; // Solo puede ir a EN_ATENCION
        case EN_ATENCION:
            return nuevoEstado == EstadoSolicitud.ATENDIDA;    // Solo puede ir a ATENDIDA
        case ATENDIDA:
            return nuevoEstado == EstadoSolicitud.CERRADA;     // Solo puede ir a CERRADA
        case CERRADA:
            return false; // No puede salir de CERRADA
        default:
            return false;
    }
}
```

---

## 📊 Ejemplo completo: Trazando una solicitud del estudio hasta el cierre

### Escenario:
- **Usuario**: María (ID=1, rol=ESTUDIANTE)
- **Solicitud ID**: 42

### Timeline:

#### **T0: 10:30 - CREAR SOLICITUD**
```
María crea solicitud → Estado: REGISTRADA
Historial: [Acción: "Solicitud creada", Usuario: María, Estado: REGISTRADA]
```

#### **T1: 10:45 - CLASIFICAR SOLICITUD**
Coordinador académico (ID=5, rol=COORDINADOR) clasifica:

```http
PATCH /api/solicitudes/42/clasificar?usuarioId=5
{
  "tipoSolicitud": "CAMBIO_CALIFICACION",
  "prioridad": "ALTA",
  "justificacionPrioridad": "Parcial debe revisarse"
}
```

```java
// En SolicitudServiceImpl.clasificar():
solicitud.clasificar(tipo, prioridad, justificacion);
// Estado cambia a: CLASIFICADA
registrarHistorial(...);
```

```
Estado: CLASIFICADA
Prioridad: ALTA
Historial: [
  ...,
  Acción: "Solicitud clasificada", 
  Usuario: Coordinador,
  Estado anterior: REGISTRADA → Estado nuevo: CLASIFICADA
]
```

#### **T2: 11:00 - ASIGNAR A RESPONSABLE**
```http
PATCH /api/solicitudes/42/asignar?usuarioId=5
{
  "responsableId": 10  // Profesor Especializado
}
```

```
Estado: EN_ATENCION
Responsable ID: 10
Historial: [
  ...,
  Acción: "Responsable asignado",
  Usuario: Coordinador,
  Estado anterior: CLASIFICADA → Estado nuevo: EN_ATENCION
]
```

#### **T3: 14:00 - ATENDER SOLICITUD**
El profesor (ID=10) atiende:

```http
PATCH /api/solicitudes/42/atender?usuarioId=10
{
  "comentario": "Se revisó el parcial, calificación correcta es 4.2"
}
```

```
Estado: ATENDIDA
Historial: [
  ...,
  Acción: "Solicitud atendida",
  Usuario: Profesor,
  Comentario: "Se revisó el parcial...",
  Estado anterior: EN_ATENCION → Estado nuevo: ATENDIDA
]
```

#### **T4: 16:00 - CERRAR SOLICITUD**
```http
PATCH /api/solicitudes/42/cerrar?usuarioId=5
{
  "comentarioCierre": "Caso finalizado. María fue notificada."
}
```

```
Estado: CERRADA
Observación de Cierre: "Caso finalizado..."
Historial: [
  ...,
  Acción: "Solicitud cerrada",
  Usuario: Coordinador,
  Comentario: "Caso finalizado...",
  Estado anterior: ATENDIDA → Estado nuevo: CERRADA
]
```

### Historial final (GET /api/solicitudes/42/historial):
```json
[
  {
    "id": 1,
    "solicitudId": 42,
    "usuarioId": 1,
    "accion": "Solicitud creada",
    "estadoAnterior": null,
    "estadoNuevo": "REGISTRADA",
    "fecha": "2026-04-14T10:30:45",
    "comentario": null
  },
  {
    "id": 2,
    "solicitudId": 42,
    "usuarioId": 5,
    "accion": "Solicitud clasificada",
    "estadoAnterior": "REGISTRADA",
    "estadoNuevo": "CLASIFICADA",
    "fecha": "2026-04-14T10:45:00",
    "comentario": null
  },
  ...
]
```

---

## 🗄️ Estructura de la Base de Datos

### Tabla: `usuarios`
```
┌─────────────────────────────────────────┐
│ id (PK) │ nombre  │ correo │ password   │
│ activo  │ rol     │       │            │
├─────────────────────────────────────────┤
│ 1       │ María   │ m@... │ hash...    │
│ TRUE    │ ESTUDIANTE     │            │
│ 5       │ Carlos  │ c@... │ hash...    │
│ TRUE    │ COORDINADOR    │            │
│ 10      │ Profesor│ p@... │ hash...    │
│ TRUE    │ PROFESOR       │            │
└─────────────────────────────────────────┘
```

### Tabla: `solicitudes`
```
┌──────────────────────────────────────────────────────┐
│ id  │ descripcion       │ fecha_creacion │ estado    │
│ ... │ tipo_solicitud    │ prioridad      │ canal...  │
│ solicitante_id │ responsable_id │            │
├──────────────────────────────────────────────────────┤
│ 42  │ "Quiero reconsiderar mi..." │ 2026-04-14...│
│ ... │ CAMBIO_CALIFICACION │ ALTA      │ PORTAL_WEB│
│ 1   │ 10                 │                           │
└──────────────────────────────────────────────────────┘
```

### Tabla: `historial_solicitudes`
```
┌───────────────────────────────────────────────────────┐
│ id  │ solicitud_id │ usuario_accion_id │ accion       │
│ ... │ fecha        │ estado_anterior   │ estado_nuevo │
├───────────────────────────────────────────────────────┤
│ 1   │ 42           │ 1                 │ "Solicitud creada" │
│ 2   │ 42           │ 5                 │ "Solicitud clasificada" │
│ ... │ ...          │ ...               │ ...          │
└───────────────────────────────────────────────────────┘
```

---

## 🔗 Relaciones entre Entidades

```
                    ┌──────────────────┐
                    │    USUARIO       │
                    ├──────────────────┤
                    │ id (PK)          │
                    │ nombre           │
                    │ correo           │
                    │ rol              │
                    │ activo           │
                    └──────────────────┘
                      ▲          ▲
                      │1        │1
                      │         │
         (solicitante)│         │(responsable)
                      │         │
                    ┌─┴─────────┴─┐
                    │  SOLICITUD   │
                    ├──────────────┤
                    │ id (PK)      │
                    │ descripcion  │
                    │ estado       │
                    │ prioridad    │
                    │ tipo         │
                    │ fecha_*      │
                    │ solicitante_id (FK)
                    │ responsable_id (FK)
                    └───────┬──────┘
                            │1
                            │
                            │*
                    ┌───────▼──────────┐
                    │ HISTORIAL        │
                    ├──────────────────┤
                    │ id (PK)          │
                    │ accion           │
                    │ fecha            │
                    │ estado_anterior  │
                    │ estado_nuevo     │
                    │ solicitud_id (FK)│
                    │ usuario_id (FK)  │
                    └──────────────────┘
```

---

## 📲 Endpoints Principales

| Método | Endpoint | Descripción | Requiere Auth |
|--------|----------|-------------|---------------|
| POST | `/api/auth/login` | Autenticarse | ❌ |
| POST | `/api/auth/registro` | Registrar usuario | ❌ |
| POST | `/api/solicitudes` | Crear solicitud | ✅ |
| GET | `/api/solicitudes` | Listar solicitudes (con filtros) | ✅ |
| GET | `/api/solicitudes/{id}` | Obtener solicitud por ID | ✅ |
| PATCH | `/api/solicitudes/{id}/clasificar` | Clasificar solicitud | ✅ |
| PATCH | `/api/solicitudes/{id}/asignar` | Asignar responsable | ✅ |
| PATCH | `/api/solicitudes/{id}/atender` | Marcar como atendida | ✅ |
| PATCH | `/api/solicitudes/{id}/cerrar` | Cerrar solicitud | ✅ |
| GET | `/api/solicitudes/{id}/historial` | Obtener historial | ✅ |

---

## 🎯 Conceptos Clave

### DTO (Data Transfer Object)
Objetos que representan los datos que viajan por la red (entrada/salida):
- `SolicitudCreateRequest` - Lo que envía el cliente para crear
- `SolicitudResponse` - Lo que retorna el servidor

### Entidad de Dominio
Objetos que representan el modelo de negocio en la BD:
- `Solicitud` - La solicitud en sí
- `Usuario` - Usuario del sistema
- `HistorialSolicitud` - Registro de cambios

### Repository
Interface que proporciona métodos CRUD para interactuar con la BD:
```java
interface SolicitudRepository extends JpaRepository<Solicitud, Long> {
    // Hereda: save(), findById(), findAll(), delete(), etc.
}
```

### Service
Lógica de negocio que orquesta operaciones:
```java
class SolicitudServiceImpl implements SolicitudService {
    public SolicitudResponse crear(...) {
        // 1. Validar
        // 2. Transformar (DTO → Entidad)
        // 3. Guardar (repositorio)
        // 4. Registrar eventos
        // 5. Retornar (Entidad → DTO)
    }
}
```

---

## 🔐 Seguridad con JWT

### Flujo de autenticación:

1. **Cliente login** → Envía correo + contraseña
2. **Servidor valida** → Busca usuario, verifica contraseña
3. **Servidor genera JWT** → Token que contiene: correo, rol, fecha expiración
4. **Cliente recibe JWT** → Lo almacena en localStorage/sessionStorage
5. **Cliente en futuras peticiones** → Envía JWT en header `Authorization: Bearer <token>`
6. **JwtFilter valida JWT** → Si es válido, permite la petición; si no, rechaza (401)

### Contenido del JWT (ejemplo decodificado):
```json
{
  "sub": "maria@example.com",
  "rol": "ESTUDIANTE",
  "iat": 1713098445,
  "exp": 1713134445  // Expira en 10 horas
}
```

---

## 📌 Resumen del Flujo Completo

```
┌─────────────────────────────────────────────────────────────────────┐
│                    CLIENTE FRONTEND                                  │
│  (Envía solicitud HTTP con JWT en header)                           │
└──────────────────────────────┬──────────────────────────────────────┘
                               │ POST /api/solicitudes
                               │ + SolicitudCreateRequest
                               │ + JWT
                               ▼
┌─────────────────────────────────────────────────────────────────────┐
│                      JwtFilter                                       │
│  (Valida que el JWT sea válido)                                    │
└──────────────────────────────┬──────────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────────┐
│                  SolicitudController                                 │
│  (Recibe SolicitudCreateRequest y solicitanteId)                   │
│  @PostMapping → calls solicitudService.crear()                     │
└──────────────────────────────┬──────────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────────┐
│                  SolicitudServiceImpl                                 │
│  1. Obtiene Usuario desde UsuarioRepository.findById()            │
│  2. Crea nueva Solicitud(descripcion, usuario, canal)             │
│  3. Guarda con solicitudRepository.save()                         │
│  4. Registra evento en historialRepository                        │
│  5. Transforma a SolicitudResponse                                │
└──────────────────────────────┬──────────────────────────────────────┘
                               │
        ┌──────────────────────┼──────────────────────┐
        ▼                      ▼                      ▼
   UsuarioRepository    SolicitudRepository    HistorialRepository
   findById()           save()                 save()
        │                      │                      │
        └──────────────────────┼──────────────────────┘
                               │ (Operaciones SQL)
                               ▼
                    ┌────────────────────┐
                    │  BASE DE DATOS     │
                    │  PostgreSQL        │
                    └────────────────────┘
                               │
                               ▼ Retorna entidades guardadas
                               │
        ┌──────────────────────┴──────────────────────┐
        │     Transformación a DTOs                   │
        │  (Entidad → SolicitudResponse)              │
        └──────────────────────┬──────────────────────┘
                               │
                               ▼
                    ┌────────────────────┐
                    │ SolicitudResponse  │
                    │ (JSON)             │
                    └────────────────────┘
                               │
                               ▼
               ┌─────────────────────────────┐
               │ HTTP 201 Created + Response │
               │ (Retorna al cliente)        │
               └─────────────────────────────┘
```

---

## 🚀 Puntos Clave a Recordar

✅ **Las solicitudes comienzan en estado REGISTRADA**
✅ **Cada cambio de estado se registra en el historial (auditoría)**
✅ **Las validaciones de transición evitan cambios de estado inválidos**
✅ **Los JWTs se validan en cada petición protegida**
✅ **Los DTOs protegen la estructura interna del sistema**
✅ **Los Repositorios abstraen la interacción con la BD**
✅ **Los Servicios contienen toda la lógica de negocio**

---

## 📚 Archivos Importantes

```
src/main/java/co/edu/uniquindio/gestion_solicitudes/
├── controller/
│   ├── AuthController.java          ← Autenticación
│   ├── SolicitudController.java      ← CRUD de solicitudes
│   └── UsuarioController.java        ← Gestión de usuarios
│
├── service/
│   ├── AuthService.java             ← Interface
│   ├── SolicitudService.java         ← Interface
│   ├── Implementar/
│   │   ├── AuthServiceImpl.java       ← Lógica autenticación
│   │   ├── SolicitudServiceImpl.java  ← Lógica solicitudes
│   │   └── UsuarioServiceImpl.java    ← Lógica usuarios
│
├── repository/
│   ├── UsuarioRepository.java        ← CRUD Usuarios
│   ├── SolicitudRepository.java      ← CRUD Solicitudes
│   └── HistorialSolicitudRepository.java ← CRUD Historial
│
├── domain/                          ← Entidades de dominio
│   ├── Solicitud.java
│   ├── Usuario.java
│   ├── HistorialSolicitud.java
│   ├── EstadoSolicitud.java         ← Enum
│   ├── PrioridadSolicitud.java      ← Enum
│   ├── TipoSolicitud.java           ← Enum
│   └── Rol.java                      ← Enum
│
├── dto/                             ← Data Transfer Objects
│   ├── SolicitudCreateRequest.java  ← Entrada crear
│   ├── SolicitudResponse.java       ← Salida solicitud
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   └── ...
│
└── configuracion/
    ├── JwtUtil.java                 ← Generación/validación JWT
    ├── JwtFilter.java               ← Filtro de autenticación
    └── SecurityConfig.java          ← Configuración Spring Security
```

