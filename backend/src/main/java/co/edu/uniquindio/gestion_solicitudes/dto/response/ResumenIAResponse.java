package co.edu.uniquindio.gestion_solicitudes.dto.response;

import lombok.*;
/**
 * DTO de respuesta con el resumen generado por IA (RF-09).
 *
 * @author Manu-Z
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumenIAResponse {
    /** Texto del resumen generado por el modelo de IA o por el fallback. */
    private String resumen;
    /** Nombre del proveedor que generó el resumen. Ej: "Gemini 1.5 Flash" o "Fallback (reglas)". */
    private String generadoPor;
}
