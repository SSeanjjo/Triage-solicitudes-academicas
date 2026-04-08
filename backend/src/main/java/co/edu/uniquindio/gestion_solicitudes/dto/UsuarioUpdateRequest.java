package co.edu.uniquindio.gestion_solicitudes.dto;



public class UsuarioUpdateRequest {

    private String nombre;
    private String correo;

    public UsuarioUpdateRequest() {}

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
}