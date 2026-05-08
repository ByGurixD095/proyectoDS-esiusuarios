package edu.esi.ds.esiusuarios.service.implementation;

import edu.esi.ds.esiusuarios.model.ResetToken;
import edu.esi.ds.esiusuarios.model.User;
import edu.esi.ds.esiusuarios.repository.ResetTokenDAO;
import edu.esi.ds.esiusuarios.repository.UserDAO;
import edu.esi.ds.esiusuarios.service.IEmailService;
import edu.esi.ds.esiusuarios.service.IUsuarioService;
import edu.esi.ds.esiusuarios.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class UsuarioService implements IUsuarioService {

    private final UserDAO userDAO;
    private final ResetTokenDAO resetTokenDAO;
    private final JwtService jwtService;
    private final IEmailService emailService;
    private final PasswordEncoder encoder;

    public UsuarioService(UserDAO userDAO,
            ResetTokenDAO resetTokenDAO,
            JwtService jwtService,
            IEmailService emailService,
            PasswordEncoder encoder) {
        this.userDAO = userDAO;
        this.resetTokenDAO = resetTokenDAO;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.encoder = encoder;
    }

    @Override
    public void register(String name, String pwd, String email) {
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setName(name);
        user.setEmail(email);
        user.setPwd(encoder.encode(pwd));
        userDAO.save(user);
    }

    @Override
    public String login(String name, String email, String pwd) {
        User user = (name != null && !name.isEmpty()) ? userDAO.findByName(name) : userDAO.findByEmail(email);

        if (user == null || !user.isActive() || !encoder.matches(pwd, user.getPwd())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Credenciales inválidas");
        }

        return jwtService.generarToken(user.getEmail());
    }

    @Override
    public String validateToken(String token) {
        if (!jwtService.esValido(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido");
        }
        return jwtService.extraerEmail(token);
    }

    @Override
    public void cancelarCuenta(String token) {
        String email = validateToken(token);
        User user = userDAO.findByEmail(email);
        if (user != null) {
            user.setActive(false);
            userDAO.save(user);
        }
    }

    @Override
    public void solicitarResetPassword(String email) {
        User user = userDAO.findByEmail(email);
        if (user == null)
            return;

        String rawToken = UUID.randomUUID().toString().replace("-", "");

        ResetToken rt = new ResetToken();
        rt.setId(UUID.randomUUID().toString());
        rt.setToken(hashToken(rawToken));
        rt.setUser(user);
        rt.setExpires(LocalDateTime.now().plusMinutes(10));
        resetTokenDAO.save(rt);

        emailService.sendPasswordResetEmail(email, rawToken);
    }

    @Override
    public void resetPassword(String token, String nuevaPwd) {
        ResetToken rt = resetTokenDAO.findByToken(hashToken(token));

        if (rt == null || rt.isUsed() || rt.getExpires().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Token no válido o expirado");
        }

        User user = rt.getUser();
        user.setPwd(encoder.encode(nuevaPwd));
        userDAO.save(user);

        rt.setUsed(true);
        resetTokenDAO.save(rt);
    }

    @Override
    public void cambiarPassword(String token, String pwdActual, String pwdNueva) {
        String email = validateToken(token);
        User user = userDAO.findByEmail(email);

        if (user == null || !encoder.matches(pwdActual, user.getPwd())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Contraseña incorrecta");
        }

        user.setPwd(encoder.encode(pwdNueva));
        userDAO.save(user);
    }

    private String hashToken(String rawToken) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Error criptográfico", e);
        }
    }
}