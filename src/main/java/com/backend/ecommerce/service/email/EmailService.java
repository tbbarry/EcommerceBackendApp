package com.backend.ecommerce.service.email;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendMail(String to, String userName, String link, String siteName, EmailTemplate template) {


        String html = loadTemplate(template)
                .replace("{{site_name}}", siteName)
                .replace("{{user_name}}", userName)
                .replace("{{link}}", link);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(template.getSubject(siteName));
            helper.setText(html, true); // HTML

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Erreur envoi email");
        }
    }
    
    private String loadTemplate(EmailTemplate template) {
            try {
                InputStream inputStream = getClass()
                        .getResourceAsStream("/template/" + template.getFileName());

                return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            } catch (Exception e) {
                throw new RuntimeException("Erreur chargement template email", e);
            }
    }
}

