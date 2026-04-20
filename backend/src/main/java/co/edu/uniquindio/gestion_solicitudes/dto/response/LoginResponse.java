package co.edu.uniquindio.gestion_solicitudes.dto.response;

import co.edu.uniquindio.gestion_solicitudes.domain.Rol;
import lombok.*;

/**
 * DTO de respuesta tras un login exitoso.
 *
 * @author Manu-Z
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    /** Token JWT para autenticar las siguientes peticiones. */
    private String token;
    /** Tipo de token. Siempre "Bearer". */
    private String tipo;
    /** Rol del usuario autenticado. */
    private Rol rol;
    /** Nombre del usuario autenticado. */
    private String nombre;
}