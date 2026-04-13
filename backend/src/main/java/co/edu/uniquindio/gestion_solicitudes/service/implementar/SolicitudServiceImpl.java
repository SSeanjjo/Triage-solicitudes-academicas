package co.edu.uniquindio.gestion_solicitudes.service.implementar;



import co.edu.uniquindio.gestion_solicitudes.domain.*;
import co.edu.uniquindio.gestion_solicitudes.dto.request.*;
import co.edu.uniquindio.gestion_solicitudes.dto.response.HistorialEventoResponse;
import co.edu.uniquindio.gestion_solicitudes.dto.response.SolicitudResponse;
import co.edu.uniquindio.gestion_solicitudes.exception.BadRequestException;
import co.edu.uniquindio.gestion_solicitudes.exception.ResourceNotFoundException;
import co.edu.uniquindio.gestion_solicitudes.repository.HistorialSolicitudRepository;
import co.edu.uniquindio.gestion_solicitudes.repository.SolicitudRepository;
import co.edu.uniquindio.gestion_solicitudes.repository.UsuarioRepository;
import co.edu.uniquindio.gestion_solicitudes.service.SolicitudService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SolicitudServiceImpl implements SolicitudService {

    private final SolicitudRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;
    private final HistorialSolicitudRepository historialRepository;

    public SolicitudServiceImpl(SolicitudRepository solicitudRepository,
                                UsuarioRepository usuarioRepository,
                                HistorialSolicitudRepository historialRepository) {
        this.solicitudRepository = solicitudRepository;
        this.usuarioRepository = usuarioRepository;
        this.historialRepository = historialRepository;
    }

    @Override
    public SolicitudResponse crear(SolicitudCreateRequest request, Long solicitanteId) {
        Usuario solicitante = usuarioRepository.findById(solicitanteId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Solicitud solicitud = new Solicitud(
                request.getDescripcion(),
                solicitante,
                request.getCanalOrigen()
        );
        solicitud.setTipo(request.getTipoSolicitud());
        solicitudRepository.save(solicitud);

        registrarHistorial(solicitud, solicitante, "Solicitud creada", null,
                null, EstadoSolicitud.REGISTRADA);

        return mapearResponse(solicitud);
    }

    @Override
    public SolicitudResponse obtenerPorId(Long id) {
        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada"));
        return mapearResponse(solicitud);
    }

    @Override
    public List<SolicitudResponse> listar(EstadoSolicitud estado, TipoSolicitud tipo,
                                          PrioridadSolicitud prioridad, Long responsableId) {
        return solicitudRepository.findAll()
                .stream()
                .filter(s -> estado == null || s.getEstado() == estado)
                .filter(s -> tipo == null || s.getTipo() == tipo)
                .filter(s -> prioridad == null || s.getPrioridad() == prioridad)
                .filter(s -> responsableId == null ||
                        (s.getResponsable() != null && s.getResponsable().getId().equals(responsableId)))
                .map(this::mapearResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SolicitudResponse clasificar(Long id, ClasificacionRequest request, Long usuarioId) {
        Solicitud solicitud = obtenerSolicitud(id);
        Usuario usuario = obtenerUsuario(usuarioId);

        if (!solicitud.puedeTransicionarA(EstadoSolicitud.CLASIFICADA)) {
            throw new BadRequestException("La solicitud no puede clasificarse en su estado actual");
        }

        EstadoSolicitud estadoAnterior = solicitud.getEstado();
        solicitud.clasificar(request.getTipoSolicitud(), request.getPrioridad(),
                request.getJustificacionPrioridad());
        solicitudRepository.save(solicitud);

        registrarHistorial(solicitud, usuario, "Solicitud clasificada",
                null, estadoAnterior, EstadoSolicitud.CLASIFICADA);

        return mapearResponse(solicitud);
    }

    @Override
    public SolicitudResponse asignar(Long id, AsignacionRequest request, Long usuarioId) {
        Solicitud solicitud = obtenerSolicitud(id);
        Usuario usuario = obtenerUsuario(usuarioId);
        Usuario responsable = usuarioRepository.findById(request.getResponsableId())
                .orElseThrow(() -> new ResourceNotFoundException("Responsable no encontrado"));

        if (!responsable.estaActivo()) {
            throw new BadRequestException("El responsable no está activo");
        }

        if (!solicitud.puedeTransicionarA(EstadoSolicitud.EN_ATENCION)) {
            throw new BadRequestException("La solicitud no puede asignarse en su estado actual");
        }

        EstadoSolicitud estadoAnterior = solicitud.getEstado();
        solicitud.asignarResponsable(responsable);
        solicitud.marcarEnAtencion();
        solicitudRepository.save(solicitud);

        registrarHistorial(solicitud, usuario, "Responsable asignado",
                null, estadoAnterior, EstadoSolicitud.EN_ATENCION);

        return mapearResponse(solicitud);
    }

    @Override
    public SolicitudResponse atender(Long id, AtenderRequest request, Long usuarioId) {
        Solicitud solicitud = obtenerSolicitud(id);
        Usuario usuario = obtenerUsuario(usuarioId);

        if (!solicitud.puedeTransicionarA(EstadoSolicitud.ATENDIDA)) {
            throw new BadRequestException("La solicitud no puede atenderse en su estado actual");
        }

        EstadoSolicitud estadoAnterior = solicitud.getEstado();
        solicitud.marcarAtendida();
        solicitudRepository.save(solicitud);

        registrarHistorial(solicitud, usuario, "Solicitud atendida",
                request.getComentario(), estadoAnterior, EstadoSolicitud.ATENDIDA);

        return mapearResponse(solicitud);
    }

    @Override
    public SolicitudResponse cerrar(Long id, CierreRequest request, Long usuarioId) {
        Solicitud solicitud = obtenerSolicitud(id);
        Usuario usuario = obtenerUsuario(usuarioId);

        if (!solicitud.puedeTransicionarA(EstadoSolicitud.CERRADA)) {
            throw new BadRequestException("La solicitud no puede cerrarse en su estado actual");
        }

        EstadoSolicitud estadoAnterior = solicitud.getEstado();
        solicitud.cerrar(request.getComentarioCierre());
        solicitudRepository.save(solicitud);

        registrarHistorial(solicitud, usuario, "Solicitud cerrada",
                request.getComentarioCierre(), estadoAnterior, EstadoSolicitud.CERRADA);

        return mapearResponse(solicitud);
    }

    @Override
    public List<HistorialEventoResponse> obtenerHistorial(Long id) {
        obtenerSolicitud(id);
        return historialRepository.findBySolicitudId(id)
                .stream()
                .map(this::mapearHistorial)
                .collect(Collectors.toList());
    }

    private void registrarHistorial(Solicitud solicitud, Usuario usuario, String accion,
                                    String observacion, EstadoSolicitud estadoAnterior,
                                    EstadoSolicitud estadoNuevo) {
        HistorialSolicitud historial = new HistorialSolicitud(
                solicitud, usuario, accion, observacion, estadoAnterior, estadoNuevo);
        historialRepository.save(historial);
    }

    private Solicitud obtenerSolicitud(Long id) {
        return solicitudRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada"));
    }

    private Usuario obtenerUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    private SolicitudResponse mapearResponse(Solicitud solicitud) {
        SolicitudResponse response = new SolicitudResponse();
        response.setId(solicitud.getId());
        response.setEstado(solicitud.getEstado());
        response.setPrioridad(solicitud.getPrioridad());
        response.setTipoSolicitud(solicitud.getTipo());
        response.setCanalOrigen(solicitud.getCanalOrigen());
        response.setDescripcion(solicitud.getDescripcion());
        response.setJustificacionPrioridad(solicitud.getJustificacionPrioridad());
        response.setObservacionCierre(solicitud.getObservacionCierre());
        response.setFechaCreacion(solicitud.getFechaCreacion());
        response.setFechaActualizacion(solicitud.getFechaActualizacion());
        if (solicitud.getSolicitante() != null)
            response.setSolicitanteId(solicitud.getSolicitante().getId());
        if (solicitud.getResponsable() != null)
            response.setResponsableId(solicitud.getResponsable().getId());
        return response;
    }

    private HistorialEventoResponse mapearHistorial(HistorialSolicitud h) {
        HistorialEventoResponse response = new HistorialEventoResponse();
        response.setId(h.getId());
        response.setSolicitudId(h.getSolicitud().getId());
        response.setUsuarioId(h.getUsuarioAccion().getId());
        response.setAccion(h.getAccion());
        response.setEstadoAnterior(h.getEstadoAnterior());
        response.setEstadoNuevo(h.getEstadoNuevo());
        response.setFecha(h.getFecha());
        response.setComentario(h.getObservacion());
        return response;
    }
}