package co.edu.uniquindio.gestion_solicitudes.configuracion;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * Utilidad para generación y validación de tokens JWT.
 * <p>
 * Genera tokens firmados con HMAC-SHA256 que incluyen el correo del usuario
 * como subject y el rol como claim adicional. Los tokens tienen una validez
 * de 10 horas desde su emisión.
 * </p>
 * <p>
 * La clave de firma se genera aleatoriamente en cada arranque del servidor,
 * por lo que los tokens previos quedan inválidos al reiniciar.
 * </p>
 *
 * @author Manu-Z
 * @version 1.0
 */
@Component
public class JwtUtil {

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long EXPIRACION = 1000 * 60 * 60 * 10; // 10 horas

    /**
     * Genera un token JWT firmado para el usuario autenticado.
     *
     * @param correo correo del usuario (subject del token)
     * @param rol    rol del usuario incluido como claim
     * @return token JWT en formato compacto {@code header.payload.signature}
     */
    public String generarToken(String correo, String rol) {
        return Jwts.builder()
                .setSubject(correo)
                .claim("rol", rol)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRACION))
                .signWith(key)
                .compact();
    }

    /**
     * Extrae el correo del subject del token JWT.
     *
     * @param token token JWT válido
     * @return correo del usuario
     */
    public String obtenerCorreo(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Extrae el rol del claim del token JWT.
     *
     * @param token token JWT válido
     * @return rol del usuario como String
     */
    public boolean validarToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    /**
     * Valida que el token JWT sea auténtico y no haya expirado.
     *
     * @param token token JWT a validar
     * @return {@code true} si el token es válido, {@code false} si está
     *         expirado, mal formado o firmado con otra clave
     */
    public String obtenerRol(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("rol", String.class);
    }
}
