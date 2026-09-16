package com.lb.health;

import com.lb.backend.BackendInstance;
import com.lb.backend.ResourcePool;
import com.lb.config.HealthCheckProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class ActiveHealthCheckStrategy implements HealthCheckStrategy {
    private final RestTemplate restTemplate;
    private final HealthCheckProperties properties;

    @Override
    public void checkHealth(ResourcePool resourcePool) {
        if(!properties.getActive().isEnabled()) {
            return;
        }

        for(BackendInstance instance : resourcePool.getAll()) {
            checkInstance(instance);
        }
    }

    private void checkInstance(BackendInstance instance) {
        String url = String.format("%s%s", instance.getUrl(), properties.getActive().getHealthEndpoint());
        try {
            restTemplate.getForObject(url, String.class);
            instance.markHealthy();
            log.debug("Instance {} is healthy", instance.getId());
        } catch(RestClientException e) {
            instance.markUnhealthy();;
            log.warn("Instance {} is unhealthy: {}", instance.getId(), e.getMessage());
        }
    }

    @Override
    public String getName() {
        return "ActiveHealthCheck";
    }
}
