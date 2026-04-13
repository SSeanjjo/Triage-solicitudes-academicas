package co.edu.uniquindio.gestion_solicitudes.service;



import co.edu.uniquindio.gestion_solicitudes.dto.response.LoginResponse;
import co.edu.uniquindio.gestion_solicitudes.dto.request.UsuarioCreateRequest;
import co.edu.uniquindio.gestion_solicitudes.dto.request.LoginRequest;
import co.edu.uniquindio.gestion_solicitudes.dto.response.UsuarioResponse;


public interface AuthService {

    LoginResponse login(LoginRequest request);
    UsuarioResponse registrar(UsuarioCreateRequest request);
}
