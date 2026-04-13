package co.edu.uniquindio.gestion_solicitudes.service;

import co.edu.uniquindio.gestion_solicitudes.configuracion.JwtUtil;
import co.edu.uniquindio.gestion_solicitudes.domain.Rol;
import co.edu.uniquindio.gestion_solicitudes.domain.Usuario;
import co.edu.uniquindio.gestion_solicitudes.dto.request.LoginRequest;
import co.edu.uniquindio.gestion_solicitudes.dto.response.LoginResponse;
import co.edu.uniquindio.gestion_solicitudes.dto.request.UsuarioCreateRequest;
import co.edu.uniquindio.gestion_solicitudes.dto.response.UsuarioResponse;
import co.edu.uniquindio.gestion_solicitudes.exception.BadRequestException;
import co.edu.uniquindio.gestion_solicitudes.repository.UsuarioRepository;
import co.edu.uniquindio.gestion_solicitudes.service.implementar.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    private Usuario usuario;
    private LoginRequest loginRequest;
    private UsuarioCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        usuario = new Usuario("Juan Perez", "juan@test.com", "encodedPassword", true, Rol.ESTUDIANTE);
        loginRequest = new LoginRequest("juan@test.com", "123456");
        createRequest = new UsuarioCreateRequest("Juan Perez", "juan@test.com", "123456", Rol.ESTUDIANTE);
    }

    @Test
    void login_cuandoCredencialesCorrectas_debeRetornarToken() {
        when(usuarioRepository.findByCorreo("juan@test.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("123456", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generarToken(anyString(), anyString())).thenReturn("token123");

        LoginResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("token123", response.getToken());
        assertEquals("Bearer", response.getTipo());
        assertEquals(Rol.ESTUDIANTE, response.getRol());
        assertEquals("Juan Perez", response.getNombre());
    }

    @Test
    void login_cuandoCorreoNoExiste_debeLanzarExcepcion() {
        when(usuarioRepository.findByCorreo("juan@test.com")).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> authService.login(loginRequest));
    }

    @Test
    void login_cuandoPasswordIncorrecto_debeLanzarExcepcion() {
        when(usuarioRepository.findByCorreo("juan@test.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("123456", "encodedPassword")).thenReturn(false);

        assertThrows(BadRequestException.class, () -> authService.login(loginRequest));
    }

    @Test
    void registrar_cuandoDatosCorrectos_debeCrearUsuario() {
        when(usuarioRepository.existsByCorreo("juan@test.com")).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        UsuarioResponse response = authService.registrar(createRequest);

        assertNotNull(response);
        assertEquals("Juan Perez", response.getNombre());
        assertEquals("juan@test.com", response.getCorreo());
        assertEquals(Rol.ESTUDIANTE, response.getRol());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void registrar_cuandoCorreoYaExiste_debeLanzarExcepcion() {
        when(usuarioRepository.existsByCorreo("juan@test.com")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.registrar(createRequest));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
}
