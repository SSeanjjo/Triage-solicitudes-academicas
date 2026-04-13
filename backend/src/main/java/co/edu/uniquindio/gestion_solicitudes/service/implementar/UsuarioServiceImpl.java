package co.edu.uniquindio.gestion_solicitudes.service.implementar;

import co.edu.uniquindio.gestion_solicitudes.domain.Rol;
import co.edu.uniquindio.gestion_solicitudes.domain.Usuario;
import co.edu.uniquindio.gestion_solicitudes.dto.request.UsuarioUpdateRequest;
import co.edu.uniquindio.gestion_solicitudes.dto.response.UsuarioResponse;
import co.edu.uniquindio.gestion_solicitudes.exception.ResourceNotFoundException;
import co.edu.uniquindio.gestion_solicitudes.repository.UsuarioRepository;
import co.edu.uniquindio.gestion_solicitudes.service.UsuarioService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UsuarioResponse obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return mapearResponse(usuario);
    }

    @Override
    public List<UsuarioResponse> listar(Boolean activo, Rol rol) {
        return usuarioRepository.findAll()
                .stream()
                .filter(u -> activo == null || u.isActivo() == activo)
                .filter(u -> rol == null || u.getRol() == rol)
                .map(this::mapearResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UsuarioResponse actualizar(Long id, UsuarioUpdateRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (request.getNombre() != null) {
            usuario.setNombre(request.getNombre());
        }
        if (request.getCorreo() != null) {
            usuario.setCorreo(request.getCorreo());
        }

        usuarioRepository.save(usuario);
        return mapearResponse(usuario);
    }

    private UsuarioResponse mapearResponse(Usuario usuario) {
        UsuarioResponse response = new UsuarioResponse();
        response.setId(usuario.getId());
        response.setNombre(usuario.getNombre());
        response.setCorreo(usuario.getCorreo());
        response.setRol(usuario.getRol());
        response.setActivo(usuario.isActivo());
        return response;
    }
}