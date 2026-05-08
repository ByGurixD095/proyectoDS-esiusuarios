package edu.esi.ds.esiusuarios.controller;

import edu.esi.ds.esiusuarios.dto.*;
import edu.esi.ds.esiusuarios.service.UsuarioService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    UsuarioService userService;

    // ── Validar token ─────────────────────────────────────────
    @GetMapping("/token/{token}")
    public ResponseEntity<String> validateToken(@PathVariable String token) {
        String email = userService.validateToken(token);
        return ResponseEntity.ok(email);
    }

    // ── Registro ──────────────────────────────────────────────
    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
        if (!request.pwd1().equals(request.pwd2())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Las contraseñas no coinciden");
        }

        userService.register(request.name(), request.pwd1(), request.email());
        return ResponseEntity.ok().build();
    }

    // ── Login ─────────────────────────────────────────────────
    @PutMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest request) {
        String token = userService.login(request.name(), request.email(), request.pwd());
        return ResponseEntity.ok(token);
    }

    // ── Eliminar cuenta ───────────────────────────────────────
    @DeleteMapping("/removeUser")
    public ResponseEntity<Void> removeUser(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        userService.cancelarCuenta(token);
        return ResponseEntity.noContent().build();
    }

    // ── Recuperación de contraseña ────────────────────────────
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        try {
            userService.solicitarResetPassword(request.email());
        } catch (ResponseStatusException e) {
            // Se ignora intencionadamente por seguridad (evitar enumeración de usuarios)
        }
        return ResponseEntity.ok().build();
    }

    // ── Reset de contraseña ───────────────────────────────────
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        System.out.println("Reset password request: token=" + request.token() + ", pwd= [PROTEGIDO]");

        try {
            userService.resetPassword(request.token(), request.pwd());
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El token no es válido o ha expirado.");
        }

        return ResponseEntity.ok().build();
    }

    // ── Cambio de contraseña ──────────────────────────────────
    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody ChangePasswordRequest request) {

        String token = authHeader.replace("Bearer ", "");
        userService.cambiarPassword(token, request.pwdActual(), request.pwdNueva());
        return ResponseEntity.ok().build();
    }
}