package co.edu.uniquindio.gestion_solicitudes.service.implementar;

import co.edu.uniquindio.gestion_solicitudes.domain.*;
import co.edu.uniquindio.gestion_solicitudes.dto.response.ResumenIAResponse;
import co.edu.uniquindio.gestion_solicitudes.dto.response.SugerenciaIAResponse;
import co.edu.uniquindio.gestion_solicitudes.exception.ResourceNotFoundException;
import co.edu.uniquindio.gestion_solicitudes.repository.SolicitudRepository;
import co.edu.uniquindio.gestion_solicitudes.service.IAService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
/**
 * Implementación del servicio de IA usando la API de Google Gemini.
 * <p>
 * Realiza llamadas HTTP a la API de Gemini para generar resúmenes (RF-09)
 * y sugerencias de clasificación (RF-10). Si la API no está disponible o
 * responde con error, delega automáticamente al {@link IAServiceFallbackImpl}
 * para garantizar el funcionamiento sin IA (RF-11).
 * </p>
 * <p>
 * Esta clase es instanciada condicionalmente desde {@link IAServiceConfig}
 * solo cuando la propiedad {@code ai.gemini.enabled=true} y la API key
 * están configuradas correctamente.
 * </p>
 *
 * @author Manu-Z, SseanJjo
 * @version 1.0
 * @see IAServiceFallbackImpl
 * @see IAServiceConfig
 */
@Slf4j
public class IAServiceGeminiImpl implements IAService {
    private final SolicitudRepository solicitudRepository;
    private final IAServiceFallbackImpl fallback;
    private final String apiKey;
    private final String apiUrl;
    private final int timeoutMs;
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    /**
     * Construye una instancia del servicio Gemini con todas sus dependencias.
     *
     * @param solicitudRepository repositorio para consultar datos de solicitudes
     * @param fallback            implementación de respaldo usada cuando Gemini falla
     * @param apiKey              clave de autenticación para la API de Gemini
     * @param apiUrl              URL del endpoint de Gemini
     * @param timeoutMs           tiempo máximo de espera en milisegundos para la respuesta
     */

    public IAServiceGeminiImpl(SolicitudRepository solicitudRepository,
                               IAServiceFallbackImpl fallback,
                               String apiKey,
                               String apiUrl,
                               int timeoutMs) {
        this.solicitudRepository = solicitudRepository;
        this.fallback = fallback;
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
        this.timeoutMs = timeoutMs;
    }

    /**
     * Sugiere clasificación enviando la descripción a Gemini como prompt.
     * <p>
     * Si Gemini responde con error o supera el timeout, activa el fallback
     * automáticamente sin lanzar excepción al cliente.
     * </p>
     *
     * @param descripcion texto de la solicitud a clasificar
     * @return sugerencia generada por Gemini o por el fallback si falla
     */

    @Override
    public SugerenciaIAResponse sugerirClasificacion(String descripcion) {
        log.info("Solicitando clasificación IA (Gemini) para descripción recibida");

        String prompt = """
                Eres un asistente de gestión de solicitudes académicas universitarias.
                Clasifica la siguiente solicitud y responde ÚNICAMENTE con un JSON válido, sin texto adicional,
                sin bloques de código markdown, con exactamente este formato:
                {"tipoSugerido":"VALOR","prioridadSugerida":"VALOR","confianza":0.0,"explicacion":"texto"}
                
                Valores válidos para tipoSugerido: REGISTRO_ASIGNATURA, HOMOLOGACION, CANCELACION, CUPO, CONSULTA_ACADEMICA
                Valores válidos para prioridadSugerida: ALTA, MEDIA, BAJA
                confianza: número entre 0.0 y 1.0
                
                Descripción de la solicitud: "%s"
                """.formatted(descripcion);

        try {
            String respuestaJson = llamarGemini(prompt);
            return mapper.readValue(respuestaJson, SugerenciaIAResponse.class);
        } catch (Exception e) {
            log.warn("Gemini falló en clasificación, activando fallback. Causa: {}", e.getMessage());
            return fallback.sugerirClasificacion(descripcion);
        }
    }

    /**
     * Genera un resumen de la solicitud enviando su información completa a Gemini.
     * <p>
     * Incluye descripción, estado, tipo, prioridad, responsable, canal de origen
     * e historial de eventos como contexto para el modelo.
     * </p>
     *
     * @param solicitudId id de la solicitud a resumir
     * @return resumen generado por Gemini o por el fallback si falla
     * @throws ResourceNotFoundException si la solicitud no existe
     */

    @Override
    public ResumenIAResponse generarResumen(Long solicitudId) {
        log.info("Generando resumen IA (Gemini) para solicitud #{}", solicitudId);

        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada"));

        // Construir historial como texto
        String historialTexto = solicitud.getHistorial().stream()
                .map(h -> String.format("- [%s] %s → %s: %s",
                        h.getFecha() != null ? h.getFecha().toLocalDate() : "?",
                        h.getEstadoAnterior(),
                        h.getEstadoNuevo(),
                        h.getObservacion() != null ? h.getObservacion() : "sin comentario"))
                .reduce("", (a, b) -> a + "\n" + b);

        String prompt = """
                Eres un asistente administrativo universitario. Resume en máximo 3 oraciones
                el estado actual y el historial de esta solicitud académica.
                Sé claro, directo y profesional. No uses listas ni bullets, solo párrafo.
                
                Solicitud #%d
                Descripción: %s
                Estado actual: %s
                Tipo: %s
                Prioridad: %s
                Responsable: %s
                Canal de origen: %s
                Historial de eventos:%s
                """.formatted(
                solicitud.getId(),
                solicitud.getDescripcion(),
                solicitud.getEstado(),
                solicitud.getTipo() != null ? solicitud.getTipo() : "Sin clasificar",
                solicitud.getPrioridad() != null ? solicitud.getPrioridad() : "Sin definir",
                solicitud.getResponsable() != null ? solicitud.getResponsable().getNombre() : "Sin asignar",
                solicitud.getCanalOrigen(),
                historialTexto.isBlank() ? " Sin eventos registrados." : historialTexto
        );

        try {
            String resumen = llamarGemini(prompt);
            return ResumenIAResponse.builder()
                    .resumen(resumen.trim())
                    .generadoPor("Gemini 1.5 Flash")
                    .build();
        } catch (Exception e) {
            log.warn("Gemini falló en resumen, activando fallback. Causa: {}", e.getMessage());
            return fallback.generarResumen(solicitudId);
        }
    }

    /**
     * Realiza la llamada HTTP POST a la API de Gemini.
     *
     * @param prompt texto del prompt a enviar al modelo
     * @return texto de respuesta extraído de la respuesta JSON de Gemini
     * @throws Exception si el status HTTP no es 200 o si supera el timeout
     */

    private String llamarGemini(String prompt) throws Exception {
        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", prompt)))
                )
        );

        String jsonBody = mapper.writeValueAsString(body);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "?key=" + apiKey))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofMillis(timeoutMs))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Gemini respondió con status " + response.statusCode()
                    + ": " + response.body());
        }

        // Extraer el texto de la respuesta de Gemini
        Map<?, ?> responseMap = mapper.readValue(response.body(), Map.class);
        List<?> candidates = (List<?>) responseMap.get("candidates");
        Map<?, ?> content = (Map<?, ?>) ((Map<?, ?>) candidates.get(0)).get("content");
        List<?> parts = (List<?>) content.get("parts");
        return (String) ((Map<?, ?>) parts.get(0)).get("text");
    }
}