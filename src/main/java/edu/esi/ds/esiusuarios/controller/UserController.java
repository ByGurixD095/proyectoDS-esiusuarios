package edu.esi.ds.esiusuarios.controller;

import edu.esi.ds.esiusuarios.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    UsuarioService userService;

    // POST /users/register
    @PostMapping("/register")
    public void register(@RequestBody Map<String, Object> info) {
        String email = info.get("email").toString().trim();
        String name = info.get("name").toString().trim();
        String pwd1 = info.get("pwd1").toString().trim();
        String pwd2 = info.get("pwd2").toString().trim();

        // Validación de email con regex (como hace el libro)
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
        Matcher matcher = pattern.matcher(email);
        if (!matcher.find()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El correo suministrado no tiene un formato válido");
        }

        // Contraseñas coinciden y longitud mínima
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
            // Capturamos duplicado de name o email como hace el libro
            SQLException cause = (SQLException) e.getCause().getCause();
            throw new ResponseStatusException(HttpStatus.CONFLICT, cause.getMessage());
        }
    }

    // PUT /users/login → devuelve el JWT
    @PutMapping("/login")
    public String login(@RequestBody Map<String, Object> info) {
        String name = info.get("name").toString().trim();
        String pwd = info.get("pwd").toString().trim();
        return userService.login(name, pwd);
    }

    // DELETE /users/removeUser → requiere JWT en header Authorization
    @DeleteMapping("/removeUser")
    public void removeUser(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        userService.cancelarCuenta(token);
    }

    // POST /users/forgot-password
    @PostMapping("/forgot-password")
    public void forgotPassword(@RequestBody Map<String, Object> info) {
        String email = info.get("email").toString().trim();
        userService.solicitarResetPassword(email);
    }

    // POST /users/reset-password
    @PostMapping("/reset-password")
    public void resetPassword(@RequestBody Map<String, Object> info) {
        String token = info.get("token").toString().trim();
        String nuevaPwd = info.get("pwd").toString().trim();

        if (nuevaPwd.length() < 8) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "La contraseña debe tener al menos 8 caracteres");
        }
        userService.resetPassword(token, nuevaPwd);
    }
}