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

/**
 * Implementación de respaldo del servicio de IA basada en reglas locales.
 * <p>
 * Garantiza el funcionamiento del sistema sin depender de servicios externos (RF-11).
 * Utiliza mapas de palabras clave predefinidas para inferir el tipo y prioridad
 * de una solicitud, y construye resúmenes concatenando los campos de la entidad.
 * </p>
 * <p>
 * Es usada en dos escenarios:
 * <ul>
 *   <li>Cuando Gemini no está configurado ({@code ai.gemini.enabled=false})</li>
 *   <li>Cuando Gemini falla en tiempo de ejecución (timeout, error HTTP, etc.)</li>
 * </ul>
 * </p>
 *
 * @author Manu-Z, SseanJjo
 * @version 1.0
 */

@Slf4j
@RequiredArgsConstructor

public class IAServiceFallbackImpl implements IAService {

    private final SolicitudRepository solicitudRepository;

    /**
     * Mapa de palabras clave para inferir el tipo de solicitud.
     * Clave: palabra clave en minúsculas. Valor: tipo de solicitud correspondiente.
     */

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

    /**
     * Mapa de palabras clave para inferir la prioridad de la solicitud.
     * Clave: palabra clave en minúsculas. Valor: prioridad correspondiente.
     */

    private static final Map<String, PrioridadSolicitud> KEYWORDS_PRIORIDAD = Map.of(
            "urgente", PrioridadSolicitud.ALTA,
            "critico", PrioridadSolicitud.ALTA,
            "inmediato", PrioridadSolicitud.ALTA,
            "importante", PrioridadSolicitud.ALTA,
            "plazo", PrioridadSolicitud.MEDIA,
            "pronto", PrioridadSolicitud.MEDIA
    );

    /**
     * Infiere el tipo y prioridad de una solicitud mediante palabras clave (RF-10 fallback).
     * <p>
     * Recorre los mapas de palabras clave buscando coincidencias en el texto.
     * Si no encuentra ninguna, sugiere {@code CONSULTA_ACADEMICA} con confianza baja (0.3).
     * </p>
     *
     * @param descripcion texto de la solicitud en cualquier formato
     * @return sugerencia con tipo, prioridad, confianza y explicación del criterio
     */

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

    /**
     * Genera un resumen básico concatenando los campos principales de la solicitud (RF-09 fallback).
     *
     * @param solicitudId id de la solicitud
     * @return resumen con los datos básicos de la solicitud
     * @throws ResourceNotFoundException si la solicitud no existe
     */

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
