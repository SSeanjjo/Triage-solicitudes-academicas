# 🧠 Mapa Mental del Backend

## El Concepto Central: SOLICITUD

```
                          ┌─────────────┐
                          │ SOLICITUD   │
                          │  (id = 42)  │
                          └──────┬──────┘
                                 │
                    ┌────────────┼────────────┐
                    │            │            │
                    ▼            ▼            ▼
              ┌──────────┐  ┌─────────┐  ┌──────────────┐
              │ SOLICITANTE │ ESTADO  │  │ RESPONSABLE  │
              │(María, id=1)│(REGISTRA│  │(García,id=10)│
              │             │  DA)    │  │              │
              └──────────┘  └────┬────┘  └──────────────┘
                                 │
                     ┌───────────┼───────────┐
                     │           │           │
                    REGISTRADA   │        CERRADA
                        ↓        │         ▲
                        └────→ CLASIFICADA
                             ↓
                        EN_ATENCION
                             ↓
                          ATENDIDA
                             ↓
                   (Registra en HISTORIAL)
```

---

## El Flujo: Entrada → Procesamiento → Salida

```
                    ENTRADA
                      │
        ┌─────────────▼─────────────┐
        │  JSON: SolicitudCreateRequest  │
        │  {tipo, descripcion, canal}    │
        └─────────────┬─────────────┘
                      │
                      ▼
            ┌─────────────────────┐
            │  VALIDACIÓN         │
            │  - JWT válido?      │
            │  - Usuario existe?  │
            │  - Datos correctos? │
            └──────────┬──────────┘
                       │ ✅ TODO OK
                       ▼
            ┌─────────────────────┐
            │  PROCESAMIENTO      │
            │  - Crear Solicitud  │
            │  - Guardar en BD    │
            │  - Registrar evento │
            └──────────┬──────────┘
                       │
                       ▼
        ┌─────────────────────────┐
        │  JSON: SolicitudResponse    │
        │  {id, estado, ...}         │
        └─────────────────────────┘
                      │
                    SALIDA
```

---

## Las 3 Capas Explicadas

### CAPA 1: CONTROLADOR
```
┌────────────────────────────────┐
│     SOLICITUD CONTROLLER       │
├────────────────────────────────┤
│  @PostMapping                  │
│  public ResponseEntity<>       │
│    crear(@RequestBody request, │
│          @RequestParam id)     │
│                                │
│  → Recibe HTTP                 │
│  → Deserializa JSON            │
│  → Llama service.crear()       │
│  → Retorna HTTP 201            │
└────────────────────────────────┘
```

### CAPA 2: SERVICIO
```
┌────────────────────────────────┐
│   SOLICITUD SERVICE IMPL       │
├────────────────────────────────┤
│  public SolicitudResponse      │
│    crear(request, id)          │
│                                │
│  1. Obtener usuario            │
│  2. Crear solicitud            │
│  3. Guardar en BD              │
│  4. Registrar historial        │
│  5. Mapear a DTO               │
│  6. Retornar                   │
└────────────────────────────────┘
```

### CAPA 3: REPOSITORIO
```
┌────────────────────────────────┐
│  SOLICITUD REPOSITORY          │
├────────────────────────────────┤
│  UsuarioRepository.findById()  │
│  SolicitudRepository.save()    │
│  HistorialRepository.save()    │
│                                │
│  → Ejecuta SQL                 │
│  → Retorna entidades           │
└────────────────────────────────┘
```

---

## Base de Datos: Cómo Se Conectan

```
usuarios
  ↑
  │ 1
  │
solicitudes
  │ 1
  │
  └──→ historial_solicitudes


Ejemplo concreto:
─────────────────

Usuario María (id=1)
  ├── Solicitud #42 (solicitante)
  │    └── Evento 1: "Creada"
  │    └── Evento 2: "Clasificada"
  │    └── Evento 3: "Asignada"
  │    └── Evento 4: "Atendida"
  │    └── Evento 5: "Cerrada"
  │
Usuario García (id=10)
  └── Solicitud #42 (responsable)
       └── Evento 3: "Asignada a mí"
       └── Evento 4: "Atendida por mí"
```

---

## Los Objetos en Su Contexto

```
MUNDO REAL           CÓDIGO                    BD
──────────────────────────────────────────────────
                     SolicitudCreateRequest
Cliente              (DTO - entrada)
  │                        │
  └─ POST JSON             │
                           ▼
                     Solicitud (Entity)      (en memoria)
                           │
                           │ save()
                           ▼
                                         INSERT INTO solicitudes
                                         (tabla "solicitudes")
                                              │
                                              ▼
                                         SELECT * FROM...
                                              │
                                              ▼
                     Solicitud (Entity)      (desde BD)
                           │
                     mapearResponse()
                           │
                           ▼
                     SolicitudResponse
                     (DTO - salida)
                           │
                     HTTP 201 JSON
                           │
Cliente <─────────────────┘
```

---

## El Viaje del Usuario

```
1. USUARIO REGISTRADO
   └─ POST /api/auth/registro
      {correo, password, nombre, rol}
      → Usuario guardado en BD

2. USUARIO LOGIN
   └─ POST /api/auth/login
      {correo, password}
      → Retorna JWT (token)

3. USUARIO CREA SOLICITUD
   └─ POST /api/solicitudes
      Authorization: Bearer JWT
      {tipoSolicitud, descripcion, canalOrigen}
      → Solicitud #42 creada

4. COORDINADOR CLASIFICA
   └─ PATCH /api/solicitudes/42/clasificar
      Authorization: Bearer JWT
      {tipo, prioridad, justificacion}
      → Solicitud estado: CLASIFICADA

5. COORDINADOR ASIGNA
   └─ PATCH /api/solicitudes/42/asignar
      Authorization: Bearer JWT
      {responsableId: 10}
      → Solicitud estado: EN_ATENCION

6. PROFESOR ATIENDE
   └─ PATCH /api/solicitudes/42/atender
      Authorization: Bearer JWT
      {comentario}
      → Solicitud estado: ATENDIDA

7. COORDINADOR CIERRA
   └─ PATCH /api/solicitudes/42/cerrar
      Authorization: Bearer JWT
      {comentarioCierre}
      → Solicitud estado: CERRADA

8. USUARIO VE HISTORIAL
   └─ GET /api/solicitudes/42/historial
      Authorization: Bearer JWT
      → Array de eventos
```

---

## Máquina de Estados Visual

```
                    ┌─────────────────┐
                    │   REGISTRADA    │ ← INICIO
                    └────────┬────────┘
                             │
                    (método: clasificar())
                             │
                    ┌────────▼────────┐
                    │  CLASIFICADA    │
                    └────────┬────────┘
                             │
                    (método: asignar())
                             │
                    ┌────────▼────────┐
                    │  EN_ATENCION    │
                    └────────┬────────┘
                             │
                    (método: atender())
                             │
                    ┌────────▼────────┐
                    │    ATENDIDA     │
                    └────────┬────────┘
                             │
                    (método: cerrar())
                             │
                    ┌────────▼────────┐
                    │     CERRADA     │ ← FIN
                    └─────────────────┘
                             │
                    (sin retorno ❌)


Validación: solicitud.puedeTransicionarA(nuevoEstado)
├─ REGISTRADA → CLASIFICADA ✅
├─ REGISTRADA → EN_ATENCION ❌
├─ CLASIFICADA → REGISTRADA ❌
├─ EN_ATENCION → CLASIFICADA ❌
└─ CERRADA → CUALQUIER ❌
```

---

## Seguridad: JWT

```
CLIENTE                                SERVER
  │                                      │
  │ POST /api/auth/login                │
  │ {correo, password}                  │
  ├─────────────────────────────────────>
  │                            buscar usuario
  │                            validar password
  │                            generar JWT:
  │                            {sub: correo,
  │                             rol: rol,
  │                             iat: now,
  │                             exp: now+10h}
  │                            firmar con clave
  │<─ {"token": "eyJ...", "rol": "ESTUDIANTE"}
  │
  │ Almacenar token en localStorage
  │
  │ POST /api/solicitudes                │
  │ Authorization: Bearer eyJ...         │
  │ {tipoSolicitud, ...}                 │
  ├─────────────────────────────────────>
  │                            JwtFilter:
  │                            1. Extrae token
  │                            2. Valida firma
  │                            3. Valida exp
  │                            ✅ OK → continúa
  │                            ❌ FAIL → 401
  │
  │<─ {"id": 42, "estado": "REGISTRADA"}
```

---

## Datos en Movimiento

```
CLIENTE FRONTEND
   │
   │ JSON: {"tipo": "...", "descripcion": "..."}
   │
   ▼─────► CONTROLLER (desserializa)
           │
           └─► DTO: SolicitudCreateRequest
               │
               ▼─────► SERVICE
                       │
                       ├─► BD: findById(usuario)
                       │
                       ├─► Crea: Entity Solicitud
                       │
                       ├─► BD: save(solicitud)
                       │
                       ├─► BD: save(historial)
                       │
                       └─► Mapea: Entity → DTO
                           │
                           └─► SolicitudResponse
                               │
                               ▼─────► CONTROLLER (serializa)
                                       │
                                       └─► JSON
                                           │
                                           ▼
                                       CLIENTE FRONTEND
```

---

## Cuando Ocurre Cada Cosa

```
TIEMPO    EVENTO                    ESTADO           QUIÉN
─────────────────────────────────────────────────────────────
08:00     Crear solicitud           REGISTRADA       María
08:01     Historial: "Creada"       REGISTRADA       Sistema
 │
09:00     Clasificar                CLASIFICADA      Carlos
09:01     Historial: "Clasificada"  CLASIFICADA      Sistema
 │
10:00     Asignar responsable       EN_ATENCION      Carlos
10:01     Historial: "Asignada"     EN_ATENCION      Sistema
 │
14:00     Atender                   ATENDIDA         García
14:01     Historial: "Atendida"     ATENDIDA         Sistema
 │
15:00     Cerrar                    CERRADA          Carlos
15:01     Historial: "Cerrada"      CERRADA          Sistema
          (FIN)
```

---

## Reglas del Juego (Validaciones)

```
┌─────────────────────────────────────────────────────┐
│ REGLA 1: JWT VÁLIDO O RECHAZAR                     │
│ └─ Si token inválido → 401 Unauthorized            │
├─────────────────────────────────────────────────────┤
│ REGLA 2: USUARIO DEBE EXISTIR                      │
│ └─ Si user_id no existe → 404 Not Found            │
├─────────────────────────────────────────────────────┤
│ REGLA 3: TRANSICIÓN DE ESTADOS VÁLIDA              │
│ └─ Si no puedeTransicionarA() → 422 Error          │
├─────────────────────────────────────────────────────┤
│ REGLA 4: RESPONSABLE DEBE ESTAR ACTIVO             │
│ └─ Si responsable.activo = false → 400 Error       │
├─────────────────────────────────────────────────────┤
│ REGLA 5: REGISTRAR CADA CAMBIO                     │
│ └─ Cada transición → new HistorialSolicitud()      │
└─────────────────────────────────────────────────────┘
```

---

## La Responsabilidad de Cada Componente

```
┌────────────────────────────────────────────────────────┐
│                   CONTROLLER                           │
│  Responsable de: HTTP protocol                        │
│  ├─ Recibir requests                                  │
│  ├─ Mapear parámetros                                │
│  ├─ Serializar respuestas                            │
│  └─ Retornar HTTP codes                              │
└────────────────────────────────────────────────────────┘
         │
         │ Llama
         ▼
┌────────────────────────────────────────────────────────┐
│                   SERVICE                             │
│  Responsable de: Lógica de negocio                   │
│  ├─ Validaciones                                     │
│  ├─ Transformaciones DTO ↔ Entity                    │
│  ├─ Orquestar múltiples operaciones                  │
│  └─ Registrar eventos                                │
└────────────────────────────────────────────────────────┘
         │
         │ Usa
         ▼
┌────────────────────────────────────────────────────────┐
│                REPOSITORY (DAO)                        │
│  Responsable de: CRUD en BD                           │
│  ├─ SELECT                                            │
│  ├─ INSERT                                            │
│  ├─ UPDATE                                            │
│  └─ DELETE                                            │
└────────────────────────────────────────────────────────┘
         │
         │ Accede
         ▼
┌────────────────────────────────────────────────────────┐
│                   BD (PostgreSQL)                      │
│  Responsable de: Almacenar datos                      │
│  ├─ Tablas                                            │
│  ├─ Relaciones                                        │
│  └─ Integridad                                        │
└────────────────────────────────────────────────────────┘
```

---

## Resumen Mental

```
┌──────────────────────────────────────────────┐
│  SOLICITUD = Unidad de Trabajo Principal    │
├──────────────────────────────────────────────┤
│  USUARIO = Quién hace las cosas             │
├──────────────────────────────────────────────┤
│  ESTADO = Dónde está la solicitud           │
├──────────────────────────────────────────────┤
│  HISTORIAL = Registro de todo lo que pasó   │
├──────────────────────────────────────────────┤
│  DTO = Lo que viaja por la red              │
├──────────────────────────────────────────────┤
│  ENTIDAD = Lo que se guarda en BD           │
├──────────────────────────────────────────────┤
│  CONTROLLER = Recepcionista                 │
├──────────────────────────────────────────────┤
│  SERVICE = Gerente                          │
├──────────────────────────────────────────────┤
│  REPOSITORY = Empleado de BD                │
├──────────────────────────────────────────────┤
│  JWT = Credencial de seguridad              │
└──────────────────────────────────────────────┘
```

---

**¡Ahora visualizas mentalmente cómo funciona el backend!** 🧠


