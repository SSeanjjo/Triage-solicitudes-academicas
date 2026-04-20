package co.edu.uniquindio.gestion_solicitudes.controller;

import co.edu.uniquindio.gestion_solicitudes.domain.Rol;
import co.edu.uniquindio.gestion_solicitudes.dto.request.UsuarioUpdateRequest;
import co.edu.uniquindio.gestion_solicitudes.dto.response.UsuarioResponse;
import co.edu.uniquindio.gestion_solicitudes.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de usuarios del sistema.
 * <p>
 * Expone los endpoints para consultar, listar y actualizar usuarios.
 * Todas las operaciones están restringidas al rol ADMINISTRADOR,
 * ya que implican acceso a datos de otros usuarios del sistema.
 * </p>
 *
 * @author Manu-Z
 * @version 1.0
 * @see UsuarioService
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Lista todos los usuarios del sistema aplicando filtros opcionales.
     * <p>
     * Si no se proporcionan filtros, retorna todos los usuarios registrados.
     * Solo accesible para el rol ADMINISTRADOR.
     * </p>
     *
     * @param activo filtra por estado del usuario: {@code true} activos,
     *               {@code false} inactivos, {@code null} todos (opcional)
     * @param rol    filtra por rol: ESTUDIANTE, RESPONSABLE o ADMINISTRADOR (opcional)
     * @return {@code 200 OK} con la lista de usuarios que cumplen los filtros
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<UsuarioResponse>> listar(
            @RequestParam(required = false) Boolean activo,
            @RequestParam(required = false) Rol rol) {
        return ResponseEntity.ok(usuarioService.listar(activo, rol));
    }

    /**
     * Obtiene los datos de un usuario específico por su identificador.
     * <p>
     * Solo accesible para el rol ADMINISTRADOR.
     * </p>
     *
     * @param id identificador único del usuario
     * @return {@code 200 OK} con los datos del usuario
     * @throws co.edu.uniquindio.gestion_solicitudes.exception.ResourceNotFoundException
     *         si no existe un usuario con el id proporcionado
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<UsuarioResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    /**
     * Actualiza los datos de un usuario existente.
     * <p>
     * Solo los campos proporcionados en el request serán actualizados.
     * Los campos nulos se ignoran, manteniendo el valor actual.
     * Solo accesible para el rol ADMINISTRADOR.
     * </p>
     *
     * @param id      identificador del usuario a actualizar
     * @param request objeto con los campos a actualizar: nombre y/o correo
     * @return {@code 200 OK} con los datos actualizados del usuario
     * @throws co.edu.uniquindio.gestion_solicitudes.exception.ResourceNotFoundException
     *         si no existe un usuario con el id proporcionado
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<UsuarioResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioUpdateRequest request) {
        return ResponseEntity.ok(usuarioService.actualizar(id, request));
    }
}