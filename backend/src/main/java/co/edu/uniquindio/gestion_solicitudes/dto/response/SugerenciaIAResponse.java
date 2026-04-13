package co.edu.uniquindio.gestion_solicitudes.dto.response;



import co.edu.uniquindio.gestion_solicitudes.domain.TipoSolicitud;
import co.edu.uniquindio.gestion_solicitudes.domain.PrioridadSolicitud;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SugerenciaIAResponse {

    private TipoSolicitud tipoSugerido;
    private PrioridadSolicitud prioridadSugerida;
    private float confianza;
    private String explicacion;
}
