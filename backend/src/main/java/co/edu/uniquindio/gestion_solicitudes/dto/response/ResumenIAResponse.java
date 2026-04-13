package co.edu.uniquindio.gestion_solicitudes.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumenIAResponse {

    private String resumen;
    private String generadoPor;
}
