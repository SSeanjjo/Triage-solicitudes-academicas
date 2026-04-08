package co.edu.uniquindio.gestion_solicitudes.repository;

import co.edu.uniquindio.gestion_solicitudes.domain.Solicitud;
import co.edu.uniquindio.gestion_solicitudes.domain.EstadoSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {

    List<Solicitud> findByEstado(EstadoSolicitud estado);
    List<Solicitud> findBySolicitanteId(Long solicitanteId);
    List<Solicitud> findByResponsableId(Long responsableId);
}