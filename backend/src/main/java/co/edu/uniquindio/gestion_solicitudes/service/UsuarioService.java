package co.edu.uniquindio.gestion_solicitudes.service;


import co.edu.uniquindio.gestion_solicitudes.dto.UsuarioUpdateRequest;
import co.edu.uniquindio.gestion_solicitudes.dto.UsuarioResponse;
import co.edu.uniquindio.gestion_solicitudes.domain.Rol;
import java.util.List;

public interface UsuarioService {

    UsuarioResponse obtenerPorId(Long id);
    List<UsuarioResponse> listar(Boolean activo, Rol rol);
    UsuarioResponse actualizar(Long id, UsuarioUpdateRequest request);
}
