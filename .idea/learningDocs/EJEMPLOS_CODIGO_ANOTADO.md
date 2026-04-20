# 💻 Ejemplos de Código - Flujo Anotado

## 1. FLUJO COMPLETO: Crear una Solicitud

### 1.1. Request HTTP que envía el cliente

```http
POST http://localhost:8080/api/solicitudes?solicitanteId=1
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "tipoSolicitud": "CAMBIO_CALIFICACION",
  "descripcion": "Quiero reconsiderar mi calificación en el parcial de Cálculo",
  "canalOrigen": "PORTAL_WEB"
}
```

---

### 1.2. JwtFilter valida el token

📂 **Archivo**: `configuracion/JwtFilter.java`

```java
@Component
public class JwtFilter extends OncePerRequestFilter {
    
    private final JwtUtil jwtUtil;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) 
            throws ServletException, IOException {
        
        // PASO 1: Extrae el header "Authorization"
        String authHeader = request.getHeader("Authorization");
        
        // PASO 2: Verifica si existe y comienza con "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            
            // PASO 3: Extrae el token (sin "Bearer ")
            String token = authHeader.substring(7); // "Bearer ".length() = 7
            
            // PASO 4: Valida el token
            if (jwtUtil.validarToken(token)) {
                // ✅ Token válido → permite que continúe la petición
                String correo = jwtUtil.obtenerCorreo(token);
                // El correo se usa para identificar al usuario
                filterChain.doFilter(request, response);
            } else {
                // ❌ Token inválido → retorna 401
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token inválido o expirado");
                return;
            }
        } else {
            // ❌ No hay token → retorna 401
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token no proporcionado");
            return;
        }
    }
}
```

---

### 1.3. SolicitudController recibe la petición

📂 **Archivo**: `controller/SolicitudController.java`

```java
@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {
    
    private final SolicitudService solicitudService;
    
    public SolicitudController(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }
    
    @PostMapping  // ← Mapea POST /api/solicitudes
    public ResponseEntity<SolicitudResponse> crear(
            @RequestBody SolicitudCreateRequest request,  // ← El JSON se deserializa aquí
            @RequestParam Long solicitanteId) {          // ← Parámetro query: ?solicitanteId=1
        
        // PASO 1: El DTO se recibe con los datos del cliente
        // request.tipoSolicitud = "CAMBIO_CALIFICACION"
        // request.descripcion = "Quiero reconsiderar..."
        // request.canalOrigen = "PORTAL_WEB"
        
        // PASO 2: Delega al servicio
        SolicitudResponse response = solicitudService.crear(request, solicitanteId);
        
        // PASO 3: Retorna HTTP 201 Created
        return ResponseEntity
                .status(HttpStatus.CREATED)  // HTTP 201
                .body(response);              // JSON response
    }
}
```

**Notas:**
- `@RequestBody` desserializa el JSON a `SolicitudCreateRequest`
- `@RequestParam` extrae del query string
- `ResponseEntity` permite controlar el status HTTP

---

### 1.4. SolicitudServiceImpl procesa la lógica

📂 **Archivo**: `service/Implementar/SolicitudServiceImpl.java`

```java
@Service  // ← Esta clase es gestionada por Spring como servicio
public class SolicitudServiceImpl implements SolicitudService {
    
    private final SolicitudRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;
    private final HistorialSolicitudRepository historialRepository;
    
    // Constructor con inyección de dependencias
    public SolicitudServiceImpl(SolicitudRepository solicitudRepository,
                                UsuarioRepository usuarioRepository,
                                HistorialSolicitudRepository historialRepository) {
        this.solicitudRepository = solicitudRepository;
        this.usuarioRepository = usuarioRepository;
        this.historialRepository = historialRepository;
    }
    
    @Override
    public SolicitudResponse crear(SolicitudCreateRequest request, Long solicitanteId) {
        
        // ═══════════════════════════════════════════════════════════════════════════
        // PASO 1: BUSCAR AL USUARIO SOLICITANTE
        // ═══════════════════════════════════════════════════════════════════════════
        // Busca en la BD: SELECT * FROM usuarios WHERE id = 1
        Usuario solicitante = usuarioRepository.findById(solicitanteId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Si solicitanteId = 1, obtiene el Usuario:
        // {
        //   id: 1,
        //   nombre: "María",
        //   correo: "maria@example.com",
        //   password: "hash_encrypted",
        //   activo: true,
        //   rol: ESTUDIANTE
        // }
        
        // ═══════════════════════════════════════════════════════════════════════════
        // PASO 2: CREAR LA ENTIDAD SOLICITUD
        // ═══════════════════════════════════════════════════════════════════════════
        // Llama al constructor de Solicitud que automáticamente:
        // - Establece estado = REGISTRADA
        // - Establece fechaCreacion = LocalDateTime.now()
        // - Establece fechaActualizacion = LocalDateTime.now()
        Solicitud solicitud = new Solicitud(
                request.getDescripcion(),      // "Quiero reconsiderar..."
                solicitante,                    // Usuario(id=1, nombre="María", ...)
                request.getCanalOrigen()        // PORTAL_WEB
        );
        
        // En memoria ahora tenemos:
        // Solicitud {
        //   id: null (aún no guardada en BD),
        //   descripcion: "Quiero reconsiderar...",
        //   estado: REGISTRADA,
        //   prioridad: null,
        //   tipo: null,
        //   solicitante: Usuario(id=1, ...),
        //   responsable: null,
        //   fechaCreacion: 2026-04-14T10:30:45.123,
        //   fechaActualizacion: 2026-04-14T10:30:45.123,
        //   ...
        // }
        
        // ═══════════════════════════════════════════════════════════════════════════
        // PASO 3: ESTABLECER EL TIPO DE SOLICITUD
        // ═══════════════════════════════════════════════════════════════════════════
        solicitud.setTipo(request.getTipoSolicitud());  // CAMBIO_CALIFICACION
        
        // ═══════════════════════════════════════════════════════════════════════════
        // PASO 4: GUARDAR EN LA BASE DE DATOS
        // ═══════════════════════════════════════════════════════════════════════════
        // Ejecuta SQL:
        // INSERT INTO solicitudes (
        //   descripcion,
        //   fecha_creacion,
        //   fecha_actualizacion,
        //   estado,
        //   tipo,
        //   canal_origen,
        //   solicitante_id,
        //   prioridad,
        //   responsable_id
        // ) VALUES (
        //   'Quiero reconsiderar...',
        //   '2026-04-14 10:30:45',
        //   '2026-04-14 10:30:45',
        //   'REGISTRADA',
        //   'CAMBIO_CALIFICACION',
        //   'PORTAL_WEB',
        //   1,
        //   null,
        //   null
        // )
        solicitudRepository.save(solicitud);
        
        // Después de save(), el objeto solicitud tiene:
        // solicitud.id = 42 (generado por la BD con IDENTITY)
        
        // ═══════════════════════════════════════════════════════════════════════════
        // PASO 5: REGISTRAR EN EL HISTORIAL (AUDITORÍA)
        // ═══════════════════════════════════════════════════════════════════════════
        registrarHistorial(
                solicitud,                      // Solicitud creada
                solicitante,                    // Usuario que realizó la acción
                "Solicitud creada",             // Descripción de la acción
                null,                           // Observación (ninguna)
                null,                           // Estado anterior (no hay)
                EstadoSolicitud.REGISTRADA      // Estado nuevo
        );
        
        // Esto crea un registro en historial_solicitudes:
        // INSERT INTO historial_solicitudes (
        //   solicitud_id,
        //   usuario_accion_id,
        //   accion,
        //   fecha,
        //   estado_anterior,
        //   estado_nuevo,
        //   observacion
        // ) VALUES (
        //   42,
        //   1,
        //   'Solicitud creada',
        //   '2026-04-14 10:30:45',
        //   null,
        //   'REGISTRADA',
        //   null
        // )
        
        // ═══════════════════════════════════════════════════════════════════════════
        // PASO 6: TRANSFORMAR A DTO (SolicitudResponse)
        // ═══════════════════════════════════════════════════════════════════════════
        return mapearResponse(solicitud);
    }
    
    // ─────────────────────────────────────────────────────────────────────────────
    // Método auxiliar: mapearResponse
    // Convierte la entidad JPA a DTO de respuesta
    // ─────────────────────────────────────────────────────────────────────────────
    private SolicitudResponse mapearResponse(Solicitud solicitud) {
        SolicitudResponse response = new SolicitudResponse();
        response.setId(solicitud.getId());                          // 42
        response.setEstado(solicitud.getEstado());                  // REGISTRADA
        response.setPrioridad(solicitud.getPrioridad());            // null
        response.setTipoSolicitud(solicitud.getTipo());             // CAMBIO_CALIFICACION
        response.setCanalOrigen(solicitud.getCanalOrigen());        // PORTAL_WEB
        response.setDescripcion(solicitud.getDescripcion());        // "Quiero reconsiderar..."
        response.setJustificacionPrioridad(solicitud.getJustificacionPrioridad()); // null
        response.setObservacionCierre(solicitud.getObservacionCierre()); // null
        response.setFechaCreacion(solicitud.getFechaCreacion());    // 2026-04-14T10:30:45
        response.setFechaActualizacion(solicitud.getFechaActualizacion()); // 2026-04-14T10:30:45
        response.setSolicitanteId(solicitud.getSolicitante().getId()); // 1
        response.setResponsableId(null);                            // null (sin responsable aún)
        return response;
    }
    
    // ─────────────────────────────────────────────────────────────────────────────
    // Método auxiliar: registrarHistorial
    // Registra un evento en el historial de la solicitud
    // ─────────────────────────────────────────────────────────────────────────────
    private void registrarHistorial(Solicitud solicitud, Usuario usuario, String accion,
                                    String observacion, EstadoSolicitud estadoAnterior,
                                    EstadoSolicitud estadoNuevo) {
        HistorialSolicitud historial = new HistorialSolicitud(
                solicitud,      // Referencia a la solicitud
                usuario,        // Usuario que realizó la acción
                accion,         // Descripción de la acción
                observacion,    // Observaciones adicionales
                estadoAnterior, // Estado anterior (puede ser null)
                estadoNuevo     // Estado nuevo
        );
        historialRepository.save(historial);
    }
}
```

---

### 1.5. Respuesta HTTP que retorna al cliente

```json
HTTP/1.1 201 Created
Content-Type: application/json

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

## 2. FLUJO: Clasificar una Solicitud

### Request
```http
PATCH http://localhost:8080/api/solicitudes/42/clasificar?usuarioId=5
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "tipoSolicitud": "CAMBIO_CALIFICACION",
  "prioridad": "ALTA",
  "justificacionPrioridad": "Estudiante con buen desempeño académico"
}
```

### Procesamiento en SolicitudServiceImpl

```java
@Override
public SolicitudResponse clasificar(Long id, ClasificacionRequest request, Long usuarioId) {
    
    // PASO 1: Obtener la solicitud
    Solicitud solicitud = obtenerSolicitud(id);  // BD: SELECT * FROM solicitudes WHERE id=42
    Usuario usuario = obtenerUsuario(usuarioId); // BD: SELECT * FROM usuarios WHERE id=5
    
    // solicitud = {id: 42, estado: REGISTRADA, ...}
    // usuario = {id: 5, nombre: "Carlos", rol: COORDINADOR, ...}
    
    // PASO 2: VALIDAR TRANSICIÓN DE ESTADO
    if (!solicitud.puedeTransicionarA(EstadoSolicitud.CLASIFICADA)) {
        throw new RuntimeException("La solicitud no puede clasificarse en su estado actual");
    }
    
    // En el método puedeTransicionarA():
    // Estado actual: REGISTRADA
    // ¿Puede ir a CLASIFICADA? SÍ ✅
    
    // PASO 3: Guardar estado anterior para el historial
    EstadoSolicitud estadoAnterior = solicitud.getEstado();  // REGISTRADA
    
    // PASO 4: Clasificar la solicitud
    solicitud.clasificar(
            request.getTipoSolicitud(),          // CAMBIO_CALIFICACION
            request.getPrioridad(),              // ALTA
            request.getJustificacionPrioridad()  // "Estudiante con buen desempeño..."
    );
    
    // En el método clasificar() de la entidad:
    // this.tipo = CAMBIO_CALIFICACION
    // this.prioridad = ALTA
    // this.justificacionPrioridad = "Estudiante con buen desempeño..."
    // this.estado = CLASIFICADA  ← Cambio de estado
    // this.fechaActualizacion = LocalDateTime.now()  ← Actualiza fecha
    
    // PASO 5: Guardar cambios en BD
    solicitudRepository.save(solicitud);
    
    // BD: UPDATE solicitudes SET 
    //     estado='CLASIFICADA',
    //     tipo='CAMBIO_CALIFICACION',
    //     prioridad='ALTA',
    //     justificacion_prioridad='Estudiante...',
    //     fecha_actualizacion='2026-04-14 10:45:00'
    // WHERE id=42
    
    // PASO 6: Registrar en historial
    registrarHistorial(
            solicitud,                          // Solicitud actualizada
            usuario,                            // Carlos (coordinador)
            "Solicitud clasificada",            // Acción
            null,                               // Sin observación
            estadoAnterior,                     // REGISTRADA
            EstadoSolicitud.CLASIFICADA         // CLASIFICADA
    );
    
    // BD: INSERT INTO historial_solicitudes (
    //   solicitud_id, usuario_accion_id, accion, fecha, 
    //   estado_anterior, estado_nuevo
    // ) VALUES (
    //   42, 5, 'Solicitud clasificada', '2026-04-14 10:45:00',
    //   'REGISTRADA', 'CLASIFICADA'
    // )
    
    // PASO 7: Retornar respuesta
    return mapearResponse(solicitud);
}
```

### Response
```json
HTTP/1.1 200 OK
Content-Type: application/json

{
  "id": 42,
  "estado": "CLASIFICADA",
  "prioridad": "ALTA",
  "tipoSolicitud": "CAMBIO_CALIFICACION",
  "canalOrigen": "PORTAL_WEB",
  "solicitanteId": 1,
  "responsableId": null,
  "descripcion": "Quiero reconsiderar mi calificación en el parcial de Cálculo",
  "justificacionPrioridad": "Estudiante con buen desempeño académico",
  "observacionCierre": null,
  "fechaCreacion": "2026-04-14T10:30:45",
  "fechaActualizacion": "2026-04-14T10:45:00"
}
```

---

## 3. FLUJO: Obtener Historial

### Request
```http
GET http://localhost:8080/api/solicitudes/42/historial
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Procesamiento

```java
@Override
public List<HistorialEventoResponse> obtenerHistorial(Long id) {
    
    // PASO 1: Validar que la solicitud exista
    obtenerSolicitud(id);  // Si no existe, lanza excepción
    
    // PASO 2: Obtener del historial
    return historialRepository
            .findBySolicitudId(id)  // BD: SELECT * FROM historial_solicitudes WHERE solicitud_id=42
            .stream()
            // Retorna:
            // [
            //   HistorialSolicitud{id:1, solicitud_id:42, usuario_id:1, accion:"Solicitud creada", ...},
            //   HistorialSolicitud{id:2, solicitud_id:42, usuario_id:5, accion:"Solicitud clasificada", ...},
            //   HistorialSolicitud{id:3, solicitud_id:42, usuario_id:5, accion:"Responsable asignado", ...},
            //   ...
            // ]
            
            // PASO 3: Transformar cada historial a DTO
            .map(this::mapearHistorial)  // Convierte a HistorialEventoResponse
            .collect(Collectors.toList());
}

private HistorialEventoResponse mapearHistorial(HistorialSolicitud h) {
    HistorialEventoResponse response = new HistorialEventoResponse();
    response.setId(h.getId());
    response.setSolicitudId(h.getSolicitud().getId());
    response.setUsuarioId(h.getUsuarioAccion().getId());
    response.setAccion(h.getAccion());
    response.setEstadoAnterior(h.getEstadoAnterior());
    response.setEstadoNuevo(h.getEstadoNuevo());
    response.setFecha(h.getFecha());
    response.setComentario(h.getObservacion());
    return response;
}
```

### Response
```json
HTTP/1.1 200 OK
Content-Type: application/json

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
  {
    "id": 3,
    "solicitudId": 42,
    "usuarioId": 5,
    "accion": "Responsable asignado",
    "estadoAnterior": "CLASIFICADA",
    "estadoNuevo": "EN_ATENCION",
    "fecha": "2026-04-14T10:50:00",
    "comentario": null
  },
  {
    "id": 4,
    "solicitudId": 42,
    "usuarioId": 10,
    "accion": "Solicitud atendida",
    "estadoAnterior": "EN_ATENCION",
    "estadoNuevo": "ATENDIDA",
    "fecha": "2026-04-14T14:00:00",
    "comentario": "Se revisó el parcial. Calificación correcta es 4.2"
  }
]
```

---

## 4. FLUJO: Login (Autenticación)

### Request
```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "correo": "maria@example.com",
  "password": "password123"
}
```

### Procesamiento en AuthServiceImpl

```java
@Service
public class AuthServiceImpl implements AuthService {
    
    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;
    
    @Override
    public LoginResponse login(LoginRequest request) {
        
        // PASO 1: Buscar usuario por correo
        Usuario usuario = usuarioRepository.findByCorreo(request.getCorreo())
                // BD: SELECT * FROM usuarios WHERE correo='maria@example.com'
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // usuario = {
        //   id: 1,
        //   nombre: "María",
        //   correo: "maria@example.com",
        //   password: "$2a$10$E9YPxF/W.bFGWK3DDeTFm.uQZWAVJ0r9VRNhGQF7E88rHqSCurS7a",  (bcrypt hash)
        //   activo: true,
        //   rol: ESTUDIANTE
        // }
        
        // PASO 2: Verificar contraseña
        // En la implementación real habría:
        // if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
        //     throw new RuntimeException("Contraseña incorrecta");
        // }
        // Aquí se compara el texto plano con el hash bcrypt
        
        // PASO 3: Generar JWT
        String token = jwtUtil.generarToken(
                usuario.getCorreo(),      // "maria@example.com"
                usuario.getRol().toString() // "ESTUDIANTE"
        );
        
        // En JwtUtil.generarToken():
        // Crear JWT con:
        // - sub (subject): "maria@example.com"
        // - rol (claim personalizado): "ESTUDIANTE"
        // - iat (issued at): timestamp actual
        // - exp (expiration): timestamp actual + 10 horas
        // Firmar con clave privada HS256
        // Resultado: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWI..."
        
        // PASO 4: Retornar respuesta con token
        return new LoginResponse(token, usuario.getRol().toString());
    }
}
```

### Response
```json
HTTP/1.1 200 OK
Content-Type: application/json

{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJtYXJpYUBleGFtcGxlLmNvbSIsInJvbCI6IkVTVFVESUFOVEUiLCJpYXQiOjE3MTMwOTg0NDUsImV4cCI6MTcxMzEzNDQ0NX0.7F9e...",
  "rol": "ESTUDIANTE"
}
```

### Token decodificado (Header.Payload.Signature)

**Header:**
```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

**Payload:**
```json
{
  "sub": "maria@example.com",
  "rol": "ESTUDIANTE",
  "iat": 1713098445,
  "exp": 1713134445
}
```

**Signature:**
```
HMACSHA256(base64url(header) + "." + base64url(payload), secret_key)
```

---

## 5. VALIDACIÓN DE TRANSICIÓN DE ESTADOS

### Código en `Solicitud.java`

```java
public boolean puedeTransicionarA(EstadoSolicitud nuevoEstado) {
    switch (this.estado) {
        case REGISTRADA:
            // Desde REGISTRADA solo puede ir a CLASIFICADA
            return nuevoEstado == EstadoSolicitud.CLASIFICADA;
            
        case CLASIFICADA:
            // Desde CLASIFICADA solo puede ir a EN_ATENCION
            return nuevoEstado == EstadoSolicitud.EN_ATENCION;
            
        case EN_ATENCION:
            // Desde EN_ATENCION solo puede ir a ATENDIDA
            return nuevoEstado == EstadoSolicitud.ATENDIDA;
            
        case ATENDIDA:
            // Desde ATENDIDA solo puede ir a CERRADA
            return nuevoEstado == EstadoSolicitud.CERRADA;
            
        case CERRADA:
            // Desde CERRADA no puede transicionar
            return false;
            
        default:
            return false;
    }
}
```

### Ejemplos de uso

```java
// ✅ VÁLIDO
Solicitud solicitud = new Solicitud(...);  // Estado: REGISTRADA
solicitud.puedeTransicionarA(EstadoSolicitud.CLASIFICADA);  // true

// ❌ INVÁLIDO
Solicitud solicitud = new Solicitud(...);  // Estado: REGISTRADA
solicitud.puedeTransicionarA(EstadoSolicitud.EN_ATENCION);  // false

// ❌ INVÁLIDO
Solicitud solicitud = new Solicitud(...);
solicitud.marcarEnAtencion();  // Estado: EN_ATENCION
solicitud.marcarAtendida();    // ← Error si se intenta sin validar
// Debe usarse: if (solicitud.puedeTransicionarA(ATENDIDA)) { ... }

// ❌ INVÁLIDO - Una vez CERRADA no se puede volver
Solicitud solicitud = new Solicitud(...);
// ... (pasa por todos los estados hasta CERRADA)
solicitud.cerrar(...);  // Estado: CERRADA
solicitud.puedeTransicionarA(EstadoSolicitud.ATENDIDA);  // false
```

---

## Resumen de Conceptos

| Concepto | Qué es | Ejemplo |
|----------|--------|---------|
| **DTO** | Objeto para transferencia de datos por red | `SolicitudCreateRequest`, `SolicitudResponse` |
| **Entidad** | Objeto que se mapea a tabla en BD | `Solicitud`, `Usuario`, `HistorialSolicitud` |
| **Repository** | Interfaz para operaciones CRUD | `SolicitudRepository.save()`, `.findById()` |
| **Service** | Lógica de negocio | `SolicitudServiceImpl.crear()` |
| **Controller** | Endpoints REST | `@PostMapping`, `@GetMapping` |
| **JWT** | Token seguro para autenticación | Bearer token con claims encriptados |
| **Enum** | Tipo con valores predefinidos | `EstadoSolicitud.REGISTRADA` |


