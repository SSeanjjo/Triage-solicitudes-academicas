package co.edu.uniquindio.gestion_solicitudes.repository;

import co.edu.uniquindio.gestion_solicitudes.domain.Solicitud;
import co.edu.uniquindio.gestion_solicitudes.domain.EstadoSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repositorio JPA para la entidad {@link Solicitud}.
 * <p>
 * Provee operaciones CRUD básicas heredadas de {@link JpaRepository}
 * y consultas personalizadas para filtrar solicitudes por estado,
 * solicitante o responsable asignado.
 * </p>
 *
 * @author Manu-Z
 * @version 1.0
 */
@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {
    /**
     * Busca todas las solicitudes que se encuentran en un estado específico.
     *
     * @param estado estado por el que filtrar
     * @return lista de solicitudes en ese estado, vacía si no hay ninguna
     */
    List<Solicitud> findByEstado(EstadoSolicitud estado);
    /**
     * Busca todas las solicitudes creadas por un usuario específico.
     *
     * @param solicitanteId id del usuario solicitante
     * @return lista de solicitudes del solicitante, vacía si no hay ninguna
     */
    List<Solicitud> findBySolicitanteId(Long solicitanteId);
    /**
     * Busca todas las solicitudes asignadas a un responsable específico.
     *
     * @param responsableId id del usuario responsable
     * @return lista de solicitudes asignadas al responsable, vacía si no hay ninguna
     */
    List<Solicitud> findByResponsableId(Long responsableId);
}