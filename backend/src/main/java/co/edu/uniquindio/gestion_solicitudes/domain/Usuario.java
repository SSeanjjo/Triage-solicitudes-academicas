package co.edu.uniquindio.gestion_solicitudes.domain;


import jakarta.persistence.*;
import java.util.Objects;

/**
 * Entidad que representa un usuario del sistema de gestión de solicitudes.
 * <p>
 * Un usuario puede tener uno de tres roles: ESTUDIANTE, RESPONSABLE o ADMINISTRADOR.
 * Los estudiantes crean solicitudes, los responsables las atienden y los
 * administradores gestionan el sistema completo.
 * </p>
 *
 * @author Manu-Z
 * @version 1.0
 */

@Entity
@Table(name = "usuarios")
public class Usuario {

    /**
     * Identificador único del usuario generado automáticamente por la base de datos.
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre completo del usuario.
     */
    private String nombre;

    /**
     * Correo electrónico del usuario. Usado como identificador de autenticación.
     * Debe ser único en el sistema.
     */
    private String correo;

    /**
     * Contraseña del usuario almacenada con hash BCrypt.
     * Nunca se almacena en texto plano.
     */
    private String password;

    /**
     * Indica si el usuario está activo en el sistema.
     * Un usuario inactivo no puede ser asignado como responsable.
     */
    private boolean activo;


    /**
     * Rol del usuario en el sistema. Determina los permisos disponibles.
     *
     * @see Rol
     */
    @Enumerated(EnumType.STRING)
    private Rol rol;

    public Usuario() {}

    /**
     * Construye un nuevo usuario con todos sus atributos.
     *
     * @param nombre   nombre completo del usuario
     * @param correo   correo electrónico único
     * @param password contraseña ya codificada con BCrypt
     * @param activo   estado inicial del usuario
     * @param rol      rol asignado en el sistema
     */
    public Usuario(String nombre, String correo, String password, boolean activo, Rol rol) {
        this.nombre = nombre;
        this.correo = correo;
        this.password = password;
        this.activo = activo;
        this.rol = rol;
    }

    /**
     * Verifica si el usuario está activo en el sistema.
     *
     * @return {@code true} si el usuario está activo
     */
    public boolean estaActivo() {
        return activo;
    }


    /**
     * Verifica si el usuario tiene el rol especificado.
     *
     * @param rol rol a verificar
     * @return {@code true} si el usuario tiene ese rol
     */
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
