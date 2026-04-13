package co.edu.uniquindio.gestion_solicitudes.service;

import co.edu.uniquindio.gestion_solicitudes.domain.Rol;
import co.edu.uniquindio.gestion_solicitudes.domain.Usuario;
import co.edu.uniquindio.gestion_solicitudes.dto.response.UsuarioResponse;
import co.edu.uniquindio.gestion_solicitudes.dto.request.UsuarioUpdateRequest;
import co.edu.uniquindio.gestion_solicitudes.exception.ResourceNotFoundException;
import co.edu.uniquindio.gestion_solicitudes.repository.UsuarioRepository;
import co.edu.uniquindio.gestion_solicitudes.service.implementar.UsuarioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario("Juan Perez", "juan@test.com", "password", true, Rol.ESTUDIANTE);
    }

    @Test
    void obtenerPorId_cuandoExiste_debeRetornarUsuario() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        UsuarioResponse response = usuarioService.obtenerPorId(1L);

        assertNotNull(response);
        assertEquals("Juan Perez", response.getNombre());
        assertEquals("juan@test.com", response.getCorreo());
        assertEquals(Rol.ESTUDIANTE, response.getRol());
        assertTrue(response.isActivo());
    }

    @Test
    void obtenerPorId_cuandoNoExiste_debeLanzarExcepcion() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> usuarioService.obtenerPorId(99L));
    }

    @Test
    void listar_cuandoFiltroActivo_debeRetornarSoloActivos() {
        Usuario inactivo = new Usuario("Pedro", "pedro@test.com", "password", false, Rol.ESTUDIANTE);
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario, inactivo));

        List<UsuarioResponse> response = usuarioService.listar(true, null);

        assertEquals(1, response.size());
        assertEquals("Juan Perez", response.get(0).getNombre());
    }

    @Test
    void listar_cuandoFiltroRol_debeRetornarSoloEseRol() {
        Usuario responsable = new Usuario("Carlos", "carlos@test.com", "password", true, Rol.RESPONSABLE);
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario, responsable));

        List<UsuarioResponse> response = usuarioService.listar(null, Rol.RESPONSABLE);

        assertEquals(1, response.size());
        assertEquals("Carlos", response.get(0).getNombre());
    }

    @Test
    void actualizar_cuandoExiste_debeActualizarNombre() {
        UsuarioUpdateRequest request = new UsuarioUpdateRequest("Nuevo Nombre", null);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        UsuarioResponse response = usuarioService.actualizar(1L, request);

        assertNotNull(response);
        assertEquals("Nuevo Nombre", response.getNombre());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }
}