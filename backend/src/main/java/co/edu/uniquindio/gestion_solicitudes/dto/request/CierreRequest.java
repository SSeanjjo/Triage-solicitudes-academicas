package co.edu.uniquindio.gestion_solicitudes.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO para cerrar formalmente una solicitud académica.
 * <p>
 * Usado en el endpoint {@code PATCH /api/solicitudes/{id}/cerrar}.
 * La solicitud debe estar en estado ATENDIDA para poder ser cerrada.
 * El comentario de cierre queda registrado en el historial de eventos.
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
public class CierreRequest {

    /**
     * Observación o comentario registrado al momento del cierre formal.
     * Mínimo 10 caracteres, máximo 500 caracteres.
     */
    @NotBlank(message = "El comentario de cierre es obligatorio")
    @Size(min = 10, max = 500, message = "El comentario debe tener entre 10 y 500 caracteres")
    private String comentarioCierre;
}