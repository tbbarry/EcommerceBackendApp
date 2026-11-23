package com.backend.ecommerce.service.email;

import com.backend.ecommerce.entity.OrderMessageTemplate;
import com.backend.ecommerce.repository.OrderMessageTemplateRepository;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class OrderMessageTemplateService {
    public static final String ACCOUNT_ACTIVATION = "ACCOUNT_ACTIVATION";
    public static final String PASSWORD_RESET = "PASSWORD_RESET";
    private static final List<String> KEYS = List.of("PAID", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED",
            "ADMIN_PAID", ACCOUNT_ACTIVATION, PASSWORD_RESET);
    private static final Map<String, Template> DEFAULTS = Map.of(
            "PAID", new Template("PAID", "Order confirmed", "Thank you for your order, {{customer_name}}! Your order is confirmed."),
            "PROCESSING", new Template("PROCESSING", "Your order is being prepared", "We're getting your order ready."),
            "SHIPPED", new Template("SHIPPED", "Your order has shipped", "Good news! Your order is on its way."),
            "DELIVERED", new Template("DELIVERED", "Your order has been delivered", "Your order has been marked as delivered. We hope you love it!"),
            "CANCELLED", new Template("CANCELLED", "Your order was canceled", "Your order has been canceled."),
            "ADMIN_PAID", new Template("ADMIN_PAID", "New paid order", "A new order from {{customer_name}} is ready to prepare."),
            ACCOUNT_ACTIVATION, new Template(ACCOUNT_ACTIVATION, "Activate your account - {{site_name}}",
                    "Thanks for joining {{site_name}}."),
            PASSWORD_RESET, new Template(PASSWORD_RESET, "Reset your password - {{site_name}}",
                    "We received a request to reset your {{site_name}} password."));
    private static final Pattern ORDER_VARIABLE = Pattern.compile("\\{\\{(?:customer_name|order_number)}}");
    private static final Pattern ACCOUNT_VARIABLE = Pattern.compile("\\{\\{(?:user_name|site_name)}}");
    private final OrderMessageTemplateRepository repository;

    public record Template(String key, String subject, String message) {}
    public record Update(@NotBlank @Size(max = 200) String subject,
                         @NotBlank @Size(max = 2000) String message) {}

    @Transactional(readOnly = true)
    public List<Template> list() {
        return KEYS.stream().map(this::get).toList();
    }

    @Transactional(readOnly = true)
    public Template get(String key) {
        if (!DEFAULTS.containsKey(key)) throw new IllegalArgumentException("Unknown notification template.");
        return repository.findById(key)
                .map(item -> new Template(key, item.getSubject(), item.getMessage()))
                .orElse(DEFAULTS.get(key));
    }

    @Transactional
    public Template update(String key, Update update) {
        if (!DEFAULTS.containsKey(key)) throw new IllegalArgumentException("Unknown notification template.");
        if (update.subject().contains("\r") || update.subject().contains("\n")) {
            throw new IllegalArgumentException("The subject must be a single line.");
        }
        validateVariables(key, update.subject());
        validateVariables(key, update.message());
        repository.save(new OrderMessageTemplate(key, update.subject().trim(), update.message().trim()));
        return new Template(key, update.subject().trim(), update.message().trim());
    }

    private void validateVariables(String key, String text) {
        Pattern allowed = isAccountTemplate(key) ? ACCOUNT_VARIABLE : ORDER_VARIABLE;
        String withoutAllowed = allowed.matcher(text).replaceAll("");
        if (withoutAllowed.contains("{{") || withoutAllowed.contains("}}")) {
            throw new IllegalArgumentException(isAccountTemplate(key)
                    ? "Only {{user_name}} and {{site_name}} are supported."
                    : "Only {{customer_name}} and {{order_number}} are supported.");
        }
    }

    private boolean isAccountTemplate(String key) {
        return ACCOUNT_ACTIVATION.equals(key) || PASSWORD_RESET.equals(key);
    }

    public static String resolve(String text, String customerName, String orderNumber) {
        return text.replace("{{customer_name}}", customerName).replace("{{order_number}}", orderNumber);
    }

    public static String resolveAccount(String text, String userName, String siteName) {
        return text.replace("{{user_name}}", userName).replace("{{site_name}}", siteName);
    }
}
