package edu.esi.ds.esiusuarios.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 320)
    private String correo;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoCuenta estado = EstadoCuenta.ACTIVA;

    @Column(name = "creada_en", nullable = false, updatable = false)
    private LocalDateTime creadaEn = LocalDateTime.now();

    @Column(name = "cancelada_en")
    private LocalDateTime canceladaEn;

    @Column(name = "reset_token", length = 64)
    private String resetToken;

    @Column(name = "reset_token_expira_en")
    private LocalDateTime resetTokenExpiraEn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public EstadoCuenta getEstado() {
        return estado;
    }

    public void setEstado(EstadoCuenta estado) {
        this.estado = estado;
    }

    public LocalDateTime getCreadaEn() {
        return creadaEn;
    }

    public void setCreadaEn(LocalDateTime creadaEn) {
        this.creadaEn = creadaEn;
    }

    public LocalDateTime getCanceladaEn() {
        return canceladaEn;
    }

    public void setCanceladaEn(LocalDateTime canceladaEn) {
        this.canceladaEn = canceladaEn;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public LocalDateTime getResetTokenExpiraEn() {
        return resetTokenExpiraEn;
    }

    public void setResetTokenExpiraEn(LocalDateTime resetTokenExpiraEn) {
        this.resetTokenExpiraEn = resetTokenExpiraEn;
    }
}