package edu.esi.ds.esiusuarios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ChangePasswordRequest(
                @NotBlank(message = "La contraseña actual es obligatoria") String pwdActual,

                @NotBlank(message = "La nueva contraseña es obligatoria") @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*]).{12,}$", message = "La nueva contraseña no cumple los requisitos") String pwdNueva) {
}