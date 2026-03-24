package edu.esi.ds.esiusuarios.utils;

import org.springframework.stereotype.Component;

import edu.esi.ds.esiusuarios.service.EmailService;

@Component
public class Manager {

    private final EmailService emailService;

    public Manager(EmailService emailService) {
        this.emailService = emailService;
    }

    public EmailService getEmailService() {
        return this.emailService;
    }
}