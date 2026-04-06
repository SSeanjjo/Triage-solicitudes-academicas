package co.edu.uniquindio.gestion_solicitudes.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class HistorialSolicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fecha;
    private String accion;
    private String observacion;

    @Enumerated(EnumType.STRING)
    private EstadoSolicitud estadoAnterior;

    @Enumerated(EnumType.STRING)
    private EstadoSolicitud estadoNuevo;

    @ManyToOne
    private Solicitud solicitud;

    @ManyToOne
    private Usuario usuarioAccion;

    public HistorialSolicitud() {}

    public HistorialSolicitud(Solicitud solicitud, Usuario usuarioAccion, String accion, String observacion, EstadoSolicitud estadoAnterior, EstadoSolicitud estadoNuevo) {
        this.solicitud = solicitud;
        this.usuarioAccion = usuarioAccion;
        this.accion = accion;
        this.observacion = observacion;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.fecha = LocalDateTime.now();
    }

    public void registrarEvento(String accion, Usuario usuario) {
        this.accion = accion;
        this.usuarioAccion = usuario;
        this.fecha = LocalDateTime.now();
        if (solicitud != null) {
            this.estadoAnterior = solicitud.getEstado();
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public EstadoSolicitud getEstadoAnterior() {
        return estadoAnterior;
    }

    public void setEstadoAnterior(EstadoSolicitud estadoAnterior) {
        this.estadoAnterior = estadoAnterior;
    }

    public EstadoSolicitud getEstadoNuevo() {
        return estadoNuevo;
    }

    public void setEstadoNuevo(EstadoSolicitud estadoNuevo) {
        this.estadoNuevo = estadoNuevo;
    }

    public Solicitud getSolicitud() {
        return solicitud;
    }

    public void setSolicitud(Solicitud solicitud) {
        this.solicitud = solicitud;
    }

    public Usuario getUsuarioAccion() {
        return usuarioAccion;
    }

    public void setUsuarioAccion(Usuario usuarioAccion) {
        this.usuarioAccion = usuarioAccion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HistorialSolicitud that = (HistorialSolicitud) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
