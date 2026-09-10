package com.lb.core;

import com.lb.algorithm.LoadBalancerAlgorithm;
import com.lb.backend.BackendInstance;
import com.lb.backend.ResourcePool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Component
public class LoadBalancer {
    private final ResourcePool resourcePool;
    private final LoadBalancerAlgorithm algorithm;
    private final WebClient webClient;

    public LoadBalancer(ResourcePool resourcePool,LoadBalancerAlgorithm algorithm) {
        this.resourcePool = resourcePool;
        this.algorithm = algorithm;
        this.webClient = WebClient.builder().build();
    }

    public Mono<String> routeRequest(String path, String method, String clientIp) {
        log.debug("Routing: {} {}", method, path);

        var backends = resourcePool.getAll();
        var selected = algorithm.selectBackend(backends, clientIp)
                .orElseThrow(() -> new RuntimeException("No backends available"));

        log.info("Selected backend: {} for {} {}", selected.getId(), method, path);

        return forwardRequest(selected, method, path);
    }


    private Mono<String> forwardRequest(BackendInstance instance, String method, String path) {
        String url = instance.getUrl() + path;
        log.debug("Forwarding to: {}", url);

        return webClient
                .method(HttpMethod.valueOf(method))
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(5))
                .doOnSuccess(response -> log.debug("Got response from {}", instance.getId()))
                .doOnError(error -> log.warn("Error from {}: {}", instance.getId(), error.getMessage()));
    }
}
