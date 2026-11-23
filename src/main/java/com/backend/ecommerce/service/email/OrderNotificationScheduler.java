package com.backend.ecommerce.service.email;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class OrderNotificationScheduler {
    private final OrderNotificationDelivery delivery;

    @Scheduled(initialDelay = 5000, fixedDelay = 30000)
    public void sendPending() {
        for (int i = 0; i < 20; i++) {
            if (!delivery.sendNext()) return;
        }
    }
}
