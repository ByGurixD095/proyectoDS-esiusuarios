package edu.esi.ds.esiusuarios.service;

public abstract class EmailService {
    public abstract void sendEmail(String destinatario, Object... params);
}