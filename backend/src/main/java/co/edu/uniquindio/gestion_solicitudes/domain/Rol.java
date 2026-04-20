package co.edu.uniquindio.gestion_solicitudes.domain;

/**
 * Define los roles disponibles en el sistema.
 * <p>
 * Cada rol determina qué operaciones puede realizar el usuario:
 * <ul>
 *   <li>ESTUDIANTE: crear y consultar sus propias solicitudes</li>
 *   <li>RESPONSABLE: clasificar, atender y cerrar solicitudes asignadas</li>
 *   <li>ADMINISTRADOR: acceso completo al sistema</li>
 * </ul>
 * </p>
 *
 * @author Manu-Z
 * @version 1.0
 */
public enum Rol {
    /** Usuario que genera solicitudes académicas. */
    ESTUDIANTE,
    /** Usuario que atiende y gestiona solicitudes. */
    RESPONSABLE,
    /** Usuario con acceso total al sistema. */
    ADMINISTRADOR
}
