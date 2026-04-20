# 🎓 Explicación Ultra-Simple del Backend

## Si tu backend fuera una pizzería 🍕

### Analógía:

```
CLIENTE:       "Quiero una pizza Margarita"
                        │
                        ▼
RECEPCIONISTA: "Voy a apuntar tu orden"  ← CONTROLADOR
(SolicitudController)  - Tu nombre
                       - Tipo de pizza
                       - Tamaño
                        │
                        ▼
GERENTE:       "Voy a validar y procesar"  ← SERVICIO
(SolicitudServiceImpl)  - ¿Cliente válido?
                       - ¿Pizzería abierta?
                       - ¿Tengo ingredientes?
                        │
                        ▼
COCINERO:      "Voy a guardar en registro"  ← REPOSITORIO
(SolicitudRepository)  - Escribo en libro de órdenes
                       - La orden queda: "Orden #42: Margarita"
                        │
                        ▼
CLIENTE:       "¿Mi orden está lista?"
               "Orden #42: Margarita está en horno"
```

---

## La Solicitud Académica es lo mismo:

```
ESTUDIANTE:
"Quiero cambio de calificación en Cálculo"
        │
        ▼ (JSON HTTP POST)

CONTROLADOR:
- Recibe la solicitud
- Valida que el JSON sea correcto
- Dice: "Ok, voy a procesarla"
        │
        ▼

SERVICIO:
1. ¿El estudiante existe?
2. Crear objeto Solicitud
3. Guardar en BD
4. Registrar evento en historial
5. Retornar respuesta
        │
        ▼

REPOSITORIO:
- Ejecuta: INSERT INTO solicitudes(...)
- BD asigna ID = 42
- Retorna el objeto guardado
        │
        ▼

RESPUESTA AL ESTUDIANTE:
"Tu solicitud #42 ha sido creada. Estado: REGISTRADA"
```

---

## El Viaje del Dato (paso a paso)

### PASO 1: El Cliente Envía

```json
{
  "tipoSolicitud": "CAMBIO_CALIFICACION",
  "descripcion": "Quiero revisar mi calificación",
  "canalOrigen": "PORTAL_WEB"
}
```

### PASO 2: Llega a SolicitudController

```java
@PostMapping
public ResponseEntity<SolicitudResponse> crear(
        @RequestBody SolicitudCreateRequest request,  // ← Los datos JSON se convierten aquí
        @RequestParam Long solicitanteId) {          // ← El id viene como ?solicitanteId=1
    
    // request ahora tiene los 3 campos
    // solicitanteId = 1
    
    // Paso 3: Llamar al servicio
    return ResponseEntity.status(HttpStatus.CREATED)
            .body(solicitudService.crear(request, solicitanteId));
}
```

### PASO 3: Va a SolicitudServiceImpl

```java
public SolicitudResponse crear(SolicitudCreateRequest request, Long solicitanteId) {
    
    // 3a. Buscar usuario
    Usuario usuario = usuarioRepository.findById(solicitanteId);
    // BD: SELECT * FROM usuarios WHERE id=1
    // Resultado: Usuario(id=1, nombre="María", correo="...", ...)
    
    // 3b. Crear objeto Solicitud en memoria
    Solicitud solicitud = new Solicitud(
            request.getDescripcion(),    // "Quiero revisar mi calificación"
            usuario,                      // Usuario(id=1, ...)
            request.getCanalOrigen()      // PORTAL_WEB
    );
    // Automáticamente:
    // - estado = REGISTRADA
    // - fechaCreacion = NOW
    // - fechaActualizacion = NOW
    
    // 3c. Guardar en BD
    solicitudRepository.save(solicitud);
    // BD: INSERT INTO solicitudes(descripcion, estado, ...)
    // La BD asigna ID automático: id = 42
    
    // 3d. Registrar en historial
    registrarHistorial(solicitud, usuario, "Solicitud creada", null, 
                      null, EstadoSolicitud.REGISTRADA);
    // BD: INSERT INTO historial_solicitudes(...)
    
    // 3e. Convertir a DTO (SolicitudResponse)
    return mapearResponse(solicitud);
    // Retorna: SolicitudResponse(id=42, estado=REGISTRADA, ...)
}
```

### PASO 4: La Respuesta Regresa

```json
HTTP 201 Created

{
  "id": 42,
  "estado": "REGISTRADA",
  "tipoSolicitud": "CAMBIO_CALIFICACION",
  "descripcion": "Quiero revisar mi calificación",
  "canalOrigen": "PORTAL_WEB",
  "solicitanteId": 1,
  "responsableId": null,
  "fechaCreacion": "2026-04-14T10:30:45",
  "fechaActualizacion": "2026-04-14T10:30:45"
}
```

**¡La solicitud está creada y guardada!**

---

## Ahora... ¿Qué sucede después?

```
Solicitud #42 tiene vida propia:

08:00 → REGISTRADA (acabas de crearla)
         ✅ Historial: "Solicitud creada"

09:00 → CLASIFICADA (coordinador la clasifica)
         ✅ Historial: "Solicitud clasificada"

10:00 → EN_ATENCION (se asigna a profesor)
         ✅ Historial: "Responsable asignado"

14:00 → ATENDIDA (profesor la procesa)
         ✅ Historial: "Solicitud atendida"

15:00 → CERRADA (se finaliza)
         ✅ Historial: "Solicitud cerrada"


Cada cambio queda registrado para siempre 📜
```

---

## Los 3 Protagonistas

### 1. CONTROLADOR (Controller)
```
¿Qué es?  Recepcionista del backend
¿Qué hace? Recibe requests HTTP
           Valida que sean JSON válido
           Delega al servicio
```

### 2. SERVICIO (Service)
```
¿Qué es?  El gerente del backend
¿Qué hace? Aplica las reglas del negocio
           "¿El estudiante existe?"
           "¿Es la transición de estado válida?"
           "¿Debo registrar en historial?"
```

### 3. REPOSITORIO (Repository)
```
¿Qué es?  La conexión con la BD
¿Qué hace? INSERT, SELECT, UPDATE, DELETE
           Nada de lógica, solo CRUD
```

---

## Los 3 Tipos de Objetos

### 1. DTO (Data Transfer Object)
```
¿Qué es?  Lo que viajaEON por la red (JSON)
¿Para qué? Proteger la estructura interna
¿Ejemplo? SolicitudCreateRequest, SolicitudResponse
```

### 2. Entidad (Entity)
```
¿Qué es?  Lo que se guarda en la BD
¿Para qué? Representar datos reales
¿Ejemplo? Solicitud, Usuario, HistorialSolicitud
```

### 3. Enum
```
¿Qué es?  Un tipo con opciones predefinidas
¿Para qué? Asegurar valores válidos
¿Ejemplo? EstadoSolicitud.REGISTRADA (no puede ser "HOLA")
```

---

## El Ciclo Completo en 1 Imagen

```
CLIENTE                    BACKEND
  │                          │
  │  POST /api/solicitudes   │
  │──────────────────────────>
  │  + SolicitudCreateRequest
  │  + JWT (autenticación)
  │
  │                       JwtFilter valida
  │                          │
  │                     SolicitudController
  │                      (recibe request)
  │                          │
  │                     SolicitudServiceImpl
  │                      (lógica negocio)
  │                          │
  │                     UsuarioRepository (findById)
  │                     SolicitudRepository (save)
  │                     HistorialRepository (save)
  │                          │
  │                       BASE DE DATOS
  │                    (INSERT, SELECT)
  │                          │
  │                    mapearResponse()
  │                   (Entidad → DTO)
  │                          │
  │  HTTP 201 Created        │
  │  + SolicitudResponse JSON
  │<──────────────────────────
  │
  └─> "¡Mi solicitud #42 fue creada!"
```

---

## Lo Más Importante: El Flujo

### Recuerda esto:

```
1. VALIDAR: ¿JWT válido? ¿Datos correctos?
   └─> Si algo falla → Error 401, 400, etc

2. BUSCAR: ¿Existen los datos que necesito?
   └─> Si no existen → Error 404

3. CREAR/MODIFICAR: Aplicar lógica de negocio
   └─> Si viola reglas → Error 422

4. GUARDAR: Persistir en la BD
   └─> Si falla BD → Error 500

5. RESPONDER: Retornar DTO con datos
   └─> Cliente recibe JSON
```

---

## Preguntas Frecuentes

### P: ¿Por qué usar DTOs?
R: Porque no quiero exponer todo. Los DTOs muestran solo lo que el cliente necesita saber.

### P: ¿Por qué registrar historial?
R: Para auditoría. "¿Quién hizo qué y cuándo?" Importante para reportes y debugging.

### P: ¿Por qué validar transiciones de estado?
R: Porque una solicitud NO puede pasar de REGISTRADA directamente a CERRADA. Debe cumplir el flujo.

### P: ¿Por qué usar JWT?
R: Porque el usuario debe autenticarse. El JWT prueba que es quien dice ser.

### P: ¿Qué pasa si un endpoint falla?
R: El servicio lanza una excepción. Spring la captura y retorna HTTP 500 (o el error que corresponda).

---

## En Código = En Vida Real

```
Código Backend          Vida Real
──────────────────────────────────────────
@PostMapping            ← Recepción de solicitud
SolicitudController     ← Recepcionista
SolicitudServiceImpl     ← Gerente
SolicitudRepository     ← Libro de registro
usuarioRepository.save()← Escribir en BD
EntityNotFoundException ← "¡No existe!"
registrarHistorial()    ← Auditoría
HTTP 201 Created        ← "¡Listo!"
```

---

## Resumiendo en 5 Puntos

1. **El cliente envía JSON** con datos
2. **El controller** recibe y valida
3. **El service** aplica lógica de negocio
4. **El repository** guarda en BD
5. **El server retorna** JSON con respuesta

**Todo integrado = Tu aplicación funciona** ✅

---

## Visual Final (Lo que realmente ocurre)

```
┌────────────────────────────────────────────────────────────┐
│                                                            │
│ 1. Cliente: "¿Me haces una solicitud?"                    │
│    └─> Envía JSON vía HTTP                               │
│                                                            │
│ 2. JwtFilter: "¿Tienes permiso?"                          │
│    └─> Valida token                                       │
│                                                            │
│ 3. Controller: "¿Tus datos son válidos?"                  │
│    └─> Deserializa JSON                                  │
│                                                            │
│ 4. Service: "¿Todo tiene sentido?"                        │
│    └─> Aplicar reglas de negocio                         │
│                                                            │
│ 5. Repository: "Voy a guardar"                            │
│    └─> Ejecuta SQL INSERT                                │
│                                                            │
│ 6. BD: "Guardado. Tu ID es 42"                            │
│    └─> Asigna ID automático                              │
│                                                            │
│ 7. Service: "Voy a crear el DTO"                          │
│    └─> Convierte Entidad → DTO                           │
│                                                            │
│ 8. Controller: "Aquí está tu respuesta"                   │
│    └─> Serializa JSON                                    │
│                                                            │
│ 9. Cliente: "¡Perfecto! Mi solicitud #42"                │
│    └─> Recibe JSON                                       │
│                                                            │
└────────────────────────────────────────────────────────────┘
```

---

**¿Necesitas más detalles? Revisa los otros archivos:**

📄 **FLUJO_DATOS_EXPLICADO.md** → Explicación completa
📄 **DIAGRAMAS_FLUJO_VISUAL.md** → Diagramas bonitos
📄 **EJEMPLOS_CODIGO_ANOTADO.md** → Código con comentarios
📄 **CHEAT_SHEET.md** → Resumen rápido

