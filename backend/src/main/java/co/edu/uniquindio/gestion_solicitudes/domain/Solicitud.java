package co.edu.uniquindio.gestion_solicitudes.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descripcion;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    @Enumerated(EnumType.STRING)
    private EstadoSolicitud estado;

    @Enumerated(EnumType.STRING)
    private PrioridadSolicitud prioridad;

    private String justificacionPrioridad;

    @Enumerated(EnumType.STRING)
    private TipoSolicitud tipo;

    @Enumerated(EnumType.STRING)
    private CanalOrigen canalOrigen;

    private String observacionCierre;

    @ManyToOne
    private Usuario solicitante;

    @ManyToOne
    private Usuario responsable;

    @OneToMany(mappedBy = "solicitud", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistorialSolicitud> historial = new ArrayList<>();

    public Solicitud() {}

    public Solicitud(String descripcion, Usuario solicitante, CanalOrigen canalOrigen) {
        this.descripcion = descripcion;
        this.solicitante = solicitante;
        this.canalOrigen = canalOrigen;
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
        this.estado = EstadoSolicitud.REGISTRADA;
    }

    public void clasificar(TipoSolicitud tipo, PrioridadSolicitud prioridad, String justificacion) {
        this.tipo = tipo;
        this.prioridad = prioridad;
        this.justificacionPrioridad = justificacion;
        this.estado = EstadoSolicitud.CLASIFICADA;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public void asignarResponsable(Usuario responsable) {
        this.responsable = responsable;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public void marcarEnAtencion() {
        this.estado = EstadoSolicitud.EN_ATENCION;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public void marcarAtendida(String comentario) {
        this.estado = EstadoSolicitud.ATENDIDA;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public void cerrar(String observacionCierre) {
        this.observacionCierre = observacionCierre;
        this.estado = EstadoSolicitud.CERRADA;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public boolean puedeTransicionarA(EstadoSolicitud nuevoEstado) {
        switch (this.estado) {
            case REGISTRADA:
                return nuevoEstado == EstadoSolicitud.CLASIFICADA;
            case CLASIFICADA:
                return nuevoEstado == EstadoSolicitud.EN_ATENCION;
            case EN_ATENCION:
                return nuevoEstado == EstadoSolicitud.ATENDIDA;
            case ATENDIDA:
                return nuevoEstado == EstadoSolicitud.CERRADA;
            case CERRADA:
                return false;
            default:
                return false;
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public EstadoSolicitud getEstado() {
        return estado;
    }

    public void setEstado(EstadoSolicitud estado) {
        this.estado = estado;
    }

    public PrioridadSolicitud getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(PrioridadSolicitud prioridad) {
        this.prioridad = prioridad;
    }

    public String getJustificacionPrioridad() {
        return justificacionPrioridad;
    }

    public void setJustificacionPrioridad(String justificacionPrioridad) {
        this.justificacionPrioridad = justificacionPrioridad;
    }

    public TipoSolicitud getTipo() {
        return tipo;
    }

    public void setTipo(TipoSolicitud tipo) {
        this.tipo = tipo;
    }

    public CanalOrigen getCanalOrigen() {
        return canalOrigen;
    }

    public void setCanalOrigen(CanalOrigen canalOrigen) {
        this.canalOrigen = canalOrigen;
    }

    public String getObservacionCierre() {
        return observacionCierre;
    }

    public void setObservacionCierre(String observacionCierre) {
        this.observacionCierre = observacionCierre;
    }

    public Usuario getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(Usuario solicitante) {
        this.solicitante = solicitante;
    }

    public Usuario getResponsable() {
        return responsable;
    }

    public void setResponsable(Usuario responsable) {
        this.responsable = responsable;
    }

    public List<HistorialSolicitud> getHistorial() {
        return historial;
    }

    public void setHistorial(List<HistorialSolicitud> historial) {
        this.historial = historial;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Solicitud solicitud = (Solicitud) o;
        return Objects.equals(id, solicitud.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
