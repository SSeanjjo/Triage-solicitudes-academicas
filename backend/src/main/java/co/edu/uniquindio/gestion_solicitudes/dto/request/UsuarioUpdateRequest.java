package co.edu.uniquindio.gestion_solicitudes.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO para actualizar los datos de un usuario existente.
 * <p>
 * Usado en el endpoint {@code PUT /api/usuarios/{id}}.
 * Todos los campos son opcionales — solo los campos no nulos
 * serán actualizados, manteniendo los valores actuales para
 * los campos omitidos.
 * Solo accesible para el rol ADMINISTRADOR.
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
public class UsuarioUpdateRequest {

    /**
     * Nuevo nombre completo del usuario.
     * Si es nulo, el nombre actual no se modifica.
     * Mínimo 3 caracteres, máximo 100 caracteres.
     */
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nombre;

    /**
     * Nuevo correo electrónico del usuario.
     * Si es nulo, el correo actual no se modifica.
     * Debe tener formato de correo válido.
     */
    @Email(message = "El correo no tiene formato válido")
    private String correo;
}