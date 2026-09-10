package com.lb.algorithm;

import com.lb.backend.BackendInstance;

import java.util.List;
import java.util.Optional;

public class IpHashAlgorithm implements LoadBalancerAlgorithm {
    @Override
    public Optional<BackendInstance> selectBackend(List<BackendInstance> instances, String clientIp) {
        if(instances.isEmpty()) return Optional.empty();

        int hash = clientIp.hashCode();
        int index = hash % instances.size();

        return Optional.of(instances.get(index));
    }
}
