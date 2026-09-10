package com.lb.algorithm;

import com.lb.backend.BackendInstance;

import java.util.List;
import java.util.Optional;

public interface LoadBalancerAlgorithm {
    Optional<BackendInstance> selectBackend(List<BackendInstance> instances,String clientIp);
}
