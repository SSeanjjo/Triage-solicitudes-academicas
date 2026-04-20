package co.edu.uniquindio.gestion_solicitudes.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
/**
 * DTO para la solicitud de login de un usuario.
 *
 * @author Manu-Z
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    /** Correo electrónico registrado del usuario. */

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene formato válido")
    private String correo;

    /** Contraseña en texto plano (se compara contra el hash almacenado). */
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}