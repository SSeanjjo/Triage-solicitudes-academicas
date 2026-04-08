package co.edu.uniquindio.gestion_solicitudes.dto;

import co.edu.uniquindio.gestion_solicitudes.domain.*;
import java.time.LocalDateTime;

public class SolicitudResponse {

    private Long id;
    private EstadoSolicitud estado;
    private PrioridadSolicitud prioridad;
    private TipoSolicitud tipoSolicitud;
    private CanalOrigen canalOrigen;
    private Long solicitanteId;
    private Long responsableId;
    private String descripcion;
    private String justificacionPrioridad;
    private String observacionCierre;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public SolicitudResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public EstadoSolicitud getEstado() { return estado; }
    public void setEstado(EstadoSolicitud estado) { this.estado = estado; }

    public PrioridadSolicitud getPrioridad() { return prioridad; }
    public void setPrioridad(PrioridadSolicitud prioridad) { this.prioridad = prioridad; }

    public TipoSolicitud getTipoSolicitud() { return tipoSolicitud; }
    public void setTipoSolicitud(TipoSolicitud tipoSolicitud) { this.tipoSolicitud = tipoSolicitud; }

    public CanalOrigen getCanalOrigen() { return canalOrigen; }
    public void setCanalOrigen(CanalOrigen canalOrigen) { this.canalOrigen = canalOrigen; }

    public Long getSolicitanteId() { return solicitanteId; }
    public void setSolicitanteId(Long solicitanteId) { this.solicitanteId = solicitanteId; }

    public Long getResponsableId() { return responsableId; }
    public void setResponsableId(Long responsableId) { this.responsableId = responsableId; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getJustificacionPrioridad() { return justificacionPrioridad; }
    public void setJustificacionPrioridad(String justificacionPrioridad) { this.justificacionPrioridad = justificacionPrioridad; }

    public String getObservacionCierre() { return observacionCierre; }
    public void setObservacionCierre(String observacionCierre) { this.observacionCierre = observacionCierre; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
}
