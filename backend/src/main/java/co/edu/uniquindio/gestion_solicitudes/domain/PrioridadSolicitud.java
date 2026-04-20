package co.edu.uniquindio.gestion_solicitudes.domain;
/**
 * Define los niveles de prioridad de una solicitud académica.
 *
 * @author Manu-Z
 * @version 1.0
 */
public enum PrioridadSolicitud {
    /** Requiere atención inmediata. */
    ALTA,
    /** Requiere atención en tiempo razonable. */
    MEDIA,
    /** Puede atenderse cuando haya disponibilidad. */
    BAJA
}