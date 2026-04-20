package co.edu.uniquindio.gestion_solicitudes.repository;

import co.edu.uniquindio.gestion_solicitudes.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad {@link Usuario}.
 * <p>
 * Provee operaciones CRUD básicas y consultas personalizadas
 * para buscar usuarios por correo electrónico, usado principalmente
 * en el proceso de autenticación.
 * </p>
 *
 * @author Manu-Z
 * @version 1.0
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca un usuario por su correo electrónico.
     * Usado en el proceso de autenticación para verificar credenciales.
     *
     * @param correo correo electrónico del usuario
     * @return {@link Optional} con el usuario si existe, vacío si no
     */
    Optional<Usuario> findByCorreo(String correo);
    /**
     * Verifica si ya existe un usuario registrado con el correo indicado.
     * Usado en el registro para evitar duplicados.
     *
     * @param correo correo electrónico a verificar
     * @return {@code true} si el correo ya está registrado
     */
    boolean existsByCorreo(String correo);
}