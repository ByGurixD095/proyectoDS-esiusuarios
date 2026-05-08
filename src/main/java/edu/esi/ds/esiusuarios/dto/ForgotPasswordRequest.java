package edu.esi.ds.esiusuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequest(
                @NotBlank(message = "El email es obligatorio") @Email(message = "Formato inválido") String email) {
}