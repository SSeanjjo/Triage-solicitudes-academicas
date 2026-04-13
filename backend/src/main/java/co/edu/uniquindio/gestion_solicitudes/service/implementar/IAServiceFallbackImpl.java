package co.edu.uniquindio.gestion_solicitudes.service.implementar;

import co.edu.uniquindio.gestion_solicitudes.domain.*;
import co.edu.uniquindio.gestion_solicitudes.dto.response.ResumenIAResponse;
import co.edu.uniquindio.gestion_solicitudes.dto.response.SugerenciaIAResponse;
import co.edu.uniquindio.gestion_solicitudes.exception.ResourceNotFoundException;
import co.edu.uniquindio.gestion_solicitudes.repository.SolicitudRepository;
import co.edu.uniquindio.gestion_solicitudes.service.IAService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class IAServiceFallbackImpl implements IAService {

    private final SolicitudRepository solicitudRepository;

    private static final Map<String, TipoSolicitud> KEYWORDS_TIPO = Map.of(
            "homologacion", TipoSolicitud.HOMOLOGACION,
            "homologar", TipoSolicitud.HOMOLOGACION,
            "cupo", TipoSolicitud.CUPO,
            "cancelar", TipoSolicitud.CANCELACION,
            "cancelacion", TipoSolicitud.CANCELACION,
            "registro", TipoSolicitud.REGISTRO_ASIGNATURA,
            "asignatura", TipoSolicitud.REGISTRO_ASIGNATURA,
            "consulta", TipoSolicitud.CONSULTA_ACADEMICA,
            "informacion", TipoSolicitud.CONSULTA_ACADEMICA
    );

    private static final Map<String, PrioridadSolicitud> KEYWORDS_PRIORIDAD = Map.of(
            "urgente", PrioridadSolicitud.ALTA,
            "critico", PrioridadSolicitud.ALTA,
            "inmediato", PrioridadSolicitud.ALTA,
            "importante", PrioridadSolicitud.ALTA,
            "plazo", PrioridadSolicitud.MEDIA,
            "pronto", PrioridadSolicitud.MEDIA
    );

    @Override
    public SugerenciaIAResponse sugerirClasificacion(String descripcion) {
        log.info("Sugerencia por REGLAS (fallback)");
        String texto = descripcion.toLowerCase().trim();

        TipoSolicitud tipo = null;
        PrioridadSolicitud prioridad = PrioridadSolicitud.BAJA;
        float confianza = 0.0f;
        StringBuilder explicacion = new StringBuilder();

        for (Map.Entry<String, TipoSolicitud> entry : KEYWORDS_TIPO.entrySet()) {
            if (texto.contains(entry.getKey())) {
                tipo = entry.getValue();
                confianza = 0.7f;
                explicacion.append("Palabra clave '").append(entry.getKey())
                        .append("' sugiere ").append(entry.getValue()).append(". ");
                break;
            }
        }

        for (Map.Entry<String, PrioridadSolicitud> entry : KEYWORDS_PRIORIDAD.entrySet()) {
            if (texto.contains(entry.getKey())) {
                prioridad = entry.getValue();
                confianza = Math.min(confianza + 0.15f, 1.0f);
                explicacion.append("Palabra '").append(entry.getKey())
                        .append("' sugiere prioridad ").append(entry.getValue()).append(". ");
                break;
            }
        }

        if (tipo == null) {
            tipo = TipoSolicitud.CONSULTA_ACADEMICA;
            confianza = 0.3f;
            explicacion.append("Sin palabras clave reconocidas. Se sugiere CONSULTA_ACADEMICA por defecto.");
        }

        return SugerenciaIAResponse.builder()
                .tipoSugerido(tipo)
                .prioridadSugerida(prioridad)
                .confianza(confianza)
                .explicacion("[Fallback] " + explicacion.toString().trim())
                .build();
    }

    @Override
    public ResumenIAResponse generarResumen(Long solicitudId) {
        log.info("Resumen por REGLAS (fallback) para solicitud: {}", solicitudId);

        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada"));

        StringBuilder resumen = new StringBuilder();
        resumen.append("Solicitud #").append(solicitud.getId()).append("\n");
        resumen.append("Descripcion: ").append(solicitud.getDescripcion()).append("\n");
        resumen.append("Estado: ").append(solicitud.getEstado()).append("\n");
        if (solicitud.getTipo() != null)
            resumen.append("Tipo: ").append(solicitud.getTipo()).append("\n");
        if (solicitud.getPrioridad() != null)
            resumen.append("Prioridad: ").append(solicitud.getPrioridad()).append("\n");
        if (solicitud.getResponsable() != null)
            resumen.append("Responsable: ").append(solicitud.getResponsable().getNombre()).append("\n");
        resumen.append("Canal: ").append(solicitud.getCanalOrigen()).append("\n");
        resumen.append("Fecha creacion: ").append(solicitud.getFechaCreacion()).append("\n");

        return ResumenIAResponse.builder()
                .resumen(resumen.toString())
                .generadoPor("Fallback (reglas)")
                .build();
    }
}
