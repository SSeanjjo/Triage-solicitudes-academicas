package co.edu.uniquindio.gestion_solicitudes.service;

/**
 * Servicio para la gestión y consulta de usuarios del sistema.
 * <p>
 * Define las operaciones disponibles sobre usuarios: consulta individual,
 * listado con filtros y actualización de datos. Restringido al rol ADMINISTRADOR.
 * </p>
 *
 * @author Manu-Z
 * @version 1.0
 * @see UsuarioServiceImpl
 */
import co.edu.uniquindio.gestion_solicitudes.dto.request.UsuarioUpdateRequest;
import co.edu.uniquindio.gestion_solicitudes.dto.response.UsuarioResponse;
import co.edu.uniquindio.gestion_solicitudes.domain.Rol;
import java.util.List;

public interface UsuarioService {
    /**
     * Obtiene un usuario por su identificador único.
     *
     * @param id id del usuario
     * @return datos del usuario encontrado
     * @throws co.edu.uniquindio.gestion_solicitudes.exception.ResourceNotFoundException
     *         si no existe el usuario
     */
    UsuarioResponse obtenerPorId(Long id);

    /**
     * Lista todos los usuarios aplicando filtros opcionales combinables.
     * Los filtros nulos son ignorados y retorna todos los usuarios.
     *
     * @param activo filtra por estado: {@code true} activos,
     *               {@code false} inactivos, {@code null} todos
     * @param rol    filtra por rol del usuario (opcional)
     * @return lista de usuarios que cumplen los filtros activos
     */
    List<UsuarioResponse> listar(Boolean activo, Rol rol);
    /**
     * Actualiza los datos de un usuario existente.
     * Solo los campos no nulos del request son actualizados.
     *
     * @param id      id del usuario a actualizar
     * @param request campos a actualizar: nombre y/o correo
     * @return datos actualizados del usuario
     * @throws co.edu.uniquindio.gestion_solicitudes.exception.ResourceNotFoundException
     *         si no existe el usuario
     */
    UsuarioResponse actualizar(Long id, UsuarioUpdateRequest request);
}
