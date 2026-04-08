package co.edu.uniquindio.gestion_solicitudes.dto;

import co.edu.uniquindio.gestion_solicitudes.domain.EstadoSolicitud;
import java.time.LocalDateTime;

public class HistorialEventoResponse {

    private Long id;
    private Long solicitudId;
    private Long usuarioId;
    private String accion;
    private EstadoSolicitud estadoAnterior;
    private EstadoSolicitud estadoNuevo;
    private LocalDateTime fecha;
    private String comentario;

    public HistorialEventoResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSolicitudId() { return solicitudId; }
    public void setSolicitudId(Long solicitudId) { this.solicitudId = solicitudId; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public String getAccion() { return accion; }
    public void setAccion(String accion) { this.accion = accion; }

    public EstadoSolicitud getEstadoAnterior() { return estadoAnterior; }
    public void setEstadoAnterior(EstadoSolicitud estadoAnterior) { this.estadoAnterior = estadoAnterior; }

    public EstadoSolicitud getEstadoNuevo() { return estadoNuevo; }
    public void setEstadoNuevo(EstadoSolicitud estadoNuevo) { this.estadoNuevo = estadoNuevo; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
}
