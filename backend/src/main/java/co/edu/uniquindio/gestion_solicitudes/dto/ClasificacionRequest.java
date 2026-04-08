package co.edu.uniquindio.gestion_solicitudes.dto;



import co.edu.uniquindio.gestion_solicitudes.domain.TipoSolicitud;
import co.edu.uniquindio.gestion_solicitudes.domain.PrioridadSolicitud;

public class ClasificacionRequest {

    private TipoSolicitud tipoSolicitud;
    private PrioridadSolicitud prioridad;
    private String justificacionPrioridad;

    public ClasificacionRequest() {}

    public TipoSolicitud getTipoSolicitud() { return tipoSolicitud; }
    public void setTipoSolicitud(TipoSolicitud tipoSolicitud) { this.tipoSolicitud = tipoSolicitud; }

    public PrioridadSolicitud getPrioridad() { return prioridad; }
    public void setPrioridad(PrioridadSolicitud prioridad) { this.prioridad = prioridad; }

    public String getJustificacionPrioridad() { return justificacionPrioridad; }
    public void setJustificacionPrioridad(String justificacionPrioridad) { this.justificacionPrioridad = justificacionPrioridad; }
}