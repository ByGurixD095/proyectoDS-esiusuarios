package edu.esi.ds.esiusuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
                String name,
                @Email(message = "Formato inválido") String email,
                @NotBlank(message = "La contraseña es obligatoria") String pwd) {
}