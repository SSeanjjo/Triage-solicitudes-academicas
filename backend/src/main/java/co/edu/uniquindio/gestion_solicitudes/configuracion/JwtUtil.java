package co.edu.uniquindio.gestion_solicitudes.configuracion;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
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
 *
 * @author Manu-Z
 * @version 1.0
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    private Key getKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }
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
                .signWith(getKey())
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
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validarToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String obtenerRol(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("rol", String.class);
    }
}
