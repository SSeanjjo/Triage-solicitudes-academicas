package co.edu.uniquindio.gestion_solicitudes.service;



import co.edu.uniquindio.gestion_solicitudes.dto.response.ResumenIAResponse;
import co.edu.uniquindio.gestion_solicitudes.dto.response.SugerenciaIAResponse;

public interface IAService {

    SugerenciaIAResponse sugerirClasificacion(String descripcion);
    ResumenIAResponse generarResumen(Long solicitudId);
}
