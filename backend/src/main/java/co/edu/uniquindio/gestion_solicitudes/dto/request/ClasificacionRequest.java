package co.edu.uniquindio.gestion_solicitudes.dto.request;

import co.edu.uniquindio.gestion_solicitudes.domain.PrioridadSolicitud;
import co.edu.uniquindio.gestion_solicitudes.domain.TipoSolicitud;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClasificacionRequest {

    @NotNull(message = "El tipo de solicitud es obligatorio")
    private TipoSolicitud tipoSolicitud;

    @NotNull(message = "La prioridad es obligatoria")
    private PrioridadSolicitud prioridad;

    @NotBlank(message = "La justificación es obligatoria")
    @Size(min = 10, max = 500, message = "La justificación debe tener entre 10 y 500 caracteres")
    private String justificacionPrioridad;
}