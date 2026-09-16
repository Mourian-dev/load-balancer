package com.lb.health;

import com.lb.backend.BackendInstance;
import com.lb.backend.ResourcePool;
import com.lb.config.HealthCheckProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class HealthCheckManager {
    private final ResourcePool resourcePool;
    private final HealthCheckProperties properties;
    private final ActiveHealthCheckStrategy activeHealthCheckStrategy;
    private final PassiveHealthCheckStrategy passiveHealthCheckStrategy;

    public List<BackendInstance> getHealthyBackends() {
        return resourcePool.getAll().stream().filter(BackendInstance::isHealthy).toList();
    }

    public boolean isHealthy(BackendInstance instance) {
        return instance != null && instance.isHealthy();
    }

    public boolean isHealthy(String id) {
        return isHealthy(
                resourcePool
                        .getAll()
                        .stream()
                        .filter(b -> b.getId().equals(id))
                        .findFirst()
                        .orElse(null)
        );
    }

    public void recordSuccess(BackendInstance instance) {
        if(instance == null) return;
        passiveHealthCheckStrategy.recordSuccess(instance);
        log.trace("Recorded success for backend {}", instance.getId());
    }

    public void recordFailure(BackendInstance instance) {
        if(instance == null) return;
        passiveHealthCheckStrategy.recordFailure(instance);
        log.trace("Recorded failure for backend {}", instance.getId());
    }

    public List<BackendInstance> getAll() {
        return resourcePool.getAll();
    }

    public String getHealthStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("Health Check Status:\n");

        for(BackendInstance instance : resourcePool.getAll()) {
            String status = instance.isHealthy() ? "HEALTHY" : "UNHEALTHY";
            sb.append(String.format("   %s: %s (failures: %d/%d)\n", instance.getId(), status, instance.getFailureCount().get(), properties.getPassive().getFailureThreshold()));
        }

        return sb.toString();
    }

    @Scheduled(fixedDelayString = "${laod-balancer.health-check.active.interval-seconds:10}000")
    public void runActiveHealthChecks() {
        activeHealthCheckStrategy.checkHealth(resourcePool);
    }

    @Scheduled(fixedDelayString = "30000")
    public void attemptRecovery() {
        List<BackendInstance> unhealthy = resourcePool.getAll().stream().filter(b -> !b.isHealthy()).toList();

        if(unhealthy.isEmpty()) {
            return;
        }

        BackendInstance instance = unhealthy.get(0);
        log.info("Attempting recovery of backend {}", instance.getId());
        activeHealthCheckStrategy.checkHealth(resourcePool);
    }
}
