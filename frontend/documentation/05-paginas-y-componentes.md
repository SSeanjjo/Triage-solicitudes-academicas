# 05 — Páginas y componentes

Cada sección describe una pantalla de la aplicación: qué hace, qué ve el usuario y los detalles técnicos.

---

## Navbar (`shared/components/navbar/`)

**Ruta:** Visible en todas las páginas autenticadas

La barra de navegación superior que persiste en toda la app.

### Lo que ve el usuario
- Logo e ícono del sistema ("Sistema de Triage Académico")
- Enlace a **Dashboard**
- Enlace a **Solicitudes**
- Enlace a **Usuarios** (solo visible para ADMINISTRADOR)
- Menú desplegable con nombre del usuario, su rol y botón de **Cerrar Sesión**

### Detalles técnicos
- Usa `routerLinkActive="active"` para resaltar el enlace de la ruta actual
- El menú de usuario usa `MatMenu` de Angular Material
- Muestra u oculta opciones según el rol usando `*ngIf` con `authService.currentRole()`
- En móvil, el texto del logo se oculta para ahorrar espacio

---

## Login (`pages/login/`)

**Ruta:** `/login`  
**Acceso:** Pública (sin autenticación)

### Lo que ve el usuario
- Tarjeta centrada con el logo y nombre del sistema
- Campo de correo electrónico
- Campo de contraseña con botón para mostrar/ocultar
- Botón "Iniciar Sesión" (muestra spinner mientras carga)
- Enlace "¿No tienes cuenta? Regístrate"

### Lógica principal
1. El formulario usa `ReactiveFormsModule` con validadores (`required`, `email`)
2. Al hacer submit, llama a `AuthService.login()`
3. Si el login es exitoso: guarda la sesión y navega a `/dashboard`
4. Si hay error: muestra un snackbar rojo con el mensaje de error

### Validaciones del formulario
| Campo | Regla |
|---|---|
| Correo | Requerido, formato de email válido |
| Contraseña | Requerido |

---

## Registro (`pages/registro/`)

**Ruta:** `/registro`  
**Acceso:** Pública

### Lo que ve el usuario
- Formulario de registro con campos: Nombre, Correo, Contraseña
- Selector de rol (Estudiante, Responsable, Administrador)
- Botón "Registrarse"
- Enlace "¿Ya tienes cuenta? Inicia sesión"

### Lógica principal
1. Llama a `AuthService.registro()` con los datos del formulario
2. Si es exitoso: muestra snackbar verde y navega a `/login`
3. Si hay error: muestra snackbar rojo

---

## Dashboard (`pages/dashboard/`)

**Ruta:** `/dashboard`  
**Acceso:** Autenticado (cualquier rol)

### Lo que ve el usuario
- Saludo personalizado: "¡Bienvenido, [Nombre]!" con su rol
- Tarjetas de estadísticas:
  - Total de solicitudes
  - Solicitudes por estado (Registrada, Clasificada, En Atención, Atendida, Cerrada)
  - Solicitudes por prioridad (Alta, Media, Baja)
- Tarjetas de acciones rápidas:
  - "Ver Solicitudes" → navega a `/solicitudes`
  - "Nueva Solicitud" → navega a `/solicitudes/nueva` (solo ESTUDIANTE/ADMIN)
  - "Gestionar Usuarios" → navega a `/usuarios` (solo ADMIN)

### Lógica principal
1. Al inicializar, carga todas las solicitudes desde `SolicitudService`
2. Usa `computed()` para calcular las estadísticas a partir de las solicitudes cargadas
3. Las tarjetas de acción se muestran según el rol del usuario autenticado

### Ejemplo de signal computed
```typescript
readonly totalSolicitudes = computed(() => this.solicitudes().length);
readonly solicitudesAltas = computed(() =>
  this.solicitudes().filter(s => s.prioridad === Prioridad.ALTA).length
);
```

---

## Lista de solicitudes (`pages/solicitudes/lista/`)

**Ruta:** `/solicitudes`  
**Acceso:** Autenticado

### Lo que ve el usuario
- Tabla con todas las solicitudes (columnas: ID, Título, Tipo, Estado, Prioridad, Solicitante, Fecha)
- Panel de filtros:
  - Por Estado (dropdown)
  - Por Tipo (dropdown)
  - Por Prioridad (dropdown)
- Paginación (10 / 25 / 50 por página)
- Los chips de estado tienen colores:
  - Registrada → gris
  - Clasificada → azul
  - En Atención → naranja
  - Atendida → verde claro
  - Cerrada → verde oscuro
- Clic en cualquier fila navega al detalle de esa solicitud

### Lógica principal
1. Carga solicitudes con `SolicitudService.getSolicitudes()`
2. Los filtros están en un formulario reactivo — al cambiar un filtro se recargan las solicitudes con los parámetros
3. Usa `MatTable` con `MatPaginator` (paginación) y `MatSort` (ordenamiento por columnas)
4. Los RESPONSABLE/ADMIN ven todas las solicitudes; los ESTUDIANTE solo ven las suyas (filtrado en backend)

---

## Nueva solicitud (`pages/solicitudes/nueva/`)

**Ruta:** `/solicitudes/nueva`  
**Acceso:** ESTUDIANTE o ADMINISTRADOR

### Lo que ve el usuario
- Formulario con:
  - **Título** (5–150 caracteres) con contador de caracteres
  - **Descripción** (10–1000 caracteres) con contador de caracteres
  - **Tipo de solicitud** (dropdown): Registro de asignatura, Homologación, Cancelación, Cupo, Consulta académica
  - **Canal de entrada** (dropdown): CSU, Correo, SAC, Telefónico
- Botón **"Sugerencia IA"**: analiza la descripción y sugiere el tipo de solicitud
- Panel de resultado de IA (si se usó):
  - Tipo sugerido
  - Nivel de confianza
  - Justificación
  - Botón para aplicar la sugerencia al formulario
- Panel lateral con descripción de cada tipo de solicitud (informativo)
- Botones "Cancelar" y "Crear Solicitud"

### Lógica de la sugerencia IA
1. El usuario escribe la descripción
2. Pulsa "Sugerencia IA"
3. Se llama a `IaService.getSugerenciaClasificacion(descripcion)`
4. El backend analiza el texto con IA y devuelve: tipo sugerido, confianza, justificación
5. El usuario puede aplicar la sugerencia (se rellena el campo "Tipo" automáticamente) o ignorarla

### Validaciones
| Campo | Regla |
|---|---|
| Título | Requerido, 5–150 caracteres |
| Descripción | Requerido, 10–1000 caracteres |
| Tipo | Requerido |
| Canal | Requerido |

---

## Detalle de solicitud (`pages/solicitudes/detalle/`)

**Ruta:** `/solicitudes/:id`  
**Acceso:** Autenticado

Esta es la pantalla más compleja. Permite ver todos los datos de una solicitud y gestionarla según su estado actual.

### Lo que ve el usuario

**Sección de información:**
- Título y descripción de la solicitud
- Tipo, canal de entrada
- Solicitante (nombre) y Responsable asignado (si tiene)
- Fecha de creación y última actualización
- Chips de Estado y Prioridad con colores

**Acciones disponibles** (aparecen o desaparecen según el estado y rol):

| Estado actual | Acción disponible | Roles que pueden hacerlo |
|---|---|---|
| REGISTRADA | Clasificar (tipo + prioridad) | RESPONSABLE, ADMINISTRADOR |
| CLASIFICADA | Asignar responsable | RESPONSABLE (a sí mismo), ADMINISTRADOR (elige de lista) |
| EN_ATENCION | Marcar como Atendida | RESPONSABLE, ADMINISTRADOR |
| ATENDIDA | Cerrar solicitud | RESPONSABLE, ADMINISTRADOR |

**Resumen IA** (solo RESPONSABLE/ADMIN):
- Botón para generar resumen inteligente de la solicitud
- Muestra el resumen generado con botón para regenerar

**Historial de cambios:**
- Línea de tiempo vertical con cada cambio registrado
- Cada entrada muestra: quién hizo el cambio, qué cambió, cuándo

### Lógica principal
1. Carga la solicitud con `SolicitudService.getSolicitud(id)`
2. Carga el historial con `SolicitudService.getHistorial(id)`
3. Si es admin y el estado es CLASIFICADA, carga la lista de responsables con `UsuarioService.getUsuarios()`
4. Cada botón de acción llama al endpoint correspondiente del backend
5. Después de cada acción exitosa, recarga la solicitud para mostrar el nuevo estado

---

## Gestión de usuarios (`pages/usuarios/`)

**Ruta:** `/usuarios`  
**Acceso:** Solo ADMINISTRADOR

### Lo que ve el usuario
- Tabla de todos los usuarios del sistema (columnas: ID, Nombre, Correo, Rol, Estado)
- Filtros:
  - Por Rol (dropdown)
  - Por Estado Activo (Todos / Activos / Inactivos)
- Edición en línea: al hacer clic en un usuario, la fila se convierte en formulario editable
  - Campos editables: Nombre, Correo, Rol, Activo (checkbox)
  - Botones "Guardar" y "Cancelar" en la misma fila
  - Spinner de carga durante el guardado
- Chips de color para roles y estado activo/inactivo

### Lógica principal
1. Carga usuarios con `UsuarioService.getUsuarios()`
2. `editingUserId` signal rastrea qué fila está en modo edición
3. Al guardar, llama a `UsuarioService.updateUsuario(id, datos)`
4. Actualiza la tabla localmente para evitar recargar todo
