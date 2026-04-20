package co.edu.uniquindio.gestion_solicitudes.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
/**
 * DTO para solicitar la generación de un resumen de IA (RF-09).
 *
 * @author Manu-Z
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumenIARequest {
    /** Identificador único de la solicitud a resumir. */
    @NotNull(message = "El id de la solicitud es obligatorio")
    private Long solicitudId;
}
