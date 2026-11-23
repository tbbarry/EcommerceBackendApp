package com.backend.ecommerce.service;

import org.springframework.stereotype.Component;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
public class ContactRateLimiter {
    private static final Duration WINDOW = Duration.ofMinutes(10);
    private static final int LIMIT = 5;
    private static final int MAX_CLIENTS = 10000;
    private final Map<String, Window> clients = new HashMap<>();
    private final Clock clock;

    public ContactRateLimiter() { this(Clock.systemUTC()); }
    ContactRateLimiter(Clock clock) { this.clock = clock; }

    public synchronized boolean allow(String client) {
        Instant now = clock.instant();
        clients.values().removeIf(window -> !now.isBefore(window.start.plus(WINDOW)));
        Window window = clients.get(client);
        if (window == null) {
            if (clients.size() >= MAX_CLIENTS) return false;
            clients.put(client, new Window(now, 1));
            return true;
        }
        if (window.count >= LIMIT) return false;
        clients.put(client, new Window(window.start, window.count + 1));
        return true;
    }

    private record Window(Instant start, int count) {}
}
