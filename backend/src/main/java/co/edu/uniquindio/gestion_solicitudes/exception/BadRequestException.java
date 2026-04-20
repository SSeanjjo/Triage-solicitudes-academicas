package co.edu.uniquindio.gestion_solicitudes.exception;

/**
 * Excepción lanzada cuando una petición contiene datos inválidos
 * o viola las reglas de negocio del sistema.
 * <p>
 * Resulta en una respuesta HTTP {@code 400 Bad Request}.
 * Ejemplos: correo ya registrado, transición de estado inválida,
 * responsable inactivo.
 * </p>
 *
 * @author Manu-Z
 * @version 1.0
 */
public class BadRequestException extends RuntimeException {
    /**
     * @param mensaje descripción del error de validación o negocio
     */
    public BadRequestException(String mensaje) {
        super(mensaje);
    }
}
