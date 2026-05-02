package edu.esi.ds.esiusuarios.service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class GmailService extends EmailService {

    @Value("${correo.username}")
    private String username;

    @Value("${correo.password}")
    private String appPassword;

    @Override
    public void sendEmail(String destinatario, Object... params) {
        String asunto = params.length > 0 ? params[0].toString() : "Notificación";
        String cuerpo = params.length > 1 ? params[1].toString() : "";

        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.connectiontimeout", "10000");
            props.put("mail.smtp.timeout", "10000");
            props.put("mail.smtp.writetimeout", "10000");

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, appPassword);
                }
            });

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject(asunto, "UTF-8");
            message.setContent(cuerpo, "text/html; charset=UTF-8");

            Transport.send(message);
            System.out.println("Correo enviado correctamente a " + destinatario);

        } catch (Exception e) {
            System.err.println("Error al enviar correo a " + destinatario + ": " + e.getMessage());
        }
    }
}