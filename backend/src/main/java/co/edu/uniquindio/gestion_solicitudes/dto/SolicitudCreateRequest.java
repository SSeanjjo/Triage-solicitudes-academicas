package co.edu.uniquindio.gestion_solicitudes.dto;



import co.edu.uniquindio.gestion_solicitudes.domain.TipoSolicitud;
import co.edu.uniquindio.gestion_solicitudes.domain.CanalOrigen;

public class SolicitudCreateRequest {

    private TipoSolicitud tipoSolicitud;
    private String descripcion;
    private CanalOrigen canalOrigen;

    public SolicitudCreateRequest() {}

    public TipoSolicitud getTipoSolicitud() { return tipoSolicitud; }
    public void setTipoSolicitud(TipoSolicitud tipoSolicitud) { this.tipoSolicitud = tipoSolicitud; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public CanalOrigen getCanalOrigen() { return canalOrigen; }
    public void setCanalOrigen(CanalOrigen canalOrigen) { this.canalOrigen = canalOrigen; }
}