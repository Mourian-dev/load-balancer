package com.lb.backend;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
@Getter
public class BackendInstance {
    private final String id;
    private final String host;
    private final int port;
    private final AtomicInteger failureCount = new AtomicInteger(0);
    @Setter
    private int weight = 1;
    @Setter
    private int failureThreshold = 3;
    @Setter
    private volatile boolean isHealthy = true;
    @Setter
    public volatile Instant lastHealthCheckTime;

    public void markHealthy() {
        this.isHealthy = true;
        this.lastHealthCheckTime = Instant.now();
        this.failureCount.set(0);
    }

    public void markUnhealthy() {
        this.isHealthy = false;
        this.lastHealthCheckTime = Instant.now();
    }

    public void recordFailure() {
        this.failureCount.incrementAndGet();
    }

    public void resetFailures() {
        this.failureCount.set(0);
    }

    public String getUrl() {
        return String.format("http://%s:%d", host, port);
    }
}
