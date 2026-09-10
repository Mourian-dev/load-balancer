package com.lb.controller;

import com.lb.core.LoadBalancer;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@RestController
public class LoadBalancerController {
    private final LoadBalancer loadBalancer;

    @RequestMapping(value = "/**", method = {
            RequestMethod.GET,
            RequestMethod.POST,
            RequestMethod.PUT,
            RequestMethod.DELETE,
            RequestMethod.PATCH
    })
    public Mono<ResponseEntity<String>> route(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        log.info("LB received: {} {}", method, path);

        return loadBalancer
                .routeRequest(path, method, request.getRemoteAddr())
                .map(ResponseEntity::ok)
                .onErrorResume(err -> {
                    log.error("Error: {}", err.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Service Unavailable: " + err.getMessage()));
                });
    }

    @GetMapping("/lb/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("load balancer is healthy");
    }
}
