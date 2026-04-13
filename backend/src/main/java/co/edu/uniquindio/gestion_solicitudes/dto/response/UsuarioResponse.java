package co.edu.uniquindio.gestion_solicitudes.dto.response;

import co.edu.uniquindio.gestion_solicitudes.domain.Rol;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponse {

    private Long id;
    private String nombre;
    private String correo;
    private Rol rol;
    private boolean activo;
}