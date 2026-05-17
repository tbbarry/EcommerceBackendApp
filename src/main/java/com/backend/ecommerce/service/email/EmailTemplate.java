package com.backend.ecommerce.service.email;

public enum EmailTemplate {

    ACTIVATION_ACCOUNT("activation-email.html", "Activation de votre compte - %s"),
    RESET_PASSWORD("reset-password-email.html", "Réinitialisation du mot de passe - %s");

    private final String fileName;
    private final String subject;

    EmailTemplate(String fileName, String subject) {
        this.fileName = fileName;
        this.subject = subject;
    }

    public String getSubject(String siteName) {
        return subject.formatted(siteName);
    }

    public String getFileName() {
        return fileName;
    }
}