package com.lb.algorithm;

import com.lb.backend.BackendInstance;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinAlgorithm implements LoadBalancerAlgorithm {

    private AtomicInteger counter = new AtomicInteger(0);

    @Override
    public Optional<BackendInstance> selectBackend(List<BackendInstance> instances,String clientIp) {
        if(instances.isEmpty()) return Optional.empty();
        return Optional.of(instances.get(counter.getAndIncrement() % instances.size()));
    }
}
