package co.edu.uniquindio.gestion_solicitudes.controller;


import co.edu.uniquindio.gestion_solicitudes.dto.LoginRequest;
import co.edu.uniquindio.gestion_solicitudes.dto.UsuarioCreateRequest;
import co.edu.uniquindio.gestion_solicitudes.dto.LoginResponse;
import co.edu.uniquindio.gestion_solicitudes.dto.UsuarioResponse;
import co.edu.uniquindio.gestion_solicitudes.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponse> registrar(@RequestBody UsuarioCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrar(request));
    }
}