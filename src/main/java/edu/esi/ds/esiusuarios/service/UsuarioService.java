package edu.esi.ds.esiusuarios.service;

import edu.esi.ds.esiusuarios.model.ResetToken;
import edu.esi.ds.esiusuarios.model.User;
import edu.esi.ds.esiusuarios.repository.ResetTokenDAO;
import edu.esi.ds.esiusuarios.repository.UserDAO;
import edu.esi.ds.esiusuarios.utils.Manager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UsuarioService {

    @Autowired
    UserDAO userDAO;
    @Autowired
    ResetTokenDAO resetTokenDAO;
    @Autowired
    JwtService jwtService;
    @Autowired
    Manager manager;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // ── REGISTER ──────────────────────────────────────────────────

    public void register(String name, String pwd, String email) {
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setName(name);
        user.setEmail(email);
        user.setPwd(encoder.encode(pwd));
        userDAO.save(user);
    }

    // ── LOGIN → devuelve JWT ───────────────────────────────────────

    public String login(String name, String email, String pwd) {
        User user = null;

        if (!name.isEmpty()) {
            user = userDAO.findByName(name);
        } else {
            user = userDAO.findByEmail(email);
        }

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Credenciales inválidas");
        }

        if (!user.isActive()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cuenta cancelada");
        }

        if (!encoder.matches(pwd, user.getPwd())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Credenciales inválidas");
        }

        return jwtService.generarToken(user.getEmail());
    }

    // ── VALIDATE TOKEN ────────────────────

    public String validateToken(String token) {
        if (!jwtService.esValido(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido o expirado");
        }
        return jwtService.extraerEmail(token);
    }

    // ── CANCEL ACCOUNT ────────────────────────────────────────────

    public void cancelarCuenta(String token) {
        String email = validateToken(token);
        User user = userDAO.findByEmail(email);

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }

        user.setActive(false);
        userDAO.save(user);
    }

    // ── FORGOT PASSWORD ───────────────────────────────────────────

    public void solicitarResetPassword(String email) {
        User user = userDAO.findByEmail(email);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Email no encontrado");
        }

        ResetToken rt = new ResetToken();
        rt.setId(UUID.randomUUID().toString());
        rt.setToken(UUID.randomUUID().toString().replace("-", ""));
        rt.setUser(user);
        rt.setExpiraEn(LocalDateTime.now().plusMinutes(30));
        resetTokenDAO.save(rt);

        String cuerpo = "<p>Tu token de recuperación es: <strong>" + rt.getToken() + "</strong></p>"
                + "<p>Expira en 30 minutos.</p>";
        manager.getEmailService().sendEmail(email, "Recuperación de contraseña", cuerpo);
    }

    // ── RESET PASSWORD ────────────────────────────────────────────

    public void resetPassword(String token, String nuevaPwd) {
        ResetToken rt = resetTokenDAO.findByToken(token);

        if (rt == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Token no válido");
        }

        if (rt.isUsado() || rt.getExpiraEn().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Token expirado o ya utilizado");
        }

        User user = rt.getUser();
        user.setPwd(encoder.encode(nuevaPwd));
        userDAO.save(user);

        rt.setUsado(true);
        resetTokenDAO.save(rt);
    }
}
