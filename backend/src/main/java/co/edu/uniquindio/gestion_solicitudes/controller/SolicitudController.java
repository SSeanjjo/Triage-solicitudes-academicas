package co.edu.uniquindio.gestion_solicitudes.controller;

import co.edu.uniquindio.gestion_solicitudes.domain.EstadoSolicitud;
import co.edu.uniquindio.gestion_solicitudes.domain.PrioridadSolicitud;
import co.edu.uniquindio.gestion_solicitudes.domain.TipoSolicitud;
import co.edu.uniquindio.gestion_solicitudes.dto.request.*;
import co.edu.uniquindio.gestion_solicitudes.dto.response.HistorialEventoResponse;
import co.edu.uniquindio.gestion_solicitudes.dto.response.SolicitudResponse;
import co.edu.uniquindio.gestion_solicitudes.service.SolicitudService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión del ciclo de vida de las solicitudes académicas.
 * <p>
 * Expone los endpoints para crear, consultar y transicionar solicitudes a través
 * de sus estados: REGISTRADA → CLASIFICADA → EN_ATENCION → ATENDIDA → CERRADA.
 * Cada operación registra un evento en el historial de la solicitud.
 * </p>
 *
 * @author Manu-Z
 * @version 1.0
 * @see SolicitudService
 */

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {

    private final SolicitudService solicitudService;

    public SolicitudController(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }

    /**
     * Crea una nueva solicitud académica en estado REGISTRADA.
     * Solo accesible para usuarios con rol ESTUDIANTE.
     *
     * @param request       datos de la solicitud: tipo, descripción y canal de origen
     * @param solicitanteId id del usuario que realiza la solicitud
     * @return {@code 201 Created} con los datos de la solicitud creada
     */

    @PostMapping
    @PreAuthorize("hasRole('ESTUDIANTE')")
    public ResponseEntity<SolicitudResponse> crear(
            @Valid @RequestBody SolicitudCreateRequest request,
            @RequestParam Long solicitanteId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(solicitudService.crear(request, solicitanteId));
    }

    /**
     * Lista las solicitudes aplicando filtros opcionales.
     *
     * @param estado        filtra por estado de la solicitud (opcional)
     * @param tipo          filtra por tipo de solicitud (opcional)
     * @param prioridad     filtra por prioridad (opcional)
     * @param responsableId filtra por responsable asignado (opcional)
     * @return {@code 200 OK} con la lista de solicitudes que cumplen los filtros
     */

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RESPONSABLE', 'ESTUDIANTE')")
    public ResponseEntity<List<SolicitudResponse>> listar(
            @RequestParam(required = false) EstadoSolicitud estado,
            @RequestParam(required = false) TipoSolicitud tipo,
            @RequestParam(required = false) PrioridadSolicitud prioridad,
            @RequestParam(required = false) Long responsableId) {
        return ResponseEntity.ok(solicitudService.listar(estado, tipo, prioridad, responsableId));
    }

    /**
     * Obtiene una solicitud por su identificador único.
     *
     * @param id identificador de la solicitud
     * @return {@code 200 OK} con los datos de la solicitud
     * @throws ResourceNotFoundException si no existe la solicitud
     */

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RESPONSABLE', 'ESTUDIANTE')")
    public ResponseEntity<SolicitudResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(solicitudService.obtenerPorId(id));
    }

    /**
     * Clasifica una solicitud asignándole tipo y prioridad. Transiciona de
     * REGISTRADA a CLASIFICADA. Solo para ADMINISTRADOR y RESPONSABLE.
     *
     * @param id       id de la solicitud a clasificar
     * @param request  tipo, prioridad y justificación
     * @param usuarioId id del usuario que realiza la acción
     * @return {@code 200 OK} con la solicitud actualizada
     */

    @PatchMapping("/{id}/clasificar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RESPONSABLE')")
    public ResponseEntity<SolicitudResponse> clasificar(
            @PathVariable Long id,
            @Valid @RequestBody ClasificacionRequest request,
            @RequestParam Long usuarioId) {
        return ResponseEntity.ok(solicitudService.clasificar(id, request, usuarioId));
    }

    /**
     * Asigna un responsable a la solicitud y la transiciona a EN_ATENCION.
     * Solo para ADMINISTRADOR y RESPONSABLE.
     *
     * @param id        id de la solicitud
     * @param request   id del responsable a asignar
     * @param usuarioId id del usuario que realiza la acción
     * @return {@code 200 OK} con la solicitud actualizada
     */

    @PatchMapping("/{id}/asignar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RESPONSABLE')")
    public ResponseEntity<SolicitudResponse> asignar(
            @PathVariable Long id,
            @Valid @RequestBody AsignacionRequest request,
            @RequestParam Long usuarioId) {
        return ResponseEntity.ok(solicitudService.asignar(id, request, usuarioId));
    }

    /**
     * Marca la solicitud como ATENDIDA. Solo para ADMINISTRADOR y RESPONSABLE.
     *
     * @param id        id de la solicitud
     * @param request   comentario de la atención realizada
     * @param usuarioId id del usuario que realiza la acción
     * @return {@code 200 OK} con la solicitud actualizada
     */

    @PatchMapping("/{id}/atender")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RESPONSABLE')")
    public ResponseEntity<SolicitudResponse> atender(
            @PathVariable Long id,
            @Valid @RequestBody AtenderRequest request,
            @RequestParam Long usuarioId) {
        return ResponseEntity.ok(solicitudService.atender(id, request, usuarioId));
    }

    /**
     * Cierra formalmente la solicitud. Transiciona de ATENDIDA a CERRADA.
     * Solo para ADMINISTRADOR y RESPONSABLE.
     *
     * @param id        id de la solicitud
     * @param request   observación de cierre
     * @param usuarioId id del usuario que realiza la acción
     * @return {@code 200 OK} con la solicitud actualizada
     */

    @PatchMapping("/{id}/cerrar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RESPONSABLE')")
    public ResponseEntity<SolicitudResponse> cerrar(
            @PathVariable Long id,
            @Valid @RequestBody CierreRequest request,
            @RequestParam Long usuarioId) {
        return ResponseEntity.ok(solicitudService.cerrar(id, request, usuarioId));
    }

    /**
     * Obtiene el historial completo de eventos de una solicitud.
     *
     * @param id id de la solicitud
     * @return {@code 200 OK} con la lista de eventos ordenados cronológicamente
     */

    @GetMapping("/{id}/historial")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RESPONSABLE', 'ESTUDIANTE')")
    public ResponseEntity<List<HistorialEventoResponse>> obtenerHistorial(@PathVariable Long id) {
        return ResponseEntity.ok(solicitudService.obtenerHistorial(id));
    }
}