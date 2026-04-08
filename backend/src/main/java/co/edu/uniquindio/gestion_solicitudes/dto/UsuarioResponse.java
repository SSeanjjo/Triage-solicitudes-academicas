package co.edu.uniquindio.gestion_solicitudes.dto;

import co.edu.uniquindio.gestion_solicitudes.domain.Rol;

public class UsuarioResponse {

    private Long id;
    private String nombre;
    private String correo;
    private Rol rol;
    private boolean activo;

    public UsuarioResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
