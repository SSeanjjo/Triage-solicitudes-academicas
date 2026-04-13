package co.edu.uniquindio.gestion_solicitudes.service.implementar;


import co.edu.uniquindio.gestion_solicitudes.configuracion.JwtUtil;
import co.edu.uniquindio.gestion_solicitudes.domain.Usuario;
import co.edu.uniquindio.gestion_solicitudes.dto.request.LoginRequest;
import co.edu.uniquindio.gestion_solicitudes.dto.request.UsuarioCreateRequest;
import co.edu.uniquindio.gestion_solicitudes.dto.response.LoginResponse;
import co.edu.uniquindio.gestion_solicitudes.dto.response.UsuarioResponse;
import co.edu.uniquindio.gestion_solicitudes.exception.BadRequestException;
import co.edu.uniquindio.gestion_solicitudes.repository.UsuarioRepository;
import co.edu.uniquindio.gestion_solicitudes.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UsuarioRepository usuarioRepository,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByCorreo(request.getCorreo())
                .orElseThrow(() -> new BadRequestException("Correo o password incorrectos"));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new BadRequestException("Correo o password incorrectos");
        }

        String token = jwtUtil.generarToken(usuario.getCorreo(), usuario.getRol().name());

        return new LoginResponse(token, "Bearer", usuario.getRol(), usuario.getNombre());
    }

    @Override
    public UsuarioResponse registrar(UsuarioCreateRequest request) {
        if (usuarioRepository.existsByCorreo(request.getCorreo())) {
            throw new BadRequestException("El correo ya está registrado");
        }

        Usuario usuario = new Usuario(
                request.getNombre(),
                request.getCorreo(),
                passwordEncoder.encode(request.getPassword()),
                true,
                request.getRol()
        );

        usuarioRepository.save(usuario);

        UsuarioResponse response = new UsuarioResponse();
        response.setId(usuario.getId());
        response.setNombre(usuario.getNombre());
        response.setCorreo(usuario.getCorreo());
        response.setRol(usuario.getRol());
        response.setActivo(usuario.isActivo());

        return response;
    }
}