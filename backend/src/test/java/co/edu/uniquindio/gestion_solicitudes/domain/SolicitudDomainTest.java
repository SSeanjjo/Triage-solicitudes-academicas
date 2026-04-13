package co.edu.uniquindio.gestion_solicitudes.domain;



import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SolicitudDomainTest {

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
    void cuandoSeCreaSolicitud_estadoDebeSerRegistrada() {
        assertEquals(EstadoSolicitud.REGISTRADA, solicitud.getEstado());
    }

    @Test
    void puedeTransicionarA_desdeRegistrada_soloPermiteClasificada() {
        assertTrue(solicitud.puedeTransicionarA(EstadoSolicitud.CLASIFICADA));
        assertFalse(solicitud.puedeTransicionarA(EstadoSolicitud.EN_ATENCION));
        assertFalse(solicitud.puedeTransicionarA(EstadoSolicitud.ATENDIDA));
        assertFalse(solicitud.puedeTransicionarA(EstadoSolicitud.CERRADA));
    }

    @Test
    void puedeTransicionarA_desdeClasificada_soloPermiteEnAtencion() {
        solicitud.clasificar(TipoSolicitud.HOMOLOGACION, PrioridadSolicitud.ALTA, "justificacion");

        assertTrue(solicitud.puedeTransicionarA(EstadoSolicitud.EN_ATENCION));
        assertFalse(solicitud.puedeTransicionarA(EstadoSolicitud.REGISTRADA));
        assertFalse(solicitud.puedeTransicionarA(EstadoSolicitud.ATENDIDA));
        assertFalse(solicitud.puedeTransicionarA(EstadoSolicitud.CERRADA));
    }

    @Test
    void puedeTransicionarA_desdeEnAtencion_soloPermiteAtendida() {
        solicitud.clasificar(TipoSolicitud.HOMOLOGACION, PrioridadSolicitud.ALTA, "justificacion");
        solicitud.asignarResponsable(responsable);
        solicitud.marcarEnAtencion();

        assertTrue(solicitud.puedeTransicionarA(EstadoSolicitud.ATENDIDA));
        assertFalse(solicitud.puedeTransicionarA(EstadoSolicitud.CLASIFICADA));
        assertFalse(solicitud.puedeTransicionarA(EstadoSolicitud.CERRADA));
    }

    @Test
    void puedeTransicionarA_desdeAtendida_soloPermiteCerrada() {
        solicitud.clasificar(TipoSolicitud.HOMOLOGACION, PrioridadSolicitud.ALTA, "justificacion");
        solicitud.asignarResponsable(responsable);
        solicitud.marcarEnAtencion();
        solicitud.marcarAtendida();

        assertTrue(solicitud.puedeTransicionarA(EstadoSolicitud.CERRADA));
        assertFalse(solicitud.puedeTransicionarA(EstadoSolicitud.REGISTRADA));
        assertFalse(solicitud.puedeTransicionarA(EstadoSolicitud.EN_ATENCION));
    }

    @Test
    void puedeTransicionarA_desdeCerrada_noPermiteNada() {
        solicitud.clasificar(TipoSolicitud.HOMOLOGACION, PrioridadSolicitud.ALTA, "justificacion");
        solicitud.asignarResponsable(responsable);
        solicitud.marcarEnAtencion();
        solicitud.marcarAtendida();
        solicitud.cerrar("Cerrada formalmente");

        assertFalse(solicitud.puedeTransicionarA(EstadoSolicitud.REGISTRADA));
        assertFalse(solicitud.puedeTransicionarA(EstadoSolicitud.CLASIFICADA));
        assertFalse(solicitud.puedeTransicionarA(EstadoSolicitud.EN_ATENCION));
        assertFalse(solicitud.puedeTransicionarA(EstadoSolicitud.ATENDIDA));
    }
}
