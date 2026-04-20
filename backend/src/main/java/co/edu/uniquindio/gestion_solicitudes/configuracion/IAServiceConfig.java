package co.edu.uniquindio.gestion_solicitudes.configuracion;

import co.edu.uniquindio.gestion_solicitudes.repository.SolicitudRepository;
import co.edu.uniquindio.gestion_solicitudes.service.IAService;
import co.edu.uniquindio.gestion_solicitudes.service.implementar.IAServiceFallbackImpl;
import co.edu.uniquindio.gestion_solicitudes.service.implementar.IAServiceGeminiImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class IAServiceConfig {

    @Value("${ai.gemini.enabled:false}")
    private boolean geminiEnabled;

    @Value("${ai.gemini.api-key:fallback}")
    private String apiKey;

    @Value("${ai.gemini.url:}")
    private String apiUrl;

    @Value("${ai.gemini.timeout-ms:5000}")
    private int timeoutMs;

    @Bean
    public IAServiceFallbackImpl iaServiceFallback(SolicitudRepository solicitudRepository) {
        return new IAServiceFallbackImpl(solicitudRepository);
    }

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