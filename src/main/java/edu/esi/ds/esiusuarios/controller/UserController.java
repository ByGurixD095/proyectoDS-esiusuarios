package edu.esi.ds.esiusuarios.controller;

import edu.esi.ds.esiusuarios.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLException;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    UsuarioService userService;

    @GetMapping("/token/{token}")
    public ResponseEntity<String> validateToken(@PathVariable String token) {
        String email = userService.validateToken(token);
        return ResponseEntity.ok(email);
    }

    // POST /users/register
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody Map<String, Object> info) {
        String email = info.get("email").toString().trim();
        String name = info.get("name").toString().trim();
        String pwd1 = info.get("pwd1").toString().trim();
        String pwd2 = info.get("pwd2").toString().trim();

        // According to rfc 5322
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
        if (!pattern.matcher(email).find()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El correo suministrado no tiene un formato válido");
        }
        if (!pwd1.equals(pwd2)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Las contraseñas no coinciden");
        }
        if (pwd1.length() < 8) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "La contraseña debe tener al menos 8 caracteres");
        }

        try {
            userService.register(name, pwd1, email);
        } catch (DataAccessException e) {
            SQLException cause = (SQLException) e.getCause().getCause();
            throw new ResponseStatusException(HttpStatus.CONFLICT, cause.getMessage());
        }

        return ResponseEntity.ok().build();
    }

    // PUT /users/login → devuelve el JWT en el body
    @PutMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, Object> info) {
        String name = info.get("name").toString().trim();
        String pwd = info.get("pwd").toString().trim();
        String token = userService.login(name, pwd);
        return ResponseEntity.ok(token);
    }

    // DELETE /users/removeUser
    @DeleteMapping("/removeUser")
    public ResponseEntity<Void> removeUser(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        userService.cancelarCuenta(token);
        return ResponseEntity.noContent().build();
    }

    // POST /users/forgot-password
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody Map<String, Object> info) {
        String email = info.get("email").toString().trim();
        userService.solicitarResetPassword(email);
        return ResponseEntity.ok().build();
    }

    // POST /users/reset-password
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody Map<String, Object> info) {
        String token = info.get("token").toString().trim();
        String nuevaPwd = info.get("pwd").toString().trim();

        if (nuevaPwd.length() < 8) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "La contraseña debe tener al menos 8 caracteres");
        }
        userService.resetPassword(token, nuevaPwd);
        return ResponseEntity.ok().build();
    }
}