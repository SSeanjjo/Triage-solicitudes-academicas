package co.edu.uniquindio.gestion_solicitudes.domain;

/**
 * Define los posibles estados de una solicitud académica.
 * <p>
 * El ciclo de vida obligatorio es:
 * REGISTRADA → CLASIFICADA → EN_ATENCION → ATENDIDA → CERRADA.
 * No se permiten transiciones que salten estados ni retrocesos.
 * </p>
 *
 * @author Manu-Z
 * @version 1.0
 */
public enum EstadoSolicitud {
    /** Solicitud recién creada, pendiente de clasificación. */
    REGISTRADA,
    /** Solicitud con tipo y prioridad asignados, pendiente de atención. */
    CLASIFICADA,
    /** Solicitud con responsable asignado y en proceso de atención. */
    EN_ATENCION,
    /** Solicitud atendida, pendiente de cierre formal. */
    ATENDIDA,
    /** Solicitud cerrada formalmente. Estado terminal. */
    CERRADA
}
