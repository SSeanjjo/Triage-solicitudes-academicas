package co.edu.uniquindio.gestion_solicitudes.dto.response;

import co.edu.uniquindio.gestion_solicitudes.domain.EstadoSolicitud;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialEventoResponse {

    private Long id;
    private Long solicitudId;
    private Long usuarioId;
    private String accion;
    private EstadoSolicitud estadoAnterior;
    private EstadoSolicitud estadoNuevo;
    private LocalDateTime fecha;
    private String comentario;
}