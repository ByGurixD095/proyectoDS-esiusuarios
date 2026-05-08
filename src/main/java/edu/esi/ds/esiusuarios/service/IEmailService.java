package edu.esi.ds.esiusuarios.service;

public interface IEmailService {
    void sendEmail(String destinatario, String asunto, String cuerpo);

    void sendPasswordResetEmail(String destinatario, String token);
}