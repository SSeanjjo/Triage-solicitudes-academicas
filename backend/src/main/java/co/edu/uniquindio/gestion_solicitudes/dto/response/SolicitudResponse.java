package co.edu.uniquindio.gestion_solicitudes.dto.response;

import co.edu.uniquindio.gestion_solicitudes.domain.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudResponse {

    private Long id;
    private EstadoSolicitud estado;
    private PrioridadSolicitud prioridad;
    private TipoSolicitud tipoSolicitud;
    private CanalOrigen canalOrigen;
    private Long solicitanteId;
    private Long responsableId;
    private String descripcion;
    private String justificacionPrioridad;
    private String observacionCierre;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}