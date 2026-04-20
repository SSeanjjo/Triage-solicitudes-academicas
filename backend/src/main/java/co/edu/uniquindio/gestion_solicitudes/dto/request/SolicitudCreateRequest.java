package co.edu.uniquindio.gestion_solicitudes.dto.request;

import co.edu.uniquindio.gestion_solicitudes.domain.CanalOrigen;
import co.edu.uniquindio.gestion_solicitudes.domain.TipoSolicitud;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
/**
 * DTO para crear una nueva solicitud académica.
 *
 * @author Manu-Z
 * @version 1.0
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudCreateRequest {
    /** Tipo de solicitud académica. */
    @NotNull(message = "El tipo de solicitud es obligatorio")
    private TipoSolicitud tipoSolicitud;

    /** Descripción detallada. Mínimo 10, máximo 1000 caracteres. */
    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 10, max = 1000, message = "La descripción debe tener entre 10 y 1000 caracteres")
    private String descripcion;

    /** Canal por el que ingresa la solicitud. */
    @NotNull(message = "El canal de origen es obligatorio")
    private CanalOrigen canalOrigen;
}