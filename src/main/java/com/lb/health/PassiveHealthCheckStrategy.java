package com.lb.health;

import com.lb.backend.BackendInstance;
import com.lb.backend.ResourcePool;
import com.lb.config.HealthCheckProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PassiveHealthCheckStrategy implements HealthCheckStrategy {
    private final HealthCheckProperties properties;

    public void recordFailure(BackendInstance instance) {
        if(!properties.getPassive().isEnabled()) {
            return;
        }

        instance.recordFailure();
        int threshold = properties.getPassive().getFailureThreshold();
        int failures = instance.getFailureCount().get();

        if(failures >= threshold) {
            instance.markUnhealthy();
            log.warn("Backend {} marked unhealth {} failures >= {} threshold", instance.getId(), failures, threshold);
        }
    }

    public void recordSuccess(BackendInstance instance) {
        if(!properties.getPassive().isEnabled()) {
            return;
        }

        instance.resetFailures();
        if(!instance.isHealthy()) {
            instance.markHealthy();
            log.info("Backend {} recovered", instance.getId());
        }
    }

    @Override
    public void checkHealth(ResourcePool resourcePool) {
        throw new UnsupportedOperationException("Periodic check is not done");
    }

    @Override
    public String getName() {
        return "PassiveHealthCheckStrategy";
    }
}
