package co.edu.uniquindio.gestion_solicitudes.dto.response;

import co.edu.uniquindio.gestion_solicitudes.domain.EstadoSolicitud;
import lombok.*;
import java.time.LocalDateTime;
/**
 * DTO de respuesta con un evento del historial de una solicitud.
 *
 * @author Manu-Z
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialEventoResponse {
    /** Identificador del evento. */
    private Long id;
    /** Id de la solicitud a la que pertenece el evento. */
    private Long solicitudId;
    /** Id del usuario que ejecutó la acción. */
    private Long usuarioId;
    /** Descripción de la acción realizada. */
    private String accion;
    /** Estado anterior de la solicitud antes del evento. */
    private EstadoSolicitud estadoAnterior;
    /** Nuevo estado tras el evento. */
    private EstadoSolicitud estadoNuevo;
    /** Fecha y hora del evento. */
    private LocalDateTime fecha;
    /** Comentario registrado por el usuario al ejecutar la acción. */
    private String comentario;
}