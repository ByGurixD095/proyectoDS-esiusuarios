package edu.esi.ds.esiusuarios.controller;

import edu.esi.ds.esiusuarios.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    UsuarioService userService;

    // ── Validar token ─────────────────────────────────────────
    @GetMapping("/token/{token}")
    public ResponseEntity<String> validateToken(@PathVariable String token) {
        // Si el token no es válido, el servicio lanza 401 — no filtramos nada más
        String email = userService.validateToken(token);
        return ResponseEntity.ok(email);
    }

    // ── Registro ──────────────────────────────────────────────
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody Map<String, Object> info) {
        String email = getField(info, "email");
        String name = getField(info, "name");
        String pwd1 = getField(info, "pwd1");
        String pwd2 = getField(info, "pwd2");

        Pattern emailPattern = Pattern.compile(
                "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
        if (!emailPattern.matcher(email).find()) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "El formato del correo no es válido.");
        }

        if (name.length() < 3 || name.length() > 100) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "El nombre debe tener entre 3 y 100 caracteres.");
        }

        if (!pwd1.equals(pwd2)) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "Las contraseñas no coinciden.");
        }

        if (pwd1.length() < 8) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "La contraseña debe tener al menos 8 caracteres.");
        }

        try {
            userService.register(name, pwd1, email);
        } catch (DataAccessException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe una cuenta con ese nombre de usuario o correo.");
        }

        return ResponseEntity.ok().build();
    }

    // ── Login ─────────────────────────────────────────────────
    @PutMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, Object> info) {
        String name = getField(info, "name");
        String email = getField(info, "email");
        String pwd = getField(info, "pwd");

        String token = userService.login(name, email, pwd);
        return ResponseEntity.ok(token);
    }

    // ── Eliminar cuenta ───────────────────────────────────────
    @DeleteMapping("/removeUser")
    public ResponseEntity<Void> removeUser(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        userService.cancelarCuenta(token);
        return ResponseEntity.noContent().build();
    }

    // ── Recuperación de contraseña ────────────────────────────
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody Map<String, Object> info) {
        String email = getField(info, "email");

        try {
            userService.solicitarResetPassword(email);
        } catch (ResponseStatusException e) {
            // ── Respuesta siempre 200, nunca revelamos si el email existe ──
            // Un atacante no puede enumerar cuentas por fuerza bruta.
        }

        return ResponseEntity.ok().build();
    }

    // ── Reset de contraseña ───────────────────────────────────
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody Map<String, Object> info) {
        String token = getField(info, "token");
        String nuevaPwd = getField(info, "pwd");

        if (nuevaPwd.length() < 8) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "La contraseña debe tener al menos 8 caracteres.");
        }

        try {
            userService.resetPassword(token, nuevaPwd);
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El token no es válido o ha expirado.");
        }

        return ResponseEntity.ok().build();
    }

    private String getField(Map<String, Object> map, String key) {
        Object val = map.get(key);
        return val != null ? val.toString().trim() : "";
    }
}