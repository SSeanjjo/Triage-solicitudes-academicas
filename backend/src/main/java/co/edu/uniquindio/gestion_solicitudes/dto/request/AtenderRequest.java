package co.edu.uniquindio.gestion_solicitudes.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
/**
 * DTO para marcar una solicitud académica como atendida.
 * <p>
 * Usado en el endpoint {@code PATCH /api/solicitudes/{id}/atender}.
 * La solicitud debe estar en estado EN_ATENCION para poder ser atendida.
 * El comentario queda registrado en el historial de eventos como
 * evidencia de la atención realizada.
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
public class AtenderRequest {

    /**
     * Comentario que describe la atención realizada sobre la solicitud.
     * Mínimo 10 caracteres, máximo 500 caracteres.
     */
    @NotBlank(message = "El comentario es obligatorio")
    @Size(min = 10, max = 500, message = "El comentario debe tener entre 10 y 500 caracteres")
    private String comentario;
}