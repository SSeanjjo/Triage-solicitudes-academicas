package co.edu.uniquindio.gestion_solicitudes.service;

import co.edu.uniquindio.gestion_solicitudes.dto.response.ResumenIAResponse;
import co.edu.uniquindio.gestion_solicitudes.dto.response.SugerenciaIAResponse;

/**
 * Servicio de inteligencia artificial para el sistema de gestión de solicitudes.
 * <p>
 * Define las operaciones de IA disponibles: generación de resúmenes automáticos
 * y sugerencias de clasificación. Puede ser implementado por un proveedor externo
 * como Gemini, o por un mecanismo de reglas locales (fallback) para garantizar
 * el funcionamiento independiente del sistema (RF-11).
 * </p>
 *
 * @author Manu-Z, SseanJjo
 * @version 1.0
 * @see IAServiceGeminiImpl
 * @see IAServiceFallbackImpl
 */
public interface IAService {

    /**
     * Sugiere un tipo y prioridad para una solicitud académica a partir
     * de su descripción textual (RF-10).
     * <p>
     * La sugerencia generada debe ser confirmada o ajustada por un usuario humano
     * antes de ser aplicada a la solicitud.
     * </p>
     *
     * @param descripcion texto descriptivo ingresado por el solicitante,
     *                    con un mínimo de 10 caracteres y máximo de 1000
     * @return {@link SugerenciaIAResponse} con el tipo sugerido, prioridad,
     *         nivel de confianza y explicación del criterio usado
     */
    SugerenciaIAResponse sugerirClasificacion(String descripcion);

    /**
     * Genera un resumen textual del estado e historial de una solicitud (RF-09).
     * <p>
     * El resumen permite a los responsables comprender rápidamente el caso
     * sin necesidad de revisar todo el historial de eventos.
     * </p>
     *
     * @param solicitudId identificador único de la solicitud a resumir
     * @return {@link ResumenIAResponse} con el resumen generado y el nombre
     *         del proveedor que lo generó (Gemini o Fallback)
     * @throws co.edu.uniquindio.gestion_solicitudes.exception.ResourceNotFoundException
     *         si no existe una solicitud con el id proporcionado
     */
    ResumenIAResponse generarResumen(Long solicitudId);
}