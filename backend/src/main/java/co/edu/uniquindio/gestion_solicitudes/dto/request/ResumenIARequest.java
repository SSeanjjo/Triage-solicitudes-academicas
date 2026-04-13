package co.edu.uniquindio.gestion_solicitudes.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumenIARequest {

    @NotNull(message = "El id de la solicitud es obligatorio")
    private Long solicitudId;
}
