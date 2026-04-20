# 🚀 Cheat Sheet - Resumen Rápido

## 📌 La Pregunta Clave: ¿Cómo funciona crear una solicitud desde cero?

### Visión de 5 segundos:
```
Cliente envía JSON → Controller recibe → Service procesa → Repository guarda en BD → Respuesta JSON
```

---

## 🔄 Los 3 Patrones Principales

### 1️⃣ DTO (Data Transfer Object)
**Qué es**: Objeto que viaja por la red

```java
// INPUT (lo que envía cliente)
SolicitudCreateRequest {
  tipoSolicitud: "CAMBIO_CALIFICACION",
  descripcion: "...",
  canalOrigen: "PORTAL_WEB"
}

// OUTPUT (lo que retorna servidor)
SolicitudResponse {
  id: 42,
  estado: "REGISTRADA",
  tipoSolicitud: "CAMBIO_CALIFICACION",
  ...
}
```

**Por qué**: Protege la estructura interna del sistema

---

### 2️⃣ Entidad de Dominio
**Qué es**: Objeto que se guarda en la BD

```java
Solicitud {
  id: 42,
  descripcion: "...",
  estado: REGISTRADA,
  fechaCreacion: LocalDateTime,
  solicitante: Usuario,
  responsable: Usuario,
  historial: List<HistorialSolicitud>
}
```

**Relaciones**:
- Solicitud → Usuario (solicitante)
- Solicitud → Usuario (responsable)
- Solicitud → HistorialSolicitud (1 a muchos)

---

### 3️⃣ Repository (CRUD)
**Qué es**: Interfaz que hace queries a la BD

```java
SolicitudRepository extends JpaRepository<Solicitud, Long> {
  // Métodos heredados:
  save()      // INSERT o UPDATE
  findById()  // SELECT por ID
  findAll()   // SELECT todos
  delete()    // DELETE
  
  // Métodos custom:
  findBySolicitudId()  // Query personalizado
}
```

---

## 🎯 5 Estados de una Solicitud

```
REGISTRADA → CLASIFICADA → EN_ATENCION → ATENDIDA → CERRADA
   ↑              ↑              ↑           ↑          ↑
 Creada    Clasificada    Asignada    Procesada   Finalizada
```

**Regla**: Solo puede ir de un estado al siguiente (no saltar)

```java
solicitud.puedeTransicionarA(nuevoEstado) // true/false
```

---

## 📊 Tabla de Responsabilidades por Capa

| Capa | Responsabilidad | Archivos |
|------|-----------------|----------|
| **Controller** | Recibir HTTP, mapear parámetros | `*Controller.java` |
| **Service** | Lógica de negocio, validaciones | `*ServiceImpl.java` |
| **Repository** | CRUD en BD | `*Repository.java` |
| **Domain** | Entidades, enums, validaciones | `domain/` |
| **DTO** | Transferencia de datos | `dto/` |

---

## 🔐 JWT (Autenticación)

### Login
```
POST /api/auth/login
→ Enviar correo + contraseña
← Recibir TOKEN
```

### Usar Token
```
Todas las peticiones:
Header: Authorization: Bearer <TOKEN>
→ JwtFilter valida
← Si es válido, continúa
← Si es inválido, 401 Unauthorized
```

### Contenido del Token
```json
{
  "sub": "maria@example.com",  // Usuario
  "rol": "ESTUDIANTE",          // Rol
  "iat": 1713098445,           // Emitido
  "exp": 1713134445            // Expira en 10 horas
}
```

---

## 🔍 El Flujo Crear Solicitud (Paso a Paso)

```
1. Cliente: POST /api/solicitudes?solicitanteId=1
   + SolicitudCreateRequest JSON
   + Authorization: Bearer JWT

2. JwtFilter: Valida JWT
   ✅ Válido → continúa
   ❌ Inválido → 401

3. SolicitudController.crear()
   - Recibe SolicitudCreateRequest
   - Recibe solicitanteId
   - Llama: solicitudService.crear()

4. SolicitudServiceImpl.crear()
   a) usuarioRepository.findById(solicitanteId)
      → SELECT * FROM usuarios WHERE id=1
      → Retorna Usuario
   
   b) new Solicitud(descripcion, usuario, canal)
      → Crea objeto en memoria
      → Estado = REGISTRADA
      → Fecha = NOW()
   
   c) solicitudRepository.save(solicitud)
      → INSERT INTO solicitudes(...)
      → BD asigna ID (supongamos 42)
      → Retorna solicitud con ID
   
   d) registrarHistorial(solicitud, usuario, "Solicitud creada", ...)
      → INSERT INTO historial_solicitudes(...)
   
   e) mapearResponse(solicitud)
      → Convierte Solicitud → SolicitudResponse
      → Retorna DTO

5. SolicitudController retorna
   → HTTP 201 Created
   → JSON con SolicitudResponse

6. Cliente recibe respuesta
   → ID = 42
   → Estado = REGISTRADA
   → Otros campos...
```

---

## 🎁 Estructura de DTOs

### Request DTOs (Lo que cliente envía)
```java
SolicitudCreateRequest {
  tipoSolicitud: TipoSolicitud,
  descripcion: String,
  canalOrigen: CanalOrigen
}

ClasificacionRequest {
  tipoSolicitud: TipoSolicitud,
  prioridad: PrioridadSolicitud,
  justificacionPrioridad: String
}

AsignacionRequest {
  responsableId: Long
}

AtenderRequest {
  comentario: String
}

CierreRequest {
  comentarioCierre: String
}
```

### Response DTOs (Lo que servidor retorna)
```java
SolicitudResponse {
  id: Long,
  estado: EstadoSolicitud,
  prioridad: PrioridadSolicitud,
  tipoSolicitud: TipoSolicitud,
  canalOrigen: CanalOrigen,
  solicitanteId: Long,
  responsableId: Long,
  descripcion: String,
  justificacionPrioridad: String,
  observacionCierre: String,
  fechaCreacion: LocalDateTime,
  fechaActualizacion: LocalDateTime
}

HistorialEventoResponse {
  id: Long,
  solicitudId: Long,
  usuarioId: Long,
  accion: String,
  estadoAnterior: EstadoSolicitud,
  estadoNuevo: EstadoSolicitud,
  fecha: LocalDateTime,
  comentario: String
}
```

---

## 📈 Flujo General (Todavía SIMPLIFICADO)

```
┌─────────────────────────────┐
│  CLIENTE (Frontend/Postman) │
└──────────────┬──────────────┘
               │
        HTTP Request (JSON)
               │
        ┌──────▼──────┐
        │  JwtFilter  │ ← Valida token
        └──────┬──────┘
               │
    ┌──────────▼──────────┐
    │ SolicitudController │ ← Mapea entrada
    └──────────┬──────────┘
               │
    ┌──────────▼──────────────┐
    │ SolicitudServiceImpl    │ ← Lógica negocio
    └──────────┬──────────────┘
               │
    ┌──────────┴──────────────┬──────────────┐
    │                         │              │
 ┌──▼───────────┐  ┌─────────▼────┐  ┌────▼──────────┐
 │UsuarioRepo  │  │SolicitudRepo │  │HistorialRepo │
 └──┬───────────┘  └──────┬───────┘  └────┬──────────┘
    │                     │              │
    └─────────┬───────────┴──────────────┘
              │
              ▼
        ┌──────────────┐
        │ BASE DE DATOS│
        └──────────────┘
              │
    Retorna datos a repository
              │
    mapearResponse() (DTO)
              │
    ┌─────────▼─────────┐
    │ SolicitudResponse │
    └─────────┬─────────┘
              │
        HTTP Response (JSON)
              │
        ┌─────▼──────────────┐
        │ CLIENTE (recibe)   │
        └────────────────────┘
```

---

## 🔗 Relaciones en la BD

```
usuarios (1)
    │
    ├──→ solicitudes (1) → (M) historial_solicitudes
    │      ↑ solicitante
    │      └ responsable
    │
    └──→ solicitudes (0..1)  ← Usuario puede ser responsable
```

**SQL:**
```sql
ALTER TABLE solicitudes 
ADD CONSTRAINT fk_solicitante 
FOREIGN KEY (solicitante_id) REFERENCES usuarios(id);

ALTER TABLE solicitudes 
ADD CONSTRAINT fk_responsable 
FOREIGN KEY (responsable_id) REFERENCES usuarios(id);

ALTER TABLE historial_solicitudes 
ADD CONSTRAINT fk_solicitud 
FOREIGN KEY (solicitud_id) REFERENCES solicitudes(id);

ALTER TABLE historial_solicitudes 
ADD CONSTRAINT fk_usuario_accion 
FOREIGN KEY (usuario_accion_id) REFERENCES usuarios(id);
```

---

## 🛠️ Endpoints Principales

| Método | Endpoint | Qué hace |
|--------|----------|----------|
| POST | `/api/auth/login` | Login (obtener JWT) |
| POST | `/api/auth/registro` | Registrar usuario |
| POST | `/api/solicitudes` | Crear solicitud ← **ESTE ES EL EJEMPLO** |
| GET | `/api/solicitudes` | Listar solicitudes (con filtros) |
| GET | `/api/solicitudes/{id}` | Obtener una solicitud |
| PATCH | `/api/solicitudes/{id}/clasificar` | Clasificar (REGISTRADA → CLASIFICADA) |
| PATCH | `/api/solicitudes/{id}/asignar` | Asignar responsable (CLASIFICADA → EN_ATENCION) |
| PATCH | `/api/solicitudes/{id}/atender` | Atender (EN_ATENCION → ATENDIDA) |
| PATCH | `/api/solicitudes/{id}/cerrar` | Cerrar (ATENDIDA → CERRADA) |
| GET | `/api/solicitudes/{id}/historial` | Obtener historial de cambios |

---

## 🎨 Enums (Tipos predefinidos)

### EstadoSolicitud
```java
REGISTRADA, CLASIFICADA, EN_ATENCION, ATENDIDA, CERRADA
```

### PrioridadSolicitud
```java
BAJA, MEDIA, ALTA
```

### TipoSolicitud
```java
CAMBIO_CALIFICACION, CONVALIDACION, RECLAMO_ACADEMICO, ...
```

### CanalOrigen
```java
PORTAL_WEB, EMAIL, PRESENCIAL, TELEFONO
```

### Rol
```java
ESTUDIANTE, COORDINADOR, PROFESOR, ADMIN
```

---

## 💾 Archivos Clave

| Archivo | Qué hace |
|---------|----------|
| `SolicitudController.java` | Endpoints POST/GET/PATCH |
| `SolicitudServiceImpl.java` | Lógica de crear, clasificar, etc |
| `SolicitudRepository.java` | Queries a la BD |
| `Solicitud.java` | Entidad (modelo dominio) |
| `SolicitudCreateRequest.java` | DTO de entrada |
| `SolicitudResponse.java` | DTO de salida |
| `HistorialSolicitud.java` | Auditoría de cambios |
| `JwtUtil.java` | Generar/validar tokens |
| `JwtFilter.java` | Validar JWT en cada request |

---

## 🎯 Concepto Clave: El Mapeo

```
JSON String          DTO Object            Domain Entity         JSON Response
{"tipo": "...",      →  Solicitud      →   INSERT/UPDATE   →    {"id": 42,
 "desc": "..."}         CreateRequest          en BD                "estado":
                                                              "REGISTRADA", ...}
                        (Entrada)              (Persistencia)       (Salida)
```

El **Service** es el puente que conecta todo:
```java
DTO Input → Validar → Transformar → Guardar en BD → Leer de BD → DTO Output
```

---

## ⚡ Reglas de Oro

1. **DTOs = Seguridad**: Nunca devuelves las entidades directamente
2. **Services = Lógica**: Todo lo importante ocurre aquí
3. **Repositories = BD**: Solo CRUD, nada de lógica
4. **Validaciones**: En el nivel más alto posible (en Service)
5. **Historial**: Registra CADA cambio importante
6. **Estados**: No salten transiciones, siempre válida
7. **JWT**: Siempre en el header `Authorization: Bearer <token>`

---

## 🔍 Para Debuggear

Si algo falla, sigue este orden:

1. ¿JWT válido? → Ver logs de `JwtFilter`
2. ¿Controller recibe datos? → Ver `@RequestBody` y `@RequestParam`
3. ¿Service ejecuta? → Ver logs en `ServiceImpl`
4. ¿BD guarda? → Ver logs de SQL (hibernate)
5. ¿Respuesta correcta? → Ver mapeo a DTO

---

## 📚 Documentación Completa

He creado 3 archivos en la raíz del proyecto:

1. **FLUJO_DATOS_EXPLICADO.md** - Explicación completa y detallada
2. **DIAGRAMAS_FLUJO_VISUAL.md** - Diagramas de secuencia y ASCII
3. **EJEMPLOS_CODIGO_ANOTADO.md** - Código con comentarios línea por línea

¡Revísalos en el orden que prefieras! 🚀

