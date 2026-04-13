package co.edu.uniquindio.gestion_solicitudes.exception;


public class BadRequestException extends RuntimeException {
    public BadRequestException(String mensaje) {
        super(mensaje);
    }
}
