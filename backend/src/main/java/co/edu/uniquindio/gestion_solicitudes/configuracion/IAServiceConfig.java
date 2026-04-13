package co.edu.uniquindio.gestion_solicitudes.configuracion;

import co.edu.uniquindio.gestion_solicitudes.repository.SolicitudRepository;
import co.edu.uniquindio.gestion_solicitudes.service.IAService;
import co.edu.uniquindio.gestion_solicitudes.service.implementar.IAServiceFallbackImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class IAServiceConfig {

    @Bean
    public IAService iaService(SolicitudRepository solicitudRepository) {
        log.info("IA Provider: FALLBACK (basado en reglas).");
        return new IAServiceFallbackImpl(solicitudRepository);
    }
}