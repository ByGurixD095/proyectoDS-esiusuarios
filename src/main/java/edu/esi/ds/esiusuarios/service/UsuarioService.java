package edu.esi.ds.esiusuarios.service;

public class UsuarioService {

    public String validateToken(String tokenID) {
        if (tokenID == null || tokenID.isEmpty()) {
            throw new IllegalArgumentException();
        }

        String userName = "Usuario obtenido de la base de datos";
        return userName;
    }

    public String token() {
        return "token";
    }
}
