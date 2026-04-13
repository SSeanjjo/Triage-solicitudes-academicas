package co.edu.uniquindio.gestion_solicitudes.service;

import co.edu.uniquindio.gestion_solicitudes.domain.*;
import co.edu.uniquindio.gestion_solicitudes.dto.response.ResumenIAResponse;
import co.edu.uniquindio.gestion_solicitudes.dto.response.SugerenciaIAResponse;
import co.edu.uniquindio.gestion_solicitudes.exception.ResourceNotFoundException;
import co.edu.uniquindio.gestion_solicitudes.repository.SolicitudRepository;
import co.edu.uniquindio.gestion_solicitudes.service.implementar.IAServiceFallbackImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IAServiceTest {

    @Mock
    private SolicitudRepository solicitudRepository;

    @InjectMocks
    private IAServiceFallbackImpl iaService;

    private Usuario solicitante;
    private Solicitud solicitud;

    @BeforeEach
    void setUp() {
        solicitante = new Usuario("Juan Perez", "juan@test.com", "password", true, Rol.ESTUDIANTE);
        solicitud = new Solicitud("Solicito homologación urgente", solicitante, CanalOrigen.CORREO);
        solicitud.clasificar(TipoSolicitud.HOMOLOGACION, PrioridadSolicitud.ALTA, "justificacion");
    }

    @Test
    void sugerirClasificacion_cuandoContieneHomologacion_debeSugerirHomologacion() {
        SugerenciaIAResponse response = iaService.sugerirClasificacion(
                "Necesito homologar una materia de otra universidad");

        assertNotNull(response);
        assertEquals(TipoSolicitud.HOMOLOGACION, response.getTipoSugerido());
    }

    @Test
    void sugerirClasificacion_cuandoContieneUrgente_debeSugerirPrioridadAlta() {
        SugerenciaIAResponse response = iaService.sugerirClasificacion(
                "Necesito homologar una materia urgente");

        assertNotNull(response);
        assertEquals(PrioridadSolicitud.ALTA, response.getPrioridadSugerida());
    }

    @Test
    void sugerirClasificacion_cuandoContieneCupo_debeSugerirCupo() {
        SugerenciaIAResponse response = iaService.sugerirClasificacion(
                "Necesito un cupo en la materia de calculo");

        assertNotNull(response);
        assertEquals(TipoSolicitud.CUPO, response.getTipoSugerido());
    }

    @Test
    void sugerirClasificacion_cuandoSinPalabrasClaves_debeSugerirConsultaPorDefecto() {
        SugerenciaIAResponse response = iaService.sugerirClasificacion(
                "Tengo una pregunta general");

        assertNotNull(response);
        assertEquals(TipoSolicitud.CONSULTA_ACADEMICA, response.getTipoSugerido());
        assertTrue(response.getConfianza() < 0.5f);
    }

    @Test
    void generarResumen_cuandoSolicitudExiste_debeRetornarResumen() {
        when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));

        ResumenIAResponse response = iaService.generarResumen(1L);

        assertNotNull(response);
        assertNotNull(response.getResumen());
        assertEquals("Fallback (reglas)", response.getGeneradoPor());
        assertTrue(response.getResumen().contains("HOMOLOGACION"));
    }

    @Test
    void generarResumen_cuandoSolicitudNoExiste_debeLanzarExcepcion() {
        when(solicitudRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> iaService.generarResumen(99L));
    }
}
