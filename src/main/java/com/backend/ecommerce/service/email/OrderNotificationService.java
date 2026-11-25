package com.backend.ecommerce.service.email;

import com.backend.ecommerce.entity.Order;
import com.backend.ecommerce.entity.OrderEmailNotification;
import com.backend.ecommerce.enums.OrderStatus;
import com.backend.ecommerce.repository.OrderEmailNotificationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class OrderNotificationService {
    private final OrderEmailNotificationRepository notifications;
    private final ObjectMapper objectMapper;
    private final String adminEmail;
    private final OrderMessageTemplateService templates;

    public OrderNotificationService(OrderEmailNotificationRepository notifications, ObjectMapper objectMapper,
                                    OrderMessageTemplateService templates,
                                    @Value("${ADMIN_NOTIFICATION_EMAIL}") String adminEmail) {
        this.notifications = notifications;
        this.objectMapper = objectMapper;
        this.templates = templates;
        try {
            InternetAddress address = new InternetAddress(adminEmail, true);
            address.validate();
            if (address.getPersonal() != null || !adminEmail.equals(address.getAddress())) {
                throw new AddressException("Adresse e-mail invalide");
            }
        } catch (AddressException e) {
            throw new IllegalStateException("ADMIN_NOTIFICATION_EMAIL doit contenir une adresse e-mail valide.", e);
        }
        this.adminEmail = adminEmail;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void queueStatusChange(Order order, OrderStatus previousStatus) {
        queue(order, previousStatus, false);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void queuePaymentConfirmed(Order order, OrderStatus previousStatus) {
        queue(order, previousStatus, true);
    }

    private void queue(Order order, OrderStatus previousStatus, boolean paymentConfirmed) {
        if (order.getStatus() == previousStatus || order.getStatus() == OrderStatus.PENDING_PAYMENT) return;

        String items;
        try {
            items = objectMapper.writeValueAsString(order.getOrderItems().stream().map(OrderEmailItem::from).toList());
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Impossible de préparer les articles pour la notification.", e);
        }
        notifications.save(notification(order, order.getUser().getEmail(), false, items));
        if (paymentConfirmed && previousStatus == OrderStatus.PENDING_PAYMENT && order.getStatus() == OrderStatus.PAID) {
            notifications.save(notification(order, adminEmail, true, items));
        }
    }

    private OrderEmailNotification notification(Order order, String recipient, boolean admin, String items) {
        OrderMessageTemplateService.Template template = templates.get(admin ? "ADMIN_PAID" : order.getStatus().name());
        String customerName = order.getUser().getFirstname();
        String number = order.getOrderNumber();
        return OrderEmailNotification.builder()
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .recipient(recipient)
                .customerName(order.getUser().getFirstname())
                .orderStatus(order.getStatus())
                .total(order.getTotal())
                .itemsSnapshot(items)
                .adminNotification(admin)
                .subjectSnapshot(OrderMessageTemplateService.resolve(template.subject(), customerName, number)
                        .replace('\r', ' ').replace('\n', ' '))
                .messageSnapshot(OrderMessageTemplateService.resolve(template.message(), customerName, number))
                .nextAttemptAt(LocalDateTime.now())
                .build();
    }
}
