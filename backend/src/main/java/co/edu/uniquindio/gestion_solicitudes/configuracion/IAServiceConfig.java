package co.edu.uniquindio.gestion_solicitudes.configuracion;

import co.edu.uniquindio.gestion_solicitudes.repository.SolicitudRepository;
import co.edu.uniquindio.gestion_solicitudes.service.IAService;
import co.edu.uniquindio.gestion_solicitudes.service.implementar.IAServiceFallbackImpl;
import co.edu.uniquindio.gestion_solicitudes.service.implementar.IAServiceGeminiImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración del proveedor de inteligencia artificial del sistema.
 * <p>
 * Decide en tiempo de arranque qué implementación de {@link IAService}
 * se utilizará: Gemini (proveedor externo) o Fallback (reglas locales).
 * La decisión se basa en las propiedades {@code ai.gemini.enabled} y
 * {@code ai.gemini.api-key} del archivo {@code application.properties}.
 * </p>
 * <p>
 * Este diseño garantiza el cumplimiento de RF-11: el sistema funciona
 * correctamente sin IA externa si no está configurada o si falla.
 * </p>
 *
 * @author Manu-Z
 * @version 1.0
 * @see IAServiceGeminiImpl
 * @see IAServiceFallbackImpl
 */
@Slf4j
@Configuration
public class IAServiceConfig {

    /**
     * Indica si el proveedor Gemini está habilitado.
     * Valor por defecto: {@code false}.
     */
    @Value("${ai.gemini.enabled:false}")
    private boolean geminiEnabled;
    /**
     * Clave de autenticación para la API de Gemini.
     * Valor por defecto: {@code "fallback"} si no se configura.
     */
    @Value("${ai.gemini.api-key:fallback}")
    private String apiKey;
    /**
     * URL del endpoint de generación de contenido de Gemini.
     */
    @Value("${ai.gemini.url:}")
    private String apiUrl;

    /**
     * Tiempo máximo de espera para la respuesta de Gemini en milisegundos.
     * Valor por defecto: {@code 5000} ms.
     */
    @Value("${ai.gemini.timeout-ms:5000}")
    private int timeoutMs;

    /**
     * Crea el bean de la implementación fallback de IA basada en reglas locales.
     * <p>
     * Este bean siempre se crea independientemente de si Gemini está habilitado,
     * ya que es usado como respaldo en caso de fallo del proveedor externo.
     * </p>
     *
     * @param solicitudRepository repositorio de solicitudes necesario para generar resúmenes
     * @return instancia de {@link IAServiceFallbackImpl}
     */
    @Bean
    public IAServiceFallbackImpl iaServiceFallback(SolicitudRepository solicitudRepository) {
        return new IAServiceFallbackImpl(solicitudRepository);
    }

    /**
     * Crea el bean principal del servicio de IA.
     * <p>
     * Si {@code ai.gemini.enabled=true} y la API key es válida,
     * retorna {@link IAServiceGeminiImpl}. En caso contrario retorna
     * {@link IAServiceFallbackImpl} (RF-11).
     * </p>
     *
     * @param solicitudRepository repositorio de solicitudes
     * @param fallback            implementación de respaldo ya creada
     * @return implementación activa de {@link IAService}
     */

    @Bean
    public IAService iaService(SolicitudRepository solicitudRepository,
                               IAServiceFallbackImpl fallback) {

        // RF-11: si no está habilitado o la key es la de fallback, usa reglas locales
        boolean keyValida = apiKey != null
                && !apiKey.isBlank()
                && !apiKey.equals("fallback");

        if (geminiEnabled && keyValida) {
            log.info("IA Provider: GEMINI ({})", apiUrl);
            return new IAServiceGeminiImpl(solicitudRepository, fallback, apiKey, apiUrl, timeoutMs);
        }

        log.info("IA Provider: FALLBACK (basado en reglas). " +
                "geminiEnabled={}, keyValida={}", geminiEnabled, keyValida);
        return fallback;
    }
}