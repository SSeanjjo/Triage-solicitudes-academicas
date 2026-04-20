# 📚 DOCUMENTACIÓN DEL FLUJO DE DATOS - Backend Gestión de Solicitudes Académicas

> Documentación completa explicando cómo funciona el backend, desde que se crea una solicitud hasta su cierre.

## 🚀 ¿Por Dónde Empiezo?

### **Tengo 5 minutos:**
👉 Lee [`ULTRA_SIMPLE.md`](ULTRA_SIMPLE.md)
- Analogía con pizzería
- Conceptos básicos fáciles
- Preguntas frecuentes

### **Tengo 15 minutos:**
👉 Lee [`MAPA_MENTAL.md`](MAPA_MENTAL.md)
- Diagramas visuales
- Flujo de datos
- Máquina de estados

### **Tengo 30 minutos:**
👉 Lee [`FLUJO_DATOS_EXPLICADO.md`](FLUJO_DATOS_EXPLICADO.md)
- Explicación completa
- Arquitectura en capas
- Ejemplo paso a paso

### **Quiero todo:**
👉 Lee en orden:
1. [`ULTRA_SIMPLE.md`](ULTRA_SIMPLE.md) - 5 min
2. [`MAPA_MENTAL.md`](MAPA_MENTAL.md) - 10 min
3. [`CHEAT_SHEET.md`](CHEAT_SHEET.md) - 10 min
4. [`DIAGRAMAS_FLUJO_VISUAL.md`](DIAGRAMAS_FLUJO_VISUAL.md) - 30 min
5. [`EJEMPLOS_CODIGO_ANOTADO.md`](EJEMPLOS_CODIGO_ANOTADO.md) - 30 min

---

## 📁 Todos los Documentos

| Archivo | Duración | Contenido |
|---------|----------|-----------|
| [`INDICE.md`](INDICE.md) | 5 min | Índice y guía de lectura |
| [`ULTRA_SIMPLE.md`](ULTRA_SIMPLE.md) | 5 min | Explicación súper simple |
| [`MAPA_MENTAL.md`](MAPA_MENTAL.md) | 10 min | Mapas mentales visuales |
| [`CHEAT_SHEET.md`](CHEAT_SHEET.md) | 10 min | Resumen rápido |
| [`REFERENCIA_RAPIDA.md`](REFERENCIA_RAPIDA.md) | 3 min | Una página de referencia |
| [`FLUJO_DATOS_EXPLICADO.md`](FLUJO_DATOS_EXPLICADO.md) | 45 min | Explicación completa |
| [`DIAGRAMAS_FLUJO_VISUAL.md`](DIAGRAMAS_FLUJO_VISUAL.md) | 30 min | Diagramas ASCII |
| [`EJEMPLOS_CODIGO_ANOTADO.md`](EJEMPLOS_CODIGO_ANOTADO.md) | 30 min | Código con comentarios |

---

## 🎯 Tu Pregunta: ¿Cómo funciona crear una solicitud?

### Respuesta en 30 segundos:
```
1. Cliente envía JSON
2. JwtFilter valida autenticación
3. SolicitudController recibe
4. SolicitudServiceImpl valida y procesa
5. UsuarioRepository busca usuario
6. SolicitudRepository guarda en BD
7. HistorialRepository registra evento
8. Service retorna DTO
9. Cliente recibe respuesta HTTP 201
```

### Respuesta en 3 minutos:
Lee la sección "El Ciclo Completo" en [`ULTRA_SIMPLE.md`](ULTRA_SIMPLE.md)

### Respuesta en 20 minutos:
Lee la sección "2. FLUJO DE CREACIÓN" en [`FLUJO_DATOS_EXPLICADO.md`](FLUJO_DATOS_EXPLICADO.md)

### Respuesta con Código:
Lee la sección "1. FLUJO COMPLETO" en [`EJEMPLOS_CODIGO_ANOTADO.md`](EJEMPLOS_CODIGO_ANOTADO.md)

---

## 🏗️ Arquitectura en 1 Imagen

```
CLIENTE
   ↓ HTTP JSON
CONTROLADOR (recibe)
   ↓
SERVICIO (procesa lógica)
   ↓
REPOSITORIO (CRUD)
   ↓
BASE DE DATOS (guarda)
   ↓
REPOSITORIO (retorna)
   ↓
SERVICIO (mapea DTO)
   ↓
CONTROLADOR (serializa)
   ↓ HTTP JSON
CLIENTE
```

---

## 📝 Los 3 Conceptos Clave

### 1. DTO (Data Transfer Object)
Lo que viaja por la red:
```java
SolicitudCreateRequest  // Entrada
SolicitudResponse       // Salida
```

### 2. Entidad
Lo que se guarda en la BD:
```java
Solicitud, Usuario, HistorialSolicitud
```

### 3. Repository
CRUD en la BD:
```java
.save(), .findById(), .findAll(), .delete()
```

---

## 🔄 Los 5 Estados de una Solicitud

```
REGISTRADA → CLASIFICADA → EN_ATENCION → ATENDIDA → CERRADA
```

**Regla**: Solo puede transicionar en este orden, sin saltar.

---

## 🔐 Seguridad con JWT

1. **Login**: POST /api/auth/login → Recibe JWT
2. **Usar**: Todas las peticiones llevan `Authorization: Bearer <JWT>`
3. **Validar**: JwtFilter valida el token antes de procesar

---

## 📊 Base de Datos

Tres tablas principales:

### usuarios
```sql
id, nombre, correo, password, rol, activo
```

### solicitudes
```sql
id, descripcion, estado, tipo, canal_origen,
solicitante_id, responsable_id, fecha_creacion, ...
```

### historial_solicitudes
```sql
id, solicitud_id, usuario_accion_id, accion,
estado_anterior, estado_nuevo, fecha, ...
```

---

## 🎮 Endpoints Principales

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/auth/login` | Obtener JWT |
| POST | `/api/solicitudes` | **Crear solicitud** ⭐ |
| GET | `/api/solicitudes` | Listar solicitudes |
| PATCH | `/api/solicitudes/{id}/clasificar` | Clasificar |
| PATCH | `/api/solicitudes/{id}/asignar` | Asignar responsable |
| PATCH | `/api/solicitudes/{id}/atender` | Atender |
| PATCH | `/api/solicitudes/{id}/cerrar` | Cerrar |
| GET | `/api/solicitudes/{id}/historial` | Obtener historial |

---

## 💻 Archivos Clave del Backend

```
src/main/java/co/edu/uniquindio/gestion_solicitudes/
├── configuracion/
│   ├── JwtUtil.java          ← Tokens JWT
│   ├── JwtFilter.java        ← Validación en requests
│   └── SecurityConfig.java   ← Configuración seguridad
│
├── controller/
│   ├── SolicitudController.java      ← Endpoints REST
│   ├── AuthController.java           ← Login
│   └── UsuarioController.java        ← Usuarios
│
├── service/
│   └── Implementar/
│       ├── SolicitudServiceImpl.java  ← Lógica solicitudes
│       ├── AuthServiceImpl.java       ← Lógica autenticación
│       └── UsuarioServiceImpl.java    ← Lógica usuarios
│
├── repository/
│   ├── SolicitudRepository.java           ← CRUD solicitudes
│   ├── UsuarioRepository.java             ← CRUD usuarios
│   └── HistorialSolicitudRepository.java  ← CRUD historial
│
├── domain/
│   ├── Solicitud.java              ← Entidad
│   ├── Usuario.java                ← Entidad
│   ├── HistorialSolicitud.java     ← Entidad
│   ├── EstadoSolicitud.java        ← Enum
│   ├── PrioridadSolicitud.java     ← Enum
│   ├── TipoSolicitud.java          ← Enum
│   ├── CanalOrigen.java            ← Enum
│   └── Rol.java                    ← Enum
│
└── dto/
    ├── SolicitudCreateRequest.java ← Entrada
    ├── SolicitudResponse.java      ← Salida
    ├── LoginRequest.java           ← Entrada login
    ├── LoginResponse.java          ← Salida login
    └── ...
```

---

## 🔍 Ejemplo: Crear una Solicitud

### Request HTTP
```http
POST http://localhost:8080/api/solicitudes?solicitanteId=1
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "tipoSolicitud": "CAMBIO_CALIFICACION",
  "descripcion": "Quiero reconsiderar mi calificación",
  "canalOrigen": "PORTAL_WEB"
}
```

### Response HTTP
```http
HTTP/1.1 201 Created
Content-Type: application/json

{
  "id": 42,
  "estado": "REGISTRADA",
  "tipoSolicitud": "CAMBIO_CALIFICACION",
  "canalOrigen": "PORTAL_WEB",
  "solicitanteId": 1,
  "responsableId": null,
  "descripcion": "Quiero reconsiderar mi calificación",
  "fechaCreacion": "2026-04-14T10:30:45",
  "fechaActualizacion": "2026-04-14T10:30:45"
}
```

### Lo que sucede internamente
1. JwtFilter valida el token
2. SolicitudController mapea la entrada
3. SolicitudServiceImpl valida datos
4. UsuarioRepository busca al usuario
5. SolicitudRepository inserta en solicitudes
6. HistorialRepository registra el evento
7. Service mapea a DTO
8. Controller retorna respuesta

---

## 📚 Lectura Recomendada

### Si eres principiante:
1. [`ULTRA_SIMPLE.md`](ULTRA_SIMPLE.md) - Empieza aquí
2. [`MAPA_MENTAL.md`](MAPA_MENTAL.md) - Visualiza
3. [`CHEAT_SHEET.md`](CHEAT_SHEET.md) - Memoriza

### Si eres programador:
1. [`CHEAT_SHEET.md`](CHEAT_SHEET.md) - Resumen rápido
2. [`EJEMPLOS_CODIGO_ANOTADO.md`](EJEMPLOS_CODIGO_ANOTADO.md) - Ve el código
3. [`FLUJO_DATOS_EXPLICADO.md`](FLUJO_DATOS_EXPLICADO.md) - Aprende la teoría

### Si quieres dominar:
1. Lee todos los documentos en orden
2. Ejecuta el backend y hazle requests
3. Mira los logs de Spring Boot
4. Abre la BD y verifica los datos

---

## ✅ Checklist: ¿Entiendes el Flujo?

- [ ] ¿Qué es un DTO?
- [ ] ¿Qué es una Entidad?
- [ ] ¿Qué es un Repository?
- [ ] ¿Cuál es el ciclo completo?
- [ ] ¿Cuáles son los 5 estados?
- [ ] ¿Cómo funciona JWT?
- [ ] ¿Qué hace cada capa?

Si respondiste "no" a alguno → Lee los documentos 📚

---

## 🎓 Conclusión

**Ahora entiendes:**
- ✅ Cómo funciona crear una solicitud
- ✅ Cómo se conectan los componentes
- ✅ Cómo se guardan los datos en la BD
- ✅ Cómo funciona la autenticación
- ✅ Cómo se registra el historial
- ✅ Cómo cambian los estados
- ✅ Cómo funciona el sistema completo

---

## 🆘 ¿Necesitas Ayuda?

| Pregunta | Documento |
|----------|-----------|
| Quiero entender rápido | [`ULTRA_SIMPLE.md`](ULTRA_SIMPLE.md) |
| Quiero ver diagramas | [`MAPA_MENTAL.md`](MAPA_MENTAL.md) |
| Quiero un resumen | [`CHEAT_SHEET.md`](CHEAT_SHEET.md) |
| Una sola página de referencia | [`REFERENCIA_RAPIDA.md`](REFERENCIA_RAPIDA.md) |
| Explicación completa | [`FLUJO_DATOS_EXPLICADO.md`](FLUJO_DATOS_EXPLICADO.md) |
| Ver diagramas técnicos | [`DIAGRAMAS_FLUJO_VISUAL.md`](DIAGRAMAS_FLUJO_VISUAL.md) |
| Leer código anotado | [`EJEMPLOS_CODIGO_ANOTADO.md`](EJEMPLOS_CODIGO_ANOTADO.md) |
| Índice y guía | [`INDICE.md`](INDICE.md) |

---

## 📞 Resumen Ejecutivo

**Backend de Gestión de Solicitudes Académicas:**
- Arquitectura: Controller → Service → Repository
- Autenticación: JWT (10 horas de duración)
- Datos: Usuario solicita → Coordinador clasifica → Profesor atiende → Se cierra
- Estados: 5 transiciones ordenadas sin saltos
- Historial: Cada cambio se registra para auditoría
- DTOs: Protegen la estructura interna

**¡Ahora estás listo para entender y modificar el backend!** 🚀

---

*Última actualización: 2026-04-14*

