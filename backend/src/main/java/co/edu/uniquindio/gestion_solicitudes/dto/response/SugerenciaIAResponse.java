package co.edu.uniquindio.gestion_solicitudes.dto.response;



import co.edu.uniquindio.gestion_solicitudes.domain.TipoSolicitud;
import co.edu.uniquindio.gestion_solicitudes.domain.PrioridadSolicitud;
import lombok.*;
/**
 * DTO de respuesta con la sugerencia de clasificación generada por IA (RF-10).
 * <p>
 * Esta sugerencia debe ser confirmada o ajustada por un usuario humano
 * antes de aplicarse a la solicitud.
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
public class SugerenciaIAResponse {
    /** Tipo de solicitud sugerido por la IA. */
    private TipoSolicitud tipoSugerido;
    /** Prioridad sugerida por la IA. */
    private PrioridadSolicitud prioridadSugerida;
    /** Nivel de confianza de la sugerencia, entre 0.0 y 1.0. */
    private float confianza;
    /** Explicación del criterio usado para generar la sugerencia. */
    private String explicacion;
}
