package co.edu.uniquindio.gestion_solicitudes.controller;

import co.edu.uniquindio.gestion_solicitudes.dto.request.LoginRequest;
import co.edu.uniquindio.gestion_solicitudes.dto.request.UsuarioCreateRequest;
import co.edu.uniquindio.gestion_solicitudes.dto.response.LoginResponse;
import co.edu.uniquindio.gestion_solicitudes.dto.response.UsuarioResponse;
import co.edu.uniquindio.gestion_solicitudes.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para la autenticación y registro de usuarios.
 * <p>
 * Expone los endpoints públicos del sistema que no requieren autenticación JWT.
 * Gestiona el inicio de sesión y el registro de nuevos usuarios.
 * </p>
 *
 * @author Manu-Z
 * @version 1.0
 * @see AuthService
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Autentica un usuario en el sistema y genera un token JWT.
     * <p>
     * Endpoint público — no requiere token de autorización.
     * El token retornado debe incluirse en las siguientes peticiones
     * en el header {@code Authorization: Bearer <token>}.
     * </p>
     *
     * @param request objeto con el correo y contraseña del usuario
     * @return {@code 200 OK} con el token JWT, tipo, rol y nombre del usuario
     * @throws co.edu.uniquindio.gestion_solicitudes.exception.BadRequestException
     *         si el correo no existe o la contraseña es incorrecta
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Registra un nuevo usuario en el sistema.
     * <p>
     * Endpoint público — no requiere token de autorización.
     * El correo debe ser único en el sistema. La contraseña se almacena
     * codificada con BCrypt, nunca en texto plano.
     * </p>
     *
     * @param request objeto con nombre, correo, contraseña y rol del nuevo usuario
     * @return {@code 201 Created} con los datos del usuario registrado
     * @throws co.edu.uniquindio.gestion_solicitudes.exception.BadRequestException
     *         si el correo ya está registrado en el sistema
     */
    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody UsuarioCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrar(request));
    }
}