package edu.esi.ds.esiusuarios.dto;

public class ResetPasswordRequest {
    private String token;
    private String pwd;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

}