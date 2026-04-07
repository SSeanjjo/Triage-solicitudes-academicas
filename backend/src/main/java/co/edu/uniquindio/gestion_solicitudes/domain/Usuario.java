package co.edu.uniquindio.gestion_solicitudes.domain;


import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "Usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String correo;
    private String password;
    private boolean activo;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    public Usuario() {}

    public Usuario(String nombre, String correo, String password, boolean activo, Rol rol) {
        this.nombre = nombre;
        this.correo = correo;
        this.password = password;
        this.activo = activo;
        this.rol = rol;
    }

    public boolean estaActivo() {
        return activo;
    }

    public boolean tieneRol(Rol rol) {
        return this.rol == rol;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(id, usuario.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
