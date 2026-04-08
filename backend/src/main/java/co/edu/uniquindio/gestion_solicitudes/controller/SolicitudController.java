package co.edu.uniquindio.gestion_solicitudes.controller;



import co.edu.uniquindio.gestion_solicitudes.domain.EstadoSolicitud;
import co.edu.uniquindio.gestion_solicitudes.domain.PrioridadSolicitud;
import co.edu.uniquindio.gestion_solicitudes.domain.TipoSolicitud;
import co.edu.uniquindio.gestion_solicitudes.dto.*;
import co.edu.uniquindio.gestion_solicitudes.dto.HistorialEventoResponse;
import co.edu.uniquindio.gestion_solicitudes.dto.SolicitudResponse;
import co.edu.uniquindio.gestion_solicitudes.service.SolicitudService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {

    private final SolicitudService solicitudService;

    public SolicitudController(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }

    @PostMapping
    public ResponseEntity<SolicitudResponse> crear(
            @RequestBody SolicitudCreateRequest request,
            @RequestParam Long solicitanteId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(solicitudService.crear(request, solicitanteId));
    }

    @GetMapping
    public ResponseEntity<List<SolicitudResponse>> listar(
            @RequestParam(required = false) EstadoSolicitud estado,
            @RequestParam(required = false) TipoSolicitud tipo,
            @RequestParam(required = false) PrioridadSolicitud prioridad,
            @RequestParam(required = false) Long responsableId) {
        return ResponseEntity.ok(solicitudService.listar(estado, tipo, prioridad, responsableId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolicitudResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(solicitudService.obtenerPorId(id));
    }

    @PatchMapping("/{id}/clasificar")
    public ResponseEntity<SolicitudResponse> clasificar(
            @PathVariable Long id,
            @RequestBody ClasificacionRequest request,
            @RequestParam Long usuarioId) {
        return ResponseEntity.ok(solicitudService.clasificar(id, request, usuarioId));
    }

    @PatchMapping("/{id}/asignar")
    public ResponseEntity<SolicitudResponse> asignar(
            @PathVariable Long id,
            @RequestBody AsignacionRequest request,
            @RequestParam Long usuarioId) {
        return ResponseEntity.ok(solicitudService.asignar(id, request, usuarioId));
    }

    @PatchMapping("/{id}/atender")
    public ResponseEntity<SolicitudResponse> atender(
            @PathVariable Long id,
            @RequestBody AtenderRequest request,
            @RequestParam Long usuarioId) {
        return ResponseEntity.ok(solicitudService.atender(id, request, usuarioId));
    }

    @PatchMapping("/{id}/cerrar")
    public ResponseEntity<SolicitudResponse> cerrar(
            @PathVariable Long id,
            @RequestBody CierreRequest request,
            @RequestParam Long usuarioId) {
        return ResponseEntity.ok(solicitudService.cerrar(id, request, usuarioId));
    }

    @GetMapping("/{id}/historial")
    public ResponseEntity<List<HistorialEventoResponse>> obtenerHistorial(@PathVariable Long id) {
        return ResponseEntity.ok(solicitudService.obtenerHistorial(id));
    }
}
