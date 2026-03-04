1. Descripción General

El Sistema de Triage Académico es una plataforma orientada a la gestión, priorización y seguimiento del ciclo de vida de solicitudes académicas dentro de la institución.

El diseño del dominio sigue principios de:

Domain-Driven Design (DDD)

Arquitectura por capas

Consistencia transaccional

Auditoría de cambios

Máquina de estados explícita

La entidad central del sistema es Solicitud, modelada como Aggregate Root.

2. Modelo de Dominio

El dominio se divide en los siguientes módulos.

2.1 Identidad y Acceso

Gestiona la autenticación y administración de usuarios.

Entidades:

Usuario

Rol (Enum)

Responsabilidades:

Registro de usuarios

Autenticación mediante JWT

Control de usuarios activos

2.2 Catálogo

Define configuraciones base del sistema.

Entidades:

TipoSolicitud

CanalOrigen

Estas entidades permiten clasificar adecuadamente las solicitudes.

2.3 Solicitudes (Aggregate Root)

La entidad Solicitud representa el núcleo del sistema.

Controla:

Registro de solicitudes

Clasificación

Priorización

Asignación de responsables

Cierre de solicitudes

Historial de eventos

3. Máquina de Estados

La entidad Solicitud implementa una máquina de estados.

Estados posibles

REGISTRADA

CLASIFICADA

PRIORIZADA

ASIGNADA

EN_GESTION

CERRADA

Reglas principales

Una solicitud inicia en REGISTRADA

No se puede cerrar sin estar en EN_GESTION

No se permiten retrocesos de estado

CERRADA es un estado final

4. Relaciones del Modelo
   Solicitud – Usuario (solicitante)

Tipo: muchos a uno

Obligatoria

Solicitud – Usuario (responsable)

Tipo: muchos a uno

Opcional hasta la asignación

Solicitud – HistorialSolicitud

Tipo: uno a muchos

Garantiza trazabilidad

Solicitud – TipoSolicitud

Tipo: muchos a uno

Solicitud – CanalOrigen

Tipo: muchos a uno

5. Reglas de Negocio

Toda solicitud debe tener solicitante válido.

Solo usuarios activos pueden ser responsables.

Cada cambio de estado genera registro en historial.

No se permiten transiciones inválidas.

El sistema debe responder HTTP 409 Conflict ante transiciones ilegales.

6. Concurrencia

El sistema puede usar control optimista mediante campo de versión en la entidad Solicitud para evitar pérdida de actualizaciones.

7. Validaciones

Email con formato válido

Prioridad dentro del enum permitido

Estados controlados por la máquina de estados

Responsable obligatorio al asignar