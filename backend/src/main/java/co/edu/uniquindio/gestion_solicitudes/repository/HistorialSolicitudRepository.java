package co.edu.uniquindio.gestion_solicitudes.repository;

import co.edu.uniquindio.gestion_solicitudes.domain.HistorialSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repositorio JPA para la entidad {@link HistorialSolicitud}.
 * <p>
 * Provee operaciones CRUD básicas y una consulta personalizada
 * para obtener todos los eventos del historial de una solicitud específica.
 * </p>
 *
 * @author Manu-Z
 * @version 1.0
 */
@Repository
public interface HistorialSolicitudRepository extends JpaRepository<HistorialSolicitud, Long> {

    /**
     * Obtiene todos los eventos del historial asociados a una solicitud.
     *
     * @param solicitudId id de la solicitud
     * @return lista de eventos ordenados por fecha de inserción,
     *         vacía si la solicitud no tiene historial
     */
    List<HistorialSolicitud> findBySolicitudId(Long solicitudId);
}
