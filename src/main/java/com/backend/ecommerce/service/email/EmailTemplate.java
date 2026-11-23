package com.backend.ecommerce.service.email;

public enum EmailTemplate {

    ACTIVATION_ACCOUNT("activation-email.html", "Activate your account - %s"),
    RESET_PASSWORD("reset-password-email.html", "Reset your password - %s"),
    ORDER_STATUS("order-status-email.html", "Your order - %s");

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