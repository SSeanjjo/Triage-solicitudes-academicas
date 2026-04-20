package co.edu.uniquindio.gestion_solicitudes.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO para asignar un responsable a una solicitud académica.
 * <p>
 * Usado en el endpoint {@code PATCH /api/solicitudes/{id}/asignar}.
 * La solicitud debe estar en estado CLASIFICADA para poder ser asignada.
 * El responsable indicado debe estar activo en el sistema.
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
public class AsignacionRequest {

    /**
     * Identificador único del usuario responsable a asignar.
     * El usuario debe existir y tener estado activo en el sistema.
     */
    @NotNull(message = "El responsable es obligatorio")
    private Long responsableId;
}