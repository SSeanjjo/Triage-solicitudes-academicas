package co.edu.uniquindio.gestion_solicitudes.service;

import co.edu.uniquindio.gestion_solicitudes.domain.*;
import co.edu.uniquindio.gestion_solicitudes.exception.BadRequestException;
import co.edu.uniquindio.gestion_solicitudes.exception.ResourceNotFoundException;
import co.edu.uniquindio.gestion_solicitudes.repository.HistorialSolicitudRepository;
import co.edu.uniquindio.gestion_solicitudes.repository.SolicitudRepository;
import co.edu.uniquindio.gestion_solicitudes.repository.UsuarioRepository;
import co.edu.uniquindio.gestion_solicitudes.service.implementar.SolicitudServiceImpl;
import co.edu.uniquindio.gestion_solicitudes.dto.request.SolicitudCreateRequest;
import co.edu.uniquindio.gestion_solicitudes.dto.response.SolicitudResponse;
import co.edu.uniquindio.gestion_solicitudes.dto.response.HistorialEventoResponse;
import co.edu.uniquindio.gestion_solicitudes.dto.request.AsignacionRequest;
import co.edu.uniquindio.gestion_solicitudes.dto.request.ClasificacionRequest;
import co.edu.uniquindio.gestion_solicitudes.dto.request.CierreRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SolicitudServiceTest {

    @Mock
    private SolicitudRepository solicitudRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private HistorialSolicitudRepository historialRepository;

    @InjectMocks
    private SolicitudServiceImpl solicitudService;

    private Usuario solicitante;
    private Usuario responsable;
    private Solicitud solicitud;

    @BeforeEach
    void setUp() {
        solicitante = new Usuario("Juan Perez", "juan@test.com", "password", true, Rol.ESTUDIANTE);
        responsable = new Usuario("Carlos", "carlos@test.com", "password", true, Rol.RESPONSABLE);
        solicitud = new Solicitud("Solicito homologación", solicitante, CanalOrigen.CORREO);
    }

    @Test
    void crear_cuandoDatosCorrectos_debeCrearSolicitud() {
        SolicitudCreateRequest request = new SolicitudCreateRequest(
                TipoSolicitud.HOMOLOGACION, "Solicito homologación", CanalOrigen.CORREO);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(solicitante));
        when(solicitudRepository.save(any(Solicitud.class))).thenReturn(solicitud);
        when(historialRepository.save(any(HistorialSolicitud.class))).thenReturn(null);

        SolicitudResponse response = solicitudService.crear(request, 1L);

        assertNotNull(response);
        assertEquals(EstadoSolicitud.REGISTRADA, response.getEstado());
        verify(solicitudRepository, times(1)).save(any(Solicitud.class));
    }

    @Test
    void crear_cuandoUsuarioNoExiste_debeLanzarExcepcion() {
        SolicitudCreateRequest request = new SolicitudCreateRequest(
                TipoSolicitud.HOMOLOGACION, "Solicito homologación", CanalOrigen.CORREO);

        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> solicitudService.crear(request, 99L));
    }

    @Test
    void clasificar_cuandoEstadoRegistrada_debeClasificar() {
        ClasificacionRequest request = new ClasificacionRequest(
                TipoSolicitud.HOMOLOGACION, PrioridadSolicitud.ALTA, "Tiene fecha límite");

        when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(solicitante));
        when(solicitudRepository.save(any(Solicitud.class))).thenReturn(solicitud);
        when(historialRepository.save(any(HistorialSolicitud.class))).thenReturn(null);

        SolicitudResponse response = solicitudService.clasificar(1L, request, 1L);

        assertNotNull(response);
        assertEquals(EstadoSolicitud.CLASIFICADA, response.getEstado());
    }

    @Test
    void clasificar_cuandoEstadoNOesRegistrada_debeLanzarExcepcion() {
        solicitud.clasificar(TipoSolicitud.HOMOLOGACION, PrioridadSolicitud.ALTA, "justificacion");
        ClasificacionRequest request = new ClasificacionRequest(
                TipoSolicitud.HOMOLOGACION, PrioridadSolicitud.ALTA, "justificacion");

        when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(solicitante));

        assertThrows(BadRequestException.class, () -> solicitudService.clasificar(1L, request, 1L));
    }

    @Test
    void asignar_cuandoResponsableActivo_debeAsignar() {
        solicitud.clasificar(TipoSolicitud.HOMOLOGACION, PrioridadSolicitud.ALTA, "justificacion");
        AsignacionRequest request = new AsignacionRequest(2L);

        when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(solicitante));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(responsable));
        when(solicitudRepository.save(any(Solicitud.class))).thenReturn(solicitud);
        when(historialRepository.save(any(HistorialSolicitud.class))).thenReturn(null);

        SolicitudResponse response = solicitudService.asignar(1L, request, 1L);

        assertNotNull(response);
        assertEquals(EstadoSolicitud.EN_ATENCION, response.getEstado());
    }

    @Test
    void asignar_cuandoResponsableInactivo_debeLanzarExcepcion() {
        solicitud.clasificar(TipoSolicitud.HOMOLOGACION, PrioridadSolicitud.ALTA, "justificacion");
        responsable.setActivo(false);
        AsignacionRequest request = new AsignacionRequest(2L);

        when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(solicitante));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(responsable));

        assertThrows(BadRequestException.class, () -> solicitudService.asignar(1L, request, 1L));
    }

    @Test
    void cerrar_cuandoEstadoAtendida_debeCerrar() {
        solicitud.clasificar(TipoSolicitud.HOMOLOGACION, PrioridadSolicitud.ALTA, "justificacion");
        solicitud.asignarResponsable(responsable);
        solicitud.marcarEnAtencion();
        solicitud.marcarAtendida();
        CierreRequest request = new CierreRequest("Solicitud cerrada formalmente");

        when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(solicitante));
        when(solicitudRepository.save(any(Solicitud.class))).thenReturn(solicitud);
        when(historialRepository.save(any(HistorialSolicitud.class))).thenReturn(null);

        SolicitudResponse response = solicitudService.cerrar(1L, request, 1L);

        assertNotNull(response);
        assertEquals(EstadoSolicitud.CERRADA, response.getEstado());
    }

    @Test
    void obtenerPorId_cuandoNoExiste_debeLanzarExcepcion() {
        when(solicitudRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> solicitudService.obtenerPorId(99L));
    }

    @Test
    void obtenerHistorial_cuandoExiste_debeRetornarLista() {
        when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));
        when(historialRepository.findBySolicitudId(1L)).thenReturn(List.of());

        List<HistorialEventoResponse> historial = solicitudService.obtenerHistorial(1L);

        assertNotNull(historial);
        verify(historialRepository, times(1)).findBySolicitudId(1L);
    }
}
