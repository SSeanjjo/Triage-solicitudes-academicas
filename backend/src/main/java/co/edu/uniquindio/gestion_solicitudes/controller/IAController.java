package co.edu.uniquindio.gestion_solicitudes.controller;

import co.edu.uniquindio.gestion_solicitudes.dto.request.ResumenIARequest;
import co.edu.uniquindio.gestion_solicitudes.dto.response.ResumenIAResponse;
import co.edu.uniquindio.gestion_solicitudes.dto.request.SugerenciaIARequest;
import co.edu.uniquindio.gestion_solicitudes.dto.response.SugerenciaIAResponse;
import co.edu.uniquindio.gestion_solicitudes.service.IAService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para los endpoints de inteligencia artificial.
 * <p>
 * Expone las funcionalidades de IA del sistema: sugerencia de clasificación (RF-10)
 * y generación de resúmenes (RF-09). Todas las operaciones requieren autenticación
 * mediante JWT y los permisos correspondientes al rol del usuario.
 * </p>
 *
 * @author Manu-Z,SseanJjo
 * @version 1.0
 * @see IAService
 */

@RestController
@RequestMapping("/api/ia")
@RequiredArgsConstructor
public class IAController {

    private final IAService iaService;

    /**
     * Sugiere el tipo y prioridad de una solicitud a partir de su descripción (RF-10).
     * <p>
     * La sugerencia es generada por IA y debe ser confirmada o ajustada
     * por un usuario humano antes de aplicarse.
     * Accesible para roles: ADMINISTRADOR, RESPONSABLE, ESTUDIANTE.
     * </p>
     *
     * @param request objeto con la descripción textual de la solicitud
     * @return {@code 200 OK} con la sugerencia de clasificación,
     *         o {@code 503 Service Unavailable} si el servicio de IA no responde
     */

    @PostMapping("/sugerencias/clasificacion")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RESPONSABLE', 'ESTUDIANTE')")
    public ResponseEntity<SugerenciaIAResponse> sugerirClasificacion(
            @Valid @RequestBody SugerenciaIARequest request) {
        try {
            return ResponseEntity.ok(iaService.sugerirClasificacion(request.getDescripcion()));
        } catch (Exception e) {
            return ResponseEntity.status(503).build();
        }
    }

    /**
     * Genera un resumen textual del estado e historial de una solicitud (RF-09).
     * <p>
     * Permite a los responsables comprender rápidamente el caso sin revisar
     * todo el historial. Accesible para roles: ADMINISTRADOR, RESPONSABLE.
     * </p>
     *
     * @param request objeto con el id de la solicitud a resumir
     * @return {@code 200 OK} con el resumen generado,
     *         o {@code 503 Service Unavailable} si el servicio de IA no responde
     */

    @PostMapping("/resumen")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RESPONSABLE')")
    public ResponseEntity<ResumenIAResponse> generarResumen(
            @Valid @RequestBody ResumenIARequest request) {
        try {
            return ResponseEntity.ok(iaService.generarResumen(request.getSolicitudId()));
        } catch (Exception e) {
            return ResponseEntity.status(503).build();
        }
    }
}