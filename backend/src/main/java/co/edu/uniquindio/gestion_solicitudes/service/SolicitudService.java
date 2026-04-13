package co.edu.uniquindio.gestion_solicitudes.service;


import co.edu.uniquindio.gestion_solicitudes.dto.request.*;
import co.edu.uniquindio.gestion_solicitudes.dto.response.HistorialEventoResponse;
import co.edu.uniquindio.gestion_solicitudes.dto.response.SolicitudResponse;
import co.edu.uniquindio.gestion_solicitudes.domain.EstadoSolicitud;
import co.edu.uniquindio.gestion_solicitudes.domain.TipoSolicitud;
import co.edu.uniquindio.gestion_solicitudes.domain.PrioridadSolicitud;

import java.util.List;

public interface SolicitudService {

    SolicitudResponse crear(SolicitudCreateRequest request, Long solicitanteId);
    SolicitudResponse obtenerPorId(Long id);
    List<SolicitudResponse> listar(EstadoSolicitud estado, TipoSolicitud tipo, PrioridadSolicitud prioridad, Long responsableId);
    SolicitudResponse clasificar(Long id, ClasificacionRequest request, Long usuarioId);
    SolicitudResponse asignar(Long id, AsignacionRequest request, Long usuarioId);
    SolicitudResponse atender(Long id, AtenderRequest request, Long usuarioId);
    SolicitudResponse cerrar(Long id, CierreRequest request, Long usuarioId);
    List<HistorialEventoResponse> obtenerHistorial(Long id);
}