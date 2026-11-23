package com.backend.ecommerce.service.email;

import com.backend.ecommerce.entity.OrderEmailNotification;
import com.backend.ecommerce.repository.OrderEmailNotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderNotificationDelivery {
    private final OrderEmailNotificationRepository notifications;
    private final OrderEmailSender sender;

    @Transactional
    public boolean sendNext() {
        Optional<OrderEmailNotification> next = notifications.lockNextReady(LocalDateTime.now());
        if (next.isEmpty()) return false;

        OrderEmailNotification notification = next.get();
        try {
            // Le verrou évite deux envois concurrents ; un arrêt après SMTP peut néanmoins provoquer une répétition.
            sender.send(notification);
            notification.setSentAt(LocalDateTime.now());
        } catch (EmailDeliveryException e) {
            int attempts = notification.getAttempts() + 1;
            notification.setAttempts(attempts);
            long delayMinutes = Math.min(60, 1L << Math.min(attempts - 1, 6));
            notification.setNextAttemptAt(LocalDateTime.now().plusMinutes(delayMinutes));
            Throwable cause = NestedExceptionUtils.getMostSpecificCause(e);
            log.warn("Échec de la notification {} (tentative {}, cause {}). Nouvelle tentative à {}.",
                    notification.getId(), attempts, cause == e ? e.getMessage() : cause.getClass().getSimpleName(),
                    notification.getNextAttemptAt());
        }
        notifications.save(notification);
        return true;
    }
}
