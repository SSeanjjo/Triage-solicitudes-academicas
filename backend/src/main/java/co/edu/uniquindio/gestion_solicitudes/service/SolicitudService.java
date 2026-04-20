package co.edu.uniquindio.gestion_solicitudes.service;


import co.edu.uniquindio.gestion_solicitudes.dto.request.*;
import co.edu.uniquindio.gestion_solicitudes.dto.response.HistorialEventoResponse;
import co.edu.uniquindio.gestion_solicitudes.dto.response.SolicitudResponse;
import co.edu.uniquindio.gestion_solicitudes.domain.EstadoSolicitud;
import co.edu.uniquindio.gestion_solicitudes.domain.TipoSolicitud;
import co.edu.uniquindio.gestion_solicitudes.domain.PrioridadSolicitud;

import java.util.List;
/**
 * Servicio para la gestión del ciclo de vida de las solicitudes académicas.
 * <p>
 * Define las operaciones disponibles sobre una solicitud: creación,
 * consulta, clasificación, asignación, atención, cierre y consulta
 * del historial de eventos.
 * </p>
 *
 * @author Manu-Z
 * @version 1.0
 * @see SolicitudServiceImpl
 */
public interface SolicitudService {
    /**
     * Crea una nueva solicitud en estado REGISTRADA.
     *
     * @param request       datos de la solicitud: tipo, descripción y canal
     * @param solicitanteId id del usuario que crea la solicitud
     * @return datos de la solicitud creada
     * @throws co.edu.uniquindio.gestion_solicitudes.exception.ResourceNotFoundException
     *         si el solicitante no existe
     */
    SolicitudResponse crear(SolicitudCreateRequest request, Long solicitanteId);
    /**
     * Obtiene una solicitud por su identificador único.
     *
     * @param id id de la solicitud
     * @return datos de la solicitud encontrada
     * @throws co.edu.uniquindio.gestion_solicitudes.exception.ResourceNotFoundException
     *         si no existe la solicitud
     */
    SolicitudResponse obtenerPorId(Long id);
    /**
     * Lista las solicitudes aplicando filtros opcionales combinables.
     * Los filtros nulos son ignorados.
     *
     * @param estado        filtra por estado (opcional)
     * @param tipo          filtra por tipo (opcional)
     * @param prioridad     filtra por prioridad (opcional)
     * @param responsableId filtra por responsable asignado (opcional)
     * @return lista de solicitudes que cumplen todos los filtros activos
     */
    List<SolicitudResponse> listar(EstadoSolicitud estado, TipoSolicitud tipo, PrioridadSolicitud prioridad, Long responsableId);
    /**
     * Clasifica una solicitud asignándole tipo, prioridad y justificación.
     * Transiciona el estado de REGISTRADA a CLASIFICADA.
     *
     * @param id        id de la solicitud
     * @param request   tipo, prioridad y justificación
     * @param usuarioId id del usuario que realiza la clasificación
     * @return solicitud con el nuevo estado y clasificación
     * @throws co.edu.uniquindio.gestion_solicitudes.exception.BadRequestException
     *         si la solicitud no está en estado REGISTRADA
     */
    SolicitudResponse clasificar(Long id, ClasificacionRequest request, Long usuarioId);
    /**
     * Asigna un responsable a la solicitud y la transiciona a EN_ATENCION.
     *
     * @param id        id de la solicitud
     * @param request   id del responsable a asignar
     * @param usuarioId id del usuario que realiza la asignación
     * @return solicitud con el responsable asignado y estado EN_ATENCION
     * @throws co.edu.uniquindio.gestion_solicitudes.exception.BadRequestException
     *         si la solicitud no está en estado CLASIFICADA o el responsable está inactivo
     */
    SolicitudResponse asignar(Long id, AsignacionRequest request, Long usuarioId);
    /**
     * Marca la solicitud como ATENDIDA registrando el comentario de atención.
     *
     * @param id        id de la solicitud
     * @param request   comentario de la atención realizada
     * @param usuarioId id del usuario que atiende
     * @return solicitud en estado ATENDIDA
     * @throws co.edu.uniquindio.gestion_solicitudes.exception.BadRequestException
     *         si la solicitud no está en estado EN_ATENCION
     */
    SolicitudResponse atender(Long id, AtenderRequest request, Long usuarioId);
    /**
     * Cierra formalmente la solicitud registrando la observación de cierre.
     * Transiciona el estado de ATENDIDA a CERRADA. Estado terminal.
     *
     * @param id        id de la solicitud
     * @param request   observación de cierre
     * @param usuarioId id del usuario que cierra
     * @return solicitud en estado CERRADA
     * @throws co.edu.uniquindio.gestion_solicitudes.exception.BadRequestException
     *         si la solicitud no está en estado ATENDIDA
     */
    SolicitudResponse cerrar(Long id, CierreRequest request, Long usuarioId);
    /**
     * Obtiene el historial completo de eventos de una solicitud.
     *
     * @param id id de la solicitud
     * @return lista de eventos ordenados cronológicamente
     * @throws co.edu.uniquindio.gestion_solicitudes.exception.ResourceNotFoundException
     *         si no existe la solicitud
     */
    List<HistorialEventoResponse> obtenerHistorial(Long id);
}