package co.edu.uniquindio.gestion_solicitudes.domain;
/**
 * Define los tipos de solicitud académica manejados por el sistema.
 *
 * @author Manu-Z
 * @version 1.0
 */
public enum TipoSolicitud {
    /** Solicitud para registrar una asignatura. */
    REGISTRO_ASIGNATURA,
    /** Solicitud para homologar materias de otra institución. */
    HOMOLOGACION,
    /** Solicitud para cancelar una asignatura. */
    CANCELACION,
    /** Solicitud para obtener un cupo en una asignatura. */
    CUPO,
    /** Consulta general de índole académica. */
    CONSULTA_ACADEMICA
}
