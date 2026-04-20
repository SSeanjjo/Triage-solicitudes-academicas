package co.edu.uniquindio.gestion_solicitudes.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad que registra cada evento ocurrido en el ciclo de vida de una solicitud.
 * <p>
 * Cada vez que una solicitud cambia de estado, se guarda un registro en esta
 * entidad con la fecha, el usuario que realizó la acción, el estado anterior
 * y el nuevo estado. Permite trazabilidad completa de la solicitud.
 * </p>
 *
 * @author Manu-Z
 * @version 1.0
 */
@Entity
@Table(name = "historial_solicitudes")
public class HistorialSolicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * Fecha y hora exacta en que ocurrió el evento.
     */
    private LocalDateTime fecha;
    /**
     * Descripción de la acción realizada. Ejemplos: "Solicitud creada",
     * "Responsable asignado", "Solicitud cerrada".
     */
    private String accion;
    /**
     * Comentario u observación adicional registrada por el usuario
     * al momento de ejecutar la acción. Puede ser nulo.
     */
    private String observacion;

    /**
     * Estado en que se encontraba la solicitud antes del evento.
     * Es nulo cuando el evento es la creación inicial.
     */
    @Enumerated(EnumType.STRING)
    private EstadoSolicitud estadoAnterior;
    /**
     * Estado al que transicionó la solicitud tras el evento.
     */
    @Enumerated(EnumType.STRING)
    private EstadoSolicitud estadoNuevo;

    /**
     * Solicitud a la que pertenece este evento del historial.
     */
    @ManyToOne
    @JoinColumn(name = "solicitud_id")
    private Solicitud solicitud;

    /**
     * Usuario que ejecutó la acción que generó este evento.
     */
    @ManyToOne
    @JoinColumn(name = "usuario_accion_id")
    private Usuario usuarioAccion;

    public HistorialSolicitud() {}

    /**
     * Construye un evento de historial con todos sus datos.
     *
     * @param solicitud      solicitud asociada al evento
     * @param usuarioAccion  usuario que ejecutó la acción
     * @param accion         descripción de la acción realizada
     * @param observacion    comentario adicional, puede ser nulo
     * @param estadoAnterior estado previo de la solicitud
     * @param estadoNuevo    nuevo estado tras la acción
     */
    public HistorialSolicitud(Solicitud solicitud, Usuario usuarioAccion, String accion, String observacion, EstadoSolicitud estadoAnterior, EstadoSolicitud estadoNuevo) {
        this.solicitud = solicitud;
        this.usuarioAccion = usuarioAccion;
        this.accion = accion;
        this.observacion = observacion;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.fecha = LocalDateTime.now();
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
