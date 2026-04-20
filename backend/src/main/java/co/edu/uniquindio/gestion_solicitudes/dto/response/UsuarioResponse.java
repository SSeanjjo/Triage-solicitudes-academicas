package co.edu.uniquindio.gestion_solicitudes.dto.response;

import co.edu.uniquindio.gestion_solicitudes.domain.Rol;

import lombok.*;

/**
 * DTO de respuesta con los datos públicos de un usuario del sistema.
 * <p>
 * Usado como respuesta en los endpoints de autenticación, registro
 * y gestión de usuarios. No incluye la contraseña ni datos sensibles.
 * </p>
 *
 * @author Manu-Z
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponse {
    /** Identificador único del usuario. */
    private Long id;
    /** Nombre completo del usuario. */
    private String nombre;
    /** Correo electrónico del usuario. */
    private String correo;
    /** Rol asignado al usuario en el sistema. */
    private Rol rol;
    /** Indica si el usuario está activo en el sistema. */
    private boolean activo;
}