package co.edu.uniquindio.gestion_solicitudes.domain;
/**
 * Define los canales por los que puede ingresar una solicitud al sistema.
 *
 * @author Manu-Z
 * @version 1.0
 */
public enum CanalOrigen {
    /** Centro de Servicios Universitarios. */
    CSU,
    /** Canal de correo electrónico. */
    CORREO,
    /** Sistema de Atención al Ciudadano. */
    SAC,
    /** Canal telefónico. */
    TELEFONICO
}
