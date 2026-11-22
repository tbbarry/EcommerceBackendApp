package com.backend.ecommerce.service.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class EmailService {
    private static final Pattern PLACEHOLDER = Pattern.compile("\\{\\{([a-z_]+)}}");
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendMail(String to, String userName, String link, String siteName, EmailTemplate template) {
        String defaultMessage = template == EmailTemplate.ACTIVATION_ACCOUNT
                ? "Thanks for joining " + siteName + "."
                : "We received a request to reset your " + siteName + " password.";
        send(to, template.getSubject(siteName), template,
                Map.of("site_name", siteName, "user_name", userName, "link", link, "message", defaultMessage), null);
    }

    public void sendAccountMail(String to, String subject, String message, String userName, String link,
                                String siteName, EmailTemplate template) {
        if (subject.contains("\r") || subject.contains("\n")) {
            throw new IllegalArgumentException("The email subject must be a single line.");
        }
        String expiry = template == EmailTemplate.ACTIVATION_ACCOUNT
                ? "This link expires in 24 hours."
                : "This link expires in 30 minutes.";
        String action = template == EmailTemplate.ACTIVATION_ACCOUNT ? "Activate my account" : "Reset password";
        String plainText = "Hi " + userName + ",\n\n" + message + "\n\n"
                + action + ":\n" + link + "\n\n" + expiry
                + (template == EmailTemplate.RESET_PASSWORD
                        ? "\n\nIf you did not request this, you can safely ignore this email. No changes will be made to your account."
                        : "");
        send(to, subject, template,
                Map.of("site_name", siteName, "user_name", userName, "link", link, "message", message),
                plainText);
    }

    public void send(String to, String subject, EmailTemplate template, Map<String, String> values, String plainText) {
        send(to, subject, template, values, plainText, null);
    }

    public void sendOrder(String to, String subject, Map<String, String> values, String plainText,
                          List<OrderEmailItem> items) {
        send(to, subject, EmailTemplate.ORDER_STATUS, values, plainText, items);
    }

    public void sendContactReply(String to, String subject, String text) {
        if (subject.contains("\r") || subject.contains("\n")) {
            throw new IllegalArgumentException("The email subject must be a single line.");
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, false);
            mailSender.send(message);
        } catch (MessagingException | MailException e) {
            throw new EmailDeliveryException("Unable to send email", e);
        }
    }

    private void send(String to, String subject, EmailTemplate template, Map<String, String> values, String plainText,
                      List<OrderEmailItem> items) {
        String html = render(template, values, items);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            helper.setTo(to);
            helper.setSubject(subject);
            if (plainText == null) helper.setText(html, true);
            else helper.setText(plainText, html);
            mailSender.send(message);
        } catch (MessagingException | MailException e) {
            throw new EmailDeliveryException("Impossible d'envoyer l'e-mail", e);
        }
    }

    private String render(EmailTemplate template, Map<String, String> values, List<OrderEmailItem> items) {
        String source;
        try (InputStream input = new ClassPathResource("template/" + template.getFileName()).getInputStream()) {
            source = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new EmailDeliveryException("Impossible de charger le modèle d'e-mail", e);
        }
        Matcher matcher = PLACEHOLDER.matcher(source);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            if ("items".equals(matcher.group(1)) && template == EmailTemplate.ORDER_STATUS && items != null) {
                matcher.appendReplacement(result, Matcher.quoteReplacement(renderItems(items)));
                continue;
            }
            String value = values.get(matcher.group(1));
            if (value == null) throw new EmailDeliveryException("Variable de modèle manquante : " + matcher.group(1), null);
            matcher.appendReplacement(result, Matcher.quoteReplacement(HtmlUtils.htmlEscape(value, StandardCharsets.UTF_8.name())));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private String renderItems(List<OrderEmailItem> items) {
        if (items.isEmpty()) {
            return "<tr><td style=\"padding:12px 0;color:#65756a;\">Item details unavailable.</td></tr>";
        }
        StringBuilder rows = new StringBuilder();
        for (OrderEmailItem item : items) {
            if (item.name() == null || item.name().isBlank() || item.quantity() <= 0) {
                throw new EmailDeliveryException("Article de commande invalide dans la notification", null);
            }
            rows.append("<tr><td style=\"padding:12px 0;border-bottom:1px solid #e8eee9;line-height:1.4;\">")
                    .append("<strong>").append(escape(item.name())).append("</strong>");
            if (!item.options().isEmpty()) {
                rows.append("<br><span style=\"color:#65756a;font-size:13px;\">")
                        .append(escape(item.options())).append("</span>");
            }
            rows.append("</td><td align=\"right\" style=\"padding:12px 0;border-bottom:1px solid #e8eee9;white-space:nowrap;\">")
                    .append("× ").append(item.quantity()).append("</td></tr>");
        }
        return rows.toString();
    }

    private String escape(String text) {
        return HtmlUtils.htmlEscape(text, StandardCharsets.UTF_8.name());
    }
}
