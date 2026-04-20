package co.edu.uniquindio.gestion_solicitudes.service;



import co.edu.uniquindio.gestion_solicitudes.dto.response.LoginResponse;
import co.edu.uniquindio.gestion_solicitudes.dto.request.UsuarioCreateRequest;
import co.edu.uniquindio.gestion_solicitudes.dto.request.LoginRequest;
import co.edu.uniquindio.gestion_solicitudes.dto.response.UsuarioResponse;

/**
 * Servicio de autenticación y registro de usuarios.
 * <p>
 * Define las operaciones de acceso al sistema: login con generación
 * de token JWT y registro de nuevos usuarios con validación de datos.
 * </p>
 *
 * @author Manu-Z
 * @version 1.0
 * @see AuthServiceImpl
 */
public interface AuthService {
    /**
     * Autentica un usuario y genera un token JWT.
     *
     * @param request objeto con correo y contraseña del usuario
     * @return token JWT, tipo, rol y nombre del usuario autenticado
     * @throws co.edu.uniquindio.gestion_solicitudes.exception.BadRequestException
     *         si el correo no existe o la contraseña es incorrecta
     */
    LoginResponse login(LoginRequest request);

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param request objeto con nombre, correo, contraseña y rol
     * @return datos del usuario registrado sin incluir la contraseña
     * @throws co.edu.uniquindio.gestion_solicitudes.exception.BadRequestException
     *         si el correo ya está registrado
     */
    UsuarioResponse registrar(UsuarioCreateRequest request);
}
