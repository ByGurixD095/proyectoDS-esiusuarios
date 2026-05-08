package edu.esi.ds.esiusuarios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ResetPasswordRequest(
        @NotBlank(message = "El token es obligatorio") String token,

        @NotBlank(message = "La nueva contraseña es obligatoria") @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*]).{12,}$", message = "La nueva contraseña no cumple con los requisitos de seguridad") String pwd) {
}