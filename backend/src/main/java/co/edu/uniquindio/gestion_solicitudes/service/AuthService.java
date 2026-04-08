package co.edu.uniquindio.gestion_solicitudes.service;



import co.edu.uniquindio.gestion_solicitudes.dto.LoginResponse;
import co.edu.uniquindio.gestion_solicitudes.dto.UsuarioCreateRequest;
import co.edu.uniquindio.gestion_solicitudes.dto.LoginRequest;
import co.edu.uniquindio.gestion_solicitudes.dto.UsuarioResponse;


public interface AuthService {

    LoginResponse login(LoginRequest request);
    UsuarioResponse registrar(UsuarioCreateRequest request);
}
