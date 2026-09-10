package com.lb.algorithm;

import com.lb.backend.BackendInstance;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class RandomAlgorithm implements LoadBalancerAlgorithm {
    private final Random random;

    public RandomAlgorithm() {
        this.random = new Random();
    }

    @Override
    public Optional<BackendInstance> selectBackend(List<BackendInstance> instances, String clientIp) {
        if(instances.isEmpty()) return Optional.empty();
        return Optional.of(instances.get(random.nextInt(instances.size())));
    }
}
