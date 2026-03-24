package edu.esi.ds.esiusuarios.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users", indexes = {
        @Index(columnList = "name", unique = true),
        @Index(columnList = "email", unique = true)
})
public class User {

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String pwd; // almacenamos el hash BCrypt, nunca en claro

    @Column(nullable = false)
    private boolean activo = true;

    // ── getters y setters ──────────────────────────────────────────

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean a) {
        this.activo = a;
    }
}