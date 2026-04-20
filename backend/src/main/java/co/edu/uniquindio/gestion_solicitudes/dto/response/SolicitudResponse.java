package co.edu.uniquindio.gestion_solicitudes.dto.response;

import co.edu.uniquindio.gestion_solicitudes.domain.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * DTO de respuesta con los datos de una solicitud académica.
 *
 * @author Manu-Z
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudResponse {
    /** Identificador único de la solicitud. */
    private Long id;
    /** Estado actual en el ciclo de vida. */
    private EstadoSolicitud estado;
    /** Prioridad asignada. Nulo si aún no fue clasificada. */
    private PrioridadSolicitud prioridad;
    /** Tipo académico. Nulo si aún no fue clasificada. */
    private TipoSolicitud tipoSolicitud;
    /** Canal por el que ingresó la solicitud. */
    private CanalOrigen canalOrigen;
    /** Id del usuario que creó la solicitud. */
    private Long solicitanteId;
    /** Id del responsable asignado. Nulo si aún no fue asignada. */
    private Long responsableId;
    /** Descripción original de la solicitud. */
    private String descripcion;
    /** Justificación de la prioridad asignada. */
    private String justificacionPrioridad;
    /** Observación registrada al cierre. */
    private String observacionCierre;
    /** Fecha y hora de creación. */
    private LocalDateTime fechaCreacion;
    /** Fecha y hora de la última actualización. */
    private LocalDateTime fechaActualizacion;
}