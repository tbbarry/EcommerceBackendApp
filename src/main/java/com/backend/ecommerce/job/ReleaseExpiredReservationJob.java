package com.backend.ecommerce.job;

import com.backend.ecommerce.service.StockReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReleaseExpiredReservationJob {

    private final StockReservationService stockReservationService;

    @Scheduled(cron = "${app.scheduler.stock-release-cron:0 */5 * * * *}")
    public void releaseExpiredReservations() {
        try {
            log.info("Starting ReleaseExpiredReservationJob");
            Integer released = stockReservationService.processExpiredReservations();
            log.info("Job completed: {} reservations released", released);
        } catch (Exception e) {
            log.error("Job failed: {}", e.getMessage(), e);
        }
    }
}
