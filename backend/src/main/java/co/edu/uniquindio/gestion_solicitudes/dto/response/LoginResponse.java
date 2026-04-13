package co.edu.uniquindio.gestion_solicitudes.dto.response;

import co.edu.uniquindio.gestion_solicitudes.domain.Rol;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private String token;
    private String tipo;
    private Rol rol;
    private String nombre;
}