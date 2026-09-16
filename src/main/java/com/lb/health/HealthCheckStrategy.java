package com.lb.health;

import com.lb.backend.ResourcePool;

public interface HealthCheckStrategy {
    void checkHealth(ResourcePool resourcePool);
    String getName();
}
