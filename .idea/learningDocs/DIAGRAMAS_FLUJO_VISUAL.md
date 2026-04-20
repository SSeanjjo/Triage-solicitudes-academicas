# 🎨 Diagramas Visuales del Flujo - Gestión de Solicitudes

## 1. DIAGRAMA DE SECUENCIA: Crear Solicitud

```
┌──────────┐        ┌────────────────┐        ┌─────────┐        ┌─────────────┐        ┌──────┐
│ CLIENTE  │        │ CONTROLADOR    │        │ SERVICIO│        │ REPOSITORIO │        │  BD  │
└────┬─────┘        └────────┬───────┘        └────┬────┘        └──────┬──────┘        └──┬───┘
     │                       │                     │                    │                  │
     │ POST /api/solicitudes │                     │                    │                  │
     ├─ SolicitudCreateRequest──>│                 │                    │                  │
     │  solicitanteId=1          │                 │                    │                  │
     │                           │                 │                    │                  │
     │                           │ crear()         │                    │                  │
     │                           ├────────────────>│                    │                  │
     │                           │                 │ findById(1)        │                  │
     │                           │                 ├───────────────────>│                  │
     │                           │                 │                    │ SELECT * WHERE  │
     │                           │                 │                    │ id=1            │
     │                           │                 │                    │<─────────────────┤
     │                           │                 │<───────────────────┤                  │
     │                           │                 │ Usuario objeto     │                  │
     │                           │                 │                    │                  │
     │                           │                 │ new Solicitud()    │                  │
     │                           │                 │ (describe, user,   │                  │
     │                           │                 │  canal)            │                  │
     │                           │                 │                    │                  │
     │                           │                 │ save(solicitud)    │                  │
     │                           │                 ├───────────────────>│                  │
     │                           │                 │                    │ INSERT INTO     │
     │                           │                 │                    │ solicitudes ... │
     │                           │                 │                    ├─────────────────>│
     │                           │                 │                    │                 │ INSERT
     │                           │                 │                    │<─────────────────┤
     │                           │                 │<───────────────────┤ id=42            │
     │                           │                 │                    │                  │
     │                           │                 │ registrarHistorial()
     │                           │                 │ (acción, usuario)  │                  │
     │                           │                 │ new HistorialSolicitud()             │
     │                           │                 │ save()             │                  │
     │                           │                 ├───────────────────>│                  │
     │                           │                 │                    │ INSERT INTO     │
     │                           │                 │                    │ historial...    │
     │                           │                 │                    ├─────────────────>│
     │                           │                 │                    │                 │ INSERT
     │                           │                 │                    │<─────────────────┤
     │                           │                 │                    │                  │
     │                           │                 │ SolicitudResponse  │                  │
     │                           │<────────────────┤ (mapearResponse()) │                  │
     │<──────────────────────────┤                 │                    │                  │
     │ HTTP 201 Created          │                 │                    │                  │
     │ {id:42, estado:REGISTRADA│                 │                    │                  │
     │  ...}                     │                 │                    │                  │
     │                           │                 │                    │                  │
```

---

## 2. FLUJO DE CAMBIO DE ESTADO

```
        ┌─────────────────────────────────────────────────────────────────┐
        │          MÁQUINA DE ESTADOS DE SOLICITUD                         │
        └─────────────────────────────────────────────────────────────────┘

                ┌───────────────┐
                │  REGISTRADA   │  ← Estado inicial (cuando se crea)
                └───────┬───────┘
                        │
                        │ clasificar()
                        │ (asignar tipo + prioridad)
                        │
                        ▼
                ┌───────────────┐
                │ CLASIFICADA   │  ← Clasificada por coordinador
                └───────┬───────┘
                        │
                        │ asignar()
                        │ (asignar a responsable)
                        │
                        ▼
                ┌───────────────┐
                │ EN_ATENCION   │  ← Asignada a un responsable
                └───────┬───────┘
                        │
                        │ atender()
                        │ (marcar como atendida)
                        │
                        ▼
                ┌───────────────┐
                │   ATENDIDA    │  ← Se procesó la solicitud
                └───────┬───────┘
                        │
                        │ cerrar()
                        │ (cerrar definitivamente)
                        │
                        ▼
                ┌───────────────┐
                │    CERRADA    │  ← Estado final (sin retorno)
                └───────────────┘


        Validación en método puedeTransicionarA():
        ─────────────────────────────────────────

        if (estadoActual == REGISTRADA && nuevoEstado == CLASIFICADA) → ✅ OK
        if (estadoActual == REGISTRADA && nuevoEstado == EN_ATENCION) → ❌ ERROR
        if (estadoActual == CERRADA && cualquierEstado != CERRADA)   → ❌ ERROR

```

---

## 3. CAPAS DE LA APLICACIÓN Y FLUJO DE DATOS

```
        ┌─────────────────────────────────────────────────────┐
        │                   CAPA PRESENTACIÓN                  │
        │  (Frontend React/Vue/Angular - No en este backend)   │
        └────────────────┬────────────────────────────────────┘
                         │ HTTP Requests
                         │ JSON Payloads
                         ▼
        ┌─────────────────────────────────────────────────────┐
        │           CAPA CONTROLADOR (REST API)               │
        │     • AuthController                                │
        │     • SolicitudController                           │
        │     • UsuarioController                             │
        │                                                      │
        │  Responsabilidades:                                 │
        │  ✓ Recibir HTTP requests                           │
        │  ✓ Mapear parámetros                               │
        │  ✓ Validar tipos básicos                           │
        │  ✓ Delegar al servicio                             │
        │  ✓ Retornar HTTP responses                         │
        └────────────────┬────────────────────────────────────┘
                         │
                         │ Parámetros + DTOs
                         │
                         ▼
        ┌─────────────────────────────────────────────────────┐
        │              CAPA SERVICIO                          │
        │     • AuthServiceImpl                                │
        │     • SolicitudServiceImpl                           │
        │     • UsuarioServiceImpl                             │
        │                                                      │
        │  Responsabilidades:                                 │
        │  ✓ Lógica de negocio                               │
        │  ✓ Validaciones complejas                          │
        │  ✓ Transformación DTO ↔ Entidad                    │
        │  ✓ Orquestar múltiples operaciones                 │
        │  ✓ Manejo de excepciones                           │
        └────────────────┬────────────────────────────────────┘
                         │
        ┌────────────────┼────────────────┬────────────────┐
        │                │                │                │
        │ Repositorio    │ Repositorio    │ Repositorio    │
        │ Usuario        │ Solicitud      │ Historial      │
        │                │                │                │
        ▼                ▼                ▼
        ┌─────────────────────────────────────────────────────┐
        │            CAPA ACCESO A DATOS (DAO)               │
        │     • UsuarioRepository                             │
        │     • SolicitudRepository                           │
        │     • HistorialSolicitudRepository                 │
        │                                                      │
        │  Responsabilidades:                                 │
        │  ✓ CRUD: Create, Read, Update, Delete             │
        │  ✓ Queries personalizadas                          │
        │  ✓ Transacciones                                   │
        │  ✓ JPA/Hibernate (ORM)                             │
        └────────────────┬────────────────────────────────────┘
                         │
                         │ SQL Queries
                         │
                         ▼
        ┌─────────────────────────────────────────────────────┐
        │            CAPA BASE DE DATOS                       │
        │            PostgreSQL Server                        │
        │                                                      │
        │  Tablas:                                            │
        │  • usuarios                                         │
        │  • solicitudes                                      │
        │  • historial_solicitudes                           │
        └─────────────────────────────────────────────────────┘
```

---

## 4. TRANSFORMACIÓN DTO → ENTIDAD → DTO

```
        CLIENTE ENVÍA JSON
        ┌─────────────────────────────────────────┐
        │{                                         │
        │  "tipoSolicitud": "CAMBIO_CALIFICACION" │
        │  "descripcion": "Revisión de nota"      │
        │  "canalOrigen": "PORTAL_WEB"            │
        │}                                         │
        └────────────────┬────────────────────────┘
                         │
                         │ Jackson deserializa
                         ▼
        PASO 1: DTO DE ENTRADA
        ┌─────────────────────────────────────────┐
        │  SolicitudCreateRequest                 │
        │  ├─ tipoSolicitud: TipoSolicitud       │
        │  ├─ descripcion: String                 │
        │  └─ canalOrigen: CanalOrigen           │
        └────────────────┬────────────────────────┘
                         │
                         │ En ServiceImpl.crear()
                         │ new Solicitud(desc, user, canal)
                         │
                         ▼
        PASO 2: ENTIDAD DE DOMINIO (EN MEMORIA)
        ┌─────────────────────────────────────────┐
        │  Solicitud                              │
        │  ├─ id: null (aún no guardada)          │
        │  ├─ descripcion: "Revisión de nota"    │
        │  ├─ estado: REGISTRADA                  │
        │  ├─ solicitante: Usuario (id=1)        │
        │  ├─ canalOrigen: PORTAL_WEB            │
        │  ├─ fechaCreacion: 2026-04-14 10:30:45│
        │  └─ ...                                 │
        └────────────────┬────────────────────────┘
                         │
                         │ solicitudRepository.save()
                         │ INSERT INTO solicitudes...
                         │
                         ▼
        PASO 3: ENTIDAD DE DOMINIO (EN BD)
        ┌─────────────────────────────────────────┐
        │  Solicitud (con id asignado por BD)    │
        │  ├─ id: 42 ✓ (generado por IDENTITY)   │
        │  ├─ descripcion: "Revisión de nota"    │
        │  ├─ estado: REGISTRADA                  │
        │  ├─ solicitante_id: 1                   │
        │  ├─ canalOrigen: PORTAL_WEB            │
        │  ├─ fechaCreacion: 2026-04-14 10:30:45│
        │  └─ ...                                 │
        └────────────────┬────────────────────────┘
                         │
                         │ mapearResponse(solicitud)
                         │
                         ▼
        PASO 4: DTO DE SALIDA
        ┌─────────────────────────────────────────┐
        │  SolicitudResponse                      │
        │  ├─ id: 42                              │
        │  ├─ estado: REGISTRADA                  │
        │  ├─ prioridad: null                     │
        │  ├─ tipoSolicitud: CAMBIO_CALIFICACION│
        │  ├─ canalOrigen: PORTAL_WEB            │
        │  ├─ solicitanteId: 1                    │
        │  ├─ descripcion: "Revisión de nota"    │
        │  ├─ fechaCreacion: 2026-04-14T10:30:45│
        │  └─ ...                                 │
        └────────────────┬────────────────────────┘
                         │
                         │ Jackson serializa
                         ▼
        CLIENTE RECIBE JSON
        ┌─────────────────────────────────────────┐
        │HTTP 201 Created                         │
        │{                                         │
        │  "id": 42,                              │
        │  "estado": "REGISTRADA",                │
        │  "tipoSolicitud": "CAMBIO_CALIFICACION"│
        │  "descripcion": "Revisión de nota",     │
        │  "fechaCreacion": "2026-04-14T10:30:45"│
        │  ...                                    │
        │}                                         │
        └─────────────────────────────────────────┘
```

---

## 5. AUTENTICACIÓN JWT

```
        PASO 1: LOGIN
        ┌────────────────────────────────────────────┐
        │ POST /api/auth/login                       │
        │ {                                          │
        │   "correo": "maria@example.com",           │
        │   "password": "password123"                │
        │ }                                          │
        └────────────────┬─────────────────────────┘
                         │
                         ▼
        PASO 2: VALIDAR EN SERVICIO
        ┌────────────────────────────────────────────┐
        │ usuarioRepository.findByCorreo()           │
        │ ├─ Busca en BD: SELECT * FROM usuarios    │
        │ ├─ Verifica password (comparar hashes)    │
        │ └─ Si es válido → generar JWT             │
        └────────────────┬─────────────────────────┘
                         │
                         ▼
        PASO 3: GENERAR JWT
        ┌────────────────────────────────────────────┐
        │ JwtUtil.generarToken()                     │
        │ {                                          │
        │   "sub": "maria@example.com",  ← Subject   │
        │   "rol": "ESTUDIANTE",         ← Claim     │
        │   "iat": 1713098445,           ← Emitido  │
        │   "exp": 1713134445            ← Expira   │
        │ }                                          │
        │ Firmado con clave privada (HS256)          │
        └────────────────┬─────────────────────────┘
                         │
                         ▼ Jackson serializa
        PASO 4: RESPUESTA AL CLIENTE
        ┌────────────────────────────────────────────┐
        │ HTTP 200 OK                                │
        │ {                                          │
        │   "token": "eyJhbGciOiJIUzI1NiIsInR5c...", │
        │   "rol": "ESTUDIANTE"                      │
        │ }                                          │
        └────────────────┬─────────────────────────┘
                         │
                         ▼ Cliente almacena token
                         (localStorage, sessionStorage, etc)


        ─────────────────────────────────────────────
        USANDO EL JWT EN FUTURAS PETICIONES
        ─────────────────────────────────────────────

        PASO 5: CLIENTE ENVÍA JWT
        ┌────────────────────────────────────────────┐
        │ POST /api/solicitudes                      │
        │ Headers:                                   │
        │   Authorization: Bearer eyJhbGciOiJIUzI1...│
        │ Body:                                      │
        │   {SolicitudCreateRequest}                 │
        └────────────────┬─────────────────────────┘
                         │
                         ▼ JwtFilter intercepta
        PASO 6: VALIDAR JWT
        ┌────────────────────────────────────────────┐
        │ JwtFilter.doFilter()                       │
        │ ├─ Extrae token del header                │
        │ ├─ JwtUtil.validarToken()                 │
        │ │  ├─ Verifica firma con clave privada   │
        │ │  ├─ Verifica fecha expiración          │
        │ │  └─ Si todo OK → retorna true           │
        │ ├─ Si válido → permite que continúe       │
        │ └─ Si inválido → retorna 401 Unauthorized │
        └────────────────┬─────────────────────────┘
                         │
                         ▼ Si es válido
        PASO 7: PROCESA SOLICITUD
        ┌────────────────────────────────────────────┐
        │ SolicitudController.crear() se ejecuta     │
        │ (porque el JWT fue válido)                 │
        └────────────────────────────────────────────┘
```

---

## 6. HISTORIAL DE AUDITORÍA

```
Cada cambio en una solicitud se registra automáticamente

        CREAR SOLICITUD
        ┌─────────────────────┐
        │ POST /solicitudes   │
        └─────────────┬───────┘
                      │
                      ▼ Automático
                ┌──────────────────────────────────────┐
                │ Se guarda en historial:              │
                │ ├─ ID: 1                             │
                │ ├─ solicitud_id: 42                  │
                │ ├─ usuario_id: 1 (solicitante)       │
                │ ├─ accion: "Solicitud creada"        │
                │ ├─ estado_anterior: null             │
                │ ├─ estado_nuevo: REGISTRADA          │
                │ ├─ fecha: 2026-04-14 10:30:45        │
                │ └─ observacion: null                 │
                └──────────────────────────────────────┘

                       ↓

        CLASIFICAR SOLICITUD
        ┌─────────────────────────────────────────────┐
        │ PATCH /solicitudes/42/clasificar            │
        │ solicitud.puedeTransicionarA(CLASIFICADA)? │
        │ ✓ REGISTRADA → CLASIFICADA es válido       │
        └─────────────────────┬───────────────────────┘
                              │
                              ▼ Automático
                    ┌──────────────────────────────────────┐
                    │ Se guarda en historial:              │
                    │ ├─ ID: 2                             │
                    │ ├─ solicitud_id: 42                  │
                    │ ├─ usuario_id: 5 (coordinador)       │
                    │ ├─ accion: "Solicitud clasificada"   │
                    │ ├─ estado_anterior: REGISTRADA       │
                    │ ├─ estado_nuevo: CLASIFICADA         │
                    │ ├─ fecha: 2026-04-14 10:45:00        │
                    │ └─ observacion: null                 │
                    └──────────────────────────────────────┘

                       ↓ ... más cambios ...

        GET /solicitudes/42/historial
        ┌──────────────────────────────────────────────────────────┐
        │ Retorna lista completa de eventos:                       │
        │                                                           │
        │ [                                                        │
        │   {                                                      │
        │     "id": 1,                                            │
        │     "accion": "Solicitud creada",                       │
        │     "estadoAnterior": null,                             │
        │     "estadoNuevo": "REGISTRADA",                        │
        │     "fecha": "2026-04-14T10:30:45",                     │
        │     "usuarioId": 1                                      │
        │   },                                                    │
        │   {                                                     │
        │     "id": 2,                                            │
        │     "accion": "Solicitud clasificada",                  │
        │     "estadoAnterior": "REGISTRADA",                     │
        │     "estadoNuevo": "CLASIFICADA",                       │
        │     "fecha": "2026-04-14T10:45:00",                     │
        │     "usuarioId": 5                                      │
        │   },                                                    │
        │   ...                                                   │
        │ ]                                                        │
        │                                                           │
        │ Ventajas:                                               │
        │ ✓ Trazabilidad completa                                 │
        │ ✓ Quién hizo qué y cuándo                               │
        │ ✓ Auditoría de cambios                                  │
        │ ✓ Debugging de problemas                                │
        └──────────────────────────────────────────────────────────┘
```

---

## 7. CICLO COMPLETO: GESTIÓN DE UNA SOLICITUD

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                                                                             │
│  T0: 08:00 - ESTUDIANTE crea solicitud                                    │
│  ┌─────────────────────────────────────────────────────────────────────┐  │
│  │ POST /api/solicitudes                                              │  │
│  │ María (ID=1, ESTUDIANTE)                                           │  │
│  │ {                                                                  │  │
│  │   "tipoSolicitud": "CAMBIO_CALIFICACION",                         │  │
│  │   "descripcion": "Revisión de mi parcial",                        │  │
│  │   "canalOrigen": "PORTAL_WEB"                                     │  │
│  │ }                                                                  │  │
│  │                                                                    │  │
│  │ Resultado:                                                         │  │
│  │ ✓ Solicitud ID 42 creada                                          │  │
│  │ ✓ Estado: REGISTRADA                                              │  │
│  │ ✓ Historial: [Evento: "Solicitud creada"]                         │  │
│  └─────────────────────────────────────────────────────────────────────┘  │
│                                                                             │
│  T1: 09:00 - COORDINADOR ACADÉMICO clasifica                             │
│  ┌─────────────────────────────────────────────────────────────────────┐  │
│  │ PATCH /api/solicitudes/42/clasificar?usuarioId=5                  │  │
│  │ Carlos (ID=5, COORDINADOR)                                        │  │
│  │ {                                                                  │  │
│  │   "tipoSolicitud": "CAMBIO_CALIFICACION",                         │  │
│  │   "prioridad": "ALTA",                                            │  │
│  │   "justificacionPrioridad": "Estudiante con buen desempeño"      │  │
│  │ }                                                                  │  │
│  │                                                                    │  │
│  │ Validación:                                                        │  │
│  │ ✓ Solicitud existe? SÍ                                            │  │
│  │ ✓ REGISTRADA → CLASIFICADA? SÍ (transición válida)               │  │
│  │                                                                    │  │
│  │ Resultado:                                                         │  │
│  │ ✓ Estado: CLASIFICADA                                             │  │
│  │ ✓ Prioridad: ALTA                                                 │  │
│  │ ✓ Historial: [..., Evento: "Solicitud clasificada"]              │  │
│  └─────────────────────────────────────────────────────────────────────┘  │
│                                                                             │
│  T2: 10:00 - COORDINADOR ASIGNA RESPONSABLE                             │
│  ┌─────────────────────────────────────────────────────────────────────┐  │
│  │ PATCH /api/solicitudes/42/asignar?usuarioId=5                    │  │
│  │ Carlos (ID=5, COORDINADOR)                                        │  │
│  │ {                                                                  │  │
│  │   "responsableId": 10                                             │  │
│  │ }                                                                  │  │
│  │                                                                    │  │
│  │ Validaciones:                                                      │  │
│  │ ✓ Usuario 10 existe? SÍ                                           │  │
│  │ ✓ Usuario 10 activo? SÍ                                           │  │
│  │ ✓ CLASIFICADA → EN_ATENCION? SÍ (transición válida)              │  │
│  │                                                                    │  │
│  │ Resultado:                                                         │  │
│  │ ✓ Estado: EN_ATENCION                                             │  │
│  │ ✓ Responsable: Profesor García (ID=10)                            │  │
│  │ ✓ Historial: [..., Evento: "Responsable asignado"]                │  │
│  └─────────────────────────────────────────────────────────────────────┘  │
│                                                                             │
│  T3: 14:00 - PROFESOR ATIENDE SOLICITUD                                  │
│  ┌─────────────────────────────────────────────────────────────────────┐  │
│  │ PATCH /api/solicitudes/42/atender?usuarioId=10                   │  │
│  │ García (ID=10, PROFESOR)                                          │  │
│  │ {                                                                  │  │
│  │   "comentario": "Se revisó el parcial. Cálculo correcto es 4.2"   │  │
│  │ }                                                                  │  │
│  │                                                                    │  │
│  │ Validación:                                                        │  │
│  │ ✓ EN_ATENCION → ATENDIDA? SÍ (transición válida)                │  │
│  │                                                                    │  │
│  │ Resultado:                                                         │  │
│  │ ✓ Estado: ATENDIDA                                                │  │
│  │ ✓ Historial: [..., Evento: "Solicitud atendida" con comentario]  │  │
│  └─────────────────────────────────────────────────────────────────────┘  │
│                                                                             │
│  T4: 15:30 - COORDINADOR CIERRA SOLICITUD                               │
│  ┌─────────────────────────────────────────────────────────────────────┐  │
│  │ PATCH /api/solicitudes/42/cerrar?usuarioId=5                     │  │
│  │ Carlos (ID=5, COORDINADOR)                                        │  │
│  │ {                                                                  │  │
│  │   "comentarioCierre": "María notificada. Caso resuelto."          │  │
│  │ }                                                                  │  │
│  │                                                                    │  │
│  │ Validación:                                                        │  │
│  │ ✓ ATENDIDA → CERRADA? SÍ (transición válida)                    │  │
│  │                                                                    │  │
│  │ Resultado:                                                         │  │
│  │ ✓ Estado: CERRADA (sin posibilidad de retorno)                    │  │
│  │ ✓ Observación de Cierre: "María notificada..."                    │  │
│  │ ✓ Historial: [..., Evento: "Solicitud cerrada"]                   │  │
│  └─────────────────────────────────────────────────────────────────────┘  │
│                                                                             │
│  HISTORIAL FINAL (GET /api/solicitudes/42/historial)                      │
│  ┌─────────────────────────────────────────────────────────────────────┐  │
│  │ Evento 1: 08:00 "Solicitud creada"                                │  │
│  │           Usuario: María (ESTUDIANTE)                             │  │
│  │           Estado: null → REGISTRADA                               │  │
│  │                                                                    │  │
│  │ Evento 2: 09:00 "Solicitud clasificada"                           │  │
│  │           Usuario: Carlos (COORDINADOR)                           │  │
│  │           Estado: REGISTRADA → CLASIFICADA                        │  │
│  │           Prioridad asignada: ALTA                                │  │
│  │                                                                    │  │
│  │ Evento 3: 10:00 "Responsable asignado"                            │  │
│  │           Usuario: Carlos (COORDINADOR)                           │  │
│  │           Estado: CLASIFICADA → EN_ATENCION                       │  │
│  │           Asignado a: García (PROFESOR)                           │  │
│  │                                                                    │  │
│  │ Evento 4: 14:00 "Solicitud atendida"                              │  │
│  │           Usuario: García (PROFESOR)                              │  │
│  │           Estado: EN_ATENCION → ATENDIDA                         │  │
│  │           Comentario: "Se revisó el parcial..."                   │  │
│  │                                                                    │  │
│  │ Evento 5: 15:30 "Solicitud cerrada"                               │  │
│  │           Usuario: Carlos (COORDINADOR)                           │  │
│  │           Estado: ATENDIDA → CERRADA                             │  │
│  │           Observación: "María notificada..."                      │  │
│  └─────────────────────────────────────────────────────────────────────┘  │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 8. ESTRUCTURA DE CARPETAS Y RESPONSABILIDADES

```
backend/
│
├── src/main/java/co/edu/uniquindio/gestion_solicitudes/
│   │
│   ├── 🔐 configuracion/
│   │   ├── JwtUtil.java
│   │   │   └─ Genera y valida tokens JWT
│   │   ├── JwtFilter.java
│   │   │   └─ Intercepta requests, valida JWT
│   │   └── SecurityConfig.java
│   │       └─ Configura Spring Security
│   │
│   ├── 🎮 controller/
│   │   ├── AuthController.java
│   │   │   ├─ POST /api/auth/login
│   │   │   └─ POST /api/auth/registro
│   │   ├── SolicitudController.java
│   │   │   ├─ POST /api/solicitudes (crear)
│   │   │   ├─ GET /api/solicitudes (listar)
│   │   │   ├─ GET /api/solicitudes/{id}
│   │   │   ├─ PATCH /api/solicitudes/{id}/clasificar
│   │   │   ├─ PATCH /api/solicitudes/{id}/asignar
│   │   │   ├─ PATCH /api/solicitudes/{id}/atender
│   │   │   ├─ PATCH /api/solicitudes/{id}/cerrar
│   │   │   └─ GET /api/solicitudes/{id}/historial
│   │   └── UsuarioController.java
│   │       └─ Gestión de usuarios
│   │
│   ├── 💼 service/
│   │   ├── AuthService.java (interfaz)
│   │   ├── SolicitudService.java (interfaz)
│   │   ├── UsuarioService.java (interfaz)
│   │   └── Implementar/
│   │       ├── AuthServiceImpl.java
│   │       │   └─ Lógica de autenticación
│   │       ├── SolicitudServiceImpl.java
│   │       │   └─ Lógica de solicitudes
│   │       └── UsuarioServiceImpl.java
│   │           └─ Lógica de usuarios
│   │
│   ├── 📊 repository/
│   │   ├── UsuarioRepository.java
│   │   │   └─ SELECT/INSERT/UPDATE/DELETE usuarios
│   │   ├── SolicitudRepository.java
│   │   │   └─ SELECT/INSERT/UPDATE/DELETE solicitudes
│   │   └── HistorialSolicitudRepository.java
│   │       └─ SELECT/INSERT historial
│   │
│   ├── 🎁 domain/
│   │   ├── Usuario.java (Entidad JPA)
│   │   ├── Solicitud.java (Entidad JPA)
│   │   ├── HistorialSolicitud.java (Entidad JPA)
│   │   ├── EstadoSolicitud.java (Enum)
│   │   ├── PrioridadSolicitud.java (Enum)
│   │   ├── TipoSolicitud.java (Enum)
│   │   ├── CanalOrigen.java (Enum)
│   │   └── Rol.java (Enum)
│   │
│   ├── 📦 dto/
│   │   ├── LoginRequest.java
│   │   ├── LoginResponse.java
│   │   ├── UsuarioCreateRequest.java
│   │   ├── UsuarioResponse.java
│   │   ├── SolicitudCreateRequest.java (entrada)
│   │   ├── SolicitudResponse.java (salida)
│   │   ├── ClasificacionRequest.java
│   │   ├── AsignacionRequest.java
│   │   ├── AtenderRequest.java
│   │   ├── CierreRequest.java
│   │   └── HistorialEventoResponse.java
│   │
│   ├── ❌ exception/
│   │   └─ (Manejo de errores)
│   │
│   └── GestionSolicitudesApplication.java
│       └─ Clase principal (@SpringBootApplication)
│
└── resources/
    ├── application.properties
    │   └─ Configuración (BD, puerto, etc)
    └── ...
```

---

## Resumen Visual Simple

```
CLIENTE
  ↓ HTTP Request (JSON)
  ↓
CONTROLADOR (recibe y valida)
  ↓
SERVICIO (lógica de negocio)
  ↓
REPOSITORIO (interactúa con BD)
  ↓
BASE DE DATOS (guarda datos)
  ↓
REPOSITORIO (retorna datos)
  ↓
SERVICIO (transforma a DTO)
  ↓
CONTROLADOR (serializa JSON)
  ↓
CLIENTE
  ↑ HTTP Response (JSON)
```

