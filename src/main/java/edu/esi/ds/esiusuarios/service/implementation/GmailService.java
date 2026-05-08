package edu.esi.ds.esiusuarios.service.implementation;

import edu.esi.ds.esiusuarios.service.IEmailService;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class GmailService implements IEmailService {

    @Value("${correo.username}")
    private String username;

    @Value("${correo.password}")
    private String appPassword;

    public void sendEmail(String destinatario, String asunto, String cuerpo) {
        if (username == null || username.isBlank() || appPassword == null || appPassword.isBlank()) {
            System.err.println("[GmailService] Credenciales no configuradas — correo no enviado a: " + destinatario);
            return;
        }

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
            message.setFrom(new InternetAddress(username, "ESIEntradas", "UTF-8"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject(asunto, "UTF-8");
            message.setContent(cuerpo, "text/html; charset=UTF-8");

            Transport.send(message);
            System.out.println("[GmailService] Correo enviado correctamente a: " + destinatario);

        } catch (AuthenticationFailedException e) {
            System.err.println("[GmailService] Error de autenticación con Gmail: " + e.getMessage());
        } catch (MessagingException e) {
            System.err.println("[GmailService] Error al enviar correo a " + destinatario + ": " + e.getMessage());
        } catch (Exception e) {
            System.err.println("[GmailService] Error inesperado al enviar correo: " + e.getMessage());
        }
    }

    @Override
    public void sendPasswordResetEmail(String destinatario, String token) {
        String cuerpo = "<div style='font-family:Helvetica Neue,Arial,sans-serif;max-width:480px;'>"
                + "<h2 style='color:#1d1d1f;'>Recuperación de contraseña</h2>"
                + "<p style='color:#6e6e73;'>Tu token de recuperación es:</p>"
                + "<div style='background:#f5f5f7;border-radius:8px;padding:16px;text-align:center;"
                + "font-family:monospace;font-size:18px;font-weight:600;color:#1d1d1f;letter-spacing:2px;'>"
                + token
                + "</div>"
                + "<p style='color:#6e6e73;font-size:13px;margin-top:16px;'>"
                + "Expira en 10 minutos. Si no solicitaste este cambio, ignora este correo.</p>"
                + "</div>";

        sendEmail(destinatario, "Recuperación de contraseña", cuerpo);
    }
}