package co.edu.uniquindio.gestion_solicitudes.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsignacionRequest {

    @NotNull(message = "El responsable es obligatorio")
    private Long responsableId;
}