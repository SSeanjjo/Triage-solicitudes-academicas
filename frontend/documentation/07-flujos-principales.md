# 07 — Flujos principales de la aplicación

Este documento describe paso a paso los flujos de uso más importantes del sistema, desde la perspectiva del usuario y del código.

---

## Flujo 1: Inicio de sesión

### Desde la perspectiva del usuario
1. El usuario abre `http://localhost:4200`
2. El router detecta que no hay sesión activa → redirige a `/login`
3. El usuario ingresa correo y contraseña
4. Hace clic en "Iniciar Sesión"
5. Aparece un spinner mientras se procesa
6. Si es correcto: es redirigido al dashboard
7. Si hay error: aparece un mensaje rojo en la parte inferior de la pantalla

### Desde la perspectiva del código
```
LoginComponent.onSubmit()
  → AuthService.login({ correo, password })
    → HttpClient POST /api/auth/login
      → Interceptor añade headers (pero aún no hay token)
      → Backend valida credenciales
      → Responde: { token, rol, nombre, id }
    ← AuthService guarda sesión en signal + localStorage
  ← LoginComponent navega a /dashboard con Router.navigate()
```

### ¿Qué se guarda en localStorage?
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "nombre": "Sebastian Lesmes",
  "rol": "ESTUDIANTE",
  "id": 5
}
```

---

## Flujo 2: Navegación protegida

### ¿Qué pasa cuando un usuario no autenticado intenta acceder a `/dashboard`?

```
Usuario escribe localhost:4200/dashboard en el navegador
  → Router evalúa la ruta /dashboard
    → canActivate: [authGuard]
      → authGuard comprueba AuthService.isAuthenticated()
        → Lee localStorage → no hay sesión guardada
        → isAuthenticated() = false
      → Guard retorna UrlTree hacia /login
  → Router redirige a /login automáticamente
```

### ¿Qué pasa cuando un RESPONSABLE intenta acceder a `/solicitudes/nueva`?

```
Responsable hace clic en "Nueva Solicitud"
  → Router evalúa la ruta /solicitudes/nueva
    → canActivate: [roleGuard([ESTUDIANTE, ADMINISTRADOR])]
      → Usuario está autenticado ✓
      → ¿Tiene rol ESTUDIANTE o ADMINISTRADOR? → NO (es RESPONSABLE)
      → Guard retorna UrlTree hacia /dashboard
  → Router redirige al dashboard
```

---

## Flujo 3: Crear una solicitud (desde el estudiante)

### Desde la perspectiva del usuario
1. ESTUDIANTE entra a `/solicitudes/nueva`
2. Escribe el título y descripción
3. (Opcional) Hace clic en "Sugerencia IA" — el sistema sugiere el tipo
4. Selecciona el tipo de solicitud y canal de entrada
5. Hace clic en "Crear Solicitud"
6. Es redirigido al detalle de la solicitud recién creada

### Desde la perspectiva del código
```
NuevaComponent.onSubmit()
  → SolicitudService.createSolicitud(formData, usuarioId)
    → HttpClient POST /api/solicitudes
      → Body: { titulo, descripcion, tipo, canal, solicitanteId }
      → Interceptor añade Authorization: Bearer <token>
      → Backend crea la solicitud con estado REGISTRADA
      → Responde con la solicitud creada { id: 42, estado: "REGISTRADA", ... }
  ← NuevaComponent navega a /solicitudes/42
```

### Sub-flujo: Sugerencia de IA
```
Usuario escribe descripción y hace clic en "Sugerencia IA"
  → IaService.getSugerenciaClasificacion(descripcion)
    → HttpClient POST /api/ia/sugerencias/clasificacion
      → Body: { descripcion: "Necesito registrar..." }
      → Backend analiza con IA
      → Responde: { tipoSugerido: "REGISTRO_ASIGNATURA", confianza: 0.87, justificacion: "..." }
  ← NuevaComponent muestra el resultado
  
Usuario hace clic en "Aplicar sugerencia"
  → El campo "Tipo" del formulario se llena con "REGISTRO_ASIGNATURA"
```

---

## Flujo 4: Clasificar una solicitud

### Desde la perspectiva del usuario (RESPONSABLE o ADMIN)
1. Abre el detalle de una solicitud en estado REGISTRADA
2. Ve el botón "Clasificar"
3. Selecciona el tipo y la prioridad en el formulario que aparece
4. Hace clic en "Guardar Clasificación"
5. El estado cambia a CLASIFICADA — el formulario desaparece y aparece el botón "Asignar"

### Diagrama de estado
```
Estado actual: REGISTRADA
  → Aparece formulario de clasificación
  → Usuario elige tipo: HOMOLOGACION, prioridad: ALTA
  → SolicitudService.clasificar(id, { tipo, prioridad }, usuarioId)
    → PATCH /api/solicitudes/42/clasificar
Estado nuevo: CLASIFICADA
```

---

## Flujo 5: Asignar responsable y atender

### Flujo completo de gestión

```
CLASIFICADA
  → RESPONSABLE hace clic en "Asignarme"
    o ADMIN selecciona un responsable de la lista
  → SolicitudService.asignar(id, responsableId, usuarioId)
    → PATCH /api/solicitudes/42/asignar

EN_ATENCION
  → RESPONSABLE/ADMIN hace clic en "Marcar como Atendida"
  → SolicitudService.atender(id, usuarioId)
    → PATCH /api/solicitudes/42/atender

ATENDIDA
  → RESPONSABLE/ADMIN hace clic en "Cerrar Solicitud"
  → SolicitudService.cerrar(id, usuarioId)
    → PATCH /api/solicitudes/42/cerrar

CERRADA ← Estado final
```

---

## Flujo 6: Ver el historial de una solicitud

Cada vez que cambia el estado de una solicitud, el backend registra una entrada en el historial.

### Lo que ve el usuario
```
Historial de cambios:
─────────────────────────────────────
  [●] Solicitud creada          
      Admin User — hace 2 días

  [●] Solicitud clasificada     
      Juan Pérez — hace 1 día   
      Tipo: HOMOLOGACION, Prioridad: ALTA

  [●] Responsable asignado      
      María García — hace 5 horas
      Asignado a: Carlos López

  [●] Solicitud atendida        
      Carlos López — hace 1 hora
─────────────────────────────────────
```

### Desde el código
```
DetalleComponent.ngOnInit()
  → SolicitudService.getHistorial(solicitudId)
    → GET /api/solicitudes/42/historial
    → Responde: [ { accion, usuarioNombre, timestamp, detalle }, ... ]
  ← DetalleComponent renderiza la línea de tiempo
```

---

## Flujo 7: Cerrar sesión

```
Usuario hace clic en su nombre (esquina superior derecha)
  → Aparece menú desplegable
  → Hace clic en "Cerrar Sesión"
    → AuthService.logout()
      → Borra el dato de localStorage
      → Limpia el signal _session (= null)
      → Router.navigate(['/login'])
  → Todos los componentes que usan isAuthenticated() se actualizan
  → El usuario ve la página de login
```

---

## Flujo 8: Gestión de usuarios (solo Admin)

### Editar un usuario
```
ADMINISTRADOR entra a /usuarios
  → UsuarioService.getUsuarios()
    → GET /api/usuarios → lista de todos los usuarios

Hace clic en un usuario de la tabla
  → La fila se convierte en formulario editable (inline)
  → Modifica nombre, correo, rol o estado activo
  → Hace clic en "Guardar"
    → UsuarioService.updateUsuario(id, { nombre, correo, rol, activo })
      → PUT /api/usuarios/3
    → La tabla se actualiza con los nuevos datos
    → La fila vuelve al modo de solo lectura
```

---

## Resumen de permisos por pantalla

| Pantalla | ESTUDIANTE | RESPONSABLE | ADMINISTRADOR |
|---|---|---|---|
| Login / Registro | ✓ | ✓ | ✓ |
| Dashboard | ✓ | ✓ | ✓ |
| Ver lista de solicitudes | Solo las propias | Todas | Todas |
| Crear solicitud | ✓ | ✗ | ✓ |
| Clasificar solicitud | ✗ | ✓ | ✓ |
| Asignar responsable | ✗ | Solo a sí mismo | ✓ |
| Atender / Cerrar | ✗ | ✓ | ✓ |
| Ver historial | ✓ | ✓ | ✓ |
| Generar resumen IA | ✗ | ✓ | ✓ |
| Gestionar usuarios | ✗ | ✗ | ✓ |
