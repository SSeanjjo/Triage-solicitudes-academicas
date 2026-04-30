export enum EstadoSolicitud {
  REGISTRADA = 'REGISTRADA',
  CLASIFICADA = 'CLASIFICADA',
  EN_ATENCION = 'EN_ATENCION',
  ATENDIDA = 'ATENDIDA',
  CERRADA = 'CERRADA',
}

export enum PrioridadSolicitud {
  ALTA = 'ALTA',
  MEDIA = 'MEDIA',
  BAJA = 'BAJA',
}

export enum TipoSolicitud {
  REGISTRO_ASIGNATURA = 'REGISTRO_ASIGNATURA',
  HOMOLOGACION = 'HOMOLOGACION',
  CANCELACION = 'CANCELACION',
  CUPO = 'CUPO',
  CONSULTA_ACADEMICA = 'CONSULTA_ACADEMICA',
}

export enum CanalOrigen {
  CSU = 'CSU',
  CORREO = 'CORREO',
  SAC = 'SAC',
  TELEFONICO = 'TELEFONICO',
}

export interface UsuarioRef {
  id: number;
  nombre: string;
  correo?: string;
}

export interface Solicitud {
  id: number;
  titulo: string;
  descripcion: string;
  tipoSolicitud: TipoSolicitud;
  estado: EstadoSolicitud;
  prioridad?: PrioridadSolicitud;
  canalOrigen: CanalOrigen;
  solicitanteId?: number;
  solicitanteNombre?: string;
  responsableId?: number;
  responsableNombre?: string;
  justificacionPrioridad?: string;
  observacionCierre?: string;
  fechaCreacion?: string;
  fechaActualizacion?: string;
}

export interface HistorialEntry {
  id?: number;
  solicitudId?: number;
  usuarioId?: number;
  accion?: string;
  comentario?: string;
  fecha?: string;
  estadoAnterior?: EstadoSolicitud;
  estadoNuevo?: EstadoSolicitud;
}

export interface CreateSolicitudRequest {
  titulo: string;
  descripcion: string;
  tipoSolicitud: TipoSolicitud;
  canalOrigen: CanalOrigen;
}

export interface ClasificarRequest {
  tipoSolicitud: TipoSolicitud;
  prioridad: PrioridadSolicitud;
  justificacionPrioridad: string;
}

export interface AtenderRequest {
  comentario: string;
}

export interface CierreRequest {
  comentarioCierre: string;
}

export interface SolicitudFilter {
  estado?: EstadoSolicitud | '';
  tipo?: TipoSolicitud | '';
  prioridad?: PrioridadSolicitud | '';
  responsableId?: number;
}

export interface IASugerencia {
  tipoSugerido?: TipoSolicitud;
  prioridadSugerida?: PrioridadSolicitud;
  justificacion?: string;
  mensaje?: string;
  tipo?: TipoSolicitud;
  prioridad?: PrioridadSolicitud;
}

export interface IAResumen {
  resumen?: string;
  summary?: string;
  texto?: string;
}
