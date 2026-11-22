package com.backend.ecommerce.service.email;

import com.backend.ecommerce.entity.OrderEmailNotification;
import com.backend.ecommerce.enums.OrderStatus;
import com.backend.ecommerce.repository.OrderRepository;
import com.backend.ecommerce.service.StoreSettingsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderEmailSender {
    private final EmailService emails;
    private final StoreSettingsService settings;
    private final String frontendUrl;
    private final Currency currency;
    private final OrderRepository orders;
    private final ObjectMapper objectMapper;

    public OrderEmailSender(EmailService emails, StoreSettingsService settings,
                            @Value("${app.frontend_app}") String frontendUrl,
                            @Value("${currency.code}") String currencyCode,
                            OrderRepository orders, ObjectMapper objectMapper) {
        this.emails = emails;
        this.settings = settings;
        this.frontendUrl = frontendUrl.replaceAll("/+$", "");
        this.currency = Currency.getInstance(currencyCode.toUpperCase(Locale.ROOT));
        this.orders = orders;
        this.objectMapper = objectMapper;
    }

    public void send(OrderEmailNotification notification) {
        String siteName = settings.getName();
        boolean admin = Boolean.TRUE.equals(notification.getAdminNotification());
        Content content = admin
                ? new Content("New paid order", "Confirmed",
                    "A new order from " + notification.getCustomerName() + " is ready to prepare.")
                : content(notification.getOrderStatus());
        String title = notification.getSubjectSnapshot() == null ? content.title() : notification.getSubjectSnapshot();
        String message = notification.getMessageSnapshot() == null ? content.message() : notification.getMessageSnapshot();
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
        format.setCurrency(currency);
        String total = format.format(notification.getTotal());
        String link = frontendUrl + (admin ? "/admin/orders?orderId=" + notification.getOrderId() : "/my-orders");
        String greeting = admin ? "Hello," : "Hi " + notification.getCustomerName() + ",";
        String action = admin ? "Manage orders" : "View my orders";
        List<OrderEmailItem> items = items(notification);
        Map<String, String> values = Map.ofEntries(
                Map.entry("site_name", siteName),
                Map.entry("greeting", greeting),
                Map.entry("title", title),
                Map.entry("message", message),
                Map.entry("order_number", notification.getOrderNumber()),
                Map.entry("status", content.status()),
                Map.entry("total", total),
                Map.entry("link", link),
                Map.entry("eyebrow", admin ? "New order" : "Order update"),
                Map.entry("action_label", action),
                Map.entry("footer", admin ? "Order notification for store administrators."
                        : "This email is about your order at " + siteName + "."));
        String itemLines = items.stream().map(item -> "- " + item.name()
                + (item.options().isEmpty() ? "" : " (" + item.options() + ")")
                + " × " + item.quantity()).collect(Collectors.joining("\n"));
        String plainText = greeting + "\n\n"
                + message + "\n\nOrder: " + notification.getOrderNumber()
                + "\nStatus: " + content.status() + "\nTotal: " + total
                + "\n\nItems:\n"
                + (itemLines.isEmpty() ? "Item details unavailable." : itemLines)
                + "\n\n" + action + ": " + link + "\n\nThe " + siteName + " team";
        emails.sendOrder(notification.getRecipient(), title + " - " + siteName,
                values, plainText, items);
    }

    private List<OrderEmailItem> items(OrderEmailNotification notification) {
        if (notification.getItemsSnapshot() == null) {
            return orders.findById(notification.getOrderId())
                    .map(order -> order.getOrderItems().stream().map(OrderEmailItem::from).toList())
                    .orElseGet(() -> {
                        log.warn("Articles indisponibles pour l'ancienne notification de commande {}.",
                                notification.getOrderId());
                        return List.of();
                    });
        }
        try {
            List<OrderEmailItem> result = objectMapper.readValue(notification.getItemsSnapshot(), new TypeReference<>() {});
            if (result == null || result.stream().anyMatch(item -> item == null || item.name() == null
                    || item.name().isBlank() || item.quantity() <= 0)) {
                throw new EmailDeliveryException("Articles de notification invalides", null);
            }
            return result;
        } catch (JsonProcessingException e) {
            throw new EmailDeliveryException("Impossible de lire les articles de la notification", e);
        }
    }

    private Content content(OrderStatus status) {
        return switch (status) {
            case PAID -> new Content("Order confirmed", "Confirmed",
                    "Thank you for your order! Your order is confirmed.");
            case PROCESSING -> new Content("Your order is being prepared", "Processing",
                    "We're getting your order ready.");
            case SHIPPED -> new Content("Your order has shipped", "Shipped",
                    "Good news! Your order is on its way.");
            case DELIVERED -> new Content("Your order has been delivered", "Delivered",
                    "Your order has been marked as delivered. We hope you love it!");
            case CANCELLED -> new Content("Your order was canceled", "Canceled",
                    "Your order has been canceled.");
            case PENDING -> throw new IllegalArgumentException("Une commande en attente ne doit pas être notifiée");
        };
    }

    private record Content(String title, String status, String message) {}
}
