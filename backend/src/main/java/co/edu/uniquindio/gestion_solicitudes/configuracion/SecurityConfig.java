package co.edu.uniquindio.gestion_solicitudes.configuracion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración de seguridad de la aplicación basada en JWT y Spring Security.
 * <p>
 * Define las rutas públicas, las rutas protegidas por rol, la política
 * de sesiones stateless y el filtro JWT que valida cada petición entrante.
 * </p>
 *
 * @author Manu-Z
 * @version 1.0
 * @see JwtFilter
 * @see JwtUtil
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    /**
     * Configura la cadena de filtros de seguridad HTTP.
     * <ul>
     *   <li>Deshabilita CSRF (API stateless)</li>
     *   <li>Sesiones stateless (sin HttpSession)</li>
     *   <li>Rutas {@code /api/auth/**} públicas</li>
     *   <li>Resto de rutas requieren autenticación</li>
     *   <li>Agrega {@link JwtFilter} antes del filtro estándar</li>
     * </ul>
     *
     * @param http configuración HTTP de Spring Security
     * @return cadena de filtros configurada
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Bean del codificador de contraseñas usando algoritmo BCrypt.
     *
     * @return instancia de {@link BCryptPasswordEncoder}
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}