package com.lb.algorithm;

import com.lb.backend.BackendInstance;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class WeightedRoundRobinAlgorithm implements LoadBalancerAlgorithm {

    private AtomicInteger counter;

    public WeightedRoundRobinAlgorithm() {
        counter = new AtomicInteger(0);
    }

    @Override
    public Optional<BackendInstance> selectBackend(List<BackendInstance> instances, String clientIp) {
        if(instances.isEmpty()) return Optional.empty();

        int totalWeight = instances.stream().mapToInt(BackendInstance::getWeight).sum();

        int position = counter.getAndIncrement() % totalWeight;

        int current = 0;
        for(BackendInstance instance : instances) {
            current += instance.getWeight();

            if(position < current) return Optional.of(instance);
        }

        return Optional.of(instances.get(0));
    }
}
