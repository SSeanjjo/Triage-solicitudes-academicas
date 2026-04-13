package co.edu.uniquindio.gestion_solicitudes.controller;



import co.edu.uniquindio.gestion_solicitudes.dto.request.ResumenIARequest;
import co.edu.uniquindio.gestion_solicitudes.dto.response.ResumenIAResponse;
import co.edu.uniquindio.gestion_solicitudes.dto.request.SugerenciaIARequest;
import co.edu.uniquindio.gestion_solicitudes.dto.response.SugerenciaIAResponse;
import co.edu.uniquindio.gestion_solicitudes.service.IAService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ia")
@RequiredArgsConstructor
public class IAController {

    private final IAService iaService;

    @PostMapping("/sugerencias/clasificacion")
    public ResponseEntity<SugerenciaIAResponse> sugerirClasificacion(
            @Valid @RequestBody SugerenciaIARequest request) {
        try {
            return ResponseEntity.ok(iaService.sugerirClasificacion(request.getDescripcion()));
        } catch (Exception e) {
            return ResponseEntity.status(503).build();
        }
    }

    @PostMapping("/resumen")
    public ResponseEntity<ResumenIAResponse> generarResumen(
            @Valid @RequestBody ResumenIARequest request) {
        try {
            return ResponseEntity.ok(iaService.generarResumen(request.getSolicitudId()));
        } catch (Exception e) {
            return ResponseEntity.status(503).build();
        }
    }
}