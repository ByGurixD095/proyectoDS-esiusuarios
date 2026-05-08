package edu.esi.ds.esiusuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "El email es obligatorio") @Email(message = "El formato del correo no es válido") String email,

        @NotBlank(message = "El nombre es obligatorio") @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres") String name,

        @NotBlank(message = "La contraseña es obligatoria") @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*]).{12,}$", message = "La contraseña no cumple con los requisitos de seguridad") String pwd1,

        @NotBlank(message = "La confirmación es obligatoria") String pwd2) {
}