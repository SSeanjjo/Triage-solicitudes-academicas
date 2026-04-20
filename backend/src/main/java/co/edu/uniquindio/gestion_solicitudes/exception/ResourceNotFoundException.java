package co.edu.uniquindio.gestion_solicitudes.exception;


/**
 * Excepción lanzada cuando un recurso solicitado no existe en el sistema.
 * <p>
 * Resulta en una respuesta HTTP {@code 404 Not Found}.
 * Ejemplos de uso: solicitud no encontrada, usuario no encontrado.
 * </p>
 *
 * @author Manu-Z
 * @version 1.0
 */
public class ResourceNotFoundException extends RuntimeException {
    /**
     * @param mensaje descripción del recurso no encontrado
     */
    public ResourceNotFoundException(String mensaje) {
        super(mensaje);
    }
}