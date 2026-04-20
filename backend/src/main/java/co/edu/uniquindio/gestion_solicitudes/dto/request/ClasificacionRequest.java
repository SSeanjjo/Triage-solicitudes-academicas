package co.edu.uniquindio.gestion_solicitudes.dto.request;

import co.edu.uniquindio.gestion_solicitudes.domain.PrioridadSolicitud;
import co.edu.uniquindio.gestion_solicitudes.domain.TipoSolicitud;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO para clasificar una solicitud con tipo, prioridad y justificación.
 *
 * @author Manu-Z
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClasificacionRequest {
    /** Tipo académico de la solicitud. */
    @NotNull(message = "El tipo de solicitud es obligatorio")
    private TipoSolicitud tipoSolicitud;
    /** Nivel de prioridad asignado. */
    @NotNull(message = "La prioridad es obligatoria")
    private PrioridadSolicitud prioridad;
    /** Texto que justifica la prioridad asignada. Mínimo 10, máximo 500 caracteres. */
    @NotBlank(message = "La justificación es obligatoria")
    @Size(min = 10, max = 500, message = "La justificación debe tener entre 10 y 500 caracteres")
    private String justificacionPrioridad;
}