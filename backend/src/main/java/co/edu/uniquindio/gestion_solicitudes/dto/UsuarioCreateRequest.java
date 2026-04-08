package co.edu.uniquindio.gestion_solicitudes.dto;



import co.edu.uniquindio.gestion_solicitudes.domain.Rol;

public class UsuarioCreateRequest {

    private String nombre;
    private String correo;
    private String password;
    private Rol rol;

    public UsuarioCreateRequest() {}

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
}