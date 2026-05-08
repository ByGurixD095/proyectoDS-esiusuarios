package edu.esi.ds.esiusuarios.service;

public interface IUsuarioService {
    void register(String name, String pwd, String email);

    String login(String name, String email, String pwd);

    String validateToken(String token);

    void cancelarCuenta(String token);

    void solicitarResetPassword(String email);

    void resetPassword(String token, String nuevaPwd);

    void cambiarPassword(String token, String pwdActual, String pwdNueva);
}