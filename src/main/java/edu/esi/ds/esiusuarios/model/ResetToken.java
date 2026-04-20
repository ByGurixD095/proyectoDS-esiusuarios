package edu.esi.ds.esiusuarios.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reset_tokens")
public class ResetToken {

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false, length = 64)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime expires;

    @Column(nullable = false)
    private boolean used = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getExpiraEn() {
        return expires;
    }

    public void setExpiraEn(LocalDateTime e) {
        this.expires = e;
    }

    public boolean isUsado() {
        return used;
    }

    public void setUsado(boolean u) {
        this.used = u;
    }
}