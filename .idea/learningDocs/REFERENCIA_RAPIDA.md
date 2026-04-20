# ⚡ Referencia Rápida - Una Página

## El Flujo en 1 Imagen

```
CLIENTE                          SERVIDOR                           BD
  │                               │                                │
  │ POST /api/solicitudes         │                                │
  │ Authorization: Bearer JWT     │                                │
  │ {tipoSolicitud: "...", ...}   │                                │
  ├──────────────────────────────>│                                │
  │                           JwtFilter valida                     │
  │                               │                                │
  │                           SolicitudController.crear()           │
  │                               │                                │
  │                           SolicitudServiceImpl                  │
  │                               │ usuarioRepository.findById()   │
  │                               ├───────────────────────────────>│
  │                               │ SELECT * FROM usuarios WHERE...│
  │                               │<───────────────────────────────┤
  │                               │ Usuario                         │
  │                               │                                │
  │                               │ new Solicitud()                │
  │                               │ solicitudRepository.save()      │
  │                               ├───────────────────────────────>│
  │                               │ INSERT INTO solicitudes(...)   │
  │                               │<───────────────────────────────┤
  │                               │ id = 42                        │
  │                               │                                │
  │                               │ registrarHistorial()           │
  │                               │ historialRepository.save()      │
  │                               ├───────────────────────────────>│
  │                               │ INSERT INTO historial(...)     │
  │                               │<───────────────────────────────┤
  │                               │                                │
  │                               │ mapearResponse()               │
  │                               │ SolicitudResponse               │
  │<──────────────────────────────┤                                │
  │ HTTP 201 Created              │                                │
  │ {id: 42, estado: REGISTRADA}  │                                │
```

---

## Los 5 Comandos SQL que Ocurren

Cuando creas una solicitud, el backend ejecuta:

### 1. Buscar Usuario
```sql
SELECT * FROM usuarios WHERE id = 1;
```
**Resultado**: Usuario María

### 2. Guardar Solicitud
```sql
INSERT INTO solicitudes (
    descripcion, 
    estado, 
    canal_origen,
    solicitante_id,
    fecha_creacion,
    fecha_actualizacion
) VALUES (
    'Quiero reconsiderar mi calificación',
    'REGISTRADA',
    'PORTAL_WEB',
    1,
    '2026-04-14 10:30:45',
    '2026-04-14 10:30:45'
);
```
**Resultado**: ID = 42 asignado automáticamente

### 3. Guardar Historial
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
    NULL,
    'REGISTRADA'
);
```
**Resultado**: Evento registrado

---

## Las 3 Capas

| Capa | Responsable | Ejemplo |
|------|-------------|---------|
| **Controller** | Recibe HTTP | `POST /api/solicitudes` → recibe JSON |
| **Service** | Lógica negocio | Validar usuario, crear solicitud, registrar evento |
| **Repository** | CRUD BD | `save()`, `findById()`, `findAll()` |

---

## Los 3 Objetos

| Objeto | Dónde | Cuándo |
|--------|-------|--------|
| **DTO** | Red (JSON) | Entrada y salida |
| **Entidad** | Memoria | Durante procesamiento |
| **Tabla** | BD | Almacenamiento |

### Viaje de un Dato

```
JSON Input
   ↓
SolicitudCreateRequest (DTO)
   ↓
Solicitud (Entidad)
   ↓
INSERT SQL
   ↓
solicitudes table (BD)
   ↓
Solicitud (Entidad)
   ↓
SolicitudResponse (DTO)
   ↓
JSON Output
```

---

## Los 5 Estados

```
REGISTRADA 
    ↓
CLASIFICADA 
    ↓
EN_ATENCION 
    ↓
ATENDIDA 
    ↓
CERRADA
```

**Regla**: No puedes saltar. REGISTRADA → EN_ATENCION es INVÁLIDO.

---

## JWT (Token de Autenticación)

### Obtenerlo
```
POST /api/auth/login
{
  "correo": "maria@example.com",
  "password": "password123"
}
↓
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "rol": "ESTUDIANTE"
}
```

### Usarlo
```
Todas las peticiones posteriores:
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
↓ JwtFilter valida
↓ Si es válido → continúa
↓ Si es inválido → 401 Unauthorized
```

---

## Los 8 Endpoints

| Método | Endpoint | Estado | ¿Qué hace? |
|--------|----------|--------|-----------|
| POST | `/api/solicitudes` | REGISTRADA → REGISTRADA | Crear |
| GET | `/api/solicitudes` | - | Listar |
| GET | `/api/solicitudes/{id}` | - | Obtener 1 |
| PATCH | `/api/solicitudes/{id}/clasificar` | REGISTRADA → CLASIFICADA | Clasificar |
| PATCH | `/api/solicitudes/{id}/asignar` | CLASIFICADA → EN_ATENCION | Asignar |
| PATCH | `/api/solicitudes/{id}/atender` | EN_ATENCION → ATENDIDA | Atender |
| PATCH | `/api/solicitudes/{id}/cerrar` | ATENDIDA → CERRADA | Cerrar |
| GET | `/api/solicitudes/{id}/historial` | - | Historial |

---

## Request vs Response

### Request: Crear Solicitud
```json
POST /api/solicitudes?solicitanteId=1
{
  "tipoSolicitud": "CAMBIO_CALIFICACION",
  "descripcion": "Quiero reconsiderar mi calificación",
  "canalOrigen": "PORTAL_WEB"
}
```

### Response: 201 Created
```json
{
  "id": 42,
  "estado": "REGISTRADA",
  "prioridad": null,
  "tipoSolicitud": "CAMBIO_CALIFICACION",
  "canalOrigen": "PORTAL_WEB",
  "solicitanteId": 1,
  "responsableId": null,
  "descripcion": "Quiero reconsiderar mi calificación",
  "justificacionPrioridad": null,
  "observacionCierre": null,
  "fechaCreacion": "2026-04-14T10:30:45",
  "fechaActualizacion": "2026-04-14T10:30:45"
}
```

---

## Las 5 Enumeraciones

```java
EstadoSolicitud:     REGISTRADA, CLASIFICADA, EN_ATENCION, ATENDIDA, CERRADA
PrioridadSolicitud:  BAJA, MEDIA, ALTA
TipoSolicitud:       CAMBIO_CALIFICACION, CONVALIDACION, ...
CanalOrigen:         PORTAL_WEB, EMAIL, PRESENCIAL, TELEFONO
Rol:                 ESTUDIANTE, COORDINADOR, PROFESOR, ADMIN
```

---

## Base de Datos: Tablas

### usuarios
```
id | nombre   | correo              | rol        | activo
---|----------|---------------------|------------|-------
1  | María    | maria@example.com   | ESTUDIANTE | true
5  | Carlos   | carlos@example.com  | COORDINADOR| true
10 | García   | garcia@example.com  | PROFESOR   | true
```

### solicitudes
```
id | descripcion          | estado       | tipo_solicitud       | solicitante_id | responsable_id | fecha_creacion
---|----------------------|--------------|----------------------|----------------|----------------|-------------------
42 | Quiero reconsiderar..| CLASIFICADA  | CAMBIO_CALIFICACION | 1              | 10             | 2026-04-14 10:30:45
```

### historial_solicitudes
```
id | solicitud_id | usuario_accion_id | accion                  | estado_anterior | estado_nuevo    | fecha
---|--------------|-------------------|-------------------------|-----------------|-----------------|-------------------
1  | 42           | 1                 | Solicitud creada        | NULL            | REGISTRADA      | 2026-04-14 10:30:45
2  | 42           | 5                 | Solicitud clasificada   | REGISTRADA      | CLASIFICADA     | 2026-04-14 10:45:00
```

---

## Las 8 Validaciones

| Validación | Error |
|------------|-------|
| JWT no válido | 401 Unauthorized |
| Usuario no existe | 404 Not Found |
| Solicitud no existe | 404 Not Found |
| Transición inválida | 422 Unprocessable Entity |
| Responsable no activo | 400 Bad Request |
| JSON malformado | 400 Bad Request |
| Error en BD | 500 Internal Server Error |
| Acceso denegado | 403 Forbidden |

---

## Archivos Importantes

```
🔐 configuracion/
  ├─ JwtUtil.java        (generar/validar tokens)
  ├─ JwtFilter.java      (validar en cada request)
  └─ SecurityConfig.java (configurar seguridad)

🎮 controller/
  └─ SolicitudController.java (endpoints REST)

💼 service/
  └─ SolicitudServiceImpl.java (lógica de negocio)

📊 repository/
  └─ SolicitudRepository.java (CRUD en BD)

🎁 domain/
  └─ Solicitud.java (entidad)

📦 dto/
  ├─ SolicitudCreateRequest.java (entrada)
  └─ SolicitudResponse.java (salida)
```

---

## Checklists: ¿Funcionará?

### Antes de crear solicitud:
- [ ] ¿JWT válido?
- [ ] ¿Usuario existe?
- [ ] ¿Datos JSON correctos?

### Después de crear:
- [ ] ¿ID asignado? (debería ser 42)
- [ ] ¿Estado es REGISTRADA?
- [ ] ¿Historial registrado?
- [ ] ¿Puedo obtener con GET?

### Para cambiar estado:
- [ ] ¿Transición válida? (REGISTRADA → CLASIFICADA OK)
- [ ] ¿Todo lo requerido está presente?
- [ ] ¿Se registró en historial?

---

## Resumen de Pasos: Crear Solicitud

```
1. Cliente: POST /api/solicitudes
2. JwtFilter: Validar token
3. Controller: Mapear request
4. Service: Obtener usuario
5. Service: Crear Solicitud
6. Repository: Guardar en BD
7. Repository: Registrar historial
8. Service: Mapear a DTO
9. Controller: Retornar JSON
10. Cliente: Recibe respuesta
```

---

## Una Línea de Código = Una Acción

| Código | Acción |
|--------|--------|
| `@PostMapping` | Escuchar GET HTTP |
| `@RequestBody` | Deserializar JSON |
| `usuarioRepository.findById()` | SELECT en BD |
| `new Solicitud()` | Crear objeto |
| `solicitudRepository.save()` | INSERT en BD |
| `registrarHistorial()` | Auditoría |
| `mapearResponse()` | DTO |
| `ResponseEntity.status()` | HTTP code |

---

## Debugging: Si falla...

1. ¿JWT falla? → Ver `JwtFilter` logs
2. ¿404? → ¿Usuario existe?
3. ¿400? → ¿JSON válido?
4. ¿Transición inválida? → ¿Estado correcto?
5. ¿BD error? → ¿Conexión correcta?

---

**Ahora ya entiendes cómo funciona el backend.** ✅

Para más detalles → `FLUJO_DATOS_EXPLICADO.md`


