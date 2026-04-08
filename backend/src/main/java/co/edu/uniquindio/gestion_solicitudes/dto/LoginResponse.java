package co.edu.uniquindio.gestion_solicitudes.dto;


import co.edu.uniquindio.gestion_solicitudes.domain.Rol;

public class LoginResponse {

    private String token;
    private String tipo;
    private Rol rol;
    private String nombre;

    public LoginResponse() {}

    public LoginResponse(String token, String tipo, Rol rol, String nombre) {
        this.token = token;
        this.tipo = tipo;
        this.rol = rol;
        this.nombre = nombre;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}