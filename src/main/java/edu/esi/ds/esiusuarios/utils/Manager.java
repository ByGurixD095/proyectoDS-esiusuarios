package edu.esi.ds.esiusuarios.utils;

import edu.esi.ds.esiusuarios.service.BrevoService;
import edu.esi.ds.esiusuarios.service.EmailService;

public class Manager {

    private static Manager instance;
    private EmailService emailService;

    private Manager() {
        this.emailService = new BrevoService();
    };

    private synchronized static Manager build() {
        if (instance == null) {
            instance = new Manager();
        }

        return instance;
    }

    public EmailService getEmailService() {
        return this.emailService;
    }
}
